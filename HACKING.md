# Hacking guide

## Running style checks and unit tests

When working on the code base, consider running the style checks and unit
tests locally, because the CI will complain anyway:

    ./gradlew :check

The above command does both. To run style checks only:

    ./gradlew ktlintCheck

If that fails, try formatting:

    ./gradlew ktlintFormat

Running unit tests only:

    ./gradlew :test --tests "*"

That will run tests from the desktop project only. On the other hand, this
will also run tests on briar core:

    ./gradlew test

## Testing different locales

To test the app with a different locale, add this e.g. in `Main.kt`:

```
Locale.setDefault(Locale("ar"))
```

and replace `ar` with a different language you would like to test, such as
Arabic in this example.

It is also possible to run from the command line using Gradle with a
different language setting:

```
GRADLE_OPTS="-Duser.language=fr -Duser.country=FR" ./gradlew run
```

## Threading

Some rules about threading in Briar Desktop:

* Always use local variables instead of the composable State objects inside
  another thread: [Source](https://code.briarproject.org/briar/briar-desktop/-/merge_requests/55#note_57632)

## Testing with multiple app instances

When experimenting with multiple app instances, it can make sense to
decrease the polling time in briar core so that adding contacts succeeds
quicker. In `RendezvousConstants`, change
`long POLLING_INTERVAL_MS = MINUTES.toMillis(1);`
to something lower such as
`long POLLING_INTERVAL_MS = SECONDS.toMillis(10);`.
