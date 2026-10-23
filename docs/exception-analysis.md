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