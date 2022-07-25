package org.beanmaker.v2.cli;

import org.xml.sax.SAXException;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import javax.xml.parsers.ParserConfigurationException;

import javax.xml.xpath.XPathException;

import java.io.IOException;

import java.nio.file.Path;

import java.util.concurrent.Callable;

@Command(name = "edit", description = "Edit a database configuration already present in the main assets file")
class DatabaseEditCommand implements Callable<Integer> {

    @Option(names = "--type", paramLabel = "<type>", description = "change database type")
    DatabaseType databaseType;

    @Option(names = "--server", paramLabel = "<server>", description = "database server FQDN")
    String server;

    @Option(names = "--port", paramLabel = "<port>", description = "port on which the database listens")
    int port = -1;

    @Option(names = "--database", paramLabel = "<database>", description = "name of database to connect to")
    String database;

    @Option(names = "--user", paramLabel = "<user>", description = "user account to connect to the database server")
    String user;

    @Option(
            names = "--cleartext-password",
            paramLabel = "<password>",
            description = "ONLY USE FOR ACCOUNTS WITH MINIMAL PRIVILEGES -- password to connect to database server, stored in clear text in configuration file"
    )
    char[] cleartextPassword;

    @Option(
            names = { "-p", "--password" },
            paramLabel = "<password>",
            interactive = true,
            description = "to be used with --passphrase option; password will be stored encrypted in config file"
    )
    char[] password;

    @Option(
            names = { "--pp", "--passphrase" },
            paramLabel = "<passphrase>",
            interactive = true,
            description = "passphrase used to encrypt/decrypt passwords in configuration files"
    )
    char[] passphrase;

    @Option(names = "--ssh", paramLabel = "<code>", description = "reference an SSH tunnel configuration to connect to the server")
    String ssh;

    @Option(names = "--no-ssh", description = "remove referenced SSH tunnel configuration")
    boolean noSSH;  // TODO: implement

    @Parameters(index = "0", paramLabel = "<code>", description = "Code to uniquely identify the database configuration")
    String code;

    @ParentCommand
    private DatabaseCommand parent;

    @Override
    public Integer call() throws XPathException, IOException, ParserConfigurationException, SAXException {
        var out = Console.MESSAGES;
        var assetsData = new AssetsData();

        // * Check existence of config file
        if (!assetsData.hasConfigFile()) {
            out.print(Status.ERROR, "there is no " + assetsData.getConfigFilename()
                    + " in your home directory. To automatically create one, use either the ", true)
                    .print(Status.ERROR, "ssh add", Console.COMMAND_STYLE)
                    .print(Status.ERROR, " or ")
                    .print(Status.ERROR, "database add", Console.COMMAND_STYLE)
                    .println(Status.ERROR, " command. Or if you have a " + assetsData.getConfigFilename()
                            + " somewhere else, please move it to your home directory ("
                            + Path.of(System.getProperty("user.home")) + ").");
            return ReturnCode.USER_ERROR.code();
        }

        // * Check database code
        if (!assetsData.hasDatabaseWithCode(code)) {
            out.print(Status.ERROR, "no database configuration with code '" + code
                            + "' exists. You might want to use the ", true)
                    .print(Status.ERROR, "database list", Console.COMMAND_STYLE)
                    .print(Status.ERROR, " command to view a list of available database configuration or use the ")
                    .print(Status.ERROR, "database add", Console.COMMAND_STYLE)
                    .println(Status.ERROR, " command to create a new configuration.");
            return ReturnCode.USER_ERROR.code();
        }

        // * Obtain database data
        var databaseConfig = assetsData.getDatabaseConfig(code);

        // * No option passed, notify and exit
        if (databaseType == null && server == null && port == -1 && database == null && user == null && cleartextPassword == null && password == null && passphrase == null) {
            out.print(Status.NOTICE, "no option has been provided. Configuration unchanged. To see the configuration use the ", true)
                    .print(Status.NOTICE, "database show " + code, Console.COMMAND_STYLE)
                    .println(Status.NOTICE, " command.");
            return ReturnCode.SUCCESS.code();
        }

        // * For each option present, record change and see if config has been modified
        boolean configChanged = false;
        if (databaseType != null)
            configChanged = databaseConfig.changeType(databaseType);
        if (server != null)
            configChanged = databaseConfig.changeServer(server) || configChanged;
        if (port != -1)
            configChanged = databaseConfig.changePort(port) || configChanged;
        if (database != null)
            configChanged = databaseConfig.changeDatabase(database) || configChanged;
        if (user != null)
            configChanged = databaseConfig.changeUser(user) || configChanged;
        if (cleartextPassword != null || password != null)
            configChanged = databaseConfig.changePasswordConfig(PasswordConfig.fromCommandOptions(out, cleartextPassword, password, passphrase))
                    || configChanged;
        if (ssh != null) {
            // TODO: check SSH code and warn if invalid
            configChanged = databaseConfig.changeSshCode(ssh) || configChanged;
        }

        // * If options do not change configuration, notify and exit
        if (!configChanged) {
            out.print(Status.NOTICE, "no difference with previous database configuration. " +
                            "Configuration file was not modified. To see the configuration use the ", true)
                    .print(Status.NOTICE, "database show " + code, Console.COMMAND_STYLE)
                    .println(Status.NOTICE, " command.");
            return ReturnCode.SUCCESS.code();
        }

        // * Write new configuration to file
        assetsData.writeConfigFile();
        out.println(Status.OK, "Database configuration was updated successfully. " );
        return ReturnCode.SUCCESS.code();
    }

}
