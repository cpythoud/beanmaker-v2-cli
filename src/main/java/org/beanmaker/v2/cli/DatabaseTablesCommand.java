package org.beanmaker.v2.cli;

import org.xml.sax.SAXException;
import picocli.CommandLine;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathException;
import java.io.IOException;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "tables", description = "List tables belonging to the database")
public class DatabaseTablesCommand implements Callable<Integer> {

    @CommandLine.Parameters(index = "0", paramLabel = "<code>", description = "Code to uniquely identify the database configuration")
    String code;

    @CommandLine.Parameters(index = "1", defaultValue = "*", paramLabel = "<filter>", description = "Wildcard filter (optional)")
    String filter;

    @CommandLine.ParentCommand
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
        var data = Console.DATA;
        var databaseConfig = assetsData.getDatabaseConfig(code);

        for (String table: databaseConfig.getTables(filter))
            data.println(table);

        return ReturnCode.SUCCESS.code();
    }

}
