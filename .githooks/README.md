# Git Hooks del Proyecto

Este directorio contiene los scripts de control de calidad local automatizado que se ejecutan antes de confirmar cambios o enviarlos al repositorio remoto.

---

## 1. Descripcion de los Hooks

### `pre-commit`
Se dispara automaticamente cada vez que un desarrollador ejecuta `git commit`.

**Funcionalidad:**
1. **Auto-formateo**: Ejecuta `./gradlew spotlessApply` para formatear automaticamente todo el codigo fuente Java (elimina espacios residuales, remueve imports en desuso y estandariza sangrias).
2. **Actualizacion del indice**: Ejecuta `git update-index --again` para asegurar que los cambios de formateo generados queden inmediatamente incorporados en el commit actual sin requerir un `git add` manual.
3. **Chequeo de Estilo y Linteo**: Ejecuta `./gradlew checkstyleMain spotlessCheck`.
4. **Bloqueo ante fallas**: Si alguna clase infringe las reglas de Checkstyle (por ejemplo metodos de mas de 10 lineas, mas de 4 parametros o imports con `*`), el commit se aborta de inmediato impidiendo que codigo no conforme sea registrado en el historial de Git.

### `pre-push`
Se dispara automaticamente cada vez que un desarrollador ejecuta `git push`.

**Funcionalidad:**
1. **Ejecucion de Pruebas**: Corre la suite completa de tests unitarios y de integracion con `./gradlew test`.
2. **Control de Cobertura JaCoCo**: Genera los reportes de metricas y ejecuta `./gradlew jacocoTestCoverageVerification`.
3. **Bloqueo ante fallas**: Si algun test falla o la cobertura global cae por debajo del 80% exigido, el comando `git push` se cancela impidiendo que codigo roto o sin cobertura llegue a GitHub.

---

## 2. Instalacion y Uso

### Instalacion Automatica (Recomendada)
Para habilitar los hooks en tu entorno local, ejecuta la siguiente tarea de Gradle incluida en el proyecto:

```bash
./gradlew installGitHooks
```

Esta tarea:
- Copia los scripts `pre-commit` y `pre-push` dentro del directorio `.git/hooks/` de tu repositorio local.
- Asigna permisos de ejecucion (`chmod +x`) a los archivos instalados.

### Instalacion Manual Alternativa
Si prefieres configurar Git para que consuma directamente los hooks de esta carpeta sin copiarlos:

```bash
git config core.hooksPath .githooks
chmod +x .githooks/pre-commit .githooks/pre-push
```

---

## 3. Como Trabajar con los Hooks

Una vez instalados, el funcionamiento es 100% transparente:
- Cuando ejecutes `git commit -m "feat: mi cambio"`, veras en la terminal los mensajes informativos del `pre-commit` formateando y chequeando las reglas de Checkstyle.
- Cuando ejecutes `git push origin <rama>`, veras la salida del `pre-push` ejecutando los tests y validando el 80% de JaCoCo.
- En caso de error, el script detendra el proceso e imprimira el reporte del motivo exacto para corregirlo antes de continuar.
