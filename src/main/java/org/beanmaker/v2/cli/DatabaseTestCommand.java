package org.beanmaker.v2.cli;

import org.xml.sax.SAXException;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import javax.xml.parsers.ParserConfigurationException;

import javax.xml.xpath.XPathException;

import java.io.IOException;

import java.util.concurrent.Callable;

@Command(name = "test", description = "Test database connection")
class DatabaseTestCommand implements Callable<Integer> {

    @Parameters(index = "0", paramLabel = "<code>", description = "Code of database configuration to test")
    String code;

    @ParentCommand
    private DatabaseCommand parent;

    @Override
    public Integer call() throws XPathException, IOException, ParserConfigurationException, SAXException {
        var msg = Console.MESSAGES;
        var assetsData = new AssetsData();

        // * Check existence of config file
        if (CommandHelper.missingAssetConfiguration(assetsData, msg))
            return ReturnCode.USER_ERROR.code();

        // * Check database code
        if (CommandHelper.missingDatabaseConfiguration(assetsData, msg, code))
            return ReturnCode.USER_ERROR.code();

        // * Obtain database data
        var databaseConfig = assetsData.getDatabaseConfig(code);

        if (databaseConfig.checkConnection()) {
            msg.println(Status.OK, "Connection successful");
            return ReturnCode.SUCCESS.code();
        }

        msg.println(Status.ERROR, "Connection failed");
        return ReturnCode.SYSTEM_ERROR.code();
    }

}
