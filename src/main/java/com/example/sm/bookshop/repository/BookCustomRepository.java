package com.example.sm.bookshop.repository;

import com.example.sm.bookshop.decorator.*;
import com.example.sm.bookshop.enums.BookSortBy;
import com.example.sm.bookshop.model.BookPurchaseLog;
import com.example.sm.common.decorator.FilterSortRequest;
import org.json.JSONException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface BookCustomRepository {

    List<BookPurchaseLog> getAllStudent(String id);
   List<BookPurchase> getPurchaseBook(String year)throws JSONException;

    List<BookPurchaseLog> getStudentByIdAndBookId(String bookId);

    List<StudentResponse> getStudentDetails() throws JSONException;
    Page<BookPurchaseLog> getStudentDetail(BookShopFilter bookShopFilter, FilterSortRequest.SortRequest<BookSortBy> sort, PageRequest pageRequest) throws InvocationTargetException, IllegalAccessException, JSONException;
    List<StudentDetailsResponse> getDetails()throws JSONException;

    Page<BookDetailExcelResponse> getBookDetail(BookShopFilter bookShopFilter, FilterSortRequest.SortRequest<BookSortBy> sort, PageRequest pageRequest)throws JSONException;
    Page<UserBookPurchasedResponse> getUserDetailByMonth(BookShopFilter bookShopFilter, FilterSortRequest.SortRequest<BookSortBy> sort, PageRequest pageRequest) throws JSONException ;

    List<TotalUserCountByMonthResponse> totalUserCount(int year, int month) throws JSONException;
    Page<BookPurchasedDetailUserResponse> getBookPurchasedDetailByMonth(BookShopFilter bookShopFilter, FilterSortRequest.SortRequest<BookSortBy> sort, PageRequest pageRequest)throws JSONException;
    List<UserBookPurchasedResponse> getUserBookPurchasedDetailINXlsx(int year,int month) throws JSONException;
}
