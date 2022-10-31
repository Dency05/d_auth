package com.example.sm.auth.controller;

import com.example.sm.bookshop.decorator.*;
import com.example.sm.bookshop.enums.Amount;
import com.example.sm.bookshop.enums.BookSortBy;
import com.example.sm.bookshop.model.BookPurchaseLog;
import com.example.sm.bookshop.model.StudentLog;
import com.example.sm.bookshop.service.BookShopService;
import com.example.sm.common.decorator.*;
import com.example.sm.common.enums.Role;
import com.example.sm.common.utils.Access;
import com.example.sm.common.utils.ExcelUtil;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/book")
public class BookShopController {
    @Autowired
    BookShopService bookShopService;


    @SneakyThrows
    @Access(levels = {Role.ADMIN})
    @RequestMapping(name = "addOrUpdateBook", value = "/add", method = RequestMethod.POST)
    public DataResponse<BookShopResponse> addOrUpdateBook (@RequestParam(required = false) String id, @RequestBody BookShopAddRequest bookShopAddRequest) {
        DataResponse<BookShopResponse> dataResponse = new DataResponse<>();
        dataResponse.setData(bookShopService.addOrUpdateBook(id,bookShopAddRequest));
        dataResponse.setStatus(Response.getOkResponse());
        return dataResponse;
    }

    @SneakyThrows
    @RequestMapping(name = "getAllStudent", value = "/getAll", method = RequestMethod.POST)
    public ListResponse<Student> getAllStudent(@RequestParam Role role) {
        ListResponse<Student> listResponse = new ListResponse<>();
        listResponse.setData(bookShopService.getAllStudent(role));
        listResponse.setStatus(Response.getSuccessResponse());
        return listResponse;
    }

    @SneakyThrows
    @Access(levels = {Role.STUDENT})
    @RequestMapping(name = "purchaseBook", value = "/purchase", method = RequestMethod.GET)
    public DataResponse<Object> purchaseBook(@RequestParam String id, @RequestParam String bookName ,@RequestParam int day) {
        DataResponse<Object> dataResponse = new DataResponse<>();
        bookShopService.purchaseBook(id,bookName,day);
        dataResponse.setStatus(Response.getOkResponse());
        return dataResponse;
    }

    @SneakyThrows
    @RequestMapping(name = "addBalance", value = "/add/balance", method = RequestMethod.GET)
    public DataResponse<Object> addBalance(@RequestParam Amount amount,@RequestParam String id) {
        DataResponse<Object> dataResponse = new DataResponse<>();
        bookShopService.addBalance(amount,id);
        dataResponse.setStatus(Response.getOkResponse());
        return dataResponse;
    }

    @SneakyThrows
    @RequestMapping(name="saveStudent",value = "save/student",method=RequestMethod.GET)
    public DataResponse<StudentLog> saveStudent (@RequestParam String id) {
        DataResponse<StudentLog> dataResponse = new DataResponse<>();
        dataResponse.setData(bookShopService.saveStudent(id));
        dataResponse.setStatus(Response.getOkResponse());
        return dataResponse;
    }

    @SneakyThrows
    @RequestMapping(name = "getPurchaseBook", value = "get/book", method = RequestMethod.GET)
    public DataResponse<BookPurchaseResponses> getPurchaseBook(String year) {
        DataResponse<BookPurchaseResponses> dataResponse = new DataResponse<>();
        dataResponse.setData(bookShopService.getPurchaseBook(year));
        dataResponse.setStatus(Response.getOkResponse());
        return dataResponse;
    }

    @SneakyThrows
    @Access(levels = {Role.STUDENT})
    @RequestMapping(name = "reSalePurchasedBook", value = "reSale/book", method = RequestMethod.GET)
    public DataResponse<BookPurchaseLog> reSalePurchasedBook(String bookId) {
        DataResponse<BookPurchaseLog> dataResponse = new DataResponse<>();
        dataResponse.setData(bookShopService.reSalePurchasedBook(bookId));
        dataResponse.setStatus(Response.getOkResponse());
        return dataResponse;
    }

    @SneakyThrows
    @Access(levels = {Role.STUDENT})
    @RequestMapping(name = "getAllStudentDetails", value = "get/Student", method = RequestMethod.GET)
    public ListResponse<StudentResponse> getAllStudentDetails() {
        ListResponse<StudentResponse> listResponse = new ListResponse<>();
        listResponse.setData(bookShopService.getAllStudentDetails());
        listResponse.setStatus(Response.getOkResponse());
        return listResponse;
    }

    @SneakyThrows
    @Access(levels = {Role.STUDENT})
    @RequestMapping(name = "getStudentDetails", value = "get/all/Student", method = RequestMethod.POST)
    public PageResponse<BookPurchaseLog> getStudentDetails(@RequestBody FilterSortRequest<BookShopFilter, BookSortBy> filterSortRequest) {
        PageResponse<BookPurchaseLog> pageResponse = new PageResponse<>();
        BookShopFilter filter = filterSortRequest.getFilter();
        FilterSortRequest.SortRequest<BookSortBy> sort = filterSortRequest.getSort();
        Pagination pagination = filterSortRequest.getPage();
        PageRequest pageRequest = PageRequest.of(pagination.getPage(), pagination.getLimit());
        pageResponse.setData(bookShopService.getStudentDetails(filter,sort,pageRequest));
        pageResponse.setStatus(Response.getOkResponse());
        return pageResponse;
    }

    @SneakyThrows
    @Access(levels = {Role.STUDENT})
    @RequestMapping(name = "getStudentInExcel", value = "get/students", method = RequestMethod.GET)
    public ListResponse<StudentDetailsResponse> getStudentInExcel() {
        ListResponse<StudentDetailsResponse> listResponse = new ListResponse<>();
        listResponse.setData(bookShopService.getStudentInExcel());
        listResponse.setStatus(Response.getOkResponse());
        return listResponse;
    }

    @SneakyThrows
    @RequestMapping(name = "importStudentInExcel", value = "/get/student/excel", method = RequestMethod.GET)
    @Access(levels = {Role.STUDENT})
    public ResponseEntity<Resource> importStudentInExcel() {
        Workbook workbook =bookShopService.importStudentInExcel();
        assert workbook != null;
        ByteArrayResource resource = ExcelUtil.getBiteResourceFromWorkbook(workbook);
        return ResponseEntity.ok()
                .contentLength(resource.contentLength())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "exported_data_xlsx" + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(resource);
    }

    @SneakyThrows
    @RequestMapping(name = "importStudentBookPurchaseRecordExcel", value = "student/purchase/record/excel", method = RequestMethod.POST)
    @Access(levels = {Role.ADMIN})
    public ResponseEntity<Resource> importStudentBookPurchaseRecordExcel(@RequestBody FilterSortRequest<BookShopFilter, BookSortBy> filterSortRequest) {
        BookShopFilter filter = filterSortRequest.getFilter();
        FilterSortRequest.SortRequest<BookSortBy> sort = filterSortRequest.getSort();
        Pagination pagination = filterSortRequest.getPage();
        PageRequest pageRequest = PageRequest.of(pagination.getPage(), pagination.getLimit());
        Workbook workbook =bookShopService.importBookDetailInExcel(filter,sort,pageRequest);
        assert workbook != null;
        ByteArrayResource resource = ExcelUtil.getBiteResourceFromWorkbook(workbook);

        return ResponseEntity.ok()
                .contentLength(resource.contentLength())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "student_book_purchase_record_xlsx" + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(resource);
    }


    @SneakyThrows
    @Access(levels = {Role.STUDENT})
    @RequestMapping(name = "getUserPurchasedDetail", value = "get/all/by/month", method = RequestMethod.POST)
    public PageResponse<UserBookPurchasedResponse> getUserPurchasedDetail(@RequestBody FilterSortRequest<BookShopFilter, BookSortBy> filterSortRequest) {
        PageResponse<UserBookPurchasedResponse> pageResponse = new PageResponse<>();
        BookShopFilter filter = filterSortRequest.getFilter();
        FilterSortRequest.SortRequest<BookSortBy> sort = filterSortRequest.getSort();
        Pagination pagination = filterSortRequest.getPage();
        PageRequest pageRequest = PageRequest.of(pagination.getPage(), pagination.getLimit());
        pageResponse.setData(bookShopService.getUserPurchasedDetail(filter,sort,pageRequest));
        pageResponse.setStatus(Response.getOkResponse());
        return pageResponse;
    }

    @SneakyThrows
    @Access(levels = {Role.STUDENT})
    @RequestMapping(name = "totalUserCountByMonth", value = "/total/count", method = RequestMethod.GET)
    public ListResponse<TotalUserCountByMonthResponse> totalUserCountByMonth(@RequestParam int year, @RequestParam int month) {
        ListResponse<TotalUserCountByMonthResponse> listResponse = new ListResponse<>();
        listResponse.setData(bookShopService.totalUserCountByMonth(year,month));
        listResponse.setStatus(Response.getOkResponse());
        return listResponse;
    }

    @SneakyThrows
    @Access(levels = {Role.STUDENT})
    @RequestMapping(name = "userPurchasedBookDetailInXlsx", value = "purchasedBookDetail/xlsx", method = RequestMethod.GET)
    public ListResponse<UserBookPurchasedResponse> userPurchasedBookDetailInXlsx(@RequestParam int year, @RequestParam int month) {
        ListResponse<UserBookPurchasedResponse> listResponse = new ListResponse<>();
        listResponse.setData(bookShopService.userPurchasedBookDetailInXlsx(year,month));
        listResponse.setStatus(Response.getOkResponse());
        return listResponse;
    }

    @SneakyThrows
    @RequestMapping(name = "importUserBookPurchasedDetailExcel", value = "/get/bookPurchasedDetail/excel", method = RequestMethod.GET)
    @Access(levels = {Role.ADMIN,Role.STUDENT})
    public ResponseEntity<Resource> importUserBookPurchasedDetailExcel(@RequestParam int year,@RequestParam int month) {
        Workbook workbook =bookShopService.importUserBookPurchasedDetailExcel(year,month);
        assert workbook != null;
        ByteArrayResource resource = ExcelUtil.getBiteResourceFromWorkbook(workbook);
        return ResponseEntity.ok()
                .contentLength(resource.contentLength())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "user_purchased_bookDetail_xlsx" + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(resource);
    }

    @SneakyThrows
    @Access(levels = {Role.STUDENT,Role.ADMIN})
    @RequestMapping(name = "getBookPurchasedDetail", value = "get/allUserDetail/by/month", method = RequestMethod.POST)
    public PageResponse<BookPurchasedDetailUserResponse> getBookPurchasedDetail(@RequestBody FilterSortRequest<BookShopFilter, BookSortBy> filterSortRequest) {
        PageResponse<BookPurchasedDetailUserResponse> pageResponse = new PageResponse<>();
        BookShopFilter filter = filterSortRequest.getFilter();
        FilterSortRequest.SortRequest<BookSortBy> sort = filterSortRequest.getSort();
        Pagination pagination = filterSortRequest.getPage();
        PageRequest pageRequest = PageRequest.of(pagination.getPage(), pagination.getLimit());
        pageResponse.setData(bookShopService.getBookPurchasedDetail(filter,sort,pageRequest));
        pageResponse.setStatus(Response.getOkResponse());
        return pageResponse;
    }

    @SneakyThrows
    @RequestMapping(name = "exportBookPurchasedDetailByMonthExcel", value = "/get/bookDetail/byMonth/excel", method = RequestMethod.POST)
    @Access(levels = {Role.ADMIN})
    public ResponseEntity<Resource> exportBookPurchasedDetailByMonthExcel(@RequestBody FilterSortRequest<BookShopFilter, BookSortBy> filterSortRequest) {
        BookShopFilter filter = filterSortRequest.getFilter();
        FilterSortRequest.SortRequest<BookSortBy> sort = filterSortRequest.getSort();
        Pagination pagination = filterSortRequest.getPage();
        PageRequest pageRequest = PageRequest.of(pagination.getPage(), pagination.getLimit());
        Workbook workbook =bookShopService.exportBookPurchasedDetailByMonthExcel(filter,sort,pageRequest);
        assert workbook != null;
        ByteArrayResource resource = ExcelUtil.getBiteResourceFromWorkbook(workbook);
        return ResponseEntity.ok()
                .contentLength(resource.contentLength())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "user_purchased_bookDetail_xlsx" + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(resource);
    }



}
