# Workflows de GitHub Actions

Este directorio contiene la configuracion de los flujos de trabajo de Integracion Continua (CI) y validaciones automatizadas del repositorio.

---

## 1. CI Pipeline (`ci.yml`)

Es el flujo principal de integracion continua responsable de asegurar la calidad del codigo, la consistencia de estilos y la correcta ejecucion de las pruebas antes de integrar cambios al repositorio.

### Eventos de Disparo (Triggers)
- **Push**: En las ramas `main`, `master`, `develop` y cualquier rama con prefijo `feature/**`.
- **Pull Request**: Hacia las ramas `main` y `master`.
- **Manual**: Mediante el evento `workflow_dispatch` desde la interfaz de GitHub Actions.

### Pasos Ejecutados
1. **Checkout del Codigo Fuente**: Descarga el repositorio mediante `actions/checkout@v4`.
2. **Configuracion de Java 21**: Configura el JDK Temurin 21 con soporte de cache para Gradle mediante `actions/setup-java@v4`.
3. **Permisos de Ejecucion**: Otorga permisos de ejecucion al wrapper `./gradlew`.
4. **Verificacion de Formateo (Spotless)**: Ejecuta `./gradlew spotlessCheck --no-daemon` para validar que el codigo cumple el formato estandarizado.
5. **Analisis Estatico (Checkstyle)**: Ejecuta `./gradlew checkstyleMain --no-daemon` aplicando las reglas de estilo del proyecto (maximo 10 lineas por metodo, maximo 4 parametros, sin imports con comodin y sin numeros magicos).
6. **Ejecucion de Pruebas**: Corre la totalidad de los tests unitarios y de integracion mediante `./gradlew test --no-daemon`.
7. **Verificacion de Cobertura (JaCoCo)**: Genera los reportes de cobertura y valida que se cumpla el umbral minimo estricto del 80% con `./gradlew jacocoTestReport jacocoTestCoverageVerification --no-daemon`.
8. **Compilacion de Artefactos**: Construye los binarios ejecutables de la aplicacion con `./gradlew assemble --no-daemon`.
9. **Publicacion de Reportes (Artifacts)**:
   - Reporte de Pruebas JUnit (`test-report`).
   - Reporte de Cobertura JaCoCo en HTML (`jacoco-report`).
   - Reporte de Estilo Checkstyle (`checkstyle-report`).
   - Retencion de 14 dias para auditoria.

---

## 2. PR Title Check (`pr-title.yml`)

Este flujo implementa una politica de gobernanza sobre los Pull Requests, asegurando que todos los aportes al proyecto respeten la convencion de nomenclatura de commits y PRs.

### Eventos de Disparo (Triggers)
- Eventos de Pull Request: `opened`, `edited`, `synchronize` y `reopened`.

### Funcionamiento y Reglas
- Extrae el titulo del Pull Request a traves del contexto `${{ github.event.pull_request.title }}`.
- Evalua mediante expresion regular que el titulo comience obligatoriamente con el prefijo `feat` (insensible a mayusculas/minusculas):
  - Formatos admitidos:
    - `feat: descripcion del cambio`
    - `feat(modulo): descripcion detallada`
    - `feat/nombre-funcionalidad`
- Si el titulo no coincide con el patron, el job finaliza inmediatamente con codigo de error `1` y emite una anotacion de error visible en la revision del Pull Request, bloqueando la aprobacion hasta que el autor ajuste el titulo.
