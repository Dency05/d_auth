package com.example.sm.bookshop.repository;

import com.example.sm.bookshop.decorator.*;
import com.example.sm.bookshop.enums.BookSortBy;
import com.example.sm.bookshop.model.BookPurchaseLog;
import com.example.sm.common.decorator.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Slf4j
public class BookCustomRepositoryImpl implements  BookCustomRepository{

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    RequestSession requestSession;

    @Override
    public List<BookPurchaseLog> getAllStudent(String id) {
            Criteria criteria = new Criteria();
            criteria = criteria.and("studentId").is(id);
            Query query = new Query();
            query.addCriteria(criteria);
            List<BookPurchaseLog> membership_logs = mongoTemplate.find(query,BookPurchaseLog.class, "bookPurchaseLog");
            System.out.println(membership_logs.size());
            return membership_logs;
        }

    @Override
    public List<BookPurchaseLog> getStudentByIdAndBookId(String bookId) {
        Criteria criteria = new Criteria();
        String id= requestSession.getJwtUser().getId();
        log.info("id:{}",id);
        criteria = criteria.and("studentId").is(id);
        criteria= criteria.and("softDelete").is(false);
        criteria=criteria.and("bookId").is(bookId);
        Query query = new Query();
        query.addCriteria(criteria);
        List<BookPurchaseLog> bookPurchaseLog = mongoTemplate.find(query,BookPurchaseLog.class, "bookPurchaseLog");
        return bookPurchaseLog;
    }
    private List<AggregationOperation> getStudentByPagination(BookShopFilter bookShopFilter,FilterSortRequest.SortRequest<BookSortBy> sort, PageRequest pagination, boolean addPage) throws JSONException {
        List<AggregationOperation> operations = new ArrayList<>();
        String fileName= FileReader.loadFile("aggregation/getStudentByPagination.json");
        JSONObject jsonObject= new JSONObject(fileName);
        //operations.add(match(new Criteria("studentId").is(bookShopFilter.getId())));
        //operations.add(match(new Criteria("studentId").in(bookShopFilter.getIds())));
        operations.add(new CustomAggregationOperation(Document.parse(CustomAggregationOperation.getJson(jsonObject,"match",Object.class))));
        operations.add(new CustomAggregationOperation(Document.parse(CustomAggregationOperation.getJson(jsonObject,"lookupById",Object.class))));
        operations.add(new CustomAggregationOperation(Document.parse(CustomAggregationOperation.getJson(jsonObject,"unwindStudent",Object.class))));
        operations.add(new CustomAggregationOperation(Document.parse(CustomAggregationOperation.getJson(jsonObject,"studentData",Object.class))));
        operations.add(match(getCriteria(bookShopFilter, operations)));
        if (addPage) {
            //sorting
            if (sort != null && sort.getSortBy() != null && sort.getOrderBy() != null) {
                operations.add(new SortOperation(Sort.by(sort.getOrderBy(), sort.getSortBy().getValue())));
            }
            if (pagination != null) {
                operations.add(skip(pagination.getOffset()));
                operations.add(limit(pagination.getPageSize()));
            }
        }
        return operations;
    }

    private List<AggregationOperation> getStudent() throws JSONException {
        List<AggregationOperation> operations = new ArrayList<>();
        String fileName = FileReader.loadFile("aggregation/studentDetails.json");
        JSONObject jsonObject = new JSONObject(fileName);
        operations.add(new CustomAggregationOperation(Document.parse(CustomAggregationOperation.getJson(jsonObject, "match", Object.class))));
        operations.add(new CustomAggregationOperation(Document.parse(CustomAggregationOperation.getJson(jsonObject, "groupById", Object.class))));
        return  operations;
    }

    private List<AggregationOperation> getbookdetails(String year) throws JSONException {
        List<AggregationOperation> operations = new ArrayList<>();
        String fileName= FileReader.loadFile("aggregation/bookPurchase.json");
        JSONObject jsonObject= new JSONObject(fileName);
        operations.add(new CustomAggregationOperation(Document.parse(CustomAggregationOperation.getJson(jsonObject,"extractMonth&Year",Object.class))));
        operations.add(match(new Criteria("dateOfYear").is(year)));
        operations.add(new CustomAggregationOperation(Document.parse(CustomAggregationOperation.getJson(jsonObject,"groupByMonth",Object.class))));
        operations.add(new CustomAggregationOperation(Document.parse(CustomAggregationOperation.getJson(jsonObject,"groupByBookName",Object.class))));
        operations.add(new CustomAggregationOperation(Document.parse(CustomAggregationOperation.getJson(jsonObject,"bookData",Object.class))));
        operations.add(new CustomAggregationOperation(Document.parse(CustomAggregationOperation.getJson(jsonObject,"groupByMonths",Object.class))));
        return operations;
    }

    private List<AggregationOperation> getTotalCountOfUser(int year,int month) throws JSONException {
        List<AggregationOperation> operations = new ArrayList<>();
        String fileName= FileReader.loadFile("aggregation/totalUserCountByMonth.json");
        JSONObject jsonObject= new JSONObject(fileName);
        operations.add(new CustomAggregationOperation(Document.parse(CustomAggregationOperation.getJson(jsonObject,"match",Object.class))));
        operations.add(new CustomAggregationOperation(Document.parse(CustomAggregationOperation.getJson(jsonObject,"extractMonth&Year",Object.class))));
        operations.add(match(new Criteria("dateOfMonth").is(month)));
        operations.add(match(new Criteria("year").is(year)));
        operations.add(new CustomAggregationOperation(Document.parse(CustomAggregationOperation.getJson(jsonObject,"lookupOnStudent",Object.class))));
        operations.add(new CustomAggregationOperation(Document.parse(CustomAggregationOperation.getJson(jsonObject,"unwindStudent",Object.class))));
        operations.add(new CustomAggregationOperation(Document.parse(CustomAggregationOperation.getJson(jsonObject,"groupBy",Object.class))));
        operations.add(new CustomAggregationOperation(Document.parse(CustomAggregationOperation.getJson(jsonObject,"groupByMonth",Object.class))));
        operations.add(new CustomAggregationOperation(Document.parse(CustomAggregationOperation.getJson(jsonObject,"project",Object.class))));
        return operations;
    }

    private List<AggregationOperation> getUserDetailByMonthYear(BookShopFilter bookShopFilter,FilterSortRequest.SortRequest<BookSortBy> sort, PageRequest pagination, boolean addPage) throws JSONException {
        List<AggregationOperation> operations = new ArrayList<>();
        String fileName = FileReader.loadFile("aggregation/userBookPurchasedDetailByMonth&Year.json");
        JSONObject jsonObject = new JSONObject(fileName);
        operations.add(new CustomAggregationOperation(Document.parse(CustomAggregationOperation.getJson(jsonObject, "match", Object.class))));
        operations.add(new CustomAggregationOperation(Document.parse(CustomAggregationOperation.getJson(jsonObject, "extractMonth&Year", Object.class))));
        operations.add(match(getCriteria(bookShopFilter, operations)));
        operations.add(match(new Criteria("dateOfMonth").is(bookShopFilter.getMonth())));
        operations.add(match(new Criteria("year").is(bookShopFilter.getYear())));
        operations.add(new CustomAggregationOperation(Document.parse(CustomAggregationOperation.getJson(jsonObject, "lookupOnStudent", Object.class))));
        operations.add(new CustomAggregationOperation(Document.parse(CustomAggregationOperation.getJson(jsonObject, "unwindStudent", Object.class))));
        operations.add(new CustomAggregationOperation(Document.parse(CustomAggregationOperation.getJson(jsonObject, "groupBy", Object.class))));
        if (addPage) {
            //sorting
            if (sort != null && sort.getSortBy() != null && sort.getOrderBy() != null) {
                operations.add(new SortOperation(Sort.by(sort.getOrderBy(), sort.getSortBy().getValue())));
            }
            if (pagination != null) {
                operations.add(skip(pagination.getOffset()));
                operations.add(limit(pagination.getPageSize()));
            }
        }
        return operations;
    }

    private List<AggregationOperation> getUserDetailInXlsx(int year,int month) throws JSONException {
        List<AggregationOperation> operations = new ArrayList<>();
        String fileName= FileReader.loadFile("aggregation/userPurchasedBookDetailInXlsx.json");
        JSONObject jsonObject= new JSONObject(fileName);
        operations.add(new CustomAggregationOperation(Document.parse(CustomAggregationOperation.getJson(jsonObject,"match",Object.class))));
        operations.add(new CustomAggregationOperation(Document.parse(CustomAggregationOperation.getJson(jsonObject,"extractMonth&Year",Object.class))));
        operations.add(match(new Criteria("dateOfMonth").is(month)));
        operations.add(match(new Criteria("year").is(year)));
        operations.add(new CustomAggregationOperation(Document.parse(CustomAggregationOperation.getJson(jsonObject,"lookupOnStudent",Object.class))));
        operations.add(new CustomAggregationOperation(Document.parse(CustomAggregationOperation.getJson(jsonObject,"unwindStudent",Object.class))));
        operations.add(new CustomAggregationOperation(Document.parse(CustomAggregationOperation.getJson(jsonObject,"groupBy",Object.class))));
        operations.add(new CustomAggregationOperation(Document.parse(CustomAggregationOperation.getJson(jsonObject,"groupByMonth",Object.class))));
        return operations;
    }

    private List<AggregationOperation> getStudentData() throws JSONException {
        List<AggregationOperation> operations = new ArrayList<>();
        String fileName = FileReader.loadFile("aggregation/studentDataInExcel.json");
        JSONObject jsonObject = new JSONObject(fileName);
        operations.add(new CustomAggregationOperation(Document.parse(CustomAggregationOperation.getJson(jsonObject, "match", Object.class))));
        operations.add(new CustomAggregationOperation(Document.parse(CustomAggregationOperation.getJson(jsonObject, "mergeData", Object.class))));
        operations.add(new CustomAggregationOperation(Document.parse(CustomAggregationOperation.getJson(jsonObject, "unwindStudent", Object.class))));
        operations.add(new CustomAggregationOperation(Document.parse(CustomAggregationOperation.getJson(jsonObject, "groupByBookName", Object.class))));
       return operations;
    }

    private List<AggregationOperation> getBookData(BookShopFilter bookShopFilter,FilterSortRequest.SortRequest<BookSortBy> sort, PageRequest pagination, boolean addPage) throws JSONException {
        List<AggregationOperation> operations = new ArrayList<>();
        String fileName = FileReader.loadFile("aggregation/bookDetailsInExcel.json");
        JSONObject jsonObject = new JSONObject(fileName);
        operations.add(match(getCriteria(bookShopFilter, operations)));
        operations.add(new CustomAggregationOperation(Document.parse(CustomAggregationOperation.getJson(jsonObject, "match", Object.class))));
        operations.add(new CustomAggregationOperation(Document.parse(CustomAggregationOperation.getJson(jsonObject,  "mergeStudentData", Object.class))));
        operations.add(new CustomAggregationOperation(Document.parse(CustomAggregationOperation.getJson(jsonObject, "unwindStudent", Object.class))));
        operations.add(new CustomAggregationOperation(Document.parse(CustomAggregationOperation.getJson(jsonObject,  "groupByBookName", Object.class))));
        operations.add(new CustomAggregationOperation(Document.parse(CustomAggregationOperation.getJson(jsonObject,  "groupByStudentName", Object.class))));

        if (addPage) {
            //sorting
            if (sort != null && sort.getSortBy() != null && sort.getOrderBy() != null) {
                operations.add(new SortOperation(Sort.by(sort.getOrderBy(), sort.getSortBy().getValue())));
            }
            if (pagination != null) {
                operations.add(skip(pagination.getOffset()));
                operations.add(limit(pagination.getPageSize()));
            }
        }
        return operations;
    }
    private List<AggregationOperation> getUserDetailByMonth(BookShopFilter bookShopFilter,FilterSortRequest.SortRequest<BookSortBy> sort, PageRequest pagination, boolean addPage) throws JSONException {
        List<AggregationOperation> operations = new ArrayList<>();
        String fileName = FileReader.loadFile("aggregation/userPurchaseBookDetailByMonth.json");
        JSONObject jsonObject = new JSONObject(fileName);

        operations.add(new CustomAggregationOperation(Document.parse(CustomAggregationOperation.getJson(jsonObject, "match", Object.class))));
        operations.add(new CustomAggregationOperation(Document.parse(CustomAggregationOperation.getJson(jsonObject,  "lookUpOnStudent", Object.class))));
        operations.add(new CustomAggregationOperation(Document.parse(CustomAggregationOperation.getJson(jsonObject, "unwindStudent", Object.class))));
        operations.add(new CustomAggregationOperation(Document.parse(CustomAggregationOperation.getJson(jsonObject,  "extractMonth", Object.class))));
        operations.add(new CustomAggregationOperation(Document.parse(CustomAggregationOperation.getJson(jsonObject,  "groupBy", Object.class))));
        operations.add(new CustomAggregationOperation(Document.parse(CustomAggregationOperation.getJson(jsonObject,  "groupByMonth", Object.class))));
        operations.add(match(getCriteria(bookShopFilter, operations)));
        if (addPage) {
            //sorting
            if (sort != null && sort.getSortBy() != null && sort.getOrderBy() != null) {
                operations.add(new SortOperation(Sort.by(sort.getOrderBy(), sort.getSortBy().getValue())));
            }
            if (pagination != null) {
                operations.add(skip(pagination.getOffset()));
                operations.add(limit(pagination.getPageSize()));
            }
        }
        return operations;
    }





    private Criteria getCriteria(BookShopFilter bookShopFilter, List<AggregationOperation> operations) {
        Criteria criteria = new Criteria();
        operations.add(new CustomAggregationOperation(
                new Document("$addFields",
                        new Document("search",
                                new Document("$concat", Arrays.asList(
                                        new Document("$ifNull", Arrays.asList("$bookName", "")),
                                        "|@|", new Document("$ifNull", Arrays.asList("$studentName", "")),
                                        "|@|", new Document("$ifNull", Arrays.asList("$month", ""))
                                )))
                )
        ));
        if (!StringUtils.isEmpty(bookShopFilter.getSearch())) {
            bookShopFilter.setSearch(bookShopFilter.getSearch().replaceAll("\\|@\\|", ""));
            bookShopFilter.setSearch(bookShopFilter.getSearch().replaceAll("\\|@@\\|", ""));
            criteria = criteria.orOperator(
                    Criteria.where("search").regex(".*" + bookShopFilter.getSearch() + ".*", "i")
            );
        }
        return criteria;
    }

    @Override
    public List<StudentResponse> getStudentDetails() throws JSONException {
        List<AggregationOperation> operations = getStudent();
        Aggregation aggregation = newAggregation(operations);
        return mongoTemplate.aggregate(aggregation, "bookPurchaseLog",StudentResponse.class).getMappedResults();
    }

    public List<UserBookPurchasedResponse> getUserBookPurchasedDetailINXlsx(int year,int month) throws JSONException {
        List<AggregationOperation> operations = getUserDetailInXlsx(year, month);
        Aggregation aggregation = newAggregation(operations);
        return mongoTemplate.aggregate(aggregation, "bookPurchaseLog",UserBookPurchasedResponse.class).getMappedResults();
    }

    public List<TotalUserCountByMonthResponse> totalUserCount(int year, int month) throws JSONException {
        List<AggregationOperation> operations = getTotalCountOfUser(year, month);
        Aggregation aggregation = newAggregation(operations);
        return mongoTemplate.aggregate(aggregation, "bookPurchaseLog",TotalUserCountByMonthResponse.class).getMappedResults();
    }

    public List<StudentDetailsResponse> getDetails() throws JSONException {
        List<AggregationOperation> operations = getStudentData();
        Aggregation aggregation = newAggregation(operations);
        return mongoTemplate.aggregate(aggregation, "bookPurchaseLog",StudentDetailsResponse.class).getMappedResults();
    }

    public Page<BookPurchasedDetailUserResponse> getBookPurchasedDetailByMonth(BookShopFilter bookShopFilter, FilterSortRequest.SortRequest<BookSortBy> sort, PageRequest pageRequest) throws JSONException {
        List<AggregationOperation> operations = getUserDetailByMonthYear(bookShopFilter,sort,pageRequest,true);
        Aggregation aggregation = newAggregation(operations);
        List<BookPurchasedDetailUserResponse> bookDetailExcelResponses= mongoTemplate.aggregate(aggregation, "bookPurchaseLog",BookPurchasedDetailUserResponse.class).getMappedResults();
        List<AggregationOperation> operationForCount = getStudentByPagination( bookShopFilter,sort, pageRequest, false);
        operationForCount.add(group().count().as("count"));
        operationForCount.add(project("count"));
        Aggregation aggregationCount = newAggregation(BookPurchaseLog.class, operationForCount);
        AggregationResults<CountQueryResult> countQueryResults = mongoTemplate.aggregate(aggregationCount, "bookPurchaseLog", CountQueryResult.class);
        long count = countQueryResults.getMappedResults().size() == 0 ? 0 : countQueryResults.getMappedResults().get(0).getCount();
        return PageableExecutionUtils.getPage(
                bookDetailExcelResponses,
                pageRequest,
                () -> count);
    }


    public Page<BookDetailExcelResponse> getBookDetail(BookShopFilter bookShopFilter, FilterSortRequest.SortRequest<BookSortBy> sort, PageRequest pageRequest) throws JSONException {
        List<AggregationOperation> operations = getBookData(bookShopFilter,sort,pageRequest,true);
        Aggregation aggregation = newAggregation(operations);
        List<BookDetailExcelResponse> bookDetailExcelResponses= mongoTemplate.aggregate(aggregation, "bookPurchaseLog",BookDetailExcelResponse.class).getMappedResults();
        List<AggregationOperation> operationForCount = getStudentByPagination( bookShopFilter,sort, pageRequest, false);
        operationForCount.add(group().count().as("count"));
        operationForCount.add(project("count"));
        Aggregation aggregationCount = newAggregation(BookPurchaseLog.class, operationForCount);
        AggregationResults<CountQueryResult> countQueryResults = mongoTemplate.aggregate(aggregationCount, "bookPurchaseLog", CountQueryResult.class);
        long count = countQueryResults.getMappedResults().size() == 0 ? 0 : countQueryResults.getMappedResults().get(0).getCount();
        return PageableExecutionUtils.getPage(
                bookDetailExcelResponses,
                pageRequest,
                () -> count);
    }

    public List<BookPurchase> getPurchaseBook(String year) throws JSONException {
        List<AggregationOperation> operations = getbookdetails(year);
        Aggregation aggregation = newAggregation(operations);
        return mongoTemplate.aggregate(aggregation, "bookPurchaseLog",BookPurchase.class).getMappedResults();
    }

    @Override
    public Page<BookPurchaseLog> getStudentDetail(BookShopFilter bookShopFilter, FilterSortRequest.SortRequest<BookSortBy> sort, PageRequest pageRequest) throws InvocationTargetException, IllegalAccessException, JSONException {
        List<AggregationOperation> operations = getStudentByPagination(bookShopFilter,sort,pageRequest,true);
        //Created Aggregation operation
        Aggregation aggregation = newAggregation(operations);

        List<BookPurchaseLog> bookPurchaseLog = mongoTemplate.aggregate(aggregation, "bookPurchaseLog",BookPurchaseLog.class).getMappedResults();
        log.info("bookPurchaseLog:{}",bookPurchaseLog);
        List<AggregationOperation> operationForCount = getStudentByPagination( bookShopFilter,sort, pageRequest, false);
        operationForCount.add(group().count().as("count"));
        operationForCount.add(project("count"));
        Aggregation aggregationCount = newAggregation(BookPurchaseLog.class, operationForCount);
        AggregationResults<CountQueryResult> countQueryResults = mongoTemplate.aggregate(aggregationCount, "bookPurchaseLog", CountQueryResult.class);
        long count = countQueryResults.getMappedResults().size() == 0 ? 0 : countQueryResults.getMappedResults().get(0).getCount();
        return PageableExecutionUtils.getPage(
                bookPurchaseLog,
                pageRequest,
                () -> count);
    }


    public Page<UserBookPurchasedResponse> getUserDetailByMonth(BookShopFilter bookShopFilter, FilterSortRequest.SortRequest<BookSortBy> sort, PageRequest pageRequest) throws JSONException {
        List<AggregationOperation> operations = getUserDetailByMonth(bookShopFilter,sort,pageRequest,true);
        Aggregation aggregation = newAggregation(operations);
        List<UserBookPurchasedResponse> bookDetailExcelResponses= mongoTemplate.aggregate(aggregation, "bookPurchaseLog", UserBookPurchasedResponse.class).getMappedResults();
        List<AggregationOperation> operationForCount = getUserDetailByMonth( bookShopFilter,sort, pageRequest, false);
        operationForCount.add(group().count().as("count"));
        operationForCount.add(project("count"));
        Aggregation aggregationCount = newAggregation(BookPurchaseLog.class, operationForCount);
        AggregationResults<CountQueryResult> countQueryResults = mongoTemplate.aggregate(aggregationCount, "bookPurchaseLog", CountQueryResult.class);
        long count = countQueryResults.getMappedResults().size() == 0 ? 0 : countQueryResults.getMappedResults().get(0).getCount();
        return PageableExecutionUtils.getPage(
                bookDetailExcelResponses,
                pageRequest,
                () -> count);
    }
}
