<template>
  <div class="app">
    <header class="app__header">
      <h1>Gimnasio - Panel Administrativo</h1>
      <nav class="app__nav">
        <router-link to="/clientes">Clientes</router-link>
        <router-link to="/entrenadores">Entrenadores</router-link>
        <router-link to="/suscripciones">Suscripciones</router-link>
        <router-link to="/acceso">Acceso QR</router-link>
        <router-link to="/sesiones">Sesiones</router-link>
        <router-link to="/reportes">Reportes</router-link>
      </nav>
    </header>
    <main class="app__content">
      <router-view />
    </main>
  </div>
</template>

<script setup>
</script>

<style>
* { box-sizing: border-box; }
body { margin: 0; font-family: Arial, Helvetica, sans-serif; background: #f4f6f8; color: #222; }
.app__header { background: #1f4e5f; color: white; padding: 1rem 1.5rem; }
.app__header h1 { margin: 0 0 .5rem 0; font-size: 1.3rem; }
.app__nav a { color: #dfeef2; margin-right: 1rem; text-decoration: none; font-size: .9rem; }
.app__nav a.router-link-active { font-weight: bold; text-decoration: underline; }
.app__content { padding: 1.5rem; max-width: 960px; margin: 0 auto; }
table { width: 100%; border-collapse: collapse; background: white; margin-top: 1rem; }
th, td { border: 1px solid #ddd; padding: .5rem; font-size: .9rem; text-align: left; }
th { background: #1f4e5f; color: white; }
form { background: white; padding: 1rem; border-radius: 6px; margin-bottom: 1rem; }
label { display: block; margin-bottom: .5rem; font-size: .85rem; }
input, select { width: 100%; padding: .4rem; margin-top: .2rem; }
button { background: #1f4e5f; color: white; border: none; padding: .5rem 1rem; border-radius: 4px; cursor: pointer; }
.msg-ok { color: #1a7a3c; }
.msg-error { color: #b3261e; }
</style>
