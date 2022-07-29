package org.beanmaker.v2.cli;

import org.xml.sax.SAXException;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathException;

import java.io.IOException;

import java.util.concurrent.Callable;

@Command(name = "show", description = "Show the details of a database configuration")
public class DatabaseShowCommand implements Callable<Integer> {

    @Parameters(index = "0", paramLabel = "<code>", description = "Code to identify the database configuration")
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

        // * Display database data
        var databaseConfig = assetsData.getDatabaseConfig(code);
        var data = new Console(ConsoleType.DATA);
        data.println(databaseConfig.getTabularRepresentation());

        return ReturnCode.SUCCESS.code();
    }

}
