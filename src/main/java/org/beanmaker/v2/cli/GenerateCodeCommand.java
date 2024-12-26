package org.beanmaker.v2.cli;

import org.beanmaker.v2.codegen.Columns;
import org.beanmaker.v2.codegen.ExtraField;
import org.beanmaker.v2.codegen.OneToManyRelationship;

import org.xml.sax.SAXException;

import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

import javax.xml.parsers.ParserConfigurationException;

import javax.xml.xpath.XPathException;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.concurrent.Callable;

@Command(name = "generate-code", aliases = { "gc" }, description = "Generate source files for current table")
public class GenerateCodeCommand implements Callable<Integer>  {

    @Option(names = "--force-refresh", description = "force refresh of all source files, including the ones that can be edited: BE CAREFUL NOT TO LOOSE YOUR WORK")
    boolean forceRefresh;

    @ArgGroup(multiplicity = "1")
    FileSet fileSet;

    static class FileSet {
        @Option(names = { "-a", "--all" }, description = "create/refresh all source files")
        boolean all;

        @Option(names = { "-m", "--most" }, description = "create/refresh standard source files (data, editor, html views, master tables and servlets)")
        boolean most;

        @Option(names = { "-d", "--data" }, description = "create/refresh data source files (for read-only operations)")
        boolean data;

        @Option(names = { "-e", "--editors" }, description = "create/refresh editor source files (for read/write operations)")
        boolean editions;

        @Option(names = { "--hf", "--html-forms" }, description = "create/refresh html dialog source files (read/write through html forms/modals, create servlets to do form processing)")
        boolean htmlDialog;

        @Option(names = { "--mt", "--master-tables" }, description = "create/refresh master table source files (includes read operations)")
        boolean masterTables;

        @Option(names = { "-r", "--refresh" }, description = "refresh existing files")
        boolean refresh;

        @Option(names = { "-f", "--file" }, description = "create/refresh a specific file")
        String fileReference;

        @Option(names = { "--csv", "--comma-separated-values" }, description = "create/refresh CSV import source files (includes bean read and write operations)")
        boolean csv;
    }

    @ParentCommand
    BeanmakerCommand parent;

    static class Data {
        private final Integer errorCode;
        private final AssetsData assetsData;
        private final ProjectData projectData;
        private final Path sourceDir;

        private Data(int errorCode) {
            this.errorCode = errorCode;
            assetsData = null;
            projectData = null;
            sourceDir = null;
        }

        private Data(AssetsData assetsData, ProjectData projectData, Path sourceDir) {
            errorCode = null;
            this.assetsData = assetsData;
            this.projectData = projectData;
            this.sourceDir = sourceDir;
        }

        boolean isOk() {
            return errorCode == null;
        }

        Integer getErrorCode() {
            return errorCode;
        }

        AssetsData getAssetsData() {
            return assetsData;
        }

        ProjectData getProjectData() {
            return projectData;
        }

        Path getSourceDir() {
            return sourceDir;
        }
    }

    static Data retriveData(Console msg) throws XPathException, IOException, ParserConfigurationException, SAXException {
        // * Load and check existence of asset config file
        var assetsData = new AssetsData();
        if (CommandHelper.missingAssetConfiguration(assetsData, msg))
            return new Data(ReturnCode.USER_ERROR.code()) ;

        // * Load and check existence of project config file
        var projectData = new ProjectData();
        if (CommandHelper.missingProjectConfiguration(projectData, msg, "table"))
            return new Data(ReturnCode.USER_ERROR.code());

        // * Check database code
        if (CommandHelper.unknownDatabaseConfigurationInProject(assetsData, msg, projectData.getDatabase()))
            return new Data(ReturnCode.USER_ERROR.code());

        // * Verify source dir exists and is writable
        var sourceDir = Path.of(projectData.getGenSourceDir());
        if (!Files.exists(sourceDir)) {
            msg.status(Status.ERROR)
                    .printStatus()
                    .println("Source directory " + projectData.getGenSourceDir() + " does not exists.");
            msg.status(Status.WARNING)
                    .print("You need to create the directory " + projectData.getGenSourceDir() + " or use the ")
                    .print("project", Console.COMMAND_STYLE)
                    .println(" command to change the source directory.");
            return new Data(ReturnCode.USER_ERROR.code());
        }
        if (!Files.isDirectory(sourceDir)) {
            msg.status(Status.ERROR)
                    .printStatus()
                    .println(projectData.getGenSourceDir() + " is not a directory.");
            CommandHelper.askToFixSourceDir(msg);
            return new Data(ReturnCode.USER_ERROR.code());
        }
        if (!Files.isWritable(sourceDir)) {
            msg.status(Status.ERROR)
                    .printStatus()
                    .println("It's not possible to write in directory " + projectData.getGenSourceDir() + ".");
            CommandHelper.askToFixSourceDir(msg);
            return new Data(ReturnCode.USER_ERROR.code());
        }

        return new Data(assetsData, projectData, sourceDir);
    }

    static Columns retrieveColumns(AssetsData assetsData, ProjectData projectData, TableData tableData) throws XPathException, IOException, ParserConfigurationException, SAXException {
        var columns = assetsData.getDatabaseConfig(projectData.getDatabase()).getColumns(tableData.getName());

        // * Use configuration data to modify columns data
        int[] counter = { 0 };
        for (var column: columns.getList()) {
            int index = ++counter[0];
            if (column.isSpecial()) {
                if (column.isItemOrder() && tableData.hasItemOrderAssociatedField())
                    columns.setItemOrderAssociatedField(index, tableData.getItemOrderAssociatedField());
                continue;
            }
            tableData.getCustomizedFieldConfiguration(column.getSqlName()).ifPresent(fieldConfig -> {
                columns.setJavaType(index, fieldConfig.getJavaType());
                columns.setJavaName(index, fieldConfig.getJavaName());
                columns.setRequired(index, fieldConfig.isRequired());
                columns.setUnique(index, fieldConfig.isUnique());
                if (fieldConfig.getAssociatedBeanClass() != null)
                    columns.setAssociatedBeanClass(index, fieldConfig.getAssociatedBeanClass());
            });
        }
        for (var relationship: tableData.getRelationships())
            columns.addOneToManyRelationship(new OneToManyRelationship(
                    relationship.javaType(),
                    relationship.javaName(),
                    relationship.table(),
                    relationship.idField()
            ));
        for (var extraField: tableData.getExtrafields())
            columns.addExtraField(
                    ExtraField.builder(extraField.getJavaType(), extraField.getJavaName())
                            .initializationExpression(extraField.getInitialization())
                            .isFinal(extraField.isFinal())
                            .addImports(extraField.getImports())
                            .create());

        return columns;
    }

    @Override
    public Integer call() throws XPathException, IOException, ParserConfigurationException, SAXException {
        var msg = new Console(ConsoleType.MESSAGES);

        var data = retriveData(msg);
        if (!data.isOk())
            return data.errorCode;
        var assetsData = data.assetsData;
        var projectData = data.projectData;
        var sourceDir = data.sourceDir;


        // * Retrieve columns data & configuration data
        var tableData = CommandHelper.checkAndRetrieveTableData(msg).orElse(null);
        if (tableData == null)
            return ReturnCode.USER_ERROR.code();
        var columns = retrieveColumns(assetsData, projectData, tableData);

        // * Generate classes
        var sourceFiles = new SourceFiles(
                new SourceFileParameters(
                        tableData.getPackageName(),
                        tableData.getBeanName(),
                        columns,
                        projectData.getProjectParameters()));

        // * Create source files skipping already existing editable classes unless --force-refresh is set
        if (fileSet.all)
            writeFiles(SourceFileList.ALL, sourceFiles, sourceDir, msg);
        else if (fileSet.most)
            writeFiles(SourceFileList.MOST, sourceFiles, sourceDir, msg);
        else if (fileSet.data)
            writeFiles(SourceFileList.DATA, sourceFiles, sourceDir, msg);
        else if (fileSet.editions)
            writeFiles(SourceFileList.EDITION, sourceFiles, sourceDir, msg);
        else if (fileSet.htmlDialog)
            writeFiles(SourceFileList.HTML_FORMS, sourceFiles, sourceDir, msg);
        else if (fileSet.csv)
            writeFiles(SourceFileList.CSV, sourceFiles, sourceDir, msg);
        else if (fileSet.masterTables)
            writeFiles(SourceFileList.MASTER_TABLES, sourceFiles, sourceDir, msg);
        else if (fileSet.refresh)
            sourceFiles.refreshFiles(sourceDir, forceRefresh, msg);
        else if (fileSet.fileReference != null) {
            if (!writeSingleFile(sourceFiles, sourceDir, msg))
                return ReturnCode.USER_ERROR.code();
        } else
            throw new AssertionError("Missing parameter: impossible situation");

        // * Exit
        msg.ok("All requested source files have been (re)created."
                + (forceRefresh ? "" : " Existing editable files were not overwritten."));
        return ReturnCode.SUCCESS.code();
    }

    private void writeFiles(SourceFileList sourceFileList, SourceFiles sourceFiles, Path sourceDir, Console msg) throws IOException {
        for (String fileReference: sourceFileList.getEditableFiles())
            sourceFiles.writeFile(fileReference, sourceDir, forceRefresh, msg);
        for (String fileReference: sourceFileList.getNonEditableFiles())
            sourceFiles.writeFile(fileReference, sourceDir, true, msg);
    }

    private boolean writeSingleFile(SourceFiles sourceFiles, Path sourceDir, Console msg) throws IOException {
        var allReferences = SourceFileList.ALL.getAllFiles();
        if (allReferences.contains(fileSet.fileReference)) {
            boolean overwrite = forceRefresh || SourceFileList.ALL.getNonEditableFiles().contains(fileSet.fileReference);
            sourceFiles.writeFile(fileSet.fileReference, sourceDir, overwrite, msg);
            return true;
        }

        msg.error("No file reference with name: " + fileSet.fileReference);
        msg.status(Status.WARNING)
                .println("File reference must be one of: " + String.join(", ", allReferences));
        return false;
    }

}
