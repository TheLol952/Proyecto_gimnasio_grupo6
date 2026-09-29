<template>
  <section>
    <h2>Control de acceso QR simulado (HU-003)</h2>

    <form @submit.prevent="registrarAcceso">
      <label>ID del cliente
        <input v-model="form.idCliente" placeholder="CLI-XXXXXXXX" required />
      </label>
      <label>Código QR (simulado)
        <input v-model="form.codigoQR" placeholder="QR-DEMO-001" />
      </label>
      <button type="submit">Simular escaneo</button>
    </form>

    <p v-if="resultado" :class="resultado.estado === 'PERMITIDO' ? 'msg-ok' : 'msg-error'">
      Acceso {{ resultado.estado }} — {{ resultado.fechaHora }}
    </p>

    <h3>Historial de accesos</h3>
    <table>
      <thead><tr><th>ID</th><th>Cliente</th><th>Fecha/Hora</th><th>Estado</th></tr></thead>
      <tbody>
        <tr v-for="a in accesos" :key="a.idAcceso">
          <td>{{ a.idAcceso }}</td>
          <td>{{ a.idCliente }}</td>
          <td>{{ a.fechaHora }}</td>
          <td>{{ a.estado }}</td>
        </tr>
      </tbody>
    </table>
  </section>
</template>

<script setup>
import { ref, onMounted } from "vue";
import api from "../services/api";

const accesos = ref([]);
const form = ref({ idCliente: "", codigoQR: "" });
const resultado = ref(null);

async function cargarAccesos() {
  const { data } = await api.get("/accesos");
  accesos.value = data;
}

async function registrarAcceso() {
  const { data } = await api.post("/accesos", form.value);
  resultado.value = data;
  await cargarAccesos();
}

onMounted(cargarAccesos);
</script>
