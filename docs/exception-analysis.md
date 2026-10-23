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