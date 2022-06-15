# Hacking guide

## Running style checks and unit tests

When working on the code base, consider running the style checks and unit
tests locally, because the CI will complain anyway:

    ./gradlew briar-desktop:check

The above command does both. To run style checks only:

    ./gradlew ktlintCheck

If that fails, try formatting:

    ./gradlew ktlintFormat

Running unit tests only:

    ./gradlew briar-desktop:test --tests "*"

That will run tests from the desktop project only. On the other hand, this
will also run tests on briar core:

    ./gradlew test

## Testing different locales

You can simply switch the language in the settings screen of the application.

To test the app with a different *default* locale, add this e.g. in `Main.kt`:

```
Locale.setDefault(Locale("ar"))
```

and replace `ar` with a different language you would like to test, such as
Arabic in this example.

It is also possible to run from the command line using Gradle with a
different *default* language setting:

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

## Updating Copyright headers

In IntelliJ, right click the root of the project tree and select
"Update Copyright". In the dialog choose "Custom scope" and select
"briar-desktop" and confirm "OK".

This applies the copyright profile configured in
`Settings → Editor → Copyright → Copyright Profiles`
in the scope which is configured in
`Settings → Appearance & Behavior → Scopes`.

Which copyright profile is applied in which scope can be configured in
`Settings → Editor → Copyright`.
