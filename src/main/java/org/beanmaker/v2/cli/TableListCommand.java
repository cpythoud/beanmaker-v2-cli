package org.beanmaker.v2.cli;

import org.xml.sax.SAXException;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import javax.xml.parsers.ParserConfigurationException;

import javax.xml.xpath.XPathException;

import java.io.IOException;

import java.util.concurrent.Callable;

@Command(name = "table-list", aliases = { "tl" }, description = "List project database tables")
public class TableListCommand implements Callable<Integer> {

    @Parameters(index = "0", defaultValue = "*", paramLabel = "<filter>", description = "Wildcard filter (optional)")
    String filter;

    @ParentCommand
    private BeanmakerCommand parent;

    @Override
    public Integer call() throws XPathException, IOException, ParserConfigurationException, SAXException {
        var msg = new Console(ConsoleType.MESSAGES);

        // * Load and check existence of asset config file
        var assetsData = new AssetsData();
        if (CommandHelper.missingAssetConfiguration(assetsData, msg))
            return ReturnCode.USER_ERROR.code();

        // * Check that we are effectively trying to edit an existing project
        var projectData = new ProjectData();
        if (CommandHelper.missingProjectConfiguration(projectData, msg, "project"))
            return ReturnCode.USER_ERROR.code();

        // * Obtain database code
        String code = projectData.getDatabase();
        if (CommandHelper.missingDatabaseConfiguration(assetsData, msg, code))
            return ReturnCode.USER_ERROR.code();

        // * Obtain database data
        var data = new Console(ConsoleType.DATA);
        var databaseConfig = assetsData.getDatabaseConfig(code);

        for (String table: databaseConfig.getTables(filter))
            data.println(table);

        return ReturnCode.SUCCESS.code();
    }

}
