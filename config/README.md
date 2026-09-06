# Estandares de Codigo, Formateo y Analisis Estatico

Este documento detalla las politicas de formateo, linteo y analisis estatico aplicadas en el proyecto para garantizar un codigo limpio, estructurado y mantenible.

---

## 1. Formateo de Codigo (Spotless)

El proyecto utiliza el plugin **Spotless** (`com.diffplug.spotless:6.25.0`) para asegurar la uniformidad en el estilo del codigo fuente Java.

### Reglas Aplicadas:
- **Importaciones Limpias (`removeUnusedImports()`)**: Remueve automaticamente cualquier importacion de clase o paquete que no este siendo utilizada en el archivo.
- **Espacios Residuales (`trimTrailingWhitespace()`)**: Elimina espacios en blanco innecesarios al final de las lineas de codigo.
- **Nueva Linea Final (`endWithNewline()`)**: Exige y garantiza que cada archivo termine con un caracter de salto de linea (`\n`).
- **Sangria y Espaciado Estandar**: Estandariza la indentacion a 4 espacios.

### Comandos de Ejecucion:
- Verificar formato:
  ```bash
  ./gradlew spotlessCheck
  ```
- Aplicar formato automaticamente:
  ```bash
  ./gradlew spotlessApply
  ```

---

## 2. Linteo y Control de Estilo (Checkstyle)

Checkstyle valida la estructura interna del codigo fuente Java para impedir malas practicas y metodos sobredimensionados.

### Ubicacion de Archivos:
- Archivo de configuracion principal: [`config/checkstyle/checkstyle.xml`](checkstyle/checkstyle.xml)
- Archivo de supresiones justificadas: [`config/checkstyle/suppressions.xml`](checkstyle/suppressions.xml)

### Reglas Obligatorias Configuradas:
1. **Prohibicion de Wildcard Imports (`AvoidStarImport`)**:
   - Queda estrictamente prohibido importar paquetes completos utilizando el caracter comodin `*` (por ejemplo `import java.util.*;`).
   - Cada clase debe ser importada de forma explicita para evitar colisiones de nombres y hacer transparentes las dependencias del archivo.
2. **Control de Numeros Magicos (`MagicNumber`)**:
   - Queda prohibido incrustar valores numericos literales directamente en el cuerpo del codigo sin una constante explicativa.
   - Valores numericos exentos de esta regla: `-1`, `0`, `1`, `2`.
   - Cualquier otro numero debe declararse como constante con nombre representativo (ej: `private static final BigDecimal DEFAULT_SALARY = ...`).
3. **Longitud Maxima por Metodo (`MethodLength`)**:
   - Cada metodo, constructor o bloque de inicializacion tiene un limite estricto de **maximo 10 lineas de codigo**.
   - No se contabilizan las lineas vacias (`countEmpty = false`).
   - Obliga a descomponer la logica en metodos pequeños, legibles y de proposito unico.
4. **Cantidad Maxima de Parametros (`ParameterNumber`)**:
   - Ningun metodo o constructor puede recibir **mas de 4 parametros**.
   - Si una operacion requiere mayor cantidad de datos, se debe modelar un objeto de transferencia (DTO / Record) o agrupar dependencias.

### Comandos de Ejecucion:
```bash
./gradlew checkstyleMain
```

---

## 3. Analisis Estatico Avanzado (PMD)

PMD analiza el arbol sintactico del codigo fuente para detectar patrones de diseño deficientes y riesgos de mantenibilidad.

### Ubicacion del Archivo:
- Archivo de reglas: [`config/pmd/pmd.xml`](pmd/pmd.xml)

### Reglas Aplicadas:
1. **`category/java/codestyle.xml/WildcardImports`**:
   - Detecta y alerta sobre cualquier intento de importacion masiva con comodines.
2. **`category/java/errorprone.xml/AvoidLiteralsInIfCondition`**:
   - Detecta e impide el uso de valores literales dentro de expresiones condicionales `if (...)`.
   - Obliga al uso de constantes o metodos semanticos (como `.isEmpty()`).
3. **`category/java/design.xml/ExcessiveMethodLength`**:
   - Fija el umbral maximo en 10 lineas por metodo (`minimum = 11.0`), alertando sobre metodos que superen dicho limite.
4. **`category/java/design.xml/ExcessiveParameterList`**:
   - Fija el umbral maximo en 4 parametros por metodo (`minimum = 5.0`), alertando sobre metodos con 5 o mas argumentos.

---

## 4. Control de Cobertura de Pruebas (JaCoCo)

JaCoCo monitorea la ejecucion de la suite de pruebas unitarias y de integracion para certificar que el codigo esta debidamente protegido.

### Reglas y Metricas Exigidas:
- **Umbral Minimo Obligatorio**: **80% de cobertura** sobre lineas/instrucciones del proyecto (`jacocoTestCoverageVerification`).
- Si la cobertura total desciende del 80%, la compilacion falla inmediatamente impidiendo confirmar el build.
- Cobertura actual alcanzada por la suite de pruebas: **superior al 92% de instrucciones y 87% de ramas**.

### Comandos de Ejecucion:
```bash
./gradlew jacocoTestReport jacocoTestCoverageVerification
```
El reporte detallado en formato HTML se genera en:
`build/reports/jacoco/test/html/index.html`
