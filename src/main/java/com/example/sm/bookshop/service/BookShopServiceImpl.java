package com.example.sm.bookshop.service;

import com.example.sm.auth.model.UserModel;
import com.example.sm.auth.repository.UserRepository;
import com.example.sm.bookshop.decorator.*;
import com.example.sm.bookshop.enums.Amount;
import com.example.sm.bookshop.enums.BookSortBy;
import com.example.sm.bookshop.model.BookPurchaseLog;
import com.example.sm.bookshop.model.BookShop;
import com.example.sm.bookshop.model.StudentLog;
import com.example.sm.bookshop.repository.*;
import com.example.sm.common.constant.MessageConstant;
import com.example.sm.common.decorator.FilterSortRequest;
import com.example.sm.common.decorator.NullAwareBeanUtilsBean;
import com.example.sm.common.enums.Role;
import com.example.sm.common.exception.NotFoundException;
import com.example.sm.common.model.AdminConfiguration;
import com.example.sm.common.model.EmailModel;
import com.example.sm.common.service.AdminConfigurationService;
import com.example.sm.common.utils.ExcelUtil;
import com.example.sm.common.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.JSONException;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookShopServiceImpl implements  BookShopService {
    private  final BookShopRepository bookShopRepository;

    private  final UserRepository userRepository;
    private  final StudentRepository studentRepository;
    private  final BookPurchaseLogRepository bookPurchaseLogRepository;
    private  final StudentLogRepository studentLogRepository;
    private  final Utils utils;
    private  final ModelMapper modelMapper;
    private  final AdminConfigurationService adminService;

    private  final NullAwareBeanUtilsBean nullAwareBeanUtilsBean;
    private  final AdminConfiguration adminConfiguration;

    public BookShopServiceImpl(BookShopRepository bookShopRepository, UserRepository userRepository, StudentRepository studentRepository, BookPurchaseLogRepository bookPurchaseLogRepository, StudentLogRepository studentLogRepository, Utils utils, ModelMapper modelMapper, AdminConfigurationService adminService, NullAwareBeanUtilsBean nullAwareBeanUtilsBean, AdminConfiguration adminConfiguration) {
        this.bookShopRepository = bookShopRepository;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.bookPurchaseLogRepository = bookPurchaseLogRepository;
        this.studentLogRepository = studentLogRepository;
        this.utils = utils;
        this.modelMapper = modelMapper;
        this.adminService = adminService;
        this.nullAwareBeanUtilsBean = nullAwareBeanUtilsBean;
        this.adminConfiguration = adminConfiguration;
    }

    @Override
    public BookShopResponse addOrUpdateBook(String id, BookShopAddRequest bookShopAddRequest) throws InvocationTargetException, IllegalAccessException {
        double discount, price;
        if (id != null) {
            BookShop bookShop = getBookShop(id);
            nullAwareBeanUtilsBean.copyProperties(bookShop, bookShopAddRequest);
            bookShopRepository.save(bookShop);
            discount = 100 - bookShopAddRequest.getDiscount();
            price = (discount * bookShopAddRequest.getPrice()) / 100;
            bookShop.setAfterDiscountPrice(price);
            return modelMapper.map(bookShop, BookShopResponse.class);
        } else {
            BookShop bookShop = modelMapper.map(bookShopAddRequest, BookShop.class);
            bookShop.setDate(new Date());
            discount = 100 - bookShopAddRequest.getDiscount();
            price = (discount * bookShopAddRequest.getPrice()) / 100;
            bookShop.setAfterDiscountPrice(price);
            bookShopRepository.save(bookShop);
            return modelMapper.map(bookShop, BookShopResponse.class);
        }
    }
    @Override
    public List<Student> getAllStudent(Role role) throws InvocationTargetException, IllegalAccessException {
        List<UserModel> userModels = userRepository.findAllByRoleAndSoftDeleteFalse(role);
        List<Student> students = new ArrayList<>();
        Student student = new Student();
        if (!CollectionUtils.isEmpty(userModels)) {
            for (UserModel userModel : userModels) {
                nullAwareBeanUtilsBean.copyProperties(student, userModel);
                student.setBalance(2000.0);
                studentRepository.save(student);
                students.add(student);
            }
        }
        return students;
    }

    @Override
    public String purchaseBook(String id, String bookName, int day) {
        double balance;
        Student student = getStudent(id);
        BookShop bookShop = bookShop(bookName);
        BookPurchaseLog bookPurchaseLog = new BookPurchaseLog();
        if (bookShop.getAfterDiscountPrice() > student.getBalance()) {
            throw new NotFoundException(MessageConstant.BALANCE_NOT_AVAILABLE);
        } else {
            balance = student.getBalance() - bookShop.getAfterDiscountPrice();
            student.setBalance(balance);
            studentRepository.save(student);
            bookPurchaseLog.setStudentId(student.getId());
            bookPurchaseLog.setStudentName(student.getFullName());
            bookPurchaseLog.setBookName(bookShop.getBookName());
            bookPurchaseLog.setBalance(student.getBalance());
            bookPurchaseLog.setPrice(bookShop.getPrice());
            bookPurchaseLog.setDate(new Date());
            bookPurchaseLogRepository.save(bookPurchaseLog);
        }
        return null;
    }

    @Override
    public void addBalance(Amount amount, String id) throws InvocationTargetException, IllegalAccessException {
        AdminConfiguration adminConfiguration = adminService.getConfiguration();
        double balance;
        Student student = getStudent(id);
        balance = student.getBalance() + amount.getValue();
        student.setBalance(balance);
        studentRepository.save(student);
        try {
            EmailModel emailModel = new EmailModel();
            emailModel.setMessage("your current balance is :   " + student.getBalance());
            emailModel.setTo(student.getEmail());
            emailModel.setCc(adminConfiguration.getTechAdmins());
            emailModel.setSubject("Balance Status");
            utils.sendEmailNow(emailModel);
        } catch (Exception e) {
            log.error("Error happened while sending result to user :{}", e.getMessage());
        }
    }

    @Override
    public StudentLog saveStudent(String id) {
        List<BookPurchaseLog> bookPurchaseLog = bookShopRepository.getAllStudent(id);
        StudentLog studentLog = new StudentLog();
        if (!CollectionUtils.isEmpty(bookPurchaseLog)) {
            for (BookPurchaseLog purchaseLog : bookPurchaseLog) {
                studentLog.setId(purchaseLog.getStudentId());
            }
        }
        Set<String> newPlanIds = bookPurchaseLog.stream().map(BookPurchaseLog::getId).collect(Collectors.toSet());
        studentLog.setStudentId(newPlanIds);
        studentLogRepository.save(studentLog);
        return studentLog;

    }

    @Override
    public BookPurchaseResponses getPurchaseBook(String year) throws InvocationTargetException, IllegalAccessException, JSONException {
        AdminConfiguration adminConfiguration = adminService.getConfiguration();
        BookPurchaseResponses bookPurchaseResponses = new BookPurchaseResponses();
        List<BookPurchase> bookPurchases = new ArrayList<>(bookShopRepository.getPurchaseBook(year));
        System.out.println("bookPurchases:"+bookPurchases);
        Set<String> titles = new LinkedHashSet<>();
        int totalCount = 0;

        HashMap<String, String> bookNameHashMap = new LinkedHashMap<>();
        bookNameHashMap.put("1", "Indian Author Prem Rawat");
        bookNameHashMap.put("2", "Hear Your self");
        bookNameHashMap.put("3", "Hear Yourself");
        bookNameHashMap.put("4", "Monsoon");

        List<BookDetails> bookDetailsLists = new ArrayList<>();
        if (!CollectionUtils.isEmpty(bookPurchases)) {
            for (BookPurchase bookPurchase : bookPurchases) {
                bookDetailsLists = bookPurchase.getBookDetails();
                List<String> bookNames = bookDetailsLists.stream().map(BookDetails::getBookName).collect(Collectors.toList());
                log.info("Book Details:{}", bookNames);
                checkBookNameExist(bookNames, bookNameHashMap, bookDetailsLists, bookPurchase);
            }
        }


        for (Map.Entry<String, String> entry : adminConfiguration.getMonthTitles().entrySet()) {
            System.out.println("inside set title");
            System.out.println(entry.getKey());
            String titleName = entry.getKey() + " - " + entry.getValue();
            titles.add(titleName);
            bookPurchaseResponses.setTitle(titles);
            boolean exist = bookPurchases.stream().anyMatch(e -> e.getMonth() != null && e.getMonth().equals(entry.getValue()));
            if (!exist) { // check month is null or not if null then set count 0 and set month name
                System.out.println("inside if con");
                List<String> names = new ArrayList<>();
                List<BookDetails> bookDetails = new ArrayList<>();
                BookPurchase bookPurchase = new BookPurchase();
                bookPurchase.setMonth(entry.getValue());
                bookPurchase.setId(entry.getValue());
                bookPurchase.setTotalCount(0);
                checkBookNameExist(names, bookNameHashMap, bookDetails, bookPurchase);
                bookPurchase.setBookDetails(bookDetails);
                bookPurchases.add(bookPurchase);
            }
        }
        bookPurchases.sort(Comparator.comparing(BookPurchase::getMonth));
        bookPurchaseResponses.setBookPurchases(bookPurchases);
        for (BookPurchase bookPurchase : bookPurchases) {
            totalCount = totalCount + bookPurchase.getTotalCount();
            bookPurchaseResponses.setTotalCount(totalCount);
        }
        log.info("totalCount:{}", totalCount);
        System.out.println("bookPurchaseResponse:"+bookPurchaseResponses);
        return bookPurchaseResponses;
    }

    @Override
    public BookPurchaseLog reSalePurchasedBook(String bookId) {
        List<BookPurchaseLog> bookPurchaseLog = bookShopRepository.getStudentByIdAndBookId(bookId);
        if (CollectionUtils.isEmpty(bookPurchaseLog)) {
            throw new NotFoundException(MessageConstant.STUDENT_NOT_FOUND);
        }
        for (BookPurchaseLog purchaseLog : bookPurchaseLog) {
            String id = purchaseLog.getBookId();
            purchaseLog.setSoftDelete(true);
            bookPurchaseLogRepository.save(purchaseLog);
            BookShop bookShop = getBookShop(id);
            log.info("bookShop :{}", bookShop);
            double resaleDis = bookShop.getReSaleDiscount();
            double discount = 100 - resaleDis;
            double price = (discount * bookShop.getAfterDiscountPrice()) / 100;
            bookShop.setAfterReSalePrice(Math.round(price));
            bookShopRepository.save(bookShop);
            Student student = getStudent(purchaseLog.getStudentId());
            double balance = student.getBalance() - bookShop.getAfterReSalePrice();
            student.setBalance(Math.round(balance));
            studentRepository.save(student);
        }
        return null;
    }

    @Override
    public List<StudentResponse> getAllStudentDetails() throws JSONException {
        List<StudentResponse> studentResponse = bookShopRepository.getStudentDetails();
        if (CollectionUtils.isEmpty(studentResponse)) {
            throw new NotFoundException(MessageConstant.STUDENT_NOT_FOUND);
        } else {
            return studentResponse;
        }
    }

    @Override
    public Page<BookPurchaseLog> getStudentDetails(BookShopFilter bookShopFilter, FilterSortRequest.SortRequest<BookSortBy> sort, PageRequest pageRequest) throws InvocationTargetException, IllegalAccessException, JSONException {
        return      bookShopRepository.getStudentDetail(bookShopFilter, sort, pageRequest);
    }

    @Override
    public List<StudentDetailsResponse> getStudentInExcel() throws JSONException {
        return bookShopRepository.getDetails();
    }

    @Override
    public Workbook importStudentInExcel() throws JSONException{
        List<StudentDetailsResponse> studentDetailsResponses = getStudentInExcel();
        HashMap<String, List<BookData>> listHashMap = new HashMap<>();
        for (StudentDetailsResponse studentDetailsResponse : studentDetailsResponses) {
            List<BookData> list = studentDetailsResponse.getBookData();
            listHashMap.put(studentDetailsResponse.get_id(), list);
        }
       Workbook workBook=  ExcelUtil.createWorkbooks(listHashMap);
       createFileAndSendEmail(workBook);
       return workBook;
    }

    @Override
    public Workbook importBookDetailInExcel(BookShopFilter filter, FilterSortRequest.SortRequest<BookSortBy> sort, PageRequest pageRequest) throws JSONException, InvocationTargetException, IllegalAccessException, IOException {
        HashMap<String,List<BookDataExcel>> hashMap = new LinkedHashMap<>();
        Page<BookDetailExcelResponse> bookDetailsDataList = bookShopRepository.getBookDetail(filter,sort ,pageRequest);
        String title= "BookDetails";
        List<BookDetailExcelResponse> bookDetailExcelResponses=bookDetailsDataList.getContent();
        log.info("book Detail Excel Response:{}",bookDetailExcelResponses);
        if (!CollectionUtils.isEmpty(bookDetailExcelResponses)){
            for (BookDetailExcelResponse bookDetailExcelResponse : bookDetailExcelResponses) {
                List<BookDataExcel> bookResponseExcels = new ArrayList<>();
                for (BookDetail bookDatum : bookDetailExcelResponse.getBookDetail()) {
                    BookDataExcel bookDataExcel = new BookDataExcel();
                    nullAwareBeanUtilsBean.copyProperties(bookDataExcel, bookDatum);
                    bookResponseExcels.add(bookDataExcel);
                }
                hashMap.put(bookDetailExcelResponse.getStudentName(),bookResponseExcels);
            }
        }
        log.info("hashMap:{}",hashMap);
        Workbook workbook= ExcelUtil.createWorkbookOnBookDetailsData(hashMap,title);
        createFileAndSendEmail(workbook);
        return workbook;
    }

    @Override
    public Page<UserBookPurchasedResponse> getUserPurchasedDetail(BookShopFilter filter, FilterSortRequest.SortRequest<BookSortBy> sort, PageRequest pageRequest) throws JSONException {
        return bookShopRepository.getUserDetailByMonth(filter,sort,pageRequest);
    }

    @Override
    public List<TotalUserCountByMonthResponse> totalUserCountByMonth(int year, int month) throws JSONException, InvocationTargetException, IllegalAccessException {
       List<TotalUserCountByMonthResponse> totalUserCountByMonthResponses= bookShopRepository.totalUserCount(year, month);
       log.info("totalUser:{}",totalUserCountByMonthResponses.toString());
       return totalUserCountByMonthResponses;
    }

    @Override
    public List<UserBookPurchasedResponse> userPurchasedBookDetailInXlsx(int year, int month) throws JSONException {
        return bookShopRepository.getUserBookPurchasedDetailINXlsx(year, month);
    }

    @Override
    public Workbook importUserBookPurchasedDetailExcel(int year, int month) throws JSONException, InvocationTargetException, IllegalAccessException, IOException {
        HashMap<String,List<UserDetailPurchasedBookXlsx>> hashMap = new LinkedHashMap<>();
        List<UserBookPurchasedResponse> bookPurchasedResponseList= bookShopRepository.getUserBookPurchasedDetailINXlsx(year, month);
        String title= "User Purchased Book Detail";
        if (!CollectionUtils.isEmpty(bookPurchasedResponseList)){
            for (UserBookPurchasedResponse userBookPurchasedResponse : bookPurchasedResponseList) {
                List<UserDetailPurchasedBookXlsx> userDetailBookXlsx = new ArrayList<>();
                for (StudentData studentData :userBookPurchasedResponse.getStudentData()){
                    UserDetailPurchasedBookXlsx purchasedBookXlsx = new UserDetailPurchasedBookXlsx();
                    nullAwareBeanUtilsBean.copyProperties(purchasedBookXlsx,studentData);
                    userDetailBookXlsx.add(purchasedBookXlsx);
                }
                hashMap.put(userBookPurchasedResponse.getMonth(),userDetailBookXlsx);
            }
        }
        log.info("hashMap:{}",hashMap);
        Workbook workbook= ExcelUtil.createWorkbookOnUserPurchasedBookDetail(hashMap,title);
        createFileAndSendEmail(workbook);
        return workbook;
    }
    @Override
    public Page<BookPurchasedDetailUserResponse> getBookPurchasedDetail( BookShopFilter filter, FilterSortRequest.SortRequest<BookSortBy> sort, PageRequest pageRequest) throws JSONException {
        return bookShopRepository.getBookPurchasedDetailByMonth(filter, sort, pageRequest);
    }

    @Override
    public Workbook exportBookPurchasedDetailByMonthExcel(BookShopFilter filter, FilterSortRequest.SortRequest<BookSortBy> sort, PageRequest pageRequest) throws JSONException, InvocationTargetException, IllegalAccessException {
        Page<BookPurchasedDetailUserResponse> bookPurchasedDetailUserResponseList= bookShopRepository.getBookPurchasedDetailByMonth(filter, sort, pageRequest);
        System.out.println(bookPurchasedDetailUserResponseList);
        List<BookPurchasedDetailByMonthInExcel> bookPurchasedDetailByMonthInExcelList= new ArrayList<>();
        for (BookPurchasedDetailUserResponse bookPurchasedDetailUserResponse : bookPurchasedDetailUserResponseList.getContent()) {
            BookPurchasedDetailByMonthInExcel bookPurchasedDetailByMonthInExcel= new BookPurchasedDetailByMonthInExcel();
            nullAwareBeanUtilsBean.copyProperties(bookPurchasedDetailByMonthInExcel,bookPurchasedDetailUserResponse);
            bookPurchasedDetailByMonthInExcelList.add(bookPurchasedDetailByMonthInExcel);
        }
        String title= "user book purchased detail by Month : "+filter.getMonth();
        System.out.println(title);
        Workbook workbook =ExcelUtil.createWorkbookFromData(bookPurchasedDetailByMonthInExcelList,title);
        createFileAndSendEmail(workbook);
        return  workbook;
    }

    void checkBookNameExist(List<String> bookDetail, HashMap<String, String> bookName, List<BookDetails> bookDetailsLists, BookPurchase bookPurchase) {
        for (Map.Entry<String, String> entry : bookName.entrySet()) {
            if (!bookDetail.contains(entry.getValue())) {
                System.out.println("inside if con");
                BookDetails bookDetails = new BookDetails();
                bookDetails.setBookName(entry.getValue());
                bookDetails.setCount(0);
                bookDetailsLists.add(bookDetails);
            }
        }
        bookDetailsLists.sort(Comparator.comparing(BookDetails::getBookName));
        bookPurchase.setBookDetails(bookDetailsLists);
    }

    private BookShop getBookShop(String id) {
        return bookShopRepository.findByIdAndSoftDeleteIsFalse(id).orElseThrow(() -> new NotFoundException(MessageConstant.BOOK_ID_NOT_FOUND));
    }
    private Student getStudent(String id) {
        return studentRepository.findByIdAndSoftDeleteIsFalse(id).orElseThrow(() -> new NotFoundException(MessageConstant.STUDENT_NOT_FOUND));
    }
    private BookShop bookShop(String name) {
        return bookShopRepository.findByBookNameAndSoftDeleteIsFalse(name).orElseThrow(() -> new NotFoundException(MessageConstant.BOOK_NOT_FOUND));
    }

    private void createFileAndSendEmail(Workbook workBook) {
        try {
            File file = new File("BookData.xlsx");
            ByteArrayResource resource = ExcelUtil.getBiteResourceFromWorkbook(workBook);
            FileUtils.writeByteArrayToFile(file, resource.getByteArray());
            File path = new File("C:\\excelFiles\\" + file.getName());
            path.createNewFile();
            sendmail(path);
        }catch (Exception e){
            log.error("Error happened in excel generation or send email of excel: {}",e.getMessage());
        }
    }

    private void sendmail(File file) throws  InvocationTargetException, IllegalAccessException {
        AdminConfiguration adminConfiguration = adminService.getConfiguration();
        try {
            EmailModel emailModel = new EmailModel();
            emailModel.setTo("sarthak.j@techroversolutions.com");
            System.out.println(emailModel.getTo());
            emailModel.setCc(adminConfiguration.getTechAdmins());
            System.out.println(emailModel.getCc());
            emailModel.setSubject("User Book Data");
            emailModel.setFile(file);
            utils.sendEmailNow(emailModel);
        } catch (Exception e) {
            log.error("Error happened while sending result to user :{}", e.getMessage());
        }
    }
}

