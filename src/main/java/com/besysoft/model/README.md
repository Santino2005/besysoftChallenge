# Modelo de dominio

El modelo fue diseñado siguiendo principios de orientación a objetos, buscando que cada entidad represente un concepto concreto del negocio y tenga una responsabilidad clara.

## Entidades

### `Product`

Representa un producto disponible en la tienda.

Contiene:

* Identificador.
* Código.
* Nombre.
* Precio.
* Categoría.

Se relaciona con `CartItem` y `SaleDetail`, ya que un producto puede estar presente en distintos carritos y en distintas ventas.

### `Category`

Representa las categorías válidas de los productos.

Se modela como `enum` para restringir los valores posibles y facilitar la búsqueda de productos por categoría.

### `Person`

Clase base para representar datos comunes entre personas del sistema.

Actualmente contiene código y nombre, que son reutilizados por `Seller`.

### `Seller`

Representa al vendedor que realiza las ventas.

Hereda de `Person` y agrega el sueldo.

Cada `Sale` está asociada a un `Seller`, permitiendo identificar qué vendedor realizó cada venta.

### `ShoppingCart`

Representa el carrito de compra actual.

Es un estado temporal que permite agregar productos y modificar sus cantidades antes de confirmar una venta.

Contiene múltiples `CartItem`.

### `CartItem`

Representa un producto dentro del carrito y la cantidad que el usuario desea comprar.

Es mutable porque la cantidad puede aumentar mientras la compra todavía no fue confirmada.

Se relaciona con un único `Product`.

### `Sale`

Representa una venta ya confirmada.

Contiene:

* Identificador.
* Vendedor que realizó la venta.
* Fecha.
* Detalles de la venta.

Una venta puede contener múltiples `SaleDetail`.

### `SaleDetail`

Representa una línea concreta de una venta.

Contiene:

* Producto vendido.
* Cantidad.
* Precio unitario al momento de la venta.

Se mantiene inmutable para conservar el estado histórico de la operación, independientemente de futuros cambios en el precio del producto.

También permite transformar un `CartItem` en el registro correspondiente dentro de una `Sale`.

## Relaciones

```text
Seller 1 ───── N Sale
Sale 1 ─────── N SaleDetail
SaleDetail N ── 1 Product

ShoppingCart 1 ── N CartItem
CartItem N ─────── 1 Product
```

El flujo de compra es:

```text
Product
   ↓
CartItem
   ↓
ShoppingCart
   ↓ checkout
SaleDetail
   ↓
Sale
   ↓
Seller
```

`ShoppingCart` y `CartItem` representan una intención de compra modificable, mientras que `Sale` y `SaleDetail` representan una operación ya realizada e histórica.

La lógica de búsqueda, persistencia y cálculo de comisiones se mantiene fuera del modelo, en servicios y repositorios, para mantener separadas las responsabilidades.
