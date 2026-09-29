<template>
  <section class="page">
    <div class="page-header">
      <div>
        <span class="eyebrow">HU-004 / HU-005</span>
        <h2>Agenda de sesiones</h2>
        <p>Programa sesiones entre clientes y entrenadores registrados.</p>
      </div>
      <button class="secondary" type="button" @click="cargarDatos" :disabled="cargando">
        {{ cargando ? "Cargando..." : "Actualizar agenda" }}
      </button>
    </div>

    <div class="grid">
      <form class="card form-card" @submit.prevent="agendar">
        <h3>Nueva sesión</h3>

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
          Entrenador *
          <select v-model="form.idEntrenador" required :disabled="entrenadores.length === 0">
            <option disabled value="">
              {{ entrenadores.length ? "Selecciona un entrenador" : "No hay entrenadores registrados" }}
            </option>
            <option
              v-for="entrenador in entrenadores"
              :key="entrenador.idEntrenador"
              :value="entrenador.idEntrenador"
            >
              {{ entrenador.nombre }} {{ entrenador.apellido }} — {{ entrenador.especialidad }}
            </option>
          </select>
        </label>

        <label>
          Hora de inicio *
          <input v-model="form.horaInicio" type="datetime-local" required />
        </label>

        <label>
          Hora de fin *
          <input v-model="form.horaFin" type="datetime-local" :min="form.horaInicio" required />
        </label>

        <button
          class="primary"
          type="submit"
          :disabled="guardando || !form.idCliente || !form.idEntrenador"
        >
          {{ guardando ? "Agendando..." : "Agendar sesión" }}
        </button>

        <p v-if="mensaje" :class="error ? 'msg-error' : 'msg-ok'" role="alert">
          {{ mensaje }}
        </p>
      </form>

      <div class="card list-card">
        <div class="list-header">
          <div>
            <h3>Sesiones programadas</h3>
            <small>{{ sesiones.length }} sesión(es)</small>
          </div>
        </div>

        <div v-if="cargando" class="empty">Cargando agenda...</div>
        <div v-else-if="sesiones.length === 0" class="empty">No hay sesiones programadas.</div>

        <div v-else class="table-wrapper">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Cliente</th>
                <th>Entrenador</th>
                <th>Inicio</th>
                <th>Fin</th>
                <th>Estado</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="sesion in sesiones" :key="sesion.idSesion">
                <td><span class="badge">{{ sesion.idSesion }}</span></td>
                <td>{{ nombreCliente(sesion.idCliente) }}</td>
                <td>{{ nombreEntrenador(sesion.idEntrenador) }}</td>
                <td>{{ formatearFecha(sesion.horaInicio) }}</td>
                <td>{{ formatearFecha(sesion.horaFin) }}</td>
                <td><span class="status">{{ sesion.estado }}</span></td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { onMounted, ref } from "vue";
import api from "../services/api";

const sesiones = ref([]);
const clientes = ref([]);
const entrenadores = ref([]);
const cargando = ref(false);
const guardando = ref(false);
const mensaje = ref("");
const error = ref(false);
const form = ref({ idCliente: "", idEntrenador: "", horaInicio: "", horaFin: "" });

function obtenerMensajeError(e, mensajePorDefecto) {
  return e.response?.data?.message
    || e.response?.data?.error
    || mensajePorDefecto;
}

function nombreCliente(id) {
  const cliente = clientes.value.find((item) => item.idCliente === id);
  return cliente ? `${cliente.nombre} ${cliente.apellido}` : id;
}

function nombreEntrenador(id) {
  const entrenador = entrenadores.value.find((item) => item.idEntrenador === id);
  return entrenador ? `${entrenador.nombre} ${entrenador.apellido}` : id;
}

function formatearFecha(fecha) {
  if (!fecha) return "—";
  return new Intl.DateTimeFormat("es-SV", {
    dateStyle: "short",
    timeStyle: "short",
  }).format(new Date(fecha));
}

async function cargarDatos() {
  cargando.value = true;
  mensaje.value = "";

  try {
    const [respuestaSesiones, respuestaClientes, respuestaEntrenadores] = await Promise.all([
      api.get("/sesiones"),
      api.get("/clientes"),
      api.get("/entrenadores"),
    ]);

    sesiones.value = respuestaSesiones.data;
    clientes.value = respuestaClientes.data;
    entrenadores.value = respuestaEntrenadores.data;

    if (!clientes.value.some((item) => item.idCliente === form.value.idCliente)) {
      form.value.idCliente = clientes.value[0]?.idCliente || "";
    }
    if (!entrenadores.value.some((item) => item.idEntrenador === form.value.idEntrenador)) {
      form.value.idEntrenador = entrenadores.value[0]?.idEntrenador || "";
    }
    error.value = false;
  } catch (e) {
    error.value = true;
    mensaje.value = obtenerMensajeError(e, "No se pudieron cargar los datos de la agenda.");
  } finally {
    cargando.value = false;
  }
}

async function agendar() {
  mensaje.value = "";

  if (new Date(form.value.horaFin) <= new Date(form.value.horaInicio)) {
    mensaje.value = "La hora de fin debe ser posterior a la hora de inicio.";
    error.value = true;
    return;
  }

  guardando.value = true;
  try {
    await api.post("/sesiones", form.value);
    form.value.horaInicio = "";
    form.value.horaFin = "";
    await cargarDatos();
    mensaje.value = "Sesión agendada correctamente.";
    error.value = false;
  } catch (e) {
    mensaje.value = obtenerMensajeError(e, "No fue posible agendar la sesión.");
    error.value = true;
  } finally {
    guardando.value = false;
  }
}

onMounted(cargarDatos);
</script>

<style scoped>
.page { max-width: 1200px; margin: 0 auto; padding: 24px; color: #172033; }
.page-header, .list-header { display: flex; justify-content: space-between; align-items: center; gap: 16px; margin-bottom: 20px; }
.eyebrow { font-size: 12px; font-weight: 700; letter-spacing: .08em; }
h2, h3 { margin: 4px 0 8px; }
p { margin: 0; }
.page-header p { color: #667085; }
.grid { display: grid; grid-template-columns: minmax(300px, 370px) 1fr; gap: 20px; }
.card { background: #fff; border: 1px solid #e4e7ec; border-radius: 14px; padding: 20px; box-shadow: 0 4px 18px rgba(16, 24, 40, .06); }
.form-card { display: flex; flex-direction: column; gap: 14px; }
label { display: flex; flex-direction: column; gap: 6px; font-size: 14px; font-weight: 600; }
input, select, button { font: inherit; }
input, select { width: 100%; box-sizing: border-box; border: 1px solid #d0d5dd; border-radius: 9px; padding: 10px 12px; background: #fff; }
button { border: 0; border-radius: 9px; padding: 10px 14px; cursor: pointer; font-weight: 700; }
button:disabled { opacity: .6; cursor: not-allowed; }
.primary { width: 100%; }
.secondary { border: 1px solid #d0d5dd; background: #fff; color: #1f4e5f; }
.table-wrapper { overflow-x: auto; }
table { width: 100%; border-collapse: collapse; }
th, td { text-align: left; padding: 11px 10px; border-bottom: 1px solid #eaecf0; white-space: nowrap; }
th { font-size: 12px; text-transform: uppercase; }
.badge { font-size: 12px; font-weight: 700; }
.status { display: inline-block; border-radius: 999px; padding: 4px 8px; background: #ecfdf3; color: #067647; font-size: 12px; font-weight: 700; }
.empty { padding: 30px 10px; text-align: center; color: #667085; }
.msg-ok, .msg-error { padding: 10px; border-radius: 8px; }
.msg-ok { background: #ecfdf3; }
.msg-error { background: #fef3f2; }
@media (max-width: 850px) { .grid { grid-template-columns: 1fr; } .page-header { align-items: stretch; flex-direction: column; } }
</style>
