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
