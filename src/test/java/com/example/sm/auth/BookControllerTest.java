package com.example.sm.auth;

import com.example.sm.auth.decorator.UserFilter;
import com.example.sm.auth.enums.UserSortBy;
import com.example.sm.bookshop.decorator.BookShopFilter;
import com.example.sm.bookshop.enums.BookSortBy;
import com.example.sm.bookshop.model.BookShop;
import com.example.sm.common.decorator.DataResponse;
import com.example.sm.common.decorator.FilterSortRequest;
import com.example.sm.common.decorator.Pagination;
import com.example.sm.common.enums.CustomHTTPHeaders;
import com.example.sm.common.model.JWTUser;
import com.example.sm.common.utils.JwtTokenUtil;
import com.example.sm.helper.DataSetBookShopHelper;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Slf4j
public class BookControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    DataSetBookShopHelper dataSetBookShopHelper;

    @BeforeEach
    public void setUp(){
        dataSetBookShopHelper.cleanUp();
        dataSetBookShopHelper.init();
    }

    @Test
    void exportBookPurchasedDetailByMonthExcel() {
        try {
            BookShop bookShop= dataSetBookShopHelper.getBookShop();
            JWTUser jwtUser = new JWTUser(bookShop.getId(), Collections.singletonList(bookShop.getRole().toString()));
            String token = jwtTokenUtil.generateToken(jwtUser);
            FilterSortRequest<BookShopFilter, BookSortBy> filterSortRequest= new FilterSortRequest<>();
            BookShopFilter bookShopFilter = new BookShopFilter();
            bookShopFilter.setMonth(9);
            bookShopFilter.setYear(2022);
            FilterSortRequest.SortRequest<BookSortBy> sortBySortRequest= new FilterSortRequest.SortRequest<>();
            sortBySortRequest.setOrderBy(Sort.Direction.ASC);
            Pagination pagination= new Pagination();
            pagination.setLimit(10);
            pagination.setPage(0);
            PageRequest pageRequest = PageRequest.of(pagination.getPage(), pagination.getLimit());
            filterSortRequest.setFilter(bookShopFilter);
            filterSortRequest.setSort(sortBySortRequest);
            filterSortRequest.setPage(pagination);
            String json = new Gson().toJson(filterSortRequest);
            MvcResult mvcResult = mockMvc.perform(post("/book/get/bookDetail/byMonth/excel")
                            .header(CustomHTTPHeaders.TOKEN.toString(), token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isOk()).andReturn();

            String result = mvcResult.getResponse().getContentAsString();
            System.out.println(result);
            DataResponse dataResponse = new Gson().fromJson(result, new TypeToken<DataResponse>() {
            }.getType());

            Assertions.assertEquals("Ok", dataResponse.getStatus().getStatus());
            dataResponse.getStatus().getDescription();
            log.info("ok.....");
        }
        catch (Exception e) {
            log.error( "response not match :{} ",e.getMessage());
        }
    }

    @Test
    void importUserBookPurchasedDetailExcel() {
        try {
            BookShop bookShop= dataSetBookShopHelper.getBookShop();
            JWTUser jwtUser = new JWTUser(bookShop.getId(), Collections.singletonList(bookShop.getRole().toString()));
            String token = jwtTokenUtil.generateToken(jwtUser);
            MvcResult mvcResult = mockMvc.perform(get("/book/get/bookPurchasedDetail/excel?month="+9 +"&year="+2022)
                            .header(CustomHTTPHeaders.TOKEN.toString(), token)
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isOk()).andReturn();

            Object result = mvcResult.getResponse().getContentAsString();

            System.out.println(result);


            /*DataResponse dataResponse = new Gson().fromJson(result, new TypeToken<DataResponse>() {
            }.getType());

            Assertions.assertEquals("Ok", dataResponse.getStatus().getStatus());
            dataResponse.getStatus().getDescription();*/
            log.info("ok.....");
        }
        catch (Exception e) {
            log.error( "response not match :{} ",e.getMessage());
        }
    }




}


