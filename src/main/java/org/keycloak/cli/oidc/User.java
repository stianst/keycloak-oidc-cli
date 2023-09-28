package org.keycloak.cli.oidc;

import org.keycloak.cli.oidc.config.Constants;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class User {

    private static CLI cli;

    private static Web web;

    public static CLI cli() {
        if (cli == null) {
            cli = new CLI();
        }
        return cli;
    }

    public static Web web() {
        if (web == null) {
            if (System.getenv().containsKey(Constants.ENV_BROWSER_CMD)) {
                web = new CmdWeb(System.getenv(Constants.ENV_BROWSER_CMD), null);
            } else {
                String osName = System.getProperty("os.name");
                if (osName.equals("Linux")) {
                    web = new LinuxWeb();
                } else if (osName.equals("Mac OS X")) {
                    web = new OSXWeb();
                } else {
                    web = new UnsupportedWeb();
                }
            }
        }
        return web;
    }

    public static class CLI {

        public void print(String... lines) {
            for (String line : lines) {
                System.out.println(line);
            }
        }

    }

    public interface Web {

        boolean isDesktopSupported();

        void browse(URI uri) throws IOException;
    }

    public static class LinuxWeb extends CmdWeb {
        public LinuxWeb() {
            super("xdg-open", "xdg-open --version");
        }
    }

    public static class OSXWeb extends CmdWeb {
        public OSXWeb() {
            super("open", null);
        }
    }

    public static class CmdWeb implements Web {

        private String cmd;
        private String supportedCmd;

        public CmdWeb(String cmd, String supportedCmd) {
            this.cmd = cmd;
            this.supportedCmd = supportedCmd;
        }

        public boolean isDesktopSupported() {
            if (supportedCmd == null) {
                return true;
            } else {
                return run(supportedCmd.split(" "));
            }
        }

        public void browse(URI uri) throws IOException {
            ArrayList<String> cmds = new ArrayList<>();
            Arrays.stream(cmd.split(" ")).forEach(c -> cmds.add(c));
            cmds.add(uri.toString());
            if (!run(cmds.toArray(new String[cmds.size()]))) {
                throw new IOException("Failed to launch browser");
            }
        }

        private static boolean run(String... cmdarray) {
            try {
                Process exec = Runtime.getRuntime().exec(cmdarray);
                exec.waitFor(5, TimeUnit.SECONDS);
                return exec.exitValue() == 0;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (IOException ioException) {
                return false;
            }
        }
    }

    public static class UnsupportedWeb implements Web {

        @Override
        public boolean isDesktopSupported() {
            return false;
        }

        @Override
        public void browse(URI uri) throws IOException {
            throw new IOException("Unsupported operating system");
        }
    }

}
