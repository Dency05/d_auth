package com.example.sm.auth;

import com.example.sm.auth.repository.UserRepository;
import com.example.sm.bookshop.decorator.BookDetails;
import com.example.sm.bookshop.decorator.BookPurchase;
import com.example.sm.bookshop.decorator.BookPurchaseResponses;
import com.example.sm.bookshop.decorator.BookShopFilter;
import com.example.sm.bookshop.enums.BookSortBy;
import com.example.sm.bookshop.model.BookPurchaseLog;
import com.example.sm.bookshop.repository.BookPurchaseLogRepository;
import com.example.sm.bookshop.repository.BookShopRepository;
import com.example.sm.bookshop.repository.StudentLogRepository;
import com.example.sm.bookshop.repository.StudentRepository;
import com.example.sm.bookshop.service.BookShopService;
import com.example.sm.bookshop.service.BookShopServiceImpl;
import com.example.sm.common.decorator.FilterSortRequest;
import com.example.sm.common.decorator.NullAwareBeanUtilsBean;
import com.example.sm.common.decorator.Pagination;
import com.example.sm.common.enums.Role;
import com.example.sm.common.model.AdminConfiguration;
import com.example.sm.common.service.AdminConfigurationService;
import com.example.sm.common.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;

import java.util.*;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
@ContextConfiguration
@Slf4j
class BookServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final BookShopRepository bookShopRepository = mock(BookShopRepository.class);
    private final StudentRepository studentRepository = mock(StudentRepository.class);
    private final BookPurchaseLogRepository bookPurchaseLogRepository = mock(BookPurchaseLogRepository.class);
    private final StudentLogRepository studentLogRepository = mock(StudentLogRepository.class);
    private final Utils utils = mock(Utils.class);
    private final ModelMapper modelMapper = mock(ModelMapper.class);
    private final AdminConfigurationService adminService = mock(AdminConfigurationService.class);
    private final NullAwareBeanUtilsBean nullAwareBeanUtilsBean = mock(NullAwareBeanUtilsBean.class);
    private final AdminConfiguration adminConfiguration = mock(AdminConfiguration.class);
    public BookShopService bookShopService = new BookShopServiceImpl(bookShopRepository, userRepository, studentRepository, bookPurchaseLogRepository, studentLogRepository, utils, modelMapper, adminService,nullAwareBeanUtilsBean, adminConfiguration);



    @Test
    void getPurchaseBookByYear() {
        try {
            var bookDetail = List.of(BookDetails.builder()
                                                               .bookName("Monsoon")
                                                               .count(1)
                                                               .build());

            var bookPurchase = List.of(BookPurchase.builder()
                                                                  .bookDetails(bookDetail)
                                                                  .id("9")
                                                                  .month("9")
                                                                  .totalCount(1)
                                                                  .build());

            var adminConfiguration = AdminConfiguration.builder().monthTitles(setMonth()).build();

            when(adminService.getConfiguration()).thenReturn(adminConfiguration);
            /*Set<String> title = new LinkedHashSet<>();
            for (Map.Entry<String, String> entry : adminConfiguration.getMonthTitles().entrySet()) {
                String titleName = entry.getKey() + " - " + entry.getValue();
                title.add(titleName);
            }*/

            /*var expectedBookPurchaseResponse = List.of(BookPurchaseResponses.builder()
                                                                           .bookPurchases(bookPurchase)
                                                                           .title(title).totalCount(1.0));
*/
            when(bookShopRepository.getPurchaseBook("2022")).thenReturn(bookPurchase);

            bookShopService.getPurchaseBook("2022");

           // assertEquals(expectedBookPurchaseResponse, bookShopService.getPurchaseBook("2022"));

        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }
    Map<String, String> setMonth(){
        Map<String,String > monthTitles= new LinkedHashMap<>();
        monthTitles.put("Jan","01");
        monthTitles.put("feb","02");
        monthTitles.put("mar","03");
        monthTitles.put("apr","04");
        monthTitles.put("may","05");
        monthTitles.put("jun","06");
        monthTitles.put("jul","07");
        monthTitles.put("aug","08");
        monthTitles.put("sep","09");
        monthTitles.put("oct","10");
        monthTitles.put("nov","11");
        monthTitles.put("dec","12");
        return monthTitles;
    }


    @Test
    void getAllStudentDetailByPagination(){
        var  bookPurchaseLog = List.of(BookPurchaseLog.builder().bookId("123").studentId("456").balance(8.9).date(new Date()).price(8.9).studentName("dency").role(Role.ANONYMOUS).softDelete(false).build());

        Page<BookPurchaseLog> page = new PageImpl<>(bookPurchaseLog);//list convert to page
        System.out.println(page.getContent());

        FilterSortRequest<BookShopFilter, BookSortBy> filterSortRequest= new FilterSortRequest<>();
        BookShopFilter filter = filterSortRequest.getFilter();
        FilterSortRequest.SortRequest<BookSortBy> sortBySortRequest= new FilterSortRequest.SortRequest<>();
        sortBySortRequest.setOrderBy(Sort.Direction.ASC);
        Pagination pagination= new Pagination();
        pagination.setLimit(10);
        pagination.setPage(0);
        PageRequest pageRequest = PageRequest.of(pagination.getPage(), pagination.getLimit());
        filterSortRequest.setFilter(filter);
        filterSortRequest.setSort(sortBySortRequest);
        filterSortRequest.setPage(pagination);

        try {
            //when
            when(bookShopRepository.getStudentDetail(filter,sortBySortRequest,pageRequest)).thenReturn(page);

            //then
            Assertions.assertEquals(page,bookShopService.getStudentDetails(filter,sortBySortRequest,pageRequest));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}