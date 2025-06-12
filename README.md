# Autocaravanas MMC

Aplicación móvil desarrollada en Kotlin para la gestión y alquiler de autocaravanas. Permite a los usuarios registrarse, consultar la disponibilidad de vehículos, realizar reservas y gestionarlas de forma sencilla.

## Descripción general

**Autocaravanas MMC** facilita el proceso de alquiler de autocaravanas, permitiendo:
- Registro e inicio de sesión de usuarios.
- Consulta de caravanas disponibles según fechas seleccionadas.
- Creación, actualización y eliminación de reservas.
- Visualización de detalles de las caravanas y reservas.

## Estructura de carpetas y clases principales

```
app/
 └── src/
      └── main/
           ├── java/com/example/autocaravanas/
           │    ├── api/
           │    │    ├── ApiAdapter.kt
           │    │    ├── ApiHelper.kt
           │    │    └── ApiService.kt
           │    ├── model/
           │    │    ├── Caravana.kt
           │    │    ├── Reserva.kt
           │    │    ├── Usuario.kt
           │    │    └── ...
           │    ├── repository/
           │    │    └── ReservaRepository.kt
           │    ├── viewmodel/
           │    │    └── ReservaViewModel.kt
           │    ├── fragments/
           │    │    ├── AddReservaFragment.kt
           │    │    ├── EditReservaFragment.kt
           │    │    ├── HomeFragment.kt
           │    ├── adapter/
           │    │    ├── CaravanasDisponiblesAdapter.kt
           │    │    └── ReservaAdapter.kt
           │    ├── EmailVerificationActivity.kt
           │    ├── LoginActivity.kt
           │    ├── MainActivity.kt
           │    └── RegisterActivity.kt
           └── res/...
```

### Explicación de las principales clases y archivos

- **api/**
  - `ApiAdapter.kt`: Configuración y adaptación del cliente para las llamadas a la API.
  - `ApiHelper.kt`: Métodos de ayuda para gestionar y simplificar las llamadas a la API REST.
  - `ApiService.kt`: Interfaz que define los endpoints de la API.

- **model/**
  - `Caravana.kt`: Modelo de datos para las autocaravanas.
  - `Reserva.kt`: Modelo de reserva (contiene info sobre usuario, caravana, fechas, precios, etc.).
  - `Usuario.kt`: Modelo de usuario.
  - Otros modelos: Respuestas de la API como ReservaResponse, DeleteResponse, disponibilidad, etc.

- **repository/**
  - `ReservaRepository.kt`: Encapsula la lógica de acceso a datos y operaciones sobre reservas, usuarios y caravanas.

- **viewmodel/**
  - `ReservaViewModel.kt`: ViewModel principal que conecta la UI con el repositorio y gestiona el estado de la app.

- **fragments/**
  - `AddReservaFragment.kt`: Fragmento para la creación de nuevas reservas.
  - `EditReservaFragment.kt`: Fragmento para editar reservas existentes.
  - `HomeFragment.kt`: Fragmento principal donde se visualiza información general o reservas.

- **adapter/**
  - `CaravanasDisponiblesAdapter.kt`: Adaptador para mostrar la lista de caravanas disponibles.
  - `ReservaAdapter.kt`: Adaptador para mostrar la lista de reservas del usuario.

- **Actividades principales:**
  - `EmailVerificationActivity.kt`: Pantalla para la verificación del correo electrónico, aparece una vez alguien decide registrarse para notificar el envío del email.
  - `LoginActivity.kt`: Pantalla de inicio de sesión.
  - `MainActivity.kt`: Actividad principal que gestiona la navegación de la app.
  - `RegisterActivity.kt`: Pantalla de registro de nuevos usuarios.

## Funcionamiento general

1. **Registro, autenticación y login:**  
   Los usuarios pueden registrarse y verificar su email. Posteriormente, pueden iniciar sesión en la aplicación.

2. **Consulta de disponibilidad:**  
   Los usuarios seleccionan fechas para ver qué caravanas están disponibles y elegir la que deseen.

3. **Gestión de reservas:**  
   Los usuarios pueden crear, editar y eliminar sus reservas desde la app.

4. **Visualización y actualización en tiempo real:**  
   La app emplea LiveData y ViewModel para mantener los datos y la interfaz sincronizados y actualizados ante cualquier cambio.

## Tecnologías utilizadas

- **Kotlin**
- **Arquitectura MVVM**
- **Android Jetpack**: ViewModel, LiveData, Navigation, DataBinding, ViewBinding.
- **Retrofit** y **OkHttp** para la comunicación con la API REST.
- **Gson** para el parseo de JSON.
- **Coroutines** para operaciones asíncronas.
- **Glide** para la carga de imágenes.

