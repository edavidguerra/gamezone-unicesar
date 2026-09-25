# Módulo de Promociones - Documentación de Análisis y Diseño

1. Visión General del Módulo

El módulo de promociones del sistema GameZone Unicesar extiende la capacidad de facturación de la tienda permitiendo aplicar distintas estrategias de descuento sobre las ventas realizas (Sale). El diseño está fundamentado en principios de Orientación a Objetos como herencia, encapsulamiento y polimorfismo, facilitando la incorporación de nuevas reglas de negocio sin modificar el núcleo del dominio existente.

2. Requerimientos Funcionales y Reglas de Negocio

2.1 Estrategias de Descuento

El sistema soporte tres modalidades principales de promoción:

Descuento Porcentual General (PercentageDiscount): Aplica un porcentaje directo de reducción sobre el costo subtotal de la venta.

Descuento por Categoría (CategoryDiscount): Evalúa los ítems de la venta y aplica una tasa de descuento únicamente a los productos pertenecientes a una categoría objetivo especificada (ej. "VIDEOGAME", "CONSOLE").

Descuento por Compra al Por Mayor (BulkPurchaseDiscount): Activa una tasa de descuento global cuando la cantidad total de artículos agregados a la venta alcanza o supera un umbral estipulado (minimumQuantity).

2.2 Validación Temporizada

Toda promoción cuenta con un período de vigencia delimitado por una fecha inicial (startDate) y una fecha final (endDate). La consulta isActiveOn(LocalDate date) verifica la validez temporal del beneficio antes de efectuar los cálculos.

3. Estructura de Clases y Polimorfismo

              ┌────────────────────────┐
              │  <<abstract>> Promotion │
              ├────────────────────────┤
              │ - id: String           │
              │ - name: String         │
              │ - startDate: LocalDate │
              │ - endDate: LocalDate   │
              ├────────────────────────┤
              │ + calculateDiscount()  │
              │ + isActiveOn()         │
              └───────────▲────────────┘
                          │
     ┌────────────────────┼────────────────────┐
     │                    │                    │
┌────┴─────────────┐ ┌────┴─────────────┐ ┌────┴──────────────────┐
│PercentageDiscount│ │ CategoryDiscount │ │BulkPurchaseDiscount│
└──────────────────┘ └──────────────────┘ └────────────────────┘


Promotion (Clase Abstracta): Funciona como el contrato unificado. Declara el método abstracto double calculateDiscount(Sale sale) que cada tipo de promoción concreta implementa según su lógica específica.

Desacoplamiento y Polimorfismo: La venta (Sale) no necesita conocer los detalles internos de cada regla comercial; únicamente evalúa el monto del descuento invocado a través de la firma polimórfica calculateDiscount().

4. Integración con la Entidad Sale

La entidad Sale fue ajustada para registrar el beneficio promocional otorgado en la transacción:

appliedPromotionName (String): Almacena el nombre descriptivo de la promoción aplicada a la venta.

discountAmount (double): Registra el monto final descontado.

Generación de Recibo (generateReceipt()): Construye la liquidación consolidando:

Subtotal antes de descuento.

Identificación de la promoción y deducción monetaria efectuada (si aplica).

Total final a pagar.