## Base de datos y migraciones

Usamos SQLite para almacenar los datos y Flyway para versionar y aplicar los cambios del esquema.

Las migraciones se encuentran en:

`src/main/resources/db/migration/`

### Convención de nombres

Cada migración tiene un número de versión y una descripción separados por dos guiones bajos. Por ejemplo:

- `V1__tablas_iniciales.sql`
- `V2__agregar_columna_observaciones.sql`
- `V3__agregar_indices.sql`

Las versiones deben ser únicas y seguir un orden incremental.

### Reglas para escribir migraciones

1. **No hay que tocar migraciones ya aplicadas.** Para cambiar el esquema, crear una migración nueva. Asi Flyway detecta los cambios pendientes y mantiene la historia de la base de datos consistente.
2. **No incluir `PRAGMA`, `BEGIN` ni `COMMIT` en los archivos de migración.** La configuración de SQLite, como `foreign_keys` y `journal_mode`, se administra por separado de las migraciones.
3. **Probar cada migración** sobre una base vacía y verificar que también funcione sobre una base que ya tenga aplicada la versión anterior.

### Verificación

Antes de integrar una migración:

- Ejecutar las migraciones sobre una base de datos vacía.
- Ejecutar los tests de restricciones e integridad.
- Comprobar que los datos y las relaciones se conserven cuando se modifica una tabla.
- Verificar que las migraciones anteriores sigan registradas en Flyway.
