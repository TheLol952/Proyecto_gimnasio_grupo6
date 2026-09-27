<template>
  <section>
    <h2>Reportes (HU-006) y vencimientos próximos (HU-007)</h2>

    <table v-if="reporte">
      <tbody>
        <tr><th>Total de clientes</th><td>{{ reporte.totalClientes }}</td></tr>
        <tr><th>Suscripciones activas</th><td>{{ reporte.suscripcionesActivas }}</td></tr>
        <tr><th>Suscripciones vencidas</th><td>{{ reporte.suscripcionesVencidas }}</td></tr>
        <tr><th>Ingresos totales</th><td>${{ reporte.ingresosTotales.toFixed(2) }}</td></tr>
      </tbody>
    </table>

    <h3>Suscripciones próximas a vencer</h3>
    <p style="font-size:.85rem;color:#555">
      Esta lista la genera el hilo de verificación en segundo plano
      (NotificacionVencimientoService), no una consulta manual.
    </p>
    <table>
      <thead><tr><th>ID</th><th>Cliente</th><th>Fecha fin</th><th>Estado</th></tr></thead>
      <tbody>
        <tr v-for="s in vencimientos" :key="s.idSuscripcion">
          <td>{{ s.idSuscripcion }}</td>
          <td>{{ s.idCliente }}</td>
          <td>{{ s.fechaFin }}</td>
          <td>{{ s.estado }}</td>
        </tr>
      </tbody>
    </table>
  </section>
</template>

<script setup>
import { ref, onMounted } from "vue";
import api from "../services/api";

const reporte = ref(null);
const vencimientos = ref([]);

onMounted(async () => {
  const [r1, r2] = await Promise.all([
    api.get("/reportes"),
    api.get("/reportes/vencimientos-proximos"),
  ]);
  reporte.value = r1.data;
  vencimientos.value = r2.data;
});
</script>
