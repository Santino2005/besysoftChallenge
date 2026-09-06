# Diseño de Repositories

La aplicación utiliza tres repositories principales:

```text
repository/
├── product/
│   └── ProductRepository
├── person/
│   └── SellerRepository
└── sale/
    └── SaleRepository
```

La decisión de implementar únicamente estos tres repositories se basa en diferenciar entre **entidades que necesitan ser almacenadas y consultadas de forma independiente** y objetos que forman parte de otras entidades o representan estados temporales.

## `ProductRepository`

Se implementa porque `Product` es una entidad independiente del sistema.

Los productos deben poder:

* almacenarse;
* buscarse por ID;
* buscarse por código;
* buscarse por categoría;
* listarse;
* eliminarse.

Por lo tanto, necesita una estructura de almacenamiento propia en memoria.

## `SellerRepository`

Se implementa porque `Seller` también representa una entidad independiente.

Los vendedores deben poder almacenarse y consultarse, además de ser asociados posteriormente a las ventas.

El repository permite, entre otras operaciones:

* guardar vendedores;
* buscar por ID;
* buscar por código;
* listar vendedores;
* eliminar vendedores.

## `SaleRepository`

Se implementa porque `Sale` representa una operación histórica que debe permanecer almacenada.

Las ventas son necesarias para:

* consultar operaciones realizadas;
* mantener el historial;
* relacionar cada venta con el vendedor que la realizó;
* obtener las ventas de un vendedor para calcular posteriormente su comisión.

## ¿Por qué no `ShoppingCartRepository`?

`ShoppingCart` representa un estado temporal de una compra.

Su función es mantener los productos que el usuario desea comprar antes de confirmar la operación:

```text
ShoppingCart
    ↓ checkout
   Sale
```

Una vez realizada la compra, el carrito deja de ser necesario para mantener el historial.

Además, la consigna solicita almacenar y registrar ventas, pero no requiere persistir carritos abandonados ni recuperarlos posteriormente.

Por este motivo, el carrito permanece como parte del dominio y no necesita un repository propio.

## ¿Por qué no `SaleDetailRepository`?

`SaleDetail` no se considera una entidad independiente dentro del caso de uso.

Un detalle representa una línea perteneciente a una única venta:

```text
Sale
└── SaleDetail
    └── Product
```

Los detalles se almacenan como parte de la `Sale`:

```java
saleRepository.save(sale);
```

y se recuperan mediante:

```java
sale.details();
```

No existe en los requerimientos una operación que necesite buscar, modificar o eliminar un `SaleDetail` de forma independiente.

Por lo tanto, crear un repository específico para `SaleDetail` agregaría complejidad sin aportar una funcionalidad necesaria.

## Criterio general

La decisión sigue el principio de **no agregar abstracciones innecesarias**.

Se crea un repository cuando el objeto:

1. necesita persistencia independiente;
2. necesita ser consultado directamente;
3. participa en operaciones de negocio de forma independiente.

Por eso:

```text
Product       -> Repository (Aplica)
Seller        -> Repository (Aplica)
Sale          -> Repository (Aplica)

ShoppingCart  -> Repository (No aplica)
SaleDetail    -> Repository (No aplica)
```

`ShoppingCart` y `SaleDetail` continúan formando parte del modelo de dominio, pero su ciclo de vida depende de otras entidades y no requieren almacenamiento independiente para cumplir con la consigna.
