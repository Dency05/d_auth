package com.example.sm.employee.repository;

import com.example.sm.employee.decorator.EmployeeResponse;
import com.example.sm.common.decorator.CountQueryResult;
import com.example.sm.common.decorator.CustomAggregationOperation;
import com.example.sm.common.decorator.EmployeeFilter;
import com.example.sm.common.decorator.FilterSortRequest;
import com.example.sm.common.enums.EmployeeSortBy;
import com.example.sm.employee.repository.EmployeeCustomRepository;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

public class EmployeeCustomRepositoryImpl implements EmployeeCustomRepository {
    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public List<EmployeeResponse> findAllEmployeeByFilterAndSortAndPage(EmployeeFilter filter, FilterSortRequest.SortRequest<EmployeeSortBy> sort, PageRequest pagination) throws InvocationTargetException, IllegalAccessException {
        List<AggregationOperation> operations = EmployeeFilterAggregation(filter, sort, pagination, true);


        //Created Aggregation operation
        Aggregation aggregation = newAggregation(operations);

        List<EmployeeResponse> users = mongoTemplate.aggregate(aggregation, "employee",EmployeeResponse.class).getMappedResults();

        // Find Count
        List<AggregationOperation> operationForCount = EmployeeFilterAggregation(filter, sort, pagination, false);
        operationForCount.add(group().count().as("count"));
        operationForCount.add(project("count"));
        Aggregation aggregationCount = newAggregation(EmployeeResponse.class, operationForCount);
        AggregationResults<CountQueryResult> countQueryResults = mongoTemplate.aggregate(aggregationCount, "users", CountQueryResult.class);
        long count = countQueryResults.getMappedResults().size() == 0 ? 0 : countQueryResults.getMappedResults().get(0).getCount();
        return users;
    }

    //create list
    //match user entered value and databasevalue(use: getCriteria method)
    //if addpage true then perfom sorting
    //return list

    private List<AggregationOperation> EmployeeFilterAggregation(EmployeeFilter filter, FilterSortRequest.SortRequest<EmployeeSortBy> sort, PageRequest pagination, boolean addPage) {
        List<AggregationOperation> operations = new ArrayList<>();

        operations.add(match(getCriteria(filter, operations)));

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

    private Criteria getCriteria(EmployeeFilter employeeFilter, List<AggregationOperation> operations) {
        Criteria criteria = new Criteria();
        operations.add(new CustomAggregationOperation(
                new Document("$addFields",
                        new Document("search",
                                new Document("$concat", Arrays.asList(
                                        new Document("$ifNull", Arrays.asList("$employeeName", "")),
                                        "|@|", new Document("$ifNull", Arrays.asList("$email", "")),
                                        "|@|", new Document("$ifNull", Arrays.asList("$Note", "")),
                                        "|@|", new Document("$ifNull", Arrays.asList("$Location", ""))
                                )
                                )
                        ))
        ));

        if (!StringUtils.isEmpty(employeeFilter.getSearch())) {
            employeeFilter.setSearch(employeeFilter.getSearch().replaceAll("\\|@\\|", ""));
            employeeFilter.setSearch(employeeFilter.getSearch().replaceAll("\\|@@\\|", ""));
            criteria = criteria.orOperator(
                    Criteria.where("search").regex(".*" + employeeFilter.getSearch() + ".*", "i")
            );
        }


        if (!StringUtils.isEmpty(employeeFilter.getId())) {
            criteria = criteria.and("_id").in(employeeFilter.getId());
        }

        criteria = criteria.and("softDelete").is(false);
        return criteria;
    }
}
