# GameZone Unicesar - Team
## Members
| Name | Student code | Role | Module |
|---|---|---|---|
| [EVER GUERRA] | [1123805049] | Technical Leader | Sales & Integration |
| [GUSTAVO RODRIGUEZ] | [1066280590] | Developer 1 | Products |
| [JUAN ARAUJO] | [1067601441] | Developer 2 | People |
## Class distribution
- Developer 1: Product (abstract), VideoGame, Console, ProductRepository, ProductService
- Developer 2: Person (abstract), Client, Seller, PersonRepository, PersonService
- Technical Leader: Sale, SaleRepository, SaleService, ConsoleMenu, Main
## Feature branches
- feature/product-module (Developer 1)
- feature/person-module (Developer 2)
- feature/sale-module, feature/ui-menu (Technical Leader)
## Committed activities per member
# Responsabilidades por Rol - Taller 2 (GameZoneUnicesar)

## Líder Técnico

1. Crear el repositorio del proyecto en GitHub con la configuración inicial (README, .gitignore, licencia).
2. Configurar las ramas del proyecto (main y develop) y activar la protección de las ramas principales.
3. Configurar el proyecto Maven con el pom.xml inicial y la estructura de paquetes de las cuatro capas.
4. Elaborar el archivo TEAM.md con la información del equipo, los roles asignados y la distribución de clases.
5. Implementar la clase del dominio de ventas con sus atributos, constructor y métodos básicos.
6. Implementar el método de cálculo del total de la venta.
7. Implementar la clase de persistencia del módulo de ventas.
8. Implementar la clase de servicio del módulo de ventas con las reglas de validación (mínimo un producto, verificación de stock, actualización de inventario).
9. Implementar la estructura básica de la clase de interfaz de usuario (menú principal).
10. Implementar los submenús de la interfaz de usuario para cada uno de los tres módulos.
11. Implementar la clase principal de la aplicación con la carga inicial de datos y la inyección de dependencias.
12. Revisar e integrar los Pull Requests de los desarrolladores en la rama de integración.
13. Elaborar el README.md final del proyecto con las instrucciones de compilación y ejecución.

---

## Desarrollo 1 (Módulo de Productos)

1. Crear la rama feature correspondiente al módulo de productos.
2. Implementar la clase base abstracta de la jerarquía de productos con sus atributos comunes, constructor y métodos comunes.
3. Declarar el método abstracto de descripción que las clases derivadas deberán implementar.
4. Implementar la primera clase derivada (videojuegos) con sus atributos particulares y la implementación del método de descripción.
5. Implementar la segunda clase derivada (consolas) con sus atributos particulares y la implementación del método de descripción.
6. Implementar la clase de persistencia del módulo de productos con los métodos de guardado y carga desde archivos.
7. Implementar la clase de servicio del módulo de productos con los métodos de registro, listado y actualización de stock.
8. Documentar todas las clases del módulo con JavaDoc en inglés.
9. Solicitar Pull Requests al Líder Técnico para la integración del módulo.

---

## Desarrollo 2 (Módulo de Personas)

1. Crear la rama feature correspondiente al módulo de personas.
2. Implementar la clase base abstracta de la jerarquía de personas con sus atributos comunes, constructor y métodos comunes.
3. Declarar el método abstracto o de negocio que las clases derivadas deberán implementar según el análisis realizado.
4. Implementar la primera clase derivada (clientes) con sus atributos particulares.
5. Implementar la segunda clase derivada (vendedores) con sus atributos particulares.