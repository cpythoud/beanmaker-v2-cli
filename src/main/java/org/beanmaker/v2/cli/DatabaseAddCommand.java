package org.beanmaker.v2.cli;

import org.xml.sax.SAXException;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathException;

import java.io.IOException;

import java.util.concurrent.Callable;

@Command(name = "add", description = "Add a database configuration to main assets file")
class DatabaseAddCommand implements Callable<Integer> {

    @Option(names = "--type", paramLabel = "<type>", defaultValue = "MYSQL", description = "database type (default: ${DEFAULT-VALUE})")
    DatabaseType databaseType;

    @Option(names = "--server", paramLabel = "<server>", required = true, description = "database server FQDN")
    String server;

    @Option(names = "--port", paramLabel = "<port>", defaultValue = "3306", description = "port on which the database listens (default: ${DEFAULT-VALUE})")
    int port;

    @Option(names = "--database", paramLabel = "<database>", required = true, description = "name of database to connect to")
    String database;

    @Option(names = "--user", paramLabel = "<user>", required = true, description = "user account to connect to the database server")
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
            description = "password to connect to database server; will only be stored in config file if a passphrase has been provided to encrypt it"
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

    @Parameters(index = "0", paramLabel = "<code>", description = "Code to uniquely identify the database configuration")
    String code;

    @ParentCommand
    private DatabaseCommand parent;

    @Override
    public Integer call() throws IOException, ParserConfigurationException, SAXException, XPathException {
        var out = Console.INSTANCE;
        var assetsData = new AssetsData();

        if (assetsData.hasDatabaseWithCode(code)) {
            out.print(Status.ERROR, "a database configuration with code '" + code
                    + "' already exists. You need to use a different code or use the ", true)
                    .print(Status.ERROR, "edit", Console.COMMAND_STYLE)
                    .println(Status.ERROR, " command to change the database configuration '" + code + "'.");
            return ReturnCode.USER_ERROR.code();
        }

        var databaseConfig = new DatabaseConfig(code);
        databaseConfig.setType(databaseType);
        databaseConfig.setServer(server);
        databaseConfig.setPort(port);
        databaseConfig.setDatabase(database);
        databaseConfig.setUser(user);

        PasswordConfig passwordConfig;
        if (cleartextPassword == null && password == null) {
            passwordConfig = PasswordConfig.promptOnly();
            if (passphrase != null)
                out.println(Status.NOTICE, "--passphrase option is not used by the command. Interactive mode selected by default.");
        } else {
            if (cleartextPassword != null) {
                passwordConfig = PasswordConfig.clearText(cleartextPassword);
                if (passphrase != null)
                    out.println(Status.NOTICE, "--passphrase option is not used by the command, since you choosed to store the password in clear text.");
            } else {
                if (passphrase == null)
                    passwordConfig = PasswordConfig.encrypted(password);
                else
                    passwordConfig = PasswordConfig.encrypted(password, passphrase);
            }
        }
        databaseConfig.setPasswordConfig(passwordConfig);

        if (ssh != null) {
            // TODO: check SSH code and warn if invalid
            databaseConfig.setSshCode(ssh);
        }

        assetsData.addDatabaseConfig(databaseConfig);
        boolean newConfig = assetsData.writeConfigFile();
        if (newConfig)
            out.println(Status.NOTICE, "a new asset configuration file has been created in " + assetsData.getConfigFileFullPath(), true);

        out.println(Status.OK, "Database configuration added successfully to asset configuration file in " + assetsData.getConfigFileFullPath());
        return ReturnCode.SUCCESS.code();
    }

}
