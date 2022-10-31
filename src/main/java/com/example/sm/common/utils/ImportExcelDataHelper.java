package com.example.sm.common.utils;

import com.amazonaws.services.athena.model.InvalidRequestException;
import com.example.sm.common.constant.MessageConstant;
import com.example.sm.common.model.ImportedData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.CollectionUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
@Slf4j
public class ImportExcelDataHelper {
    public static ImportedData getDataFromExcel(InputStream fileStream, String extension) throws IOException {
        if(extension!=null && !extension.equals("xlsx") && !extension.equals("xls")){
            throw new InvalidRequestException(MessageConstant.EXCEL_FILE_FORMAT_NOT_SUPPORTED);
        }
        ImportedData importedData = new ImportedData();
        Workbook workbook = new XSSFWorkbook(fileStream);
        // Get The Active Sheet for import work around for not putting the index static
        Sheet sheet = workbook.getSheetAt(workbook.getActiveSheetIndex());
        Map<String,List<Object>> data = new HashMap<>();
        Iterator<Row> rows = sheet.iterator();
        int index=0;
        while (rows.hasNext()) {
            //Row row = rows.next();
            Row row = sheet.getRow(index);
            if(isRowEmpty(row)) {
                index++;
                continue;
            }
            List<String> headerData = getHeaders(row);
            if(!isHeaderRow(headerData)){
                index++;
                continue;
            }
            break;
        }
        // Setup Header Row
        Row row = sheet.getRow(index);
        List<String> headers = getHeaders(row);
        headers.forEach(header -> data.put(header,new ArrayList<>()));
        importedData.setHeaders(headers);
        int headerIndex = index;
        while (rows.hasNext()){
            int i = 0;
            Row currentRow = rows.next();
            if(currentRow.getRowNum()<=headerIndex){
                continue;
            }
            if(isRowEmpty(currentRow)){
                continue;
            }
            Iterator<Cell> cellIterator = currentRow.cellIterator();
            while (cellIterator.hasNext()){
                Cell currentCell = cellIterator.next();
                Object cellValue = null;
                if(currentCell.getCellType().equals(CellType.STRING)){
                    cellValue = currentCell.getStringCellValue().trim();
                    if(ObjectUtils.isEmpty(cellValue)){
                        cellValue = null;
                    }
                } else if (currentCell.getCellType().equals(CellType.NUMERIC)) {
                    if (DateUtil.isCellDateFormatted(currentCell)) {
                        cellValue = currentCell.getDateCellValue();
                    }else{
                        currentCell.setCellType(CellType.STRING);
                        cellValue = currentCell.getStringCellValue().trim();
                    }
                } else if (currentCell.getCellType().equals(CellType.BOOLEAN)) {
                    cellValue = currentCell.getBooleanCellValue();
                }
                String headerCell = headers.get(i++);
                if(!StringUtils.isEmpty(headerCell) && !headerCell.equals(" ") && !ImportExcelDataHelper.recoverExcelHeader(headerCell).equals(cellValue)){
                    data.get(headerCell).add(cellValue);
                }
            }
        }
        importedData.getHeaders().remove("");
        importedData.getHeaders().remove(null);
        importedData.setData(data);
        importedData.getData().entrySet().removeIf(e -> e.getValue() != null && e.getValue().isEmpty());
        return importedData;
    }
    private static List<String> getHeaders(Row headerRow){
        List<String> data = new ArrayList<>();
        for (Cell currentCell : headerRow) {
            String cellValue = currentCell.getStringCellValue();
            if (!StringUtils.isEmpty(cellValue)) {
                cellValue = replaceExcelHeader(cellValue);
            }
            data.add(cellValue);
        }
        return data;
    }
    public static boolean isHeaderRow(List<String> headerData){
        if(!CollectionUtils.isEmpty(headerData)){
            int nonEmptyHeaders = 0;
            for (String header : headerData) {
                if(!StringUtils.isEmpty(header)){
                    nonEmptyHeaders++;
                }
            }
            return nonEmptyHeaders > 1;
        }
        return false;
    }
    public static boolean isRowEmpty(Row row) {
        if(row != null) {
            int notEmptyCells = 0;
            for (Cell cell : row) {
                //set foreground color here
                if (cell == null || cell.getCellType() == CellType.BLANK || (cell.getCellType() == CellType.STRING && cell.getStringCellValue().trim().isEmpty())) {
                    continue;
                }
                notEmptyCells++;
            }
            return notEmptyCells <= 0;
        }
        return true;
    }
    public static String replaceExcelHeader(String str){
        return str.replaceAll("\\.","#_#");
    }
    public static String recoverExcelHeader(String str){
        return str.replaceAll("#_#",".");
    }
    public static Map<String,Object> getMapData(int index,Map<String,List<Object>> data,Set<String> excelKeys,Map<String,String> mapping){
        Map<String,Object> dataMap = new HashMap<>();
        for(String excelKey : excelKeys){
            List<Object> dataList = data.get(ImportExcelDataHelper.replaceExcelHeader(excelKey));
            if(CollectionUtils.isEmpty(dataList)){
                continue;
            }
            dataMap.put(
                    //Get Mapping Key
                    mapping.get(excelKey),
                    //Get Data on that index
                    dataList.get(index)
            );
        }
        return dataMap;
    }
}