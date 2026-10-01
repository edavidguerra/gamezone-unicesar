# Bitácora de uso de IA — Líder Técnico

### Unificación de registerSale (ajuste A3)
- Herramienta de IA usada: Claude
- Pregunta/prompt: Tres personas habían modificado registerSale en momentos distintos y el
  método quedó con lógica pegada de cada módulo. Pedí unificarlo en un solo flujo correcto.
- Resumen de la respuesta: Me explicó que lo difícil no era juntar las tres piezas sino decidir
  el orden: la promoción se calcula sobre el subtotal de productos y accesorios (sin la garantía
  extendida, que no se promociona) y la garantía extendida se suma después del descuento, para
  que no se descuente por accidente. Si el orden se invertía, el total saldría mal sin ningún
  error de compilación, porque Java no detecta errores de lógica de negocio. También propuso
  reducir el stock como penúltimo paso, después de las validaciones y asignaciones, para no
  descontar inventario de una venta que no se completó.
- Decisión tomada: Dejé el método en ese orden y lo probé con un caso real (consola + accesorio
  + garantía + promoción), verificando el resultado a mano antes de cerrar el refactor.

### Dependencia circular entre SaleService y WarrantyService (ajuste A2)
- Herramienta de IA usada: Claude
- Pregunta/prompt: SaleService necesita llamar a WarrantyService al registrar una venta, pero
  WarrantyService necesita a SaleService para saber a qué venta pertenece cada garantía guardada.
  Pregunté cómo construir ambos en Main.java.
- Resumen de la respuesta: Explicó que si los dos constructores exigen al otro es imposible
  construir cualquiera primero. La solución fue romper el ciclo con inyección por setter:
  SaleService se crea primero sin garantías, luego se crea WarrantyService con ese SaleService y
  al final se inyecta con setWarrantyService(...). Advirtió que el orden en Main.java es
  crítico: si se invierte, warrantyService queda en null y falla con NullPointerException en la
  primera venta con consola.
- Decisión tomada: Documenté el orden de construcción en Main.java y en el análisis de
  integración, y revisamos que ningún servicio use garantías antes de la inyección.

### Dividir un commit con git add -p
- Herramienta de IA usada: Claude
- Pregunta/prompt: Edité ConsoleMenu.java con dos cambios distintos a la vez (el submenú de
  garantías y la pregunta de garantía extendida en el flujo de venta) y quería dos commits
  separados, como piden los Conventional Commits.
- Resumen de la respuesta: Me explicó git add -p (modo parche), que decide hunk por hunk qué va
  en cada commit. Algunos hunks mezclaban ambas funciones por estar cerca en el archivo, así que
  hubo que usar s (split) para partirlos y separar línea por línea qué iba en cada commit.
- Decisión tomada: Hice dos commits atómicos, uno por cada cambio lógico, en lugar de uno
  mezclado.

### Error de sintaxis heredado en SaleService.java
- Herramienta de IA usada: Claude
- Pregunta/prompt: Al trabajar las garantías pedí revisar SaleService.java antes de modificarlo.
- Resumen de la respuesta: Al leer el archivo real, y no suponer cómo debía verse, vio que el
  fragmento "public Sale" (inicio de la firma de registerSale) estaba en medio del cuerpo del
  método, después de una llave de cierre que no correspondía. Es típico de un conflicto de merge
  resuelto aceptando partes de ambas versiones sin revisar las llaves. Aclaró que no era error
  mío sino de una fusión anterior.
- Decisión tomada: Corregí la estructura del método y quedamos en revisar siempre el archivo
  completo tras resolver un conflicto de merge, en lugar de aceptar "mis cambios" o "sus
  cambios" a ciegas.

### Id del vendedor sensible a mayúsculas
- Herramienta de IA usada: Claude
- Pregunta/prompt: En la prueba completa de la venta (A3) escribí v1 y el sistema no encontró
  al vendedor, aunque data/sellers.csv sí lo tiene.
- Resumen de la respuesta: Explicó que los vendedores están guardados como V1, V2, V3 y que la
  búsqueda usa String.equals, que distingue mayúsculas ("V1".equals("v1") es false). No hay una
  capa que normalice el texto, así que el comportamiento es correcto según el código actual.
- Decisión tomada: No cambié la lógica de búsqueda; probé con el id escrito exactamente como
  está registrado y lo anoté como limitación conocida.

### "Nothing to compile - all classes are up to date"
- Herramienta de IA usada: Claude
- Pregunta/prompt: Maven decía que no había nada que compilar y el programa seguía ejecutando
  el comportamiento viejo.
- Resumen de la respuesta: Explicó que maven-compiler-plugin compara la fecha de modificación
  de cada .java con la del .class en target/classes. Si VS Code tenía cambios sin guardar, el
  archivo en disco no había cambiado y Maven concluía que no había nada nuevo, sin mostrar
  ningún error. Recomendó confirmar que el archivo estuviera guardado y, para asegurarse, usar
  mvn clean compile, que borra target/ y fuerza una recompilación completa.
- Decisión tomada: Antes de cada prueba reviso que no haya pestañas con cambios sin guardar y
  uso mvn clean compile cuando algo no refleja mis cambios.

## 2026-09-29
- Herramienta de IA usada: Claude
- Pregunta/prompt: Le pedí que leyera de nuevo el repositorio, lo comparara con los requerimientos
  del taller y me dijera qué le faltaba al proyecto; luego, que generara para el Desarrollador 1
  y el Desarrollador 2 una guía solo de lo que les faltaba (de la Fase 4 en adelante), con
  instrucciones lo más explícitas posible y siguiendo el flujo de ramas del proyecto.
- Resumen de la respuesta: Revisó el código y encontró lo pendiente: los ajustes A4 a A7 y un
  problema de persistencia de ventas. Generó dos guías en PDF, una por desarrollador, con el
  orden de las ramas, el código completo de cada cambio, los commits y los comandos de Git.
- Decisión tomada: Usé las guías para repartir el trabajo (Gustavo: A5 y parte de A7; Juan: A4,
  A6 y parte de A7) y se las entregué a los dos. Reviso en el repositorio que cada rama avance
  como indican las guías.

## 2026-09-30
- Herramienta de IA usada: Claude
- Pregunta/prompt: Pregunté qué más había que arreglar antes de los ajustes de Juan y Gustavo.
- Resumen de la respuesta: Detectó que SaleRepository solo guardaba el id, la fecha, el cliente,
  el vendedor y los productos, así que al reiniciar el programa se perdían el descuento, el costo
  de la garantía extendida, el total final y los accesorios de cada venta. Esto afectaba a las
  devoluciones (A5), porque el reembolso depende de lo que el cliente realmente pagó. Propuso una
  rama adicional, fix/sale-persistence, con dos commits, y probó el arreglo simulando un reinicio.
- Decisión tomada: Acepté la rama extra, hice los dos commits yo mismo
  (fix: persist discount, warranty cost and final total of sales y fix: load accessories when
  restoring saved sales) y la subí con su Pull Request antes de que Gustavo y Juan empezaran A4
  y A5, para que trabajaran sobre ventas que ya se guardan completas.

## 2026-09-30
- Herramienta de IA usada: Claude
- Pregunta/prompt: Le pedí terminar mi parte de la Fase 5: la documentación de integración (A8).
  Cuando no sabía qué decían los ajustes A1 y A3, me pidió el texto del enunciado.
- Resumen de la respuesta: Había supuesto mal el ajuste A1; con el texto del enunciado que le
  pasé escribió el análisis de integración de A1 a A7 (en inglés), el diagrama de clases
  integrado de los cuatro módulos, el diagrama de capas y el README actualizados. Comprobó que
  los diagramas Mermaid se dibujaran sin errores y que los nombres coincidieran con el código.
  Además detectó que el mensaje del menú de promociones por categoría todavía no menciona
  ACCESSORY.
- Decisión tomada: Hice los tres commits de la rama docs/integration-documentation y abrí su
  Pull Request. Dejé el cambio del mensaje del menú para una rama pequeña aparte
  (fix/accessory-category-prompt, ya fusionada).