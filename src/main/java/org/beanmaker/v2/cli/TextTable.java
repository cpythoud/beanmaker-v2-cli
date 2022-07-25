package org.beanmaker.v2.cli;

import org.beanmaker.v2.util.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class TextTable {

    private final int columnCount;

    private final List<List<String>> lines = new ArrayList<>();

    TextTable(int columnCount) {
        this.columnCount = columnCount;
    }

    void addLine(List<String> cells) {
        if (cells.size() != columnCount)
            throw new IllegalArgumentException("Expected " + columnCount + " columns, but got " + cells.size());

        lines.add(cells);
    }

    void addLine(String... cells) {
        addLine(Arrays.asList(cells));
    }

    @Override
    public String toString() {
        var columns = getColumns();
        var maxima = getMaxLengths(columns);

        var table = new StringBuilder();
        drawLine(table, maxima);
        for (var line: lines) {
            formatCells(table, maxima, line);
            drawLine(table, maxima);
        }

        return table.toString();
    }

    private List<List<String>> getColumns() {
        var columns = new ArrayList<List<String>>();
        for (int index = 0; index < columnCount; ++index) {
            var columnCells = new ArrayList<String>();
            for (var cells: lines)
                columnCells.add(cells.get(index));
            columns.add(columnCells);
        }
        return columns;
    }

    private List<Integer> getMaxLengths(List<List<String>> columns) {
        var maxima = new ArrayList<Integer>();
        for (var column: columns) {
            int max = 0;
            for (var text: column)
                max = Math.max(max, text.length());
            maxima.add(max);
        }
        return maxima;
    }

    private void drawLine(StringBuilder table, List<Integer> maxima) {
        table.append(" +-");
        for (int max: maxima)
            table.append(Strings.repeatString("-", max)).append("-+-");
        table.delete(table.length() -1, table.length());
        table.append("\n");
    }

    private void formatCells(StringBuilder table, List<Integer> maxima, List<String> line) {
        table.append(" | ");
        for (int i = 0; i < columnCount; ++i) {
            String cell = line.get(i);
            int length = cell.length();
            int maxLength = maxima.get(i);

            table.append(cell);
            if (cell.length() < maxLength)
                table.append(Strings.repeatString(" ", maxLength - length));
            table.append(" | ");
        }
        table.delete(table.length() -1, table.length());
        table.append("\n");
    }

}
