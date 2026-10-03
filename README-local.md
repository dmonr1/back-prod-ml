# Backend local con Flyway

Requisitos: PostgreSQL en `localhost:5432` (incluido `psql.exe`) y JDK 21 configurado en `JAVA_HOME`.

Desde la carpeta del backend, ejecuta en PowerShell:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\start-local.ps1
```

El script solicita la contrasena del usuario `postgres`, crea `db_rendimiento_local` si no existe y arranca el backend con el perfil `local`. Flyway aplica las migraciones `V1` a `V18` al arrancar; en los siguientes arranques solo aplica migraciones nuevas. Para usar otro nombre de base vacia, agrega `-DatabaseName otro_nombre`.

La migracion inicial crea datos de demostracion, pero **no recupera los datos reales de otra computadora**. Para trasladarlos, exporta la base original con `pg_dump` y restaurala por separado; no ejecutes V1 sobre una base que ya tenga datos. El perfil `dev` existente sigue desactivando Flyway para no tocar `db_rendimiento_2`.
