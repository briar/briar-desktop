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

    /*
      Unfortunately, it was not straightforward to run the jar on the Enlightenment desktop environment.
      It always starts a configuration dialog which somehow needs to be skipped.
      Therefore the following machine is disabled for now.

    enlightenment = { ... }: makeDesktopEnvironment {
      envOptions = {
        services.xserver = {
          displayManager.lightdm.enable = true;
          desktopManager.enlightenment.enable = true;
        };
      };
    };
    */
  };

  # Disable linting for simpler debugging of the testScript
  skipLint = true;
  testScript = { nodes, ... }: let
  in ''
    # test on systems with libnotify installed

    xfce.wait_for_x()
    xfce.wait_for_window("xfce4-panel")
    xfce.sleep(10)
    xfce.copy_from_host("../briar-desktop/build/libs/notificationTest.jar", "/tmp/test.jar")
    xfce.succeed("su - alice -c 'DISPLAY=:0.0 LD_LIBRARY_PATH=/run/current-system/sw/lib java -jar /tmp/test.jar &'")
    xfce.sleep(1)
    xfce.screenshot("notifications_xfce")
    xfce.shutdown()

    gnome.wait_for_x()
    gnome.sleep(5)
    gnome.send_key( 'esc' )
    gnome.sleep(5)
    gnome.copy_from_host("../briar-desktop/build/libs/notificationTest.jar", "/tmp/test.jar")
    gnome.copy_from_host("gnome.sh", "/tmp/")
    gnome.succeed("chown alice:users /tmp/gnome.sh")
    gnome.succeed("su - alice -c '/tmp/gnome.sh &'")
    gnome.sleep(1)
    gnome.screenshot("notifications_gnome")
    gnome.shutdown()

    plasma.wait_for_x()
    plasma.wait_for_window("Plasma")
    plasma.sleep(5)
    plasma.copy_from_host("../briar-desktop/build/libs/notificationTest.jar", "/tmp/test.jar")
    plasma.copy_from_host("plasma.sh", "/tmp/")
    plasma.succeed("chown alice:users /tmp/plasma.sh")
    plasma.succeed("su - alice -c '/tmp/plasma.sh &'")
    plasma.sleep(1)
    plasma.screenshot("notifications_plasma")
    plasma.shutdown()
    '';
}
