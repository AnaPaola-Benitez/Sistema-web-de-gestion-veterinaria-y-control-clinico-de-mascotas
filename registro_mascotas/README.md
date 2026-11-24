# Pruebas unitarias de la Entrega 2

Las tres funcionalidades se prueban con JUnit 5 y Mockito mediante nueve métodos automatizados. Cada clase contiene un caso normal, uno límite y uno de error:

| Clase en `src/test/java/arqui/registro_mascotas/Servicio` | Requisitos | Normal | Límite | Error |
| --- | --- | --- | --- | --- |
| `ServicioMascotaRegistroTest` | RF-01, RF-06 | Registro válido con propietario existente | Edad cero | Edad negativa |
| `ServicioMascotaActualizacionTest` | RF-03, RF-06 | Actualización válida que conserva el ID | Edad cero | Mascota inexistente |
| `ServicioMascotaEliminacionTest` | RF-04 | Eliminación válida que conserva al propietario | Último registro existente | Registro inexistente |

`@Mock` sustituye los repositorios y `@InjectMocks` construye el servicio real con ellos. `when(...).thenReturn(...)` define las respuestas del repositorio para cada escenario. Las aserciones comprueban los resultados o excepciones y `verify` comprueba que se guarda o elimina únicamente cuando corresponde. No se inicia Spring Boot ni se necesitan MySQL, XAMPP, H2 o datos guardados previamente.

Cada método recibe entidades y mocks nuevos, sin depender de otra prueba ni de su orden. El caso del último registro utiliza un mapa local con una sola mascota como estado del repositorio simulado: tras ejecutar el servicio, comprueba que se solicitó el borrado y el mapa quedó vacío. No ejecuta consultas reales ni presupone que el servicio consulte `count()`.

Estas pruebas comprueban la lógica del servicio con persistencia simulada. El borrado real de filas, las claves foráneas y las cascadas JPA corresponden a las pruebas de integración con una base de datos.

## Preparación del equipo

1. Instala un **JDK 21**. Configura `JAVA_HOME` para que apunte a la carpeta del JDK instalado, sin incluir `bin`, y agrega su subcarpeta `bin` al `PATH`. La ruta depende de cada equipo.
2. Descarga o clona el repositorio completo, incluida la carpeta oculta `.mvn`. Abre una terminal dentro de `registro_mascotas`, donde se encuentran `pom.xml`, `mvnw` y `mvnw.cmd`.
3. Ten conexión a internet durante la primera ejecución para descargar Maven y las dependencias. El proyecto incluye Maven Wrapper, por lo que no necesitas instalar Maven por separado.
4. Comprueba la versión de Java que utiliza Maven con el comando correspondiente a tu sistema. La salida debe indicar **Java version: 21...**.

**Windows: CMD o PowerShell**

```powershell
.\mvnw.cmd -version
```

**Linux o macOS**

```sh
sh ./mvnw -version
```

## Ejecutar las nueve pruebas unitarias

Desde la misma carpeta, ejecuta:

**Windows: CMD o PowerShell**

```powershell
.\mvnw.cmd clean test
```

**Linux o macOS**

```sh
sh ./mvnw clean test
```

El comando limpia la compilación anterior, recompila el proyecto y ejecuta las nueve pruebas. El resultado esperado es:

```text
Tests run: 9, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Los casos de error pasan cuando el servicio rechaza la operación mediante la excepción esperada. No es necesario modificar el código fuente ni iniciar la aplicación para ejecutar las pruebas.

## Ejecutar una clase de pruebas específica

Cada comando ejecuta los tres casos de la clase indicada. Ejecuta únicamente el que corresponda a la funcionalidad que quieras comprobar.

**Windows: CMD o PowerShell**

```powershell
.\mvnw.cmd "-Dtest=ServicioMascotaRegistroTest" test
.\mvnw.cmd "-Dtest=ServicioMascotaActualizacionTest" test
.\mvnw.cmd "-Dtest=ServicioMascotaEliminacionTest" test
```

**Linux o macOS**

```sh
sh ./mvnw "-Dtest=ServicioMascotaRegistroTest" test
sh ./mvnw "-Dtest=ServicioMascotaActualizacionTest" test
sh ./mvnw "-Dtest=ServicioMascotaEliminacionTest" test
```

El resultado esperado para cada clase es `Tests run: 3`, sin fallos ni errores, y `BUILD SUCCESS`.

## Ejecutar un método de prueba específico

Usa el formato `"-Dtest=NombreDeLaClase#nombreDelMetodo"`. Por ejemplo, para comprobar únicamente el rechazo de una edad negativa al registrar una mascota:

**Windows: CMD o PowerShell**

```powershell
.\mvnw.cmd "-Dtest=ServicioMascotaRegistroTest#edadNegativa_lanzaExcepcionSinGuardarMascota" test
```

**Linux o macOS**

```sh
sh ./mvnw "-Dtest=ServicioMascotaRegistroTest#edadNegativa_lanzaExcepcionSinGuardarMascota" test
```

El resultado esperado es `Tests run: 1`, sin fallos ni errores, y `BUILD SUCCESS`. Para ejecutar otro caso, sustituye la clase y el método por sus nombres exactos en el código.

Los informes de ejecución se generan en `target/surefire-reports`.
