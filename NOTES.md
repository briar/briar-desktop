# Notes on macOS

## Building

Build dmg package:

    ./gradlew -Dorg.gradle.java.home=$HOME/Library/Java/JavaVirtualMachines/openjdk-17.0.2/Contents/Home/ packageDmg

## Installing

Installing locally built packages seems to work without problems.

Installing packages downloaded from the internet however doesn't seem to work
as macOS puts apps downloaded from the internet into quarantine.
Some guides suggest it should suffice to change a setting in 'System preferences',
'Security & Privacy', 'Allow apps downloaded from'. On recent systems the option
'Anywhere' isn't even available.

It is possible to make that option reappear by running this:

    sudo spctl --master-disable

However I have found that this doesn't help on macOS Big Sur. Also disabling this security feature
globally doesn't seem like a very good idea. Maybe if it worked like this that disabling it temporarily
would only grant the permission to run to apps downloaded while disabled, it could be OK.

There is an option to grant an exception to single apps, but it hasn't worked here either:

    sudo spctl --add /Applications/Briar.app

What worked on my system was removing the extended attribute from the app after installing it:

    sudo xattr -rd com.apple.quarantine /Applications/Briar.app
