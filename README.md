# GameZone Unicesar

Sistema de información de la tienda de videojuegos GameZone Unicesar, desarrollado en Java con
Maven, organizado en cuatro capas (model, persistence, service, ui).

Proyecto académico — Programación III, Universidad Popular del Cesar.

## Features

- Registro y listado de videojuegos y consolas.
- Registro y listado de clientes y vendedores (vendedores precargados desde el inicio).
- Registro de ventas asociadas a un cliente, un vendedor, uno o más productos y accesorios.
- Cálculo automático del total de cada venta: subtotal, menos el descuento de la mejor
  promoción vigente, más el costo de las garantías extendidas.
- Consulta de ventas por cliente y por vendedor.
- Registro y consulta de accesorios (controles, cables y memorias), incluyendo su
  compatibilidad con consolas específicas.
- Registro de promociones (porcentaje, por categoría —videojuegos, consolas o accesorios— o
  por volumen de compra) y aplicación automática de la mejor promoción vigente al registrar
  una venta.
- Asignación automática de garantía básica a cada consola vendida, con opción de garantía
  extendida, y consulta de garantías activas o próximas a vencer.
- Registro de devoluciones de productos y accesorios dentro de los 30 días posteriores a la
  venta, con reposición automática del inventario y rechazo de unidades ya devueltas.
- Reembolso proporcional al descuento de la venta original y reembolso del costo de la
  garantía extendida cuando se devuelve una consola; las garantías de esa consola se cancelan.
- Consulta de devoluciones por cliente y por venta.
- Balance mensual: total de ventas, total de devoluciones y balance neto.
- Persistencia de todos los datos en archivos de texto dentro de la carpeta `data/`, incluidos
  el descuento, el costo de garantía y el total final de cada venta.

## Requirements

- JDK 17+
- Maven 3.8+

## How to compile

```
mvn compile
```

## How to run

```
mvn exec:java
```

## Project structure

El proyecto sigue una arquitectura de cuatro capas, con dependencias en un solo sentido:
`ui -> service -> persistence -> model`. La única excepción documentada es `ReturnRepository`,
que usa servicios para reconstruir las ventas y los productos de cada devolución.

Documentación de diseño:

- `docs/hierarchy-diagram.md` — jerarquías de herencia.
- `docs/class-diagram.md` — diagrama de clases del sistema base (productos, personas y ventas).
- `docs/integrated-class-diagram.md` — diagrama de clases integrado de los cuatro módulos.
- `docs/layers-diagram.md` — diagrama de capas y dependencias.
- `docs/integration-analysis.md` — análisis de integración de los ajustes A1 a A7.

Análisis y diagramas por módulo:

- Accesorios: `docs/accessory-analysis.md` y `docs/accessory-class-diagram.md`.
- Promociones: `docs/promotion-analysis.md` y `docs/promotion-class-diagram.md`.
- Garantías: `docs/warranty-analysis.md` y `docs/warranty-class-diagram.md`.
- Devoluciones: `docs/return-analysis.md` y `docs/return-class-diagram.md`.

## Data persistence

Los datos se guardan en archivos de texto plano dentro de la carpeta `data/` (por ejemplo
`data/sellers.csv`), y se cargan automáticamente cada vez que se ejecuta el programa. Los
archivos `data/returns.csv` y `data/warranties.csv` se generan mientras el programa se usa y no
se versionan (están en `.gitignore`). El formato de cada archivo está en
`docs/integration-analysis.md`.

## Team

Ver [TEAM.md](TEAM.md) para los integrantes del equipo y sus roles.