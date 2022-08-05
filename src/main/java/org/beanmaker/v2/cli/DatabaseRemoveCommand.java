package org.beanmaker.v2.cli;

import org.xml.sax.SAXException;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import javax.xml.parsers.ParserConfigurationException;

import javax.xml.xpath.XPathException;

import java.io.IOException;

import java.util.concurrent.Callable;

@Command(name = "remove", aliases = { "rm" }, description = "Remove database configuration(s)")
public class DatabaseRemoveCommand implements Callable<Integer> {

    @Parameters(index = "0", paramLabel = "<code>", description = "code of configuration to delete")
    String code;

    @ParentCommand
    private DatabaseCommand parent;

    @Override
    public Integer call() throws XPathException, IOException, ParserConfigurationException, SAXException {
        var msg = new Console(ConsoleType.MESSAGES);
        var assetsData = new AssetsData();

        // * Check existence of config file
        if (CommandHelper.missingAssetConfiguration(assetsData, msg))
            return ReturnCode.USER_ERROR.code();

        // * Check database code
        if (CommandHelper.missingDatabaseConfiguration(assetsData, msg, code))
            return ReturnCode.USER_ERROR.code();

        // * Remove database data
        if (CommandHelper.confirm("Are you sure you want to delete database configuration '" + code + "'?", msg)) {
            assetsData.removeDatabaseConfig(code);
            assetsData.writeConfigFile();
            msg.ok("Database configuration was removed successfully. " );
        } else
            msg.notice("Database configuration NOT removed");

        return ReturnCode.SUCCESS.code();
    }

}
