# Guía de defensa (explicado para tontos) - Proyecto MongoDB + Swing

> Objetivo: que mañana puedas explicar **qué hace cada parte**, **por qué está hecha así** y **qué decisiones tomaste**.

---

## 1) ¿Qué hace este proyecto en una frase?

Es una app de escritorio hecha con Java Swing que:
1. Guarda continentes.
2. Guarda países asociados a un continente.
3. Permite borrar países.
4. Muestra una consulta por continente con lista de países y suma total de habitantes.

La base de datos usada es MongoDB local (`mongodb://localhost:27017`) sobre la BD `local`.

---

## 2) Estructura simple del proyecto (mapa mental)

- `Entidad/`
  - `Continente.java`: modelo de un continente (nombre).
  - `Pais.java`: modelo de un país (nombre, habitantes, id del continente).
- `Controlador/`
  - `DatabaseManager.java`: toda la comunicación con MongoDB (insertar, listar, borrar, buscar ids).
- `Vistas/`
  - `Main.java`: menú principal.
  - `AñadirContinentes.java`: ventana para añadir continentes.
  - `Paises.java`: ventana para añadir y eliminar países.
  - `Consultas.java`: ventana para filtrar por continente y ver suma de habitantes.

**Decisión defendible:** separar por capas (entidad/controlador/vista) hace el código más entendible y fácil de mantener.

---

## 3) Flujo real de uso (como lo explicas en la defensa)

1. Entras en `Main` y eliges una opción del menú.
2. Si vas a países o consultas, antes se comprueba que existan continentes.
3. En cada ventana se abre conexión a MongoDB cuando se necesita.
4. Se obtienen datos (continentes o países), se pinta UI y se valida entrada.
5. Se cierran conexiones tras operaciones críticas.

**Frase útil para defender:**
> “Antes de operar con países, fuerzo que haya continentes para mantener coherencia de datos y evitar registros huérfanos.”

---

## 4) Explicación clase por clase (fácil)

## `Entidad/Continente.java`
- Tiene un atributo `nombre`.
- Tiene getters/setters en español (`getNombre`) y también en formato `getName`.

**Por qué tomaste esta decisión:** compatibilidad con código existente sin romper llamadas anteriores.

## `Entidad/Pais.java`
- Guarda `numHabitantes`, `nombrePais`, `continenteId`.
- Es un POJO (objeto simple de datos).

**Por qué:** separar “datos” de “lógica” para no mezclar responsabilidades.

## `Controlador/DatabaseManager.java`
Esta clase es clave. Hace:
- Conectar/desconectar Mongo (`runMongoDatabase`, `closeMongoDatabase`).
- Insertar continentes y países (`añadirContinentes`, `añadirPais`).
- Listar continentes y países (`getListaDeContinentes`, `getListaPaises`).
- Buscar id de continente (`getIdFromContinente`, `getIdContinentePuro`).
- Borrar país (`deletePais`).

### Decisiones importantes aquí
1. **Constantes** para URI, nombre BD y colecciones.
   - Mejor legibilidad y fácil cambio futuro.
2. **Método `limpiarTexto`** con `trim()`.
   - Evita errores por espacios raros del usuario.
3. **Manejo de excepciones** con mensajes por consola.
   - Evita que la app reviente y permite diagnosticar.

**Frase útil para defender:**
> “Centralicé toda la lógica de base de datos en un único manager para reducir duplicación y mantener un único punto de cambio.”

## `Vistas/Main.java`
- Pantalla principal con menú.
- Abre diálogos de continentes, países y consultas.
- Comprueba que haya continentes antes de abrir países/consultas.

**Por qué:** proteger el flujo lógico de la app.

## `Vistas/AñadirContinentes.java`
- Lee el texto, valida que no esté vacío.
- Comprueba si ya existe (case-insensitive).
- Si no existe, inserta.

**Por qué:** prevenir duplicados y datos vacíos.

## `Vistas/Paises.java`
- Carga combos de continentes y países.
- Añade país validando número y duplicado.
- Borra país por selección.

**Decisiones defendibles:**
- Validar habitantes como número (captura `NumberFormatException`).
- Usar combos para minimizar errores de escritura en borrado/selección.

## `Vistas/Consultas.java`
- Seleccionas continente.
- Filtra países de ese continente por id.
- Ordena alfabéticamente.
- Muestra lista y suma habitantes.

**Por qué:** ejemplo claro de consulta agregada para demostrar control de datos.

---

## 5) ¿Por qué MongoDB aquí?

- El modelo es simple y documental (continente/pais).
- Fácil para guardar documentos JSON-like.
- No necesitas joins complejos para este ejercicio.

**Frase para defensa:**
> “Mongo me permite una implementación rápida del CRUD y del filtrado para un caso académico con estructura de datos sencilla.”

---

## 6) Validaciones que sí puedes presumir

- No se aceptan continentes vacíos.
- No se duplican continentes por nombre.
- No se duplican países por nombre.
- Habitantes solo numérico.
- Consultas protegidas para estados vacíos.

---

## 7) Decisiones de estilo/código que te pueden preguntar

### “¿Por qué atributos al principio y orden de métodos?”
Porque mejora legibilidad y sigue convención habitual:
1. Atributos
2. Constructor
3. Getters/setters
4. Métodos funcionales

### “¿Por qué no metiste toda la lógica en la vista?”
Porque sería difícil mantener. Separar en controlador (`DatabaseManager`) hace más limpio el proyecto.

### “¿Por qué algunos textos y estilos de UI cambiados?”
Para diferenciar interfaz manteniendo exactamente las mismas funcionalidades del enunciado.

---

## 8) Posibles preguntas trampa del profesor (y respuesta corta)

### P: “¿Qué pasa si Mongo no está levantado?”
R: `runMongoDatabase()` devuelve false y se muestra mensaje de error al usuario.

### P: “¿Cómo relacionas país y continente?”
R: guardando el `continenteId` dentro del país; en consultas filtro por ese id.

### P: “¿Cómo evitas insertar cosas mal?”
R: con validaciones previas (vacío, duplicado, numérico) y limpieza de texto.

### P: “¿Dónde tocarías si cambia la URL de Mongo?”
R: en las constantes de `DatabaseManager`.

---

## 9) Debilidades honestas (también suma decirlas)

- No hay tests automáticos unitarios.
- Algunas operaciones podrían usar `try-with-resources`/arquitectura más avanzada.
- La UI Swing generada por NetBeans no es la más moderna, pero es correcta para el objetivo.

**Frase elegante para cerrar:**
> “He priorizado claridad, separación de responsabilidades y validaciones básicas robustas, que eran los objetivos funcionales del ejercicio.”

---

## 10) Mini guion de 1 minuto para exponer

“Esta aplicación Java Swing gestiona continentes y países usando MongoDB. La estructuré en entidades, controlador y vistas para separar datos, lógica y presentación. `DatabaseManager` centraliza toda la conexión y operaciones CRUD. En la interfaz, `Main` dirige el flujo y cada diálogo hace una tarea concreta: añadir continentes, gestionar países y consultar por continente. Implementé validaciones de vacíos, duplicados y formato numérico para mejorar consistencia. Además, en consultas filtro por id de continente y calculo la suma total de habitantes. Las decisiones están orientadas a legibilidad, mantenimiento y cumplimiento exacto de funcionalidades pedidas.”

---

## 11) Qué repasar hoy en 20 minutos

1. `DatabaseManager`: entiende cada método y cuándo se usa.
2. Flujo desde `Main` a cada diálogo.
3. Validaciones clave (duplicados, vacíos, números).
4. Relación `continenteId` en `Pais`.
5. Consulta en `Consultas` (filtrar + ordenar + sumar).

Si dominas eso, estás listo para defenderlo.
