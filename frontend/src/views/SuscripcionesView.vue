<template>
  <section class="page">
    <div class="page-header">
      <div>
        <span class="eyebrow">HU-002</span>
        <h2>Suscripciones y planes</h2>
        <p>Asocia un plan disponible a un cliente registrado.</p>
      </div>
      <button class="secondary" type="button" @click="cargarDatos" :disabled="cargando">
        {{ cargando ? "Cargando..." : "Actualizar datos" }}
      </button>
    </div>

    <div class="grid">
      <form class="card form-card" @submit.prevent="venderPlan">
        <h3>Activar suscripción</h3>

        <label>
          Cliente *
          <select v-model="form.idCliente" required :disabled="clientes.length === 0">
            <option disabled value="">
              {{ clientes.length ? "Selecciona un cliente" : "No hay clientes registrados" }}
            </option>
            <option v-for="cliente in clientes" :key="cliente.idCliente" :value="cliente.idCliente">
              {{ cliente.nombre }} {{ cliente.apellido }} — {{ cliente.idCliente }}
            </option>
          </select>
        </label>

        <label>
          Plan *
          <select v-model="form.idPlan" required :disabled="planes.length === 0">
            <option disabled value="">
              {{ planes.length ? "Selecciona un plan" : "No hay planes disponibles" }}
            </option>
            <option v-for="plan in planes" :key="plan.idPlan" :value="plan.idPlan">
              {{ plan.nombre }} — ${{ precio(plan) }}
            </option>
          </select>
        </label>

        <label>
          Método de pago
          <select v-model="form.metodoPago">
            <option value="EFECTIVO">Efectivo</option>
            <option value="TARJETA">Tarjeta</option>
          </select>
        </label>

        <button class="primary" type="submit" :disabled="guardando || !form.idCliente || !form.idPlan">
          {{ guardando ? "Procesando..." : "Vender / activar suscripción" }}
        </button>

        <p v-if="mensaje" :class="error ? 'msg-error' : 'msg-ok'" role="alert">
          {{ mensaje }}
        </p>
      </form>

      <div class="card list-card">
        <div class="list-header">
          <div>
            <h3>Planes disponibles</h3>
            <small>{{ planes.length }} plan(es)</small>
          </div>
        </div>

        <div v-if="cargando" class="empty">Cargando planes y clientes...</div>
        <div v-else-if="planes.length === 0" class="empty">No hay planes disponibles.</div>

        <div v-else class="table-wrapper">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Plan</th>
                <th>Precio base</th>
                <th>Precio final</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="plan in planes" :key="plan.idPlan">
                <td>{{ plan.idPlan }}</td>
                <td>{{ plan.nombre }}</td>
                <td>${{ Number(plan.precioBase ?? 0).toFixed(2) }}</td>
                <td><strong>${{ precio(plan) }}</strong></td>
              </tr>
            </tbody>
          </table>
        </div>

        <div v-if="suscripciones.length" class="subscriptions">
          <h3>Suscripciones del cliente seleccionado</h3>
          <div v-for="s in suscripciones" :key="s.idSuscripcion" class="subscription">
            <strong>{{ s.idSuscripcion }}</strong>
            <span>Plan: {{ s.idPlan }}</span>
            <span>Estado: {{ s.estado }}</span>
            <span>Hasta: {{ s.fechaFin }}</span>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { onMounted, ref, watch } from "vue";
import api from "../services/api";

const clientes = ref([]);
const planes = ref([]);
const suscripciones = ref([]);
const cargando = ref(false);
const guardando = ref(false);
const mensaje = ref("");
const error = ref(false);

const form = ref({
  idCliente: "",
  idPlan: "",
  metodoPago: "EFECTIVO",
});

function precio(plan) {
  if (plan.precioFinal !== undefined && plan.precioFinal !== null) {
    return Number(plan.precioFinal).toFixed(2);
  }

  // El modelo Java calcula precioFinal() como método, por lo que
  // si Jackson no lo expone como propiedad, calculamos la presentación
  // según el tipo de plan recibido por la API.
  const nombre = String(plan.nombre || "").toLowerCase();
  const precioBase = Number(plan.precioBase ?? 0);

  if (nombre.includes("anual")) {
    const descuento = Number(plan.descuento ?? 0.15);
    return (precioBase * 12 * (1 - descuento)).toFixed(2);
  }

  return precioBase.toFixed(2);
}

function obtenerMensajeError(e, mensajePorDefecto) {
  return e.response?.data?.message
    || e.response?.data?.error
    || mensajePorDefecto;
}

async function cargarClientes() {
  const { data } = await api.get("/clientes");
  clientes.value = data;

  if (!clientes.value.some((cliente) => cliente.idCliente === form.value.idCliente)) {
    form.value.idCliente = clientes.value[0]?.idCliente || "";
  }
}

async function cargarPlanes() {
  const { data } = await api.get("/planes");
  planes.value = data;

  if (!planes.value.some((plan) => plan.idPlan === form.value.idPlan)) {
    form.value.idPlan = planes.value[0]?.idPlan || "";
  }
}

async function cargarSuscripciones() {
  if (!form.value.idCliente) {
    suscripciones.value = [];
    return;
  }

  try {
    const { data } = await api.get(`/suscripciones/cliente/${form.value.idCliente}`);
    suscripciones.value = data;
  } catch {
    suscripciones.value = [];
  }
}

async function cargarDatos() {
  cargando.value = true;
  mensaje.value = "";

  try {
    await Promise.all([cargarClientes(), cargarPlanes()]);
    await cargarSuscripciones();
    error.value = false;
  } catch (e) {
    error.value = true;
    mensaje.value = obtenerMensajeError(e, "No se pudieron cargar los datos necesarios.");
  } finally {
    cargando.value = false;
  }
}

async function venderPlan() {
  guardando.value = true;
  mensaje.value = "";

  try {
    await api.post("/suscripciones", form.value);
    mensaje.value = "Suscripción registrada correctamente.";
    error.value = false;
    await cargarSuscripciones();
  } catch (e) {
    mensaje.value = obtenerMensajeError(e, "Ocurrió un error al registrar la suscripción.");
    error.value = true;
  } finally {
    guardando.value = false;
  }
}

watch(() => form.value.idCliente, cargarSuscripciones);
onMounted(cargarDatos);
</script>

<style scoped>
.page { max-width: 1200px; margin: 0 auto; padding: 24px; color: #172033; }
.page-header, .list-header { display: flex; justify-content: space-between; align-items: center; gap: 16px; margin-bottom: 20px; }
.eyebrow { font-size: 12px; font-weight: 700; letter-spacing: .08em; }
h2, h3 { margin: 4px 0 8px; }
.page-header p { color: #667085; }
.grid { display: grid; grid-template-columns: minmax(280px, 360px) 1fr; gap: 20px; }
.card { background: #fff; border: 1px solid #e4e7ec; border-radius: 14px; padding: 20px; box-shadow: 0 4px 18px rgba(16, 24, 40, .06); }
.form-card { display: flex; flex-direction: column; gap: 14px; }
label { display: flex; flex-direction: column; gap: 6px; font-size: 14px; font-weight: 600; }
input, select, button { font: inherit; }
select { width: 100%; box-sizing: border-box; border: 1px solid #d0d5dd; border-radius: 9px; padding: 10px 12px; background: #fff; }
button { border: 0; border-radius: 9px; padding: 10px 14px; cursor: pointer; font-weight: 700; }
button:disabled { opacity: .6; cursor: not-allowed; }
.primary { width: 100%; }
.secondary { border: 1px solid #d0d5dd; background: #fff; color: #1f4e5f; }
.table-wrapper { overflow-x: auto; }
table { width: 100%; border-collapse: collapse; }
th, td { text-align: left; padding: 11px 10px; border-bottom: 1px solid #eaecf0; white-space: nowrap; }
th { font-size: 12px; text-transform: uppercase; }
.empty { padding: 30px 10px; text-align: center; color: #667085; }
.msg-ok, .msg-error { padding: 10px; border-radius: 8px; }
.msg-ok { background: #ecfdf3; }
.msg-error { background: #fef3f2; }
.subscriptions { margin-top: 24px; }
.subscription { display: grid; grid-template-columns: 1fr 1fr 1fr 1fr; gap: 10px; padding: 10px; border: 1px solid #eaecf0; border-radius: 9px; margin-top: 8px; font-size: 13px; }
@media (max-width: 850px) { .grid { grid-template-columns: 1fr; } .page-header, .list-header { align-items: stretch; flex-direction: column; } .subscription { grid-template-columns: 1fr 1fr; } }
</style>
