package org.beanmaker.v2.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

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
    public Integer call() {
        // TODO: implement subcommand here ;-)
        if (passphrase == null)
            System.out.println("No PP");
        else
            System.out.println("PP found");
        return ReturnCode.SUCCESS.code();
    }

}
