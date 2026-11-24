# Información del Proyecto

#### El proyecto fue creado utilizando *Spring Boot Initializr* con las siguientes especificaciones:

- **Proyecto**: Maven
- **Lenguaje**: Java
- **Empaquetado**: Jar
- **Spring Boot Versión**: 3.4.0
- **Java Versión**: 21

#### Dependencias

- Spring Web
- Spring Boot Dev Tools
- MySQL Driver
- Spring Data JPA
- Thymeleaf

---

# Pasos para Ejecutar el Proyecto

1. **Inicia el servidor de Apache y MySQL en Xampp**
    1.1. Si es la primera vez que se ejecuta el proyecto, se deberá crear una base de datos llamada **mascotas** desde **phpMyAdmin**.
    
2. **Ejecutar `App.java`**

## Pruebas unitarias de la Entrega 2

Las tres funcionalidades se prueban con JUnit 5 y Mockito mediante nueve métodos
automatizados. Cada clase contiene un caso normal, uno límite y uno de error:

| Clase en `src/test/java/arqui/registro_mascotas/Servicio` | Requisitos | Normal | Límite | Error |
| --- | --- | --- | --- | --- |
| `ServicioMascotaRegistroTest` | RF-01, RF-06 | Registro válido con propietario existente | Edad cero | Edad negativa |
| `ServicioMascotaActualizacionTest` | RF-03, RF-06 | Actualización válida que conserva el ID | Edad cero | Mascota inexistente |
| `ServicioMascotaEliminacionTest` | RF-04 | Eliminación válida que conserva al propietario | Último registro existente | Registro inexistente |

`@Mock` sustituye los repositorios y `@InjectMocks` construye el servicio real con
ellos. `when(...).thenReturn(...)` define las respuestas del repositorio para cada
escenario. Las aserciones comprueban los resultados o excepciones y `verify`
comprueba que se guarda o elimina únicamente cuando corresponde. No se inicia
Spring Boot ni se necesitan MySQL, XAMPP, H2 o datos guardados previamente.

Cada método recibe entidades y mocks nuevos, sin depender de otra prueba ni de
su orden. El caso del último registro utiliza un mapa local con una sola mascota
como estado del repositorio simulado: tras ejecutar el servicio, comprueba que
se solicitó el borrado y el mapa quedó vacío. No ejecuta consultas reales ni
presupone que el servicio consulte `count()`.

Estas pruebas comprueban la lógica del servicio con persistencia simulada. El
borrado real de filas, las claves foráneas y las cascadas JPA corresponden a las
pruebas de integración con una base de datos.

### Ejecución durante la revisión

Abre la carpeta que contiene `pom.xml` y usa los comandos de tu consola. CMD
muestra un indicador como `C:\Users\astri>`; PowerShell suele mostrar `PS C:\...>`.

Desde **Símbolo del sistema (CMD)**:

```bat
cd /d "C:\Users\astri\Downloads\registro_mascotas (2)\registro_mascotas"
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.5.11-hotspot"
set "PATH=%JAVA_HOME%\bin;%PATH%"

mvnw.cmd -version
mvnw.cmd clean test
```

Desde **PowerShell**:

```powershell
cd "C:\Users\astri\Downloads\registro_mascotas (2)\registro_mascotas"

# Selecciona Java 21 en esta terminal. Ajusta la ruta en otro equipo.
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.5.11-hotspot"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"

.\mvnw.cmd -version
.\mvnw.cmd clean test
```

El comando `mvnw.cmd -version` debe mostrar **Java version: 21.0.5** en este equipo.
Las asignaciones de Java afectan solo a la ventana actual; repítelas en una
ventana nueva. Los comandos `$env:...` son exclusivos de PowerShell y no sirven
en CMD. En otro equipo, ajusta la ruta al JDK 21 instalado.

El comando `clean test` recompila el proyecto y ejecuta las nueve pruebas. El
resultado esperado es:

```text
Tests run: 9, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

No necesitas ejecutar `App.java`, iniciar MySQL, modificar `application.properties`
ni editar el servicio o las pruebas para esta ejecución. Los casos de error pasan
cuando el servicio lanza la excepción esperada y no guarda o elimina datos; no
aparecen como fallos de la suite. Los informes quedan en `target/surefire-reports`.

En otro equipo, prepara JDK 21 y ejecuta el comando antes de la revisión, con
internet disponible la primera vez para descargar Maven y las dependencias.
Después de esa preparación, puedes comprobar la ejecución sin red y variando
el orden de las clases:

```powershell
.\mvnw.cmd -o "-Dsurefire.runOrder=random" test
```

Para mostrar una funcionalidad por separado:

```powershell
.\mvnw.cmd "-Dtest=ServicioMascotaRegistroTest" test
.\mvnw.cmd "-Dtest=ServicioMascotaActualizacionTest" test
.\mvnw.cmd "-Dtest=ServicioMascotaEliminacionTest" test
```

### Prueba de integración existente

La prueba original `contextLoads` se conserva como
`RegistroMascotasApplicationIT`. Arranca el contexto completo de Spring y requiere
MySQL disponible y configurado. El sufijo `IT` la separa de las pruebas unitarias
que Maven Surefire descubre por defecto; no está deshabilitada. Para ejecutarla
explícitamente con su base de datos preparada:

```powershell
.\mvnw.cmd "-Dtest=RegistroMascotasApplicationIT" test
```

### Reglas y consideraciones del PDF de la Entrega 2

Las pruebas deben cubrir la lógica principal, incluir casos normales, límite y de
error cuando apliquen, tener nombres descriptivos, ser independientes y ejecutarse
durante la revisión sin ajustes del código fuente para hacerlas pasar. La entrega
se organiza aquí en tres clases con tres métodos cada una, según los casos
seleccionados para registro, actualización y eliminación.

El documento también pide commits pequeños y descriptivos para las pruebas,
trazabilidad con los requisitos y un UML coherente con el código. Solicita el
diagrama PDF en `Docs/DiagramaClases_Equipo.pdf`. Para la revisión presencial,
indica llevar el documento de instrucciones impreso y engrapado, con la rúbrica
al frente, y que participen todos los integrantes. Las reglas de publicación se
aplicarán cuando se autorice subir los cambios.

## Gestión de mascotas (RF-01 a RF-06)

El registro y la edición incluyen los atributos del UML: especie, raza, sexo y
peso en kilogramos. Son opcionales; el peso vacío se almacena como `0` y se muestra
como «Sin registrar». Se rechazan pesos negativos o valores no finitos. Los campos
nuevos se añaden mediante `ddl-auto=update`: los textos anteriores quedan vacíos
y el peso usa un valor predeterminado de `0`, sin reemplazar los datos existentes.

Después de registrar una mascota, el listado muestra un mensaje con su nombre e
identificador. También muestra el resultado de una actualización o eliminación.

Para eliminar una mascota, el enlace «Eliminar» abre una pantalla de confirmación.
«Cancelar» vuelve al listado sin cambios; «Confirmar eliminación» envía la
operación por POST. Abrir `/mascotas/eliminar/{id}` por GET no borra registros.
La eliminación conserva al propietario y se rechaza si una clave foránea impide
borrar la mascota, mostrando el motivo. La eliminación múltiple mantiene su
confirmación y se realiza en una transacción.

Este alcance cubre el módulo actual de mascotas y su relación con propietarios.
Las reglas específicas de citas, atenciones y vacunas se incorporarán junto con
esos módulos.

## Propietarios de las mascotas

Cada mascota debe estar vinculada a un propietario existente. Los formularios de
registro y edición permiten seleccionarlo, y el listado muestra su nombre. El
servicio comprueba la existencia del propietario antes de guardar; la base de
datos también exige la relación mediante `propietario_id` y una clave foránea.
Eliminar una mascota no elimina a su propietario.

Esta entrega incorpora la relación y la consulta necesaria para los selectores.
Hasta implementar el módulo de administración de propietarios, registra sus datos
reales en la tabla `propietarios` desde phpMyAdmin. Si no hay propietarios, los
formularios muestran un aviso y no permiten guardar mascotas.

### Base de datos nueva

Al arrancar la aplicación, `spring.jpa.hibernate.ddl-auto=update` crea las tablas y
la relación. Registra primero los propietarios para poder crear mascotas.

### Base de datos con mascotas anteriores

Antes de arrancar esta versión, respalda la base y realiza esta adaptación desde
phpMyAdmin con la aplicación detenida. Las mascotas anteriores todavía no tienen
propietario; `ddl-auto=update` no puede decidir a quién pertenecen ni resolver por
sí solo la nueva restricción obligatoria.

1. Crea la tabla de propietarios si todavía no existe:

```sql
CREATE TABLE propietarios (
    id INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(255) NOT NULL,
    telefono VARCHAR(255) NULL,
    correo VARCHAR(255) NULL,
    apellido VARCHAR(255) NULL,
    direccion VARCHAR(255) NULL,
    activo BIT NOT NULL DEFAULT b'1',
    PRIMARY KEY (id)
) ENGINE=InnoDB;
```

2. Añade la columna inicialmente opcional para conservar todos los registros:

```sql
ALTER TABLE mascotas ADD COLUMN propietario_id INT NULL;
```

3. Registra los propietarios reales y asigna a cada mascota su propietario. Para
   cada asignación, sustituye los marcadores por los identificadores reales antes
   de ejecutar:

```sql
UPDATE mascotas
SET propietario_id = <ID_DEL_PROPIETARIO_REAL>
WHERE id = <ID_DE_LA_MASCOTA>;
```

4. Comprueba que esta consulta no devuelve ninguna fila:

```sql
SELECT m.id, m.nombre, m.propietario_id
FROM mascotas m
LEFT JOIN propietarios p ON p.id = m.propietario_id
WHERE m.propietario_id IS NULL OR p.id IS NULL;
```

5. Cuando todas las asignaciones sean válidas, impón la relación obligatoria:

```sql
ALTER TABLE mascotas
    MODIFY COLUMN propietario_id INT NOT NULL,
    ADD CONSTRAINT fk_mascotas_propietario
        FOREIGN KEY (propietario_id) REFERENCES propietarios (id)
        ON DELETE RESTRICT ON UPDATE RESTRICT;
```

Ya puedes iniciar la aplicación. La columna y la clave foránea se añaden una sola
vez: si ya existen, revisa su definición antes de ejecutar los correspondientes
`ALTER TABLE`. Este procedimiento conserva las mascotas y sus identificadores;
cada asignación debe reflejar al propietario real de la mascota.
