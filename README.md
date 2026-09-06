# Sistema de Gestion de Ventas - Besysoft Challenge

Solucion desarrollada en Java 21 para el challenge tecnico de Besysoft. Corresponde a una aplicacion de consola orientada a objetos para la administracion de una tienda comercial: gestion de catalogo de productos, registro de vendedores, carrito de compras, realizacion de ventas y calculo de comisiones segun politicas comerciales.

---

## 1. Arquitectura del Sistema

La aplicacion implementa una arquitectura en capas desacoplada que respeta el principio de responsabilidad unica (SRP) y las buenas practicas del diseño orientado a objetos:

- **Capa de Modelo (`com.besysoft.model`)**:
  Contiene las entidades del dominio de negocio, records y enumeraciones. Representa los conceptos principales del negocio de forma aislada, sin dependencias de frameworks ni de mecanismos de almacenamiento o presentacion.

- **Capa de Repositorio (`com.besysoft.repository`)**:
  Abstrae las operaciones de persistencia a traves de interfaces de contrato (`Repository<T, ID>`, `ProductRepository`, `SellerRepository`, `SaleRepository`) e implementaciones en memoria (`InMemoryProductRepository`, `InMemorySellerRepository`, `InMemorySaleRepository`) mediante colecciones `HashMap`, garantizando el principio de inversion de dependencias (DIP).

- **Capa de Servicio (`com.besysoft.service`)**:
  Aloja la logica de negocio, validaciones de entrada, coordinacion entre repositorios y aplicacion de politicas de calculo. Los servicios devuelven objetos de tipo `Result<T>` para informar resultados exitosos o errores sin recurrir a excepciones no controladas.

- **Capa de Consola y CLI (`com.besysoft.console`)**:
  Punto de entrada a la aplicacion y capa de presentacion basada en PicoCLI. Consume exclusivamente los servicios de negocio sin duplicar logica ni acceder directamente a los repositorios. Provee soporte tanto para ejecucion por comandos individuales como para sesion interactiva (REPL).

- **Capa de Manejo de Errores y Resultados (`com.besysoft.common` / `com.besysoft.shared`)**:
  Estructuras de resultado funcional (`Result<T>`, `CorrectResult<T>`, `IncorrectResult<T>`) y manejador centralizado `GlobalErrorHandler` para transformar excepciones de dominio en mensajes claros para el usuario.

---

## 2. Clases del Modelo Utilizadas

Las entidades y estructuras de datos del dominio son:

- **`Product`**:
  Representa un producto comercial. Atributos: `productId` (UUID), `code` (String), `name` (String), `price` (BigDecimal) y `category` (Category). Valida que codigo y nombre no esten vacios, y que el precio no sea negativo ni nulo.

- **`Category`**:
  Enumeracion que define los rubros validos: `TECHNOLOGY`, `APPLIANCES`, `CLOTHING`, `HOME`, `FOOD`, `CLEANING` y `OTHER`. Incluye metodos para comparar cadenas de texto de forma insensible a mayusculas y espacios.

- **`Person`**:
  Clase base abstracta que encapsula los atributos comunes de una persona: `id` (UUID), `code` (String) y `name` (String).

- **`Seller`**:
  Especializacion de `Person` que representa a un vendedor. Agrega el atributo `salary` (BigDecimal), validando que el sueldo base no sea nulo ni negativo.

- **`CartItem`**:
  Representa un item individual dentro del carrito de compras. Contiene la referencia al producto (`Product`) y la cantidad de unidades seleccionadas (`quantity`).

- **`ShoppingCart`**:
  Contenedor en memoria que agrupa los items seleccionados por el cliente durante una sesion de compra. Permite agregar items, listar el detalle y vaciar el carrito.

- **`Sale`**:
  Representa una transaccion de venta consolidada. Atributos: `saleId` (UUID), `seller` (Seller), `saleDate` (LocalDateTime), `saleDetails` (List<SaleDetail>), `totalAmount` (BigDecimal) y `commissionAmount` (BigDecimal).

- **`SaleDetail`**:
  Linea de detalle de una venta. Registra el producto vendido, la cantidad, el precio unitario y el subtotal calculado.

---

## 3. Patrones de Diseño Aplicados

- **Strategy Pattern (Estrategia)**:
  Utilizado en el calculo de comisiones (`CommissionRule`, `UpToTwoProductsRule`, `MoreThanTwoProductsRule`, `NoCommissionRule`). La clase `CommissionCalculator` evalua las reglas configuradas para determinar el porcentaje aplicable (5% para hasta 2 productos, 10% para mas de 2 productos) sin acoplar la logica de calculo al proceso de checkout.

- **Result Pattern**:
  Sustituye el uso de excepciones para el control de flujo normal por los tipos `CorrectResult<T>` e `IncorrectResult<T>`. Permite a los servicios retornar de manera explicita el estado de una operacion y su mensaje de error si corresponde, evitando excepciones no controladas.

- **Repository Pattern**:
  Separa la logica de acceso a datos del resto del sistema. Permite cambiar el mecanismo de almacenamiento (en memoria a base de datos relacional) sin afectar los servicios ni la CLI.

- **Command Pattern**:
  A traves de las anotaciones `@Command` de PicoCLI, cada operacion de la terminal (`ProductCreateCommand`, `SellerListCommand`, `SaleCheckoutCommand`, etc.) se modela como un comando independiente, encapsulado y reutilizable.

- **Dependency Injection / Factory**:
  PicoCLI utiliza una implementacion personalizada de `CommandLine.IFactory` para instanciar los comandos proveyendoles los servicios necesarios, evitando constructores vacios o dependencias estaticas globales.

---

## 4. Ejecucion del CLI y Comandos Disponibles

El proyecto incluye el script ejecutable `./tienda` que detecta automaticamente el modo de operacion segun los argumentos provistos.

### Modo Interactivo (REPL)
Recomendado para realizar un circuito de compra completo manteniendo el estado en memoria durante toda la sesion:

```bash
./tienda
```

Muestra el prompt interactivo:
```text
==================================================
     Bienvenido al Sistema de Gestion de Tienda   
==================================================
Escriba 'tienda --help' para ver los comandos disponibles.
Escriba 'exit' o 'quit' para salir.

tienda>
```

### Modo de Comandos Directos (Batch)
Permite ejecutar un comando puntual y retornar a la terminal del sistema operativo:

```bash
./tienda product list
./tienda seller list
./tienda product find-by-category --category=TECHNOLOGY
```

### Tabla Completa de Comandos

| Grupo | Comando | Parametros | Descripcion |
|---|---|---|---|
| **product** | `product create` | `--code`, `--name`, `--price`, `--category` | Crea y almacena un nuevo producto |
| | `product list` | Ninguno | Muestra todos los productos en memoria |
| | `product find-by-id` | `--id` | Busca un producto por su UUID |
| | `product find-by-code` | `--code` | Busca un producto por su codigo unico |
| | `product find-by-category` | `--category` | Lista productos filtrando por categoria |
| | `product delete` | `--id` | Elimina un producto por su UUID |
| **seller** | `seller create` | `--code`, `--name`, `--salary` | Registra un nuevo vendedor |
| | `seller list` | Ninguno | Muestra todos los vendedores registrados |
| | `seller find-by-id` | `--id` | Busca un vendedor por su UUID |
| | `seller find-by-code` | `--code` | Busca un vendedor por su codigo |
| | `seller delete` | `--id` | Elimina un vendedor por su UUID |
| **cart** | `cart add` | `--code` (o `-c`) / `--product-id`, `-q` (cantidad) | Agrega productos al carrito por codigo o ID |
| | `cart list` | Ninguno | Muestra los productos y totales del carrito |
| | `cart clear` | Ninguno | Vacia todos los productos del carrito |
| **sale** | `sale checkout` | `--code` (o `-s`) / `--seller-id` | Finaliza la compra y asocia la venta al vendedor |
| | `sale list` | Ninguno | Muestra todas las ventas realizadas |
| | `sale find-by-id` | `--id` | Muestra el comprobante detallado de una venta |
| **commission**| `commission calculate` | `--code` (o `-s`) / `--seller-id` | Calcula la comision acumulada del vendedor |
| **seed** | `seed` | Ninguno | Recarga los datos iniciales de prueba en memoria |
| **ayuda** | `--help` o `-h` | Ninguno | Despliega el manual de ayuda de cualquier comando |
| **salida** | `exit` o `quit` | Ninguno | Finaliza la sesion de consola interactiva |

---

## 5. Contenido del Seeder (`DataSeeder`)

Para facilitar las pruebas de la aplicacion sin requerir la carga manual previa en cada inicio, la clase `DataSeeder` puebla automaticamente la memoria al arrancar la CLI o la consola interactiva. Ademas, su operacion es idempotente: verifica que las colecciones esten vacias antes de realizar la insercion.

### Productos Precargados
- **PROD-001**: Notebook Lenovo ($1.200.000,00 - Categoria: TECHNOLOGY)
- **PROD-002**: Mouse Logitech ($25.000,00 - Categoria: TECHNOLOGY)
- **PROD-003**: Teclado Redragon ($45.000,00 - Categoria: TECHNOLOGY)
- **PROD-004**: Cafetera Philips ($95.000,00 - Categoria: APPLIANCES)
- **PROD-005**: Remera Algodon ($15.000,00 - Categoria: CLOTHING)
- **PROD-006**: Cafe Colombiano ($18.000,00 - Categoria: FOOD)

### Vendedores Precargados
- **VEN-001**: Carlos Gomez (Sueldo base: $450.000,00)
- **VEN-002**: Maria Lopez (Sueldo base: $520.000,00)
- **VEN-003**: Lucas Rodriguez (Sueldo base: $480.000,00)

Tambien es posible reejecutar el seed en cualquier momento ingresando el comando `seed` dentro de la CLI.

---

## 6. Calidad, Testing y Automatizacion

- **Pruebas Automatizadas**: Bateria de pruebas unitarias y de integracion desarrolladas con JUnit 5 que cubren servicios, validaciones, calculo de comisiones, CLI y comandos.
- **Cobertura con JaCoCo**: Cobertura superior al 92% de instrucciones y 87% de ramas, cumpliendo el umbral minimo exigido del 80% (`jacocoTestCoverageVerification`).
- **Analisis Estatico**:
  - **Checkstyle**: Maximo 10 lineas por metodo, maximo 4 parametros por metodo, prohibicion de importaciones con comodin (`*`), y control de numeros magicos.
  - **PMD**: Reglas de diseño y deteccion de literales en sentencias condicionales.
  - **Spotless**: Formateador automatico de codigo fuente Java.
- **Git Hooks Locales**:
  - `pre-commit`: Ejecuta formateo con Spotless y verificacion de Checkstyle antes de confirmar cambios.
  - `pre-push`: Ejecuta la suite de pruebas y verifica el umbral de cobertura JaCoCo antes de enviar al repositorio remoto.
- **GitHub Actions**:
  - `CI Pipeline` (`.github/workflows/ci.yml`): Construccion, validacion de estilo, pruebas y generacion de reportes en cada push y PR.
  - `PR Title Check` (`.github/workflows/pr-title.yml`): Valida que los titulos de Pull Request comiencen con el prefijo `feat` (ejemplo: `feat: ...`, `feat(scope): ...`, `feat/...`).

Para documentacion detallada:
- Estandares de Linteo, Formateo y Analisis Estatico: [config/README.md](config/README.md)
- Guia y Funcionamiento de Git Hooks: [.githooks/README.md](.githooks/README.md)
- Workflows y Automatizaciones de CI: [.github/workflows/README.md](.github/workflows/README.md)

Para activar los Git Hooks locales:
```bash
./gradlew installGitHooks
```

Para ejecutar todas las validaciones de calidad localmente:
```bash
./gradlew check
```
