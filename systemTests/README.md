# System tests based on NixOS

For the tests, virtual machines are declaratively defined using NixOS.
They are currently only used to test native notification on XFCE with libnotify installed
and to manually test the behavior when libnotify is missing on the system.

## How-To

1. Generate the `notificationTest.jar`-file using `./gradlew :briar-desktop:notificationTest`
2. Install [Nix](https://github.com/NixOS/nix) on your local machine by executing
`sh <(curl -L https://nixos.org/nix/install) --no-daemon`
3. Navigate to the `systemTests` folder and execute
`$(nix-build -A driverInteractive vms.nix)/bin/nixos-test-driver --interactive`
(this may take some time on the first run, but will be much faster later on)
4. The interactive Python shell can be used to interact with the virtual machines that are defined in `vms.nix`

### Automatic test of notifications

Run `test_script()` in the interactive Python shell to automatically test working notifications on XFCE with libnotify installed.
The corresponding virtual machine is called `xfce` and after the test has completed, `notifications.png` will show the desktop with the Briar notifications.

### Manual test with missing libnotify

The virtual machine is called `xfce_without`.
Briar Desktop can be started from the interactive Python shell as follows,
after the Compose Jar has been generated with `./gradlew :briar-desktop:packageUberJarForCurrentOS`.

```python
xfce_without.copy_from_host("../briar-desktop/build/compose/jars/Briar-linux-x64-0.2.1-snapshot.jar", "/tmp/test.jar")
xfce_without.succeed("su - alice -c 'DISPLAY=:0.0 LD_LIBRARY_PATH=/run/current-system/sw/lib java -jar /tmp/test.jar &'")
```

