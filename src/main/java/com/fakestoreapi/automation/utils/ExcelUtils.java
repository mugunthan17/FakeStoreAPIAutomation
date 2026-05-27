package com.fakestoreapi.automation.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelUtils {
    
    private Workbook workbook;
    private Sheet sheet;
    private String filePath;
    
    public ExcelUtils(String filePath) {
        this.filePath = filePath;
        try {
            FileInputStream fis = new FileInputStream(filePath);
            workbook = new XSSFWorkbook(fis);
            LoggerUtil.info("Excel file loaded: " + filePath);
        } catch (IOException e) {
            LoggerUtil.error("Failed to load Excel file: " + e.getMessage());
            throw new RuntimeException("Excel file not found: " + filePath);
        }
    }
    
    public void setSheet(String sheetName) {
        sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            LoggerUtil.error("Sheet not found: " + sheetName);
            throw new RuntimeException("Sheet not found: " + sheetName);
        }
    }
    
    public void setSheet(int sheetIndex) {
        sheet = workbook.getSheetAt(sheetIndex);
    }
    
    public String getCellData(int rowNum, int colNum) {
        Row row = sheet.getRow(rowNum);
        if (row == null) return "";
        
        Cell cell = row.getCell(colNum);
        if (cell == null) return "";
        
        return getCellValueAsString(cell);
    }
    
    private String getCellValueAsString(Cell cell) {
        
        if (cell == null) {
            return "";
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return "";
        }
    }
    
    public int getRowCount() {
        return sheet.getLastRowNum() + 1;
    }
    
    public int getColumnCount() {
        return sheet.getRow(0).getLastCellNum();
    }
    
    public List<Map<String, String>> getAllData() {
        List<Map<String, String>> dataList = new ArrayList<>();
        
        Row headerRow = sheet.getRow(0);
        int rowCount = getRowCount();
        int colCount = getColumnCount();
        
        for (int i = 1; i < rowCount; i++) {
            Map<String, String> dataMap = new HashMap<>();
            Row row = sheet.getRow(i);
            
            if (row != null) {
                for (int j = 0; j < colCount; j++) {
                    String header = getCellValueAsString(headerRow.getCell(j));
                    
                    if (header != null && !header.trim().isEmpty()) {
                        String value = getCellData(i, j);
                        dataMap.put(header, value);
                    }
                }
                
                if (!dataMap.isEmpty()) {
                    dataList.add(dataMap);
                }
            }
        }
        
        return dataList;
    }
    
    public List<String> getColumnData(String columnName) {
        List<String> columnData = new ArrayList<>();
        
        Row headerRow = sheet.getRow(0);
        int colIndex = -1;
        
        
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            if (getCellValueAsString(headerRow.getCell(i)).equals(columnName)) {
                colIndex = i;
                break;
            }
        }
        
        if (colIndex == -1) {
            LoggerUtil.error("Column not found: " + columnName);
            return columnData;
        }
        
        
        for (int i = 1; i < getRowCount(); i++) {
            columnData.add(getCellData(i, colIndex));
        }
        
        return columnData;
    }
    
    public void close() {
        try {
            if (workbook != null) {
                workbook.close();
                LoggerUtil.info("Excel workbook closed");
            }
        } catch (IOException e) {
            LoggerUtil.error("Failed to close workbook: " + e.getMessage());
        }
    }
}
