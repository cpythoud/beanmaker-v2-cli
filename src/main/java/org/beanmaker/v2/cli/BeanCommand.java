package org.beanmaker.v2.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Command(
        name = "bean",
        description = "Manage current table/bean configuration",
        subcommands = {
                BeanAddExtraFieldCommand.class,
                BeanEditExtraFieldCommand.class,
                BeanDeleteExtraFieldCommand.class,
                BeanFieldCommand.class,
                BeanAddRelationshipCommand.class,
                BeanEditRelationshipCommand.class,
                BeanDeleteRelationshipCommand.class,
                BeanItemOrderCommand.class,
                BeanShowCommand.class,
                CommandLine.HelpCommand.class
        }
)
class BeanCommand {

    @ParentCommand
    BeanmakerCommand parent;

}
