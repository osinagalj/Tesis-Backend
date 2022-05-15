package com.unicen.app.model;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class XLSXBuilder {

    private File file;
    private XSSFWorkbook workbook;
    private Sheet currentSheet;
    private int indexCurrentRow;
    private XSSFFont currentCellFont;
    private CellStyle currentCellStyle;
    private Row currentRow;
    private int currentColumn;

    public static XLSXBuilder create(String sheetName) throws IOException {
        return new XLSXBuilder().startOn(sheetName);
    }

    private XLSXBuilder startOn(String sheetName) throws IOException {
        this.file = File.createTempFile(sheetName, ".xlsx");
        this.workbook = new XSSFWorkbook();
        this.currentSheet = workbook.createSheet(sheetName);
        this.currentCellFont = workbook.createFont();
        this.currentCellStyle = workbook.createCellStyle();

        return this;
    }

    public XLSXBuilder addRow() {
        this.currentRow = currentSheet.createRow(this.indexCurrentRow);
        this.indexCurrentRow += 1;
        this.currentColumn = 0;
        return this;
    }

    public XLSXBuilder addRows(int amount) {
        for (int i = 0; i < amount; i++) {
            this.addRow();
        }
        return this;
    }

    public XLSXBuilder addMultipleRows(List<List<String>> rows) {
        for (var row : rows) {
            this.addRow();
            for (String value : row) {
                this.createCell(value);
            }
        }

        return this;
    }

    public XLSXBuilder createCell(String value) {
        Cell c = this.currentRow.createCell(currentColumn);
        c.setCellValue(value);
        this.currentColumn += 1;

        CellStyle newCellStyle = workbook.createCellStyle();
        newCellStyle.cloneStyleFrom(currentCellStyle);
        c.setCellStyle(newCellStyle);

        return this;
    }

    public XLSXBuilder createCells(String... values) {
        for (String value : values) {
            createCell(value);
        }
        return this;
    }

    public XLSXBuilder setAlignment(HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment) {
        this.currentCellStyle.setAlignment(horizontalAlignment);
        this.currentCellStyle.setVerticalAlignment(verticalAlignment);
        return this;
    }

    public XLSXBuilder setColumnsWidth(Integer... columnsWidths) {
        for (int i = 0; i < columnsWidths.length; i++) {
            this.currentSheet.setColumnWidth(i, columnsWidths[i] * 256); // Makes description column width 10 characters (unit is 1/256 of a character width, so 10*256=2560)
        }
        return this;
    }

    public XLSXBuilder setCurrentFontStyle(Boolean bold, int fontHeight) {
        this.currentCellFont.setBold(bold);
        this.currentCellFont.setFontHeight(fontHeight);
        this.currentCellStyle.setFont(this.currentCellFont);
        return this;
    }

    public XLSXBuilder setCellBackground(short color) {
        this.currentCellStyle.setFillForegroundColor(color);
        this.currentCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return this;
    }

    public File build() throws IOException {
        FileOutputStream outputStream = new FileOutputStream(file);
        workbook.write(outputStream);
        workbook.close();
        outputStream.flush();
        outputStream.close();
        return file;
    }

}
