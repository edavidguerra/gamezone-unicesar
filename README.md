# GameZone Unicesar

Sistema de información de la tienda de videojuegos GameZone Unicesar, desarrollado en Java con
Maven, organizado en cuatro capas (model, persistence, service, ui).

Proyecto académico — Taller 1 de Programación III, Universidad Popular del Cesar.

## Features

- Registro y listado de videojuegos y consolas.
- Registro y listado de clientes y vendedores (vendedores precargados desde el inicio).
- Registro de ventas asociadas a un cliente, un vendedor y uno o más productos.
- Cálculo automático del total de cada venta.
- Consulta de ventas por cliente y por vendedor.
- Persistencia de todos los datos en archivos de texto dentro de la carpeta `data/`.

## Requirements

- JDK 17+
- Maven 3.8+

## How to compile
- mvn compile

## How to run
-mvn exec:java

## Project structure

El proyecto sigue una arquitectura de cuatro capas, con dependencias en un solo sentido:
`ui -> service -> persistence -> model`. Ver el detalle completo en:

- `docs/hierarchy-diagram.md` — jerarquías de herencia.
- `docs/class-diagram.md` — diagrama de clases completo.
- `docs/layers-diagram.md` — diagrama de capas y dependencias.

## Data persistence

Los datos se guardan en archivos de texto plano dentro de la carpeta `data/` (por ejemplo
`data/sellers.csv`), y se cargan automáticamente cada vez que se ejecuta el programa.

## Team

Ver [TEAM.md](TEAM.md) para los integrantes del equipo y sus roles.