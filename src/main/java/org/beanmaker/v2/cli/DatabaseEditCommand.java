package org.beanmaker.v2.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import java.util.concurrent.Callable;

@Command(name = "edit", description = "Edit a database configuration already present in the main assets file")
class DatabaseEditCommand implements Callable<Integer> {

    @Option(names = "--type", paramLabel = "<type>", description = "change database type")
    DatabaseType databaseType;

    @Option(names = "--server", paramLabel = "<server>", description = "database server FQDN")
    String server;

    @Option(names = "--port", paramLabel = "<port>", description = "port on which the database listens")
    int port;

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
    boolean noSSH;

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
