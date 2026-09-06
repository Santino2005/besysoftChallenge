# Errores personalizados

La aplicación utiliza **excepciones personalizadas** en lugar de depender únicamente de las excepciones genéricas provistas por Java.

## ¿Por qué?

### 1. Representan errores propios del dominio

Cada excepción describe un problema concreto de la aplicación.

Por ejemplo:

* `InvalidProductException` → datos inválidos de un producto.
* `InvalidSellerException` → datos inválidos de un vendedor.
* `InvalidCartItemException` → datos inválidos de un elemento del carrito.
* `InvalidSaleDetailException` → datos inválidos de un detalle de venta.
* `InvalidSaleException` → error relacionado con una venta.

Esto permite expresar mejor qué ocurrió que utilizando excepciones genéricas como `IllegalArgumentException`.

### 2. Permiten un manejo específico

El `GlobalErrorHandler` puede distinguir cada tipo de error y convertirlo en una respuesta concreta:

```text
InvalidProductException
        ↓
INVALID_PRODUCT

InvalidSellerException
        ↓
INVALID_SELLER

InvalidSaleException
        ↓
INVALID_SALE
```

De esta forma, la aplicación puede reaccionar de manera diferente según el problema ocurrido.

### 3. Conservan información adicional

Las excepciones personalizadas pueden almacenar información relevante, como el campo que produjo el error:

```java
throw new InvalidProductException(
        "price",
        "El precio no puede ser nulo ni negativo."
);
```

Esto permite devolver información más precisa mediante `GlobalErrorHandler`.

### 4. Separan errores de infraestructura de errores del dominio

Una excepción como `NullPointerException` o `IllegalArgumentException` describe un problema técnico o genérico.

En cambio:

```text
InvalidProductException
InvalidSellerException
InvalidSaleException
```

representan reglas e invariantes propias del negocio.

Esto hace que el modelo sea más expresivo y facilite su mantenimiento.

## Criterio utilizado

Se utilizan excepciones personalizadas cuando el error representa una **regla o estado inválido del dominio**.

Las excepciones estándar de Java continúan utilizándose cuando representan errores genéricos que no necesitan una clasificación propia.

Por ejemplo:

```text
Error específico del dominio
→ InvalidProductException

Error genérico de argumento
→ IllegalArgumentException
```

En conjunto, este enfoque permite que los errores sean **más descriptivos, fáciles de identificar y de manejar**, manteniendo además una separación clara entre la lógica de dominio y el manejo global de errores.
