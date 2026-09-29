<template>
  <section class="page">
    <div class="page-header">
      <div>
        <span class="eyebrow">PERSONAL</span>
        <h2>Gestión de entrenadores</h2>
        <p>Registra al personal que estará disponible para impartir sesiones.</p>
      </div>
      <button class="secondary" type="button" @click="cargarEntrenadores" :disabled="cargando">
        {{ cargando ? "Cargando..." : "Actualizar lista" }}
      </button>
    </div>

    <div class="grid">
      <form class="card form-card" @submit.prevent="registrarEntrenador">
        <h3>Registrar entrenador</h3>

        <label>
          Nombre *
          <input v-model.trim="form.nombre" required maxlength="60" placeholder="Ej. Carlos" />
        </label>

        <label>
          Apellido *
          <input v-model.trim="form.apellido" required maxlength="60" placeholder="Ej. Hernández" />
        </label>

        <label>
          Teléfono
          <input v-model.trim="form.telefono" maxlength="15" inputmode="tel" placeholder="Ej. 7000-0000" />
        </label>

        <label>
          Correo
          <input v-model.trim="form.correo" type="email" maxlength="120" placeholder="entrenador@correo.com" />
        </label>

        <label>
          Especialidad *
          <input v-model.trim="form.especialidad" required maxlength="80" placeholder="Ej. Fuerza y acondicionamiento" />
        </label>

        <label>
          Turno *
          <select v-model="form.turno" required>
            <option disabled value="">Selecciona un turno</option>
            <option value="MATUTINO">Matutino</option>
            <option value="VESPERTINO">Vespertino</option>
            <option value="NOCTURNO">Nocturno</option>
            <option value="MIXTO">Mixto</option>
          </select>
        </label>

        <button class="primary" type="submit" :disabled="guardando">
          {{ guardando ? "Guardando..." : "Registrar entrenador" }}
        </button>

        <p v-if="mensaje" :class="error ? 'msg-error' : 'msg-ok'" role="alert">
          {{ mensaje }}
        </p>
      </form>

      <div class="card list-card">
        <div class="list-header">
          <div>
            <h3>Entrenadores registrados</h3>
            <small>{{ entrenadoresFiltrados.length }} resultado(s)</small>
          </div>
          <input
            v-model.trim="busqueda"
            class="search"
            type="search"
            placeholder="Buscar entrenador..."
            aria-label="Buscar entrenador"
          />
        </div>

        <div v-if="cargando" class="empty">Cargando entrenadores...</div>
        <div v-else-if="entrenadoresFiltrados.length === 0" class="empty">
          No hay entrenadores que coincidan con la búsqueda.
        </div>

        <div v-else class="table-wrapper">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Entrenador</th>
                <th>Especialidad</th>
                <th>Turno</th>
                <th>Teléfono</th>
                <th>Correo</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="entrenador in entrenadoresFiltrados" :key="entrenador.idEntrenador">
                <td><span class="badge">{{ entrenador.idEntrenador }}</span></td>
                <td>{{ entrenador.nombre }} {{ entrenador.apellido }}</td>
                <td>{{ entrenador.especialidad }}</td>
                <td>{{ mostrarTurno(entrenador.turno) }}</td>
                <td>{{ entrenador.telefono || "—" }}</td>
                <td>{{ entrenador.correo || "—" }}</td>
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

const entrenadores = ref([]);
const busqueda = ref("");
const cargando = ref(false);
const guardando = ref(false);
const mensaje = ref("");
const error = ref(false);

const formularioVacio = () => ({
  nombre: "",
  apellido: "",
  telefono: "",
  correo: "",
  especialidad: "",
  turno: "",
});

const form = ref(formularioVacio());

const entrenadoresFiltrados = computed(() => {
  const termino = busqueda.value.toLowerCase();
  if (!termino) return entrenadores.value;

  return entrenadores.value.filter((entrenador) =>
    `${entrenador.nombre} ${entrenador.apellido} ${entrenador.especialidad} ${entrenador.turno}`
      .toLowerCase()
      .includes(termino)
  );
});

function obtenerMensajeError(e, mensajePorDefecto) {
  return e.response?.data?.message
    || e.response?.data?.error
    || mensajePorDefecto;
}

function mostrarTurno(turno) {
  if (!turno) return "—";
  return turno.charAt(0) + turno.slice(1).toLowerCase();
}

async function cargarEntrenadores() {
  cargando.value = true;
  mensaje.value = "";

  try {
    const { data } = await api.get("/entrenadores");
    entrenadores.value = data;
    error.value = false;
  } catch (e) {
    error.value = true;
    mensaje.value = obtenerMensajeError(e, "No se pudieron cargar los entrenadores.");
  } finally {
    cargando.value = false;
  }
}

async function registrarEntrenador() {
  guardando.value = true;
  mensaje.value = "";

  try {
    await api.post("/entrenadores", form.value);
    form.value = formularioVacio();
    await cargarEntrenadores();
    mensaje.value = "Entrenador registrado correctamente.";
    error.value = false;
  } catch (e) {
    mensaje.value = obtenerMensajeError(e, "Ocurrió un error al registrar el entrenador.");
    error.value = true;
  } finally {
    guardando.value = false;
  }
}

onMounted(cargarEntrenadores);
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
