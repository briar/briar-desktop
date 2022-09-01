let
  # Pin nixpkgs, see pinning tutorial for more details
  nixpkgs = fetchTarball "https://github.com/NixOS/nixpkgs/archive/8b3398bc7587ebb79f93dfeea1b8c574d3c6dba1.tar.gz";
  pkgs = import nixpkgs {};

  username = "alice";

  makeDesktopEnvironment = { envOptions }: pkgs.lib.recursiveUpdate {
    virtualisation.qemu.options = [ "-display gtk" ]; # needed to display VM in window

    services.xserver = {
      enable = true;
      desktopManager.xterm.enable = false;
      displayManager = {
        autoLogin = {
          enable = true;
          user = "${username}";
        };
      };
    };

    users = {
      mutableUsers = false;
      users = {
        "${username}" = {
          isNormalUser = true;
          description = "Alice Foobar";
          password = "foobar";
          uid = 1000;
        };
      };
    };

    environment = {
      systemPackages = [ pkgs.jdk pkgs.libnotify ];
      variables."XAUTHORITY" = "/home/alice/.Xauthority";
    };
  } envOptions;
in pkgs.nixosTest {
  system = "x86_64-linux";

  nodes = {
    xfce = { ... }: makeDesktopEnvironment {
      envOptions = {
        services.xserver = {
          displayManager.lightdm.enable = true;
          desktopManager.xfce.enable = true;
        };
      };
    };
    xfce_without = { ... }: makeDesktopEnvironment {
      envOptions = {
        services.xserver = {
          displayManager.lightdm.enable = true;
          desktopManager.xfce.enable = true;
        };
        environment.systemPackages = [ pkgs.jdk ];
      };
    };

    /*
      Unfortunately, it was not straightforward to run the jar on the following desktop environments.
      Enlightenment always starts a configuration dialog which somehow needs to be skipped,
      Gnome and Plasma doesn't show the notifications when run from the test script, but does show them when run from the terminal (Konsole).
      Additionally, Gnome only started with Wayland which is incompatible with the `wait_for_window()` function in the test script.
      Therefore these machines are disabled for now.

    enlightenment = { ... }: makeDesktopEnvironment {
      envOptions = {
        services.xserver = {
          displayManager.lightdm.enable = true;
          desktopManager.enlightenment.enable = true;
        };
      };
    };
    gnome = { ... }: makeDesktopEnvironment {
      envOptions = {
        services.xserver = {
          displayManager.gdm.enable = true;
          #displayManager.defaultSession = "gnome-xorg"; # todo: doesn't work with x11...
          desktopManager.gnome.enable = true;
        };
        services.gnome.core-utilities.enable = false; # disable gnome tools
      };
    };
    plasma = { ... }: makeDesktopEnvironment {
      envOptions = {
        services.xserver = {
          displayManager.sddm.enable = true;
          desktopManager.plasma5.enable = true;
        };
      };
    };
    */
  };

  # Disable linting for simpler debugging of the testScript
  skipLint = true;
  testScript = { nodes, ... }: let
  in ''
    def run(machine):
      machine.wait_for_x()
      machine.wait_for_window("xfce4-panel")
      machine.sleep(20)
      machine.copy_from_host("../briar-desktop/build/libs/briar-desktop-0.2.1-snapshot-notificationTest.jar", "/tmp/test.jar")
      machine.succeed("su - alice -c 'DISPLAY=:0.0 LD_LIBRARY_PATH=/run/current-system/sw/lib java -jar /tmp/test.jar &'")

    # test on system with libnotify installed
    run(xfce)
    xfce.sleep(1)
    xfce.screenshot("notifications")
    xfce.shutdown()
    '';
}
