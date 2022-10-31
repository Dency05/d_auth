package com.example.sm.bookshop.service;

import com.example.sm.bookshop.decorator.*;
import com.example.sm.bookshop.enums.Amount;
import com.example.sm.bookshop.enums.BookSortBy;
import com.example.sm.bookshop.model.BookPurchaseLog;
import com.example.sm.bookshop.model.StudentLog;
import com.example.sm.common.decorator.FilterSortRequest;
import com.example.sm.common.enums.Role;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.JSONException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.mail.MessagingException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface BookShopService {
    BookShopResponse addOrUpdateBook(String id, BookShopAddRequest bookShopAddRequest) throws InvocationTargetException, IllegalAccessException;

    List<Student> getAllStudent(Role role) throws InvocationTargetException, IllegalAccessException;

    String purchaseBook(String id, String bookName,int day);

    void addBalance(Amount amount,String id) throws InvocationTargetException, IllegalAccessException;

    StudentLog saveStudent(String id);

    BookPurchaseResponses  getPurchaseBook(String year) throws InvocationTargetException, IllegalAccessException, JSONException;

    BookPurchaseLog reSalePurchasedBook(String bookId);

    List<StudentResponse> getAllStudentDetails() throws JSONException;

    Page<BookPurchaseLog> getStudentDetails(BookShopFilter bookShopFilter, FilterSortRequest.SortRequest<BookSortBy> sort, PageRequest pageRequest) throws InvocationTargetException, IllegalAccessException, JSONException;

    List<StudentDetailsResponse> getStudentInExcel() throws JSONException;

    Workbook importStudentInExcel() throws JSONException, InvocationTargetException, IllegalAccessException, MessagingException, IOException;
    Workbook importBookDetailInExcel(BookShopFilter filter, FilterSortRequest.SortRequest<BookSortBy> sort, PageRequest pageRequest) throws JSONException, InvocationTargetException, IllegalAccessException, IOException;

    Page<UserBookPurchasedResponse> getUserPurchasedDetail(BookShopFilter filter, FilterSortRequest.SortRequest<BookSortBy> sort, PageRequest pageRequest) throws JSONException;

    List<TotalUserCountByMonthResponse> totalUserCountByMonth(int year, int month) throws JSONException, InvocationTargetException, IllegalAccessException;

    List<UserBookPurchasedResponse> userPurchasedBookDetailInXlsx(int year, int month) throws JSONException;

    Workbook importUserBookPurchasedDetailExcel(int year, int month) throws JSONException, InvocationTargetException, IllegalAccessException, IOException;

    Page<BookPurchasedDetailUserResponse> getBookPurchasedDetail(BookShopFilter filter, FilterSortRequest.SortRequest<BookSortBy> sort, PageRequest pageRequest) throws JSONException;

    Workbook exportBookPurchasedDetailByMonthExcel(BookShopFilter filter, FilterSortRequest.SortRequest<BookSortBy> sort, PageRequest pageRequest) throws JSONException, InvocationTargetException, IllegalAccessException;
}
