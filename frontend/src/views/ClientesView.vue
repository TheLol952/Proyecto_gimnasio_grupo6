<template>
  <section class="page">
    <div class="page-header">
      <div>
        <span class="eyebrow">HU-001</span>
        <h2>Gestión de clientes</h2>
        <p>Registra y consulta la información de los clientes del gimnasio.</p>
      </div>
      <button class="secondary" type="button" @click="cargarClientes" :disabled="cargando">
        {{ cargando ? "Cargando..." : "Actualizar lista" }}
      </button>
    </div>

    <div class="grid">
      <form class="card form-card" @submit.prevent="registrarCliente">
        <h3>Registrar cliente</h3>

        <label>
          Nombre *
          <input v-model.trim="form.nombre" required maxlength="60" placeholder="Ej. Ana" />
        </label>

        <label>
          Apellido *
          <input v-model.trim="form.apellido" required maxlength="60" placeholder="Ej. López" />
        </label>

        <label>
          Teléfono
          <input v-model.trim="form.telefono" maxlength="15" inputmode="tel" placeholder="Ej. 7000-0000" />
        </label>

        <label>
          Correo
          <input v-model.trim="form.correo" type="email" maxlength="120" placeholder="cliente@correo.com" />
        </label>

        <button class="primary" type="submit" :disabled="guardando">
          {{ guardando ? "Guardando..." : "Registrar cliente" }}
        </button>

        <p v-if="mensaje" :class="error ? 'msg-error' : 'msg-ok'" role="alert">
          {{ mensaje }}
        </p>
      </form>

      <div class="card list-card">
        <div class="list-header">
          <div>
            <h3>Clientes registrados</h3>
            <small>{{ clientesFiltrados.length }} resultado(s)</small>
          </div>
          <input
            v-model.trim="busqueda"
            class="search"
            type="search"
            placeholder="Buscar por nombre..."
            aria-label="Buscar cliente por nombre"
          />
        </div>

        <div v-if="cargando" class="empty">Cargando clientes...</div>
        <div v-else-if="clientesFiltrados.length === 0" class="empty">
          No hay clientes que coincidan con la búsqueda.
        </div>

        <div v-else class="table-wrapper">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Cliente</th>
                <th>Teléfono</th>
                <th>Correo</th>
                <th>Registro</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="cliente in clientesFiltrados" :key="cliente.idCliente">
                <td><span class="badge">{{ cliente.idCliente }}</span></td>
                <td>{{ cliente.nombre }} {{ cliente.apellido }}</td>
                <td>{{ cliente.telefono || "—" }}</td>
                <td>{{ cliente.correo || "—" }}</td>
                <td>{{ cliente.fechaRegistro || "—" }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from "vue";
import api from "../services/api";

const clientes = ref([]);
const busqueda = ref("");
const cargando = ref(false);
const guardando = ref(false);
const mensaje = ref("");
const error = ref(false);

const form = ref({
  nombre: "",
  apellido: "",
  telefono: "",
  correo: "",
});

const clientesFiltrados = computed(() => {
  const termino = busqueda.value.toLowerCase();
  if (!termino) return clientes.value;

  return clientes.value.filter((cliente) =>
    `${cliente.nombre} ${cliente.apellido}`.toLowerCase().includes(termino)
  );
});

function obtenerMensajeError(e, mensajePorDefecto) {
  return e.response?.data?.message
    || e.response?.data?.error
    || mensajePorDefecto;
}

async function cargarClientes() {
  cargando.value = true;

  try {
    const { data } = await api.get("/clientes");
    clientes.value = data;
  } catch (e) {
    error.value = true;
    mensaje.value = obtenerMensajeError(e, "No se pudieron cargar los clientes.");
  } finally {
    cargando.value = false;
  }
}

async function registrarCliente() {
  mensaje.value = "";
  guardando.value = true;

  try {
    await api.post("/clientes", form.value);

    mensaje.value = "Cliente registrado correctamente.";
    error.value = false;
    form.value = { nombre: "", apellido: "", telefono: "", correo: "" };
    await cargarClientes();
  } catch (e) {
    mensaje.value = obtenerMensajeError(e, "Ocurrió un error al registrar el cliente.");
    error.value = true;
  } finally {
    guardando.value = false;
  }
}

onMounted(cargarClientes);
</script>

<style scoped>
.page { max-width: 1200px; margin: 0 auto; padding: 24px; color: #172033; }
.page-header, .list-header { display: flex; justify-content: space-between; align-items: center; gap: 16px; margin-bottom: 20px; }
.eyebrow { font-size: 12px; font-weight: 700; letter-spacing: .08em; }
h2, h3 { margin: 4px 0 8px; }
p { margin: 0; }
.page-header p { color: #667085; }
.grid { display: grid; grid-template-columns: minmax(280px, 360px) 1fr; gap: 20px; }
.card { background: #fff; border: 1px solid #e4e7ec; border-radius: 14px; padding: 20px; box-shadow: 0 4px 18px rgba(16, 24, 40, .06); }
.form-card { display: flex; flex-direction: column; gap: 14px; }
label { display: flex; flex-direction: column; gap: 6px; font-size: 14px; font-weight: 600; }
input, select, button { font: inherit; }
input { width: 100%; box-sizing: border-box; border: 1px solid #d0d5dd; border-radius: 9px; padding: 10px 12px; }
button { border: 0; border-radius: 9px; padding: 10px 14px; cursor: pointer; font-weight: 700; }
button:disabled { opacity: .6; cursor: not-allowed; }
.primary { width: 100%; }
.secondary { border: 1px solid #d0d5dd; background: #fff; color: #1f4e5f; }
.search { max-width: 240px; }
.table-wrapper { overflow-x: auto; }
table { width: 100%; border-collapse: collapse; }
th, td { text-align: left; padding: 11px 10px; border-bottom: 1px solid #eaecf0; white-space: nowrap; }
th { font-size: 12px; text-transform: uppercase; }
.badge { font-size: 12px; font-weight: 700; }
.empty { padding: 30px 10px; text-align: center; color: #667085; }
.msg-ok, .msg-error { padding: 10px; border-radius: 8px; }
.msg-ok { background: #ecfdf3; }
.msg-error { background: #fef3f2; }
@media (max-width: 850px) { .grid { grid-template-columns: 1fr; } .page-header, .list-header { align-items: stretch; flex-direction: column; } .search { max-width: none; } }
</style>
