package org.beanmaker.v2.cli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

enum SourceFileList {
    ALL(FileLists.ALL_FILES_EDITABLE, FileLists.ALL_FILES_NON_EDITABLE),
    DATA(FileLists.DATA_FILES_EDITABLE, FileLists.DATA_FILES_NON_EDITABLE),
    EDITION(FileLists.EDITION_FILES_EDITABLE, FileLists.EDITION_FILES_NON_EDITABLE),
    HTML_FORMS(FileLists.HTML_FILES_EDITABLE, FileLists.HTML_FILES_NON_EDITABLE),
    MASTER_TABLES(FileLists.MASTER_TABLES_EDITABLE, FileLists.MASTER_TABLES_NON_EDITABLE);

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

    private static class FileLists {  // * Lists must in inner class, otherwise we have a forward reference problem
        private static final List<String> ALL_FILES_EDITABLE = Arrays.asList("DbBeanSource", "LabelManagerSource",
                "LocalFileManagerSource", "BeanSource", "BeanEditorSource", "BeanParametersSource",
                "LocalDbBeanFormatterSource", "BeanFormatterSource", "BeanFormatterInterfaceSource",
                "FormattedBeanDataSource", "HTMLViewSource", "BeanHTMLViewSource", "LocalMasterTableViewSource",
                "BeanMasterTableViewSource", "BaseServlet", "BeanServletSource");

        private static final List<String> ALL_FILES_NON_EDITABLE = Arrays.asList("BeanBaseSource", "BeanEditorBaseSource",
                "BeanParametersBaseSource", "BeanFormatterBaseSource", "BeanFormatterInterfaceBaseSource",
                "FormattedBeanDataBaseSource", "BeanHTMLViewBaseSource", "BeanMasterTableViewBaseSource",
                "BeanServletBaseSource");

        private static final List<String> DATA_FILES_EDITABLE = Arrays.asList("DbBeanSource", "LabelManagerSource",
                "LocalFileManagerSource", "BeanSource", "BeanParametersSource");

        private static final List<String> DATA_FILES_NON_EDITABLE =
                Arrays.asList("BeanBaseSource", "BeanParametersBaseSource");

        private static final List<String> EDITION_FILES_EDITABLE = Arrays.asList("DbBeanSource", "LabelManagerSource",
                "LocalFileManagerSource", "BeanSource", "BeanEditorSource", "BeanParametersSource");

        private static final List<String> EDITION_FILES_NON_EDITABLE = Arrays.asList("BeanBaseSource", "BeanEditorBaseSource",
                "BeanParametersBaseSource");

        private static final List<String> HTML_FILES_EDITABLE = Arrays.asList("DbBeanSource", "LabelManagerSource",
                "LocalFileManagerSource", "BeanSource", "BeanEditorSource", "BeanParametersSource",
                "HTMLViewSource", "BeanHTMLViewSource", "BaseServlet", "BeanServletSource");

        private static final List<String> HTML_FILES_NON_EDITABLE = Arrays.asList("BeanBaseSource", "BeanEditorBaseSource",
                "BeanParametersBaseSource", "BeanHTMLViewBaseSource", "BeanServletBaseSource");

        private static final List<String> MASTER_TABLES_EDITABLE = Arrays.asList("DbBeanSource", "LabelManagerSource",
                "LocalFileManagerSource", "BeanSource", "BeanEditorSource", "BeanParametersSource",
                "LocalDbBeanFormatterSource", "BeanFormatterSource", "BeanFormatterInterfaceSource",
                "FormattedBeanDataSource", "LocalMasterTableViewSource", "BeanMasterTableViewSource");

        private static final List<String> MASTER_TABLES_NON_EDITABLE = Arrays.asList("BeanBaseSource", "BeanEditorBaseSource",
                "BeanParametersBaseSource", "BeanFormatterBaseSource", "BeanFormatterInterfaceBaseSource",
                "FormattedBeanDataBaseSource", "BeanMasterTableViewBaseSource");

    }

}
