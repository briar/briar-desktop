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
GRADLE_OPTS="-Duser.language=fr -Duser.country=FR" ./gradlew pinpitRun
```

## Threading

Some rules about threading in Briar Desktop:

* Always use local variables instead of the composable State objects inside
  another
  thread: [Source](https://code.briarproject.org/briar/briar-desktop/-/merge_requests/55#note_57632)

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

## Importing Android vector drawables

There's lots of icons already existing in the Android app.
Those can be imported using [vec2compose](https://github.com/LennartEgb/vec2compose).

## Unencrypted settings

We're currently using the [Java Preferences
API](https://docs.oracle.com/javase/8/docs/api/java/util/prefs/Preferences.html)
for storing unencrypted settings. This stores some UI preferences that are
required before the database has been decrypted such as the UI language,
light/dark theme and a custom scale factor for the whole UI.
This API stores our preferences in an OS-specific manner.
On Linux they're stored in flat XML files,
on macOS they're stored in a proprietary format
and on Windows they're probably stored in the registry.

On Linux the settings are stored in an XML file at
`~/.java/.userPrefs/org/briarproject/briar/desktop/settings/prefs.xml` and can
be edited with a simple text editor. On other platforms inspecting and editing
those preferences is not as straightforward. There is however a
platform-independent UI tool called [JPUI](http://jpui.sourceforge.net/) that
allows you to inspect and edit them easily. Last release is from 2004, but it
still works ;)

There's a setting for UI previews in a separate settings node
(`~/.java/.userPrefs/org/briarproject/briar/desktop/utils/prefs.xml` on Linux).
In order to scale UI previews up on high-density devices, run
`org.briarproject.briar.desktop.SetPreviewUtilsDensity` from the test sources
once with a custom UI scale value that works on your machine.
That will persist the UI scale in the settings node so that all previews are
scaled using that factor from then on.
