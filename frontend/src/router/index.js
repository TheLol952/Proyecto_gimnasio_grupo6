import { createRouter, createWebHistory } from "vue-router";
import ClientesView from "../views/ClientesView.vue";
import SuscripcionesView from "../views/SuscripcionesView.vue";
import AccesoView from "../views/AccesoView.vue";
import SesionesView from "../views/SesionesView.vue";
import ReportesView from "../views/ReportesView.vue";

const routes = [
  { path: "/", redirect: "/clientes" },
  { path: "/clientes", component: ClientesView },
  { path: "/suscripciones", component: SuscripcionesView },
  { path: "/acceso", component: AccesoView },
  { path: "/sesiones", component: SesionesView },
  { path: "/reportes", component: ReportesView },
];

export default createRouter({
  history: createWebHistory(),
  routes,
});
