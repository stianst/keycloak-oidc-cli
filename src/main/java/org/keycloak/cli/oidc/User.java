package org.keycloak.cli.oidc;

import java.io.IOException;
import java.net.URI;
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
            String osName = System.getProperty("os.name");
            if (osName.equals("Linux")) {
                web = new LinuxWeb();
            } if (osName.equals("Mac OS X")) {
                web = new OSXWeb();
            } else {
                web = new UnsupportedWeb();
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

    public static class LinuxWeb implements Web {

        Boolean supported;

        public boolean isDesktopSupported() {
            if (supported == null) {
                supported = run("xdg-open", "--version");
            }
            return supported;
        }

        public void browse(URI uri) throws IOException {
            if (!run("xdg-open", uri.toString())) {
                throw new IOException("Failed to launch browser");
            }
        }
    }

    public static class OSXWeb implements Web {

        public boolean isDesktopSupported() {
            return true;
        }

        public void browse(URI uri) throws IOException {
            if (!run("open", uri.toString())) {
                throw new IOException("Failed to launch browser");
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
