package com.example.sm.common.utils;

import com.example.sm.auth.decorator.ExcelField;
import com.example.sm.bookshop.decorator.BookData;
import com.example.sm.bookshop.decorator.BookDataExcel;
import com.example.sm.bookshop.decorator.StudentResponseExcel;
import com.example.sm.bookshop.decorator.UserDetailPurchasedBookXlsx;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static org.apache.poi.xssf.usermodel.XSSFWorkbookFactory.createWorkbook;

@Data
@Slf4j
@Component
public class ExcelUtil {
    public static <T> Workbook createWorkbookFromData(List<T> data,String title) {
        // Create new Workbook
        Workbook workbook = createWorkbook();

        // Create A Sheet
        Sheet sheet = workbook.createSheet("Sheet1");

        // If no data then return no work needed
        if (data.size() == 0) {
            return workbook;
        }

        Row topRow = sheet.createRow(0);
        Row headerRow = sheet.createRow(1);

        //logger.info("Sheet -> row[0] -> {}",headerRow);
        /*Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 15);
        font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);*/

        //set title in excel sheet
        topRow.createCell(0).setCellValue(title);


        //set method
        List<Method> methods = setHeaders(headerRow, data.get(0).getClass());

        // This is start with one because we need to skip first element ...
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, methods.size()));
        topRow.getCell(0).getCellStyle().setAlignment(HorizontalAlignment.CENTER);

        //topRow.getCell(0).getCellStyle().setFont(font);
        int i = 2;
        for (T record : data) {
            Row row = sheet.createRow(i++);
            setData(row, methods, record, i -2);
        }

        for (int column = 0; column <= methods.size(); column++) {
            try {
                System.out.println(column);
                //sheet.autoSizeColumn(column);
            } catch (Exception ignored) {
            }
        }
        return workbook;
    }

    public static <T> Workbook createWorkbooks(HashMap<String,List<BookData>>listHashMap) {
        // Create new Workbook
        Workbook workbook = createWorkbook();
        // Create A Sheet
        Sheet sheet = workbook.createSheet("Sheet1");

        Row topRow = sheet.createRow(0);
        //Row headerRow = sheet.createRow(3);
        topRow.createCell(0).setCellValue("Book Details");

        log.info("Sheet -> {}", sheet);

        int i= topRow.getRowNum()+2;
        int rowNumber=i;
        double totalCount=0;
        for (HashMap.Entry<String,List<BookData>> entry : listHashMap.entrySet()) {
            i = i+1;
            System.out.println("Book Name Row Value: "+ i);
            Row bookNameRow = sheet.createRow(i);
            String book= entry.getKey();
            bookNameRow.createCell(0).setCellValue("bookName :  "+book);

            //create header row to set header values
            i = i+1;
            System.out.println("Book Header Row Value: "+ i);
            Row headerRow = sheet.createRow(i);

            List<Method> methods = setHeaders(headerRow, StudentResponseExcel.class);

            List<BookData> bookData= entry.getValue();

            //i = i+1;
           /* List<CellRangeAddress> cellRangeAddresses2 = new ArrayList<>();
            System.out.println("Method cell address1 row number"+i);
            cellRangeAddresses2.add(new CellRangeAddress(i, i, 0, 10));

            setCellRangeAddress(sheet,cellRangeAddresses2,methods.size());*/
            List<StudentResponseExcel> studentResponseExcels=new ArrayList<>();
            StudentResponseExcel studentResponseExcel= new StudentResponseExcel();
            for (BookData bookDatum : bookData) {
                studentResponseExcel.setStudentId(bookDatum.getStudentId());
                studentResponseExcel.setStudentName(bookDatum.getStudentName());
                studentResponseExcel.setDate(bookDatum.getDate());
                studentResponseExcel.setPrice(bookDatum.getPrice());
                studentResponseExcels.add(studentResponseExcel);
            }
            int k =1 ;
            for (StudentResponseExcel responseExcel : studentResponseExcels) {
                i= i+1;
                Row row = sheet.createRow(i);
                setData(row,methods,responseExcel,k++);
            }
            log.info("studentResponseExel:{}",studentResponseExcels.size());
            i=i+1;
            for (BookData bookDatum : bookData) {
                totalCount= totalCount+bookDatum.getPrice();
                Row count = sheet.createRow(i);
                count.createCell(0).setCellValue("totalPrice :  "+totalCount );
            }
            totalCount= 0;
            i=i+1;

        }

        return workbook;
    }
    public static <T> Workbook createWorkbookOnBookDetailsData(HashMap<String, List<BookDataExcel>> hashMap, String title) {
        Workbook workbook = createWorkbook();

        // Create A Sheet
        Sheet sheet = workbook.createSheet("Sheet1");

        // If no data then return no work needed
        if (hashMap.size() == 0) {
            return workbook;
        }
        //set title in excel sheet
        Row topRow = sheet.createRow(0);
        topRow.createCell(0).setCellValue(title);

        //add space 2 after than  print studentName
        int i=topRow.getRowNum()+2;

        for (HashMap.Entry<String,List<BookDataExcel>> entry : hashMap.entrySet()){
            i=i+1;
            Row bookName = sheet.createRow(i);
            //add student Name
            bookName.createCell(0).setCellValue(entry.getKey());

            i=i+1;
            Row header = sheet.createRow(i);
            List<Method> methods = setHeaders(header, BookDataExcel.class);
            int k=1;
            //set data
            for (BookDataExcel bookResponseExcel : entry.getValue()) {
                i= i+1;
                Row row = sheet.createRow(i);
                setData(row, methods, bookResponseExcel, k++);
            }
            i=i+1;
        }
        return  workbook;
    }

    public static <T> Workbook createWorkbookOnUserPurchasedBookDetail(HashMap<String, List<UserDetailPurchasedBookXlsx>> hashMap, String title) {
        Workbook workbook = createWorkbook();

        // Create A Sheet
        Sheet sheet = workbook.createSheet("Sheet1");

        // If no data then return no work needed
        if (hashMap.size() == 0) {
            return workbook;
        }
        //set title in excel sheet
        Row topRow = sheet.createRow(0);
        topRow.createCell(0).setCellValue(title);

        //add space 2 after than  print studentName
        int i=topRow.getRowNum()+2;

        for (HashMap.Entry<String,List<UserDetailPurchasedBookXlsx>> entry : hashMap.entrySet()){
            i=i+1;
            Row bookName = sheet.createRow(i);
            //add student Name
            bookName.createCell(0).setCellValue("Month: "+entry.getKey());

            i=i+1;
            Row header = sheet.createRow(i);
            List<Method> methods = setHeaders(header, UserDetailPurchasedBookXlsx.class);
            int k=1;
            //set data
            for (UserDetailPurchasedBookXlsx bookResponseExcel : entry.getValue()) {
                i= i+1;
                Row row = sheet.createRow(i);
                setData(row, methods, bookResponseExcel, k++);
            }
            i=i+1;
        }
        return  workbook;
    }
    private static void setCellRangeAddress(Sheet sheet,List<CellRangeAddress> cellRangeAddresses,int lastColumn){
        if(CollectionUtils.isEmpty(cellRangeAddresses)){
            return;
        }
        for (CellRangeAddress cellRangeAddress : cellRangeAddresses) {
            cellRangeAddress.setLastColumn(lastColumn);
            sheet.addMergedRegion(cellRangeAddress);
        }
    }
    private static List<Method> setHeaders(Row row, Class c) {
        // This is the list of method to be called on object in order to get data...
        List<Method> methods = new ArrayList<>();
        Method[] fields = c.getMethods();
        for (Method m : fields) {
            ExcelField excelField = m.getAnnotation(ExcelField.class);
            if (excelField != null) {
                methods.add(m);
            }
        }
        methods.sort(Comparator.comparingInt(o -> o.getAnnotation(ExcelField.class).position()));
        int i = 0;
        Cell cell = row.   createCell(i++);
       // cell.setCellValue("#");
        for (Method m : methods) {
            ExcelField excelField = m.getAnnotation(ExcelField.class);
            //logger.info("Index Problem Index -> {}",i);
            //logger.info("Index Problem Row  -> {}",row);
            //logger.info("Index Problem Method -> {}",methods);
            cell = row.createCell(i++);
            cell.setCellValue(excelField.excelHeader());
            CellUtil.setAlignment(cell, HorizontalAlignment.CENTER);
            //logger.info("Cell Value -> {}",cell.getStringCellValue());
        }
        return methods;
    }

    private static void setData(Row row,List<Method> methods,Object o,int position){
        int index  = 0;
        Cell cell = row.createCell(index++);
        cell.setCellValue(position);
        for (Method method : methods) {
            cell = row.createCell(index++);
            try {
                Object cellValue = method.invoke(o);
                if(cellValue == null){
                    cellValue = "";
                }
                cell.setCellValue(cellValue.toString().replaceAll("null",""));
                CellUtil.setAlignment(cell, HorizontalAlignment.CENTER);
            }catch (Exception ignored){
            }
        }
    }
    public static ByteArrayResource getBiteResourceFromWorkbook(Workbook workbook) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        return new  ByteArrayResource(outputStream.toByteArray());
    }
}
