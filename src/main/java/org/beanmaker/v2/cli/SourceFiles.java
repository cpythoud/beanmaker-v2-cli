package org.beanmaker.v2.cli;

import org.beanmaker.v2.codegen.BaseServletSourceFile;
import org.beanmaker.v2.codegen.BeanBaseSourceFile;
import org.beanmaker.v2.codegen.BeanCsvImportBaseSourceFile;
import org.beanmaker.v2.codegen.BeanCsvImportSourceFile;
import org.beanmaker.v2.codegen.BeanEditorBaseSourceFile;
import org.beanmaker.v2.codegen.BeanEditorSourceFile;
import org.beanmaker.v2.codegen.BeanFormatterBaseSourceFile;
import org.beanmaker.v2.codegen.BeanFormatterInterfaceBaseSourceCode;
import org.beanmaker.v2.codegen.BeanFormatterInterfaceSourceCode;
import org.beanmaker.v2.codegen.BeanFormatterSourceFile;
import org.beanmaker.v2.codegen.BeanHTMLViewBaseSourceFile;
import org.beanmaker.v2.codegen.BeanHTMLViewSourceFile;
import org.beanmaker.v2.codegen.BeanMakerSourceFile;
import org.beanmaker.v2.codegen.BeanMasterTableViewBaseSourceFile;
import org.beanmaker.v2.codegen.BeanMasterTableViewSourceFile;
import org.beanmaker.v2.codegen.BeanParametersBaseSourceFile;
import org.beanmaker.v2.codegen.BeanParametersSourceFile;
import org.beanmaker.v2.codegen.BeanServletBaseSourceFile;
import org.beanmaker.v2.codegen.BeanServletSourceFile;
import org.beanmaker.v2.codegen.BeanSourceFile;
import org.beanmaker.v2.codegen.DbBeanSourceFile;
import org.beanmaker.v2.codegen.FormattedBeanDataBaseSourceFile;
import org.beanmaker.v2.codegen.FormattedBeanDataSourceFile;
import org.beanmaker.v2.codegen.HTMLViewSourceFile;
import org.beanmaker.v2.codegen.LabelManagerSourceFile;
import org.beanmaker.v2.codegen.LocalCsvImportSourceFile;
import org.beanmaker.v2.codegen.LocalDbBeanFormatterSourceFile;
import org.beanmaker.v2.codegen.LocalFileManagerSourceFile;
import org.beanmaker.v2.codegen.LocalMasterTableViewSourceFile;
import org.beanmaker.v2.codegen.WebXMLConfig;
import org.beanmaker.v2.codegen.XMLConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

class SourceFiles {

    private static final Map<String, Function<SourceFileParameters, BeanMakerSourceFile>> CREATION_FUNCTIONS;

    static {
        CREATION_FUNCTIONS = new LinkedHashMap<>();

        CREATION_FUNCTIONS.put(
                "DbBeanSource",
                parameters -> new DbBeanSourceFile(parameters.columns().getDb(), parameters.packageName())
        );
        CREATION_FUNCTIONS.put(
                "LabelManagerSource",
                parameters -> new LabelManagerSourceFile(parameters.packageName())
        );
        CREATION_FUNCTIONS.put(
                "LocalFileManagerSource",
                parameters -> new LocalFileManagerSourceFile(parameters.packageName())
        );
        CREATION_FUNCTIONS.put(
                "BeanBaseSource",
                parameters -> new BeanBaseSourceFile(parameters.beanName(), parameters.packageName(), parameters.columns())
        );
        CREATION_FUNCTIONS.put(
                "BeanSource",
                parameters -> new BeanSourceFile(parameters.beanName(), parameters.packageName())
        );
        CREATION_FUNCTIONS.put(
                "BeanEditorBaseSource",
                parameters -> new BeanEditorBaseSourceFile(parameters.beanName(), parameters.packageName(), parameters.columns())
        );
        CREATION_FUNCTIONS.put(
                "BeanEditorSource",
                parameters -> new BeanEditorSourceFile(parameters.beanName(), parameters.packageName(), parameters.columns())
        );
        CREATION_FUNCTIONS.put(
                "BeanParametersBaseSource",
                parameters -> new BeanParametersBaseSourceFile(parameters.beanName(), parameters.packageName(), parameters.columns())
        );
        CREATION_FUNCTIONS.put(
                "BeanParametersSource",
                parameters -> new BeanParametersSourceFile(parameters.beanName(), parameters.packageName())
        );
        CREATION_FUNCTIONS.put(
                "LocalDbBeanFormatterSource",
                parameters -> new LocalDbBeanFormatterSourceFile(parameters.packageName())
        );
        CREATION_FUNCTIONS.put(
                "BeanFormatterBaseSource",
                parameters -> new BeanFormatterBaseSourceFile(parameters.beanName(), parameters.packageName(), parameters.columns())
        );
        CREATION_FUNCTIONS.put(
                "BeanFormatterSource",
                parameters -> new BeanFormatterSourceFile(parameters.beanName(), parameters.packageName())
        );
        CREATION_FUNCTIONS.put(
                "BeanFormatterInterfaceBaseSource",
                parameters -> new BeanFormatterInterfaceBaseSourceCode(parameters.beanName(), parameters.packageName(), parameters.columns())
        );
        CREATION_FUNCTIONS.put(
                "BeanFormatterInterfaceSource",
                parameters -> new BeanFormatterInterfaceSourceCode(parameters.beanName(), parameters.packageName(), parameters.columns())
        );
        CREATION_FUNCTIONS.put(
                "FormattedBeanDataBaseSource",
                parameters -> new FormattedBeanDataBaseSourceFile(parameters.beanName(), parameters.packageName(), parameters.columns())
        );
        CREATION_FUNCTIONS.put(
                "FormattedBeanDataSource",
                parameters -> new FormattedBeanDataSourceFile(parameters.beanName(), parameters.packageName())
        );
        CREATION_FUNCTIONS.put(
                "HTMLViewSource",
                parameters -> new HTMLViewSourceFile(parameters.packageName())
        );
        CREATION_FUNCTIONS.put(
                "BeanHTMLViewBaseSource",
                parameters -> new BeanHTMLViewBaseSourceFile(parameters.beanName(), parameters.packageName(), parameters.columns())
        );
        CREATION_FUNCTIONS.put(
                "BeanHTMLViewSource",
                parameters -> new BeanHTMLViewSourceFile(parameters.beanName(), parameters.packageName())
        );
        CREATION_FUNCTIONS.put(
                "LocalMasterTableViewSource",
                parameters -> new LocalMasterTableViewSourceFile(parameters.packageName())
        );
        CREATION_FUNCTIONS.put(
                "BeanMasterTableViewBaseSource",
                parameters -> new BeanMasterTableViewBaseSourceFile(parameters.beanName(), parameters.packageName(), parameters.columns())
        );
        CREATION_FUNCTIONS.put(
                "BeanMasterTableViewSource",
                parameters -> new BeanMasterTableViewSourceFile(parameters.beanName(), parameters.packageName())
        );
        CREATION_FUNCTIONS.put(
                "BaseServlet",
                parameters -> new BaseServletSourceFile(parameters.packageName())
        );
        CREATION_FUNCTIONS.put(
                "BeanServletBaseSource",
                parameters -> new BeanServletBaseSourceFile(parameters.beanName(), parameters.packageName(), parameters.columns())
        );
        CREATION_FUNCTIONS.put(
                "BeanServletSource",
                parameters -> new BeanServletSourceFile(parameters.beanName(), parameters.packageName())
        );
        CREATION_FUNCTIONS.put(
                "XMLConfigSource",
                parameters -> new XMLConfig(parameters.beanName(), parameters.packageName(), parameters.columns())
        );
        CREATION_FUNCTIONS.put(
                "WebXMLConfigSource",
                parameters -> new WebXMLConfig(parameters.beanName(), parameters.packageName())
        );
        CREATION_FUNCTIONS.put(
                "BeanCsvImportSource",
                parameters -> new BeanCsvImportSourceFile(parameters.beanName(), parameters.packageName())
        );
        CREATION_FUNCTIONS.put(
                "BeanCsvImportBaseSource",
                parameters -> new BeanCsvImportBaseSourceFile(parameters.beanName(), parameters.packageName(), parameters.columns())
        );
        CREATION_FUNCTIONS.put(
                "LocalCsvImportSource",
                parameters -> new LocalCsvImportSourceFile(parameters.packageName())
        );
    }

    private final Map<String, BeanMakerSourceFile> sourceFiles = new LinkedHashMap<>();
    private final SourceFileParameters parameters;

    SourceFiles(SourceFileParameters parameters) {
        this.parameters = parameters;
        for (String code: CREATION_FUNCTIONS.keySet())
            sourceFiles.put(code, CREATION_FUNCTIONS.get(code).apply(parameters));
    }

    void writeFile(String fileReference, Path sourceDir, boolean overwrite, Console msg) throws IOException {
        if (!sourceFiles.containsKey(fileReference))
            throw new IllegalArgumentException("No file associated with reference: " + fileReference);

        Path packagePath = resolvePackagePath(sourceDir, msg);

        var nonEditableFiles = SourceFileList.ALL.getNonEditableFiles();
        var sourceFileData = sourceFiles.get(fileReference);
        String filename = sourceFileData.getFilename();
        var sourceFile = packagePath.resolve(filename);
        if (!Files.exists(sourceFile)) {
            Files.writeString(sourceFile, sourceFileData.getSourceCode());
            msg.ok("Created source file: " + filename);
        } else if (nonEditableFiles.contains(fileReference)) {
            Files.writeString(sourceFile, sourceFileData.getSourceCode());
            msg.ok("Overwrote source file: " + filename);
        } else if (overwrite) {
            Files.writeString(sourceFile, sourceFileData.getSourceCode());
            msg.warning("Overwrote editable source file: " + filename);
        } else
            msg.notice("Skipped existing file: " + filename);
    }

    private Path resolvePackagePath(Path sourceDir, Console msg) throws IOException {
        Path current = sourceDir;

        for (String packageElement: parameters.getPackageElements()) {
            current = current.resolve(packageElement);
            if (!Files.exists(current)) {
                Files.createDirectory(current);
                msg.notice("Created directory " + current);
            }
        }

        return current;
    }

    void refreshFiles(Path sourceDir, boolean overwrite, Console msg) throws IOException {
        Path packagePath = resolvePackagePath(sourceDir, msg);
        var nonEditableFiles = SourceFileList.ALL.getNonEditableFiles();

        for (var fileReference: SourceFileList.ALL.getAllFiles()) {
            var sourceFileData = sourceFiles.get(fileReference);
            String filename = sourceFileData.getFilename();
            var sourceFile = packagePath.resolve(filename);
            if (Files.exists(sourceFile)) {
                if (nonEditableFiles.contains(fileReference)) {
                    Files.writeString(sourceFile, sourceFileData.getSourceCode());
                    msg.ok("Overwrote source file: " + filename);
                } else {
                    if (overwrite) {
                        Files.writeString(sourceFile, sourceFileData.getSourceCode());
                        msg.warning("Overwrote source file: " + filename);
                    } else
                        msg.notice("Skipped existing file: " + filename);
                }
            }
        }
    }

}
