package org.beanmaker.v2.cli;

import org.xml.sax.SAXException;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import javax.xml.parsers.ParserConfigurationException;

import javax.xml.xpath.XPathException;

import java.io.IOException;

import java.util.concurrent.Callable;

@Command(name = "list", aliases = { "ls" }, description = "List available database configurations")
public class DatabaseListCommand implements Callable<Integer> {

    @Parameters(index = "0", defaultValue = "*", paramLabel = "<filter>", description = "Wildcard filter (optional)")
    String filter;

    @ParentCommand
    private DatabaseCommand parent;

    @Override
    public Integer call() throws XPathException, IOException, ParserConfigurationException, SAXException {
        var data = new Console(ConsoleType.DATA);
        var assetsData = new AssetsData();

        for (String code: assetsData.getCodes(filter))
            data.println(code);

        return ReturnCode.SUCCESS.code();
    }

}
