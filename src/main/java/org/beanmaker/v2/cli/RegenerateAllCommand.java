package org.beanmaker.v2.cli;

import picocli.CommandLine.Command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Command(name = "regenerate-all", aliases = { "reg-all" }, description = "Regenerate code for all beans in project")
public class RegenerateAllCommand implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {
        var msg = new Console(ConsoleType.MESSAGES);

        var data = GenerateCodeCommand.retriveData(msg);
        if (!data.isOk())
            return data.getErrorCode();
        var assetsData = data.getAssetsData();
        var projectData = data.getProjectData();
        var sourceDir = data.getSourceDir();


        var beanmakerConfigDir = projectData.getConfigDir().resolve(ConfigData.BEANMAKER_SUBDIR);
        msg.ok("Config dir: " + beanmakerConfigDir.toAbsolutePath());
        var tableList = getTableList(beanmakerConfigDir);
        for (var table : tableList) {
            msg.ok("\n*** Regenerating source code for table: " + table);
            var tableData = new TableData(table);
            tableData.makeCurrent();
            msg.notice(table + " is now the current table.");
            var columns = GenerateCodeCommand.retrieveColumns(assetsData, projectData, tableData);
            var sourceFiles = new SourceFiles(
                    new SourceFileParameters(
                            tableData.getPackageName(),
                            tableData.getBeanName(),
                            columns,
                            projectData.getProjectParameters()));
            sourceFiles.refreshFiles(sourceDir, false, msg);
        }

        return ReturnCode.SUCCESS.code();
    }

    private List<String> getTableList(Path configDir) throws IOException {
        try (var walk = Files.walk(configDir)) {
            return walk
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".xml"))
                    .map(p -> p.getFileName().toString())
                    .map(name -> name.substring(0, name.length() - 4))
                    .collect(Collectors.toList());
        }
    }

}
