# Bitácora de uso de IA — Líder Técnico

## 2026-09-07
- Herramienta de IA usada: Claude
- Pregunta/prompt: Le pedí que me explicara paso a paso cómo continuar
  desde donde iba (repositorio, Maven, ramas) hasta terminar el módulo de Ventas.
- Resumen de la respuesta: Me dio la secuencia completa de comandos Git para crear una rama por
  cada actividad (feature/project-setup, feature/sale-module, feature/ui-menu), el ciclo
  commit -> push -> Pull Request -> merge -> borrado de rama, y el código completo de Sale.java,
  SaleRepository.java y SaleService.java con JavaDoc.
- Decisión tomada: Seguí ese flujo de ramas para cada actividad del taller y usé el código
  propuesto como base de mis tres clases del módulo de Ventas.

## 2026-09-07
- Herramienta de IA usada: Claude
- Pregunta/prompt: Pregunté qué iba en los campos "base" y "compare" al abrir un Pull Request,
  porque GitHub me sugería automáticamente comparar contra main.
- Resumen de la respuesta: Me explicó que "base" es la rama destino y "compare" la rama de
  origen, que en este taller "base" siempre debe ser develop (excepto el Pull Request final del
  proyecto), y que el botón "Compare & pull request" de GitHub no conoce el flujo del taller.
- Decisión tomada: Corregí manualmente el campo "base" a develop antes de crear el Pull Request,
  evitando fusionar directo a main.

## 2026-09-07
- Herramienta de IA usada: Claude
- Pregunta/prompt: Pregunté si debía usar "git push -u" en cada commit, porque me salió el error
  "the current branch has no upstream branch".
- Resumen de la respuesta: Me explicó que -u (--set-upstream) solo se necesita la primera vez que
  se sube una rama nueva; los pushes siguientes en la misma rama solo requieren "git push".
- Decisión tomada: Usé "git push -u origin <rama>" únicamente al crear cada rama nueva, y
  "git push" simple en los commits siguientes de esa misma rama.

## 2026-09-08
- Herramienta de IA usada: Claude
- Pregunta/prompt: Mostré una captura de un error de VS Code ("Language Support for Java client")
  y otro error al intentar limpiar el workspace de Java.
- Resumen de la respuesta: Me explicó que era un fallo del Language Server de la extensión de
  Java y me dio pasos para resolverlo (recargar ventana, cerrar procesos de VS Code y, como
  último recurso, desinstalar y reinstalar "Extension Pack for Java").
- Decisión tomada: Desinstalé y reinstalé la extensión; el error desapareció.

## 2026-09-08
- Herramienta de IA usada: Claude
- Pregunta/prompt: Pregunté por qué Sale.java marcaba errores de compilación al crear la clase.
- Resumen de la respuesta: Analizó una captura de mi Explorer y detectó que el archivo se llamaba
  "sale.java" en minúscula en vez de "Sale.java"; me explicó que en Java el nombre del archivo
  debe coincidir exactamente, incluyendo mayúsculas, con el nombre de la clase pública.
- Decisión tomada: Renombré el archivo a "Sale.java" con F2, lo que eliminó la mayoría de los
  errores.

## 2026-09-08
- Herramienta de IA usada: Claude
- Pregunta/prompt: Pregunté por qué Main.java marcaba "ProductRepository cannot be resolved to a
  type" y errores similares.
- Resumen de la respuesta: Me explicó que esas clases pertenecen a los módulos de mis compañeros
  y que el error es normal si sus Pull Requests aún no se han fusionado a develop; sugirió seguir
  avanzando en tareas independientes mientras tanto.
- Decisión tomada: Adelanté la creación de data/sellers.csv y los diagramas de arquitectura
  mientras mis compañeros terminaban sus módulos, en vez de quedarme esperando.


## 2026-09-08
- Herramienta de IA usada: Claude
- Pregunta/prompt: Durante ese merge se abrió un editor de texto (Vim) que no sabía cerrar, y el
  commit de fusión quedó a medias.
- Resumen de la respuesta: Me explicó qué es Vim, cómo salir sin guardar (Esc + :q!), y cómo
  terminar el commit de fusión pendiente sin abrir ningún editor (git commit --no-edit).
- Decisión tomada: Cerré Vim correctamente y completé el commit del merge con
  "git commit --no-edit".

## 2026-09-08
- Herramienta de IA usada: Claude
- Pregunta/prompt: Tras integrar los tres módulos, "mvn compile" mostraba un solo error:
  "Person.java: reached end of file while parsing". Pedí ayuda para identificarlo.
- Resumen de la respuesta: Al revisar el contenido completo del archivo, detectó que la última
  llave de cierre de la clase estaba comentada por accidente ("// }" en vez de "}").
- Decisión tomada: Corregí la línea directamente en mi rama (como dueño del repositorio), dejé
  constancia en el mensaje del commit ("fix: restore missing closing brace in Person class") y le
  avisé a mi compañero del error en su módulo.
