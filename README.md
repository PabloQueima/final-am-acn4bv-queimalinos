Plataforma de Entrenamiento — Aplicaciones Móviles
Android Studio (Java)


Alumno: Pablo Queimaliños — ACN4BV
Materia: Aplicaciones Móviles – Escuela Da Vinci
Final – segundo cuatrimestre 2025
Docente: Sergio Medina — sergiod.medina@davinci.edu.ar
03/03/2026

---

# Descripción general

Este proyecto corresponde a la entrega final de la materia Aplicaciones Móviles.

La aplicación está desarrollada en Android Studio con Java y forma parte de una plataforma de entrenamiento físico que integra autenticación real con Firebase, gestión de usuarios por roles y persistencia de datos en Firestore.

En esta instancia final se incorporó separación por roles (admin, entrenador y cliente), CRUD completo de sesiones y ejercicios, incluyendo imagenes desde Url, y registro de entrenamientos realizados.

---

# Objetivos de esta entrega

* Implementar Firebase Auth (login y registro).
* Validar sesión activa mediante Splash.
* Gestionar usuarios con roles almacenados en Firestore.
* Implementar múltiples pantallas con navegación según rol.
* CRUD completo de sesiones y ejercicios.
* Persistencia de sesiones completadas en colección independiente.
* Uso correcto de layouts, recursos y navegación con Intent + extras.
* Inclusión de imagenes desde Url para los ejercicios.

---

# Objetivos cumplidos

## Funcionales

Login real con Firebase.
Registro con asignación automática de rol "cliente".
Redirección automática según rol (admin / entrenador / cliente).
Gestión de usuarios desde tablero Admin (visualización, edición y búsqueda dinámica).
CRUD completo de ejercicios desde tablero Admin.
CRUD completo de sesiones desde tablero Entrenador.
Asignación de ejercicios a sesiones desde tablero Entrenador.
Realización de sesiones por parte del cliente.
Registro persistente de sesiones completadas.
Buscadores dinámicos en tiempo real.
Confirmaciones antes de operaciones críticas.
Validaciones en formularios.

---

## Técnicos

Uso de ConstraintLayout y LinearLayout.
Uso de RecyclerView con adapters personalizados.
Componentes: Button, TextView, EditText, ImageView.
Eventos y listeners en todas las interacciones.
Navegación entre activities con Intent y extras.
Integración con Firebase Auth y Firestore (múltiples colecciones).
Carga de imágenes desde URL mediante Glide.
Organización de recursos en strings.xml, colors.xml y dimens.xml.
Separación en paquetes: activities, adapters y models.

---

# Estructura del proyecto

app/
├── activities/ (Splash, Login, Register, Admin, Entrenador, Cliente, Gestión de sesiones y ejercicios)
├── adapters/ (Sesiones, Ejercicios disponibles y por sesión)
├── models/ (Usuario, Sesion, Ejercicio, SesionCompletada)
└── res/ (layout, drawable, values)

---

# Pantallas y flujo de uso

SplashActivity
Verifica sesión activa y redirige según rol o a Login/Register si no hay usuario logeado.

LoginActivity
Autenticación con Firebase y redirección según rol.

RegisterActivity
Registro de usuario y creación automática en Firestore con rol cliente y redirección a tablero cliente.

AdminActivity
Acceso a gestión de usuarios y ejercicios.

AdminUsuariosActivity
Listado dinámico, edición y búsqueda en tiempo real.

AdminEjerciciosActivity
CRUD completo con imágenes cargadas desde URL.

EntrenadorActivity
CRUD completo de sesiones y asignación a clientes.

EditarSesionActivity
Creación y edición de sesiones con asignación de ejercicios (series y repeticiones).

ClienteActivity
Visualización de sesiones asignadas por entrenador.

RealizarSesionActivity
Renderizado dinámico de ejercicios, timer de tiempo transcurrido en la sesión y registro de sesión completada.

DetalleEjercicioActivity
Visualización de información e imagen del ejercicio.

---

# Comportamiento dinámico implementado

Renderizado dinámico con RecyclerView.
Filtrado automático mientras se escribe en buscadores.
Confirmaciones antes de operaciones sensibles.
Validaciones de campos obligatorios.
Persistencia en Firestore de todas las operaciones.
Registro independiente de sesiones completadas.
Redirección automática según rol del usuario.

---

# Firebase Implementado

Firebase Auth:
Login, registro, validación de sesión activa y logout.

Firebase Firestore:
Colecciones utilizadas:
- usuarios
- ejercicios
- sesiones
- sesionesCompletadas

Operaciones: create, read, update y delete según entidad.

---

# Diseño y recursos

Textos centralizados en strings.xml.
Colores definidos en colors.xml.
Dimensiones en dimens.xml.
Temas definidos en themes.xml.

---

# Consideraciones finales

La aplicación cumple todos los requisitos mínimos obligatorios e incorpora funcionalidades adicionales como separación por roles, CRUD completo y persistencia avanzada en Firestore.

La arquitectura es modular, organizada y permite para integración directa con la versión web del sistema creada para la materia Plataformas de Desarrollo.

