# Exception Handling Analysis

## Question 1: Checked vs unchecked root exception

Java separates exceptions into two families. A checked exception (a subclass
of `Exception` that is not a `RuntimeException`) is enforced by the compiler:
any method that can throw it must either catch it or declare it with
`throws`, and the same applies to every caller above it. An unchecked
exception (a subclass of `RuntimeException`) is not enforced, so it travels
up the call stack with no declaration at all.

In this project `GameZoneException` extends `RuntimeException`, so the whole
hierarchy is unchecked. I think this is the right choice. Problems like
`ResourceNotFoundException` (a product, client, seller or sale that does not
exist) are business-rule failures that the intermediate layers cannot repair.
A validator or a service has nothing useful to do with them except let them
go up to the layer that can talk to the user.

**Method signatures.** Because the hierarchy is unchecked, validators,
services and repositories do not need any `throws` clause. A method such as
`findById` only declares what it does, not every way it can fail. If the root
were checked, `throws GameZoneException` would have to appear in all three
layers: the repository would declare it, the service calling the repository
would declare or catch it, and the validators and everything that calls them
would do the same. That adds noise to every signature, forces try/catch
blocks in classes that cannot handle the error, and tempts people to write
empty catch blocks just to compile. It would also couple every layer to the
exception types: adding a new one could mean editing signatures all along the
chain.

**Who catches it and the risk.** The final catch is in `ConsoleMenu`, the
outermost layer, because it is the only one that interacts with the user. It
catches `GameZoneException`, prints the message and can use `getErrorCode()`
(for example `"RESOURCE_NOT_FOUND"`), then goes back to the menu instead of
crashing. The risk of unchecked exceptions is that the compiler does not warn
me if I forget to catch one, and the program would end with a stack trace.
`ConsoleMenu` covers this by catching the root type around each menu action,
so any exception in the system, including types added later, reaches a
single handler.

**Why `IOException` is wrapped in `PersistenceException`.** `IOException` is
checked and belongs to the Java standard library, so I cannot change it. If
the repository let it escape, every service and the menu would need
`throws IOException`, and that would expose a storage detail (files) to
layers that should not know about it. Instead, the repository catches it and
throws `PersistenceException`, which is unchecked and extends
`GameZoneException`. The constructor receives the original exception as
`cause` and stores it with `initCause(cause)`, so the stack trace of the real
problem is not lost. This turns a low-level technical error into a domain
error with the code `"PERSISTENCE"` and keeps the upper layers independent
of how data is stored.

## Question 2: Why a custom exception hierarchy

If every failure were an `IllegalArgumentException`, a missing product, an
invalid value and an unreadable data file would all look the same. The only
way to tell them apart would be to read the message text, which is fragile:
rewording a message would silently break the logic. With custom types, the
type itself says what failed. `ResourceNotFoundException` means a resource of
some kind (product, client, seller or sale) does not exist, and
`PersistenceException` means a file could not be read or written. Each one
also carries an `errorCode` (`"RESOURCE_NOT_FOUND"`, `"PERSISTENCE"`), so the
caller can react to the category without parsing text.

A common root is the second big advantage. Every error the system throws on
purpose is a `GameZoneException`, so `ConsoleMenu` can handle all of them with
one `catch (GameZoneException e)`. Since every subclass is a
`GameZoneException`, that handler works with any of them and can call
`getMessage()` and `getErrorCode()` without knowing the concrete class. It
also separates our own domain errors from unexpected bugs such as
`NullPointerException`, which should not be hidden behind a friendly message.
The root is declared `abstract`, so nobody can throw a vague
`new GameZoneException(...)`; the code is forced to choose a specific type
that explains what happened.

Several design principles apply:

- **SRP (Single Responsibility).** Each class has one reason to change.
  `ResourceNotFoundException` only describes a missing resource and builds
  its own message from `resourceType` and `identifier`.
  `PersistenceException` only describes storage failures and keeps the
  original cause. The code that detects the problem, the exception that
  describes it and the menu that shows it are separate responsibilities.
- **OCP (Open/Closed).** The hierarchy is open for extension and closed for
  modification. To add a new error, for example `DuplicateProductException`,
  I create a new class that extends `GameZoneException` and passes its own
  code, such as `"DUPLICATE_PRODUCT"`. No existing exception, service or
  catch block changes, because `ConsoleMenu` already handles the root type.
- **LSP (Liskov Substitution).** A `ResourceNotFoundException` or a
  `PersistenceException` can be used anywhere a `GameZoneException` is
  expected. The handler in `ConsoleMenu` treats them through the parent type
  and works correctly with both, so the subclasses respect the contract of
  the root (a message plus an error code).
- **Encapsulation.** The `errorCode` field is `private final` and exposed
  only through `getErrorCode()`. It is set once in the constructor and
  cannot be changed afterwards, so no caller can alter the category of an
  error that is already in flight. The subclasses also hide the details: the
  caller of `ResourceNotFoundException` passes only the type and the id, and
  the class builds the message and picks the code.

In short, a custom hierarchy gives meaningful types, a single safe place to
catch domain errors and a way to add new errors without touching working
code. A generic `IllegalArgumentException` offers none of these.
## Question 3: Where validators are invoked and layering

Validators are invoked inside the service layer, right after the service
retrieves the data it needs and right before it uses that data to build or
change a domain object. In `SaleService.registerSale`, `personService` and
`productService` first fetch `client`, `seller` and each `Product`/accessory
(lines 58-59, 67, 75); only then are `PersonValidator.validatePersonExists`
and `ProductValidator.validateProductExists`/`validateStockAvailability`
called with those already-retrieved objects, and `SaleValidator.validateSaleData`
is called right before the `Sale` object is created on line 83.

This placement matches the dependency direction of the architecture:
`ui -> service -> persistence -> model`, with `validation` sitting between
`service` and `model`. Validators do not call `persistence` at all: they
never open `PersonRepository`, `ProductRepository` or any file themselves.
Instead, the service goes to persistence, gets the object, and only hands
it to the validator afterward. This keeps the validator unaware of where
the data came from, so it does not need to know whether the system reads
from a CSV file, a database, or anything else; it only validates the object
it receives.

Validators could not live in `ConsoleMenu` either, because `ConsoleMenu`
belongs to the ui layer, whose job is reading user input and printing
output, not enforcing business rules. If a second interface were added
later (for example a web interface), putting validation in `ConsoleMenu`
would force that same logic to be duplicated in the new interface instead
of being reused from a shared `service` layer, and a bug fixed in one place
could easily be forgotten in the other.

Finally, `SaleValidator.validateSaleData` is called before the `Sale`
object is built (line 83) rather than after, following a fail-fast
approach: if the data is invalid, the method stops immediately by throwing
an exception, and no `Sale` object is ever created. If the validation
happened after construction, an incomplete or invalid `Sale` would already
exist in memory with no further use, since it could not be safely saved or
processed.

## Question 4: Preserving external behaviour of the 10 menu operations

The ten original operations are the ones of the first three submenus:
register video game, register console and list products; register client,
list clients and list sellers; register sale, list all sales, list sales by
client and list sales by seller. The exception layer must not change what
the user sees when these operations succeed.

The strategy has three parts. First, the validators only reject what the
system already rejected before: a missing client or seller, a product that
does not exist, insufficient stock, a duplicated identifier or a sale with
no products. They throw a typed exception where the old code threw
`IllegalArgumentException` or returned `null`, so the happy path runs the
same instructions in the same order. Second, the services keep their public
signatures (names, parameters and return types), so `ConsoleMenu` calls them
exactly as before and no caller had to change. Third, the only visible
difference is in the error path: a failure now prints a Spanish message with
its category (for example `[No encontrado]`) and returns to the menu, where
before it ended the program with a stack trace.

The check is a manual test run before and after the change, on a copy of
the `data/` folder so that both runs start from the same files. Each of the
ten operations is executed with fixed inputs (for example video game `VG90`,
console `CO90`, client `C90` and sale `V90` with both products and an
extended warranty) and the printed output is compared line by line. The same
run then covers five error cases: a duplicated client, a sale with an
unknown product, a negative price, text where a number is expected and a
product without stock. The results of the successful operations (registered
descriptions, listings and the sale receipt) were identical in both runs;
only the menu labels changed, because they were translated to Spanish in
the same requirement. The error cases changed from a crash to a message,
which is the goal of this requirement. This run is also recorded as the baseline for
the GRASP and SOLID refactors.
