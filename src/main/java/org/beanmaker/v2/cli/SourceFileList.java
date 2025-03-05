package org.beanmaker.v2.cli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

enum SourceFileList {
    ALL(FileLists.ALL_FILES_EDITABLE, FileLists.ALL_FILES_NON_EDITABLE),
    MOST(FileLists.MOST_FILES_EDITABLE, FileLists.MOST_FILES_NON_EDITABLE),
    DATA(FileLists.DATA_FILES_EDITABLE, FileLists.DATA_FILES_NON_EDITABLE),
    EDITION(FileLists.EDITION_FILES_EDITABLE, FileLists.EDITION_FILES_NON_EDITABLE),
    HTML_FORMS(FileLists.HTML_FILES_EDITABLE, FileLists.HTML_FILES_NON_EDITABLE),
    CSV(FileLists.CSV_FILES_EDITABLE, FileLists.CSV_FILES_NON_EDITABLE),
    MASTER_TABLES(FileLists.MASTER_TABLES_EDITABLE, FileLists.MASTER_TABLES_NON_EDITABLE),
    HTML_WRAPPER(FileLists.HTML_WRAPPER_FILES_EDITABLE, FileLists.HTML_WRAPPER_FILES_NON_EDITABLE);

    private final List<String> editableFiles;
    private final List<String> nonEditableFiles;

    SourceFileList(List<String> editableFiles, List<String> nonEditableFiles) {
        this.editableFiles = Collections.unmodifiableList(editableFiles);
        this.nonEditableFiles = Collections.unmodifiableList(nonEditableFiles);
    }

    List<String> getEditableFiles() {
        return editableFiles;
    }

    List<String> getNonEditableFiles() {
        return nonEditableFiles;
    }

    List<String> getAllFiles() {
        var list = new ArrayList<>(editableFiles);
        list.addAll(nonEditableFiles);
        return list;
    }

    private static class FileLists {  // * Lists must be in inner class, otherwise we have a forward reference problem
        private static final List<String> ALL_FILES_EDITABLE = Arrays.asList("DbBeanSource", "LabelManagerSource",
                "LocalFileManagerSource", "BeanSource", "BeanEditorSource", "BeanParametersSource",
                "LocalDbBeanFormatterSource", "FormattedBeanDataSource", "HTMLViewSource", "BeanHTMLViewSource",
                "LocalMasterTableViewSource", "BeanMasterTableViewSource", "BeanHTMLWrapperSource", "BaseServlet",
                "BeanServletSource", "BeanCsvImportSource");

        private static final List<String> ALL_FILES_NON_EDITABLE = Arrays.asList("BeanBaseSource", "BeanEditorBaseSource",
                "BeanParametersBaseSource", "FormattedBeanDataBaseSource", "BeanHTMLViewBaseSource",
                "BeanMasterTableViewBaseSource", "BeanHTMLWrapperBaseSource", "BeanServletBaseSource", "BeanCsvImportBaseSource");

        private static final List<String> MOST_FILES_EDITABLE = Arrays.asList("DbBeanSource", "LabelManagerSource",
                "LocalFileManagerSource", "BeanSource", "BeanEditorSource", "BeanParametersSource",
                "LocalDbBeanFormatterSource", "FormattedBeanDataSource", "HTMLViewSource", "BeanHTMLViewSource",
                "LocalMasterTableViewSource", "BeanMasterTableViewSource", "BaseServlet", "BeanServletSource");

        private static final List<String> MOST_FILES_NON_EDITABLE = Arrays.asList("BeanBaseSource",
                "BeanEditorBaseSource", "BeanParametersBaseSource", "FormattedBeanDataBaseSource",
                "BeanHTMLViewBaseSource", "BeanMasterTableViewBaseSource", "BeanServletBaseSource");

        private static final List<String> DATA_FILES_EDITABLE = Arrays.asList("DbBeanSource", "LabelManagerSource",
                "LocalFileManagerSource", "BeanSource", "BeanParametersSource");

        private static final List<String> DATA_FILES_NON_EDITABLE =
                Arrays.asList("BeanBaseSource", "BeanParametersBaseSource");

        private static final List<String> EDITION_FILES_EDITABLE = Arrays.asList("DbBeanSource", "LabelManagerSource",
                "LocalFileManagerSource", "BeanSource", "BeanEditorSource", "BeanParametersSource",
                "LocalDbBeanFormatterSource");

        private static final List<String> EDITION_FILES_NON_EDITABLE = Arrays.asList("BeanBaseSource",
                "BeanEditorBaseSource", "BeanParametersBaseSource");

        private static final List<String> HTML_FILES_EDITABLE = Arrays.asList("DbBeanSource", "LabelManagerSource",
                "LocalFileManagerSource", "BeanSource", "BeanEditorSource", "BeanParametersSource",
                "HTMLViewSource", "BeanHTMLViewSource", "BaseServlet", "BeanServletSource",
                "LocalDbBeanFormatterSource");

        private static final List<String> HTML_FILES_NON_EDITABLE = Arrays.asList("BeanBaseSource",
                "BeanEditorBaseSource", "BeanParametersBaseSource", "BeanHTMLViewBaseSource", "BeanServletBaseSource");

        private static final List<String> CSV_FILES_EDITABLE = Arrays.asList("DbBeanSource", "LabelManagerSource",
                "LocalFileManagerSource", "BeanSource", "BeanEditorSource", "BeanParametersSource",
                "LocalDbBeanFormatterSource", "BeanCsvImportSource", "LocalCsvImportSource");

        private static final List<String> CSV_FILES_NON_EDITABLE = Arrays.asList("BeanBaseSource",
                "BeanEditorBaseSource", "BeanParametersBaseSource", "BeanCsvImportBaseSource");

        private static final List<String> MASTER_TABLES_EDITABLE = Arrays.asList("DbBeanSource", "LabelManagerSource",
                "LocalFileManagerSource", "BeanSource", "BeanEditorSource", "BeanParametersSource",
                "LocalDbBeanFormatterSource", "FormattedBeanDataSource", "LocalMasterTableViewSource",
                "BeanMasterTableViewSource");

        private static final List<String> MASTER_TABLES_NON_EDITABLE = Arrays.asList("BeanBaseSource",
                "BeanEditorBaseSource", "BeanParametersBaseSource", "FormattedBeanDataBaseSource",
                "BeanMasterTableViewBaseSource");

        private static final List<String> HTML_WRAPPER_FILES_EDITABLE = Arrays.asList("DbBeanSource", "LabelManagerSource",
                "LocalFileManagerSource", "BeanSource", "BeanEditorSource", "BeanParametersSource",
                "HTMLViewSource", "BeanHTMLViewSource", "BeanHTMLWrapperSource", "BaseServlet", "BeanServletSource",
                "LocalDbBeanFormatterSource");

        private static final List<String> HTML_WRAPPER_FILES_NON_EDITABLE = Arrays.asList("BeanBaseSource",
                "BeanEditorBaseSource", "BeanParametersBaseSource", "BeanHTMLViewBaseSource",
                "BeanHTMLWrapperBaseSource", "BeanServletBaseSource");
    }

}
