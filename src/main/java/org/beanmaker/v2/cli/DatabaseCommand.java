package org.beanmaker.v2.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Command(
        name = "database",
        aliases = { "db" },
        description = "Manage database configurations",
        subcommands = {
                DatabaseAddCommand.class,
                DatabaseEditCommand.class,
                DatabaseExportCommand.class,
                DatabaseImportCommand.class,
                DatabaseListCommand.class,
                DatabaseRemoveCommand.class,
                DatabaseShowCommand.class,
                DatabaseTablesCommand.class,
                DatabaseTestCommand.class,
                CommandLine.HelpCommand.class
        }
)
class DatabaseCommand {

    @ParentCommand
    BeanmakerCommand parent;

}
