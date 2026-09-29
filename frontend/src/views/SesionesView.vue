<template>
  <section>
    <h2>Agenda de sesiones (HU-004 / HU-005)</h2>

    <form @submit.prevent="agendar">
      <label>ID del cliente
        <input v-model="form.idCliente" required />
      </label>
      <label>ID del entrenador
        <input v-model="form.idEntrenador" required />
      </label>
      <label>Hora de inicio
        <input v-model="form.horaInicio" type="datetime-local" required />
      </label>
      <label>Hora de fin
        <input v-model="form.horaFin" type="datetime-local" required />
      </label>
      <button type="submit">Agendar sesión</button>
      <p v-if="mensaje" :class="error ? 'msg-error' : 'msg-ok'">{{ mensaje }}</p>
    </form>

    <table>
      <thead><tr><th>ID</th><th>Cliente</th><th>Entrenador</th><th>Inicio</th><th>Fin</th><th>Estado</th></tr></thead>
      <tbody>
        <tr v-for="s in sesiones" :key="s.idSesion">
          <td>{{ s.idSesion }}</td>
          <td>{{ s.idCliente }}</td>
          <td>{{ s.idEntrenador }}</td>
          <td>{{ s.horaInicio }}</td>
          <td>{{ s.horaFin }}</td>
          <td>{{ s.estado }}</td>
        </tr>
      </tbody>
    </table>
  </section>
</template>

<script setup>
import { ref, onMounted } from "vue";
import api from "../services/api";

const sesiones = ref([]);
const form = ref({ idCliente: "", idEntrenador: "", horaInicio: "", horaFin: "" });
const mensaje = ref("");
const error = ref(false);

async function cargarSesiones() {
  const { data } = await api.get("/sesiones");
  sesiones.value = data;
}

async function agendar() {
  try {
    await api.post("/sesiones", form.value);
    mensaje.value = "Sesión agendada correctamente.";
    error.value = false;
    await cargarSesiones();
  } catch (e) {
    mensaje.value = e.response?.data?.message || "No fue posible agendar (posible traslape de horario).";
    error.value = true;
  }
}

onMounted(cargarSesiones);
</script>
