package com.example.sm.auth.service;

import com.amazonaws.services.athena.model.InvalidRequestException;
import com.example.sm.auth.decorator.*;
import com.example.sm.auth.enums.UserSortBy;
import com.example.sm.auth.enums.UserStatus;
import com.example.sm.auth.model.UserModel;
import com.example.sm.auth.rabbitmq.UserPublisher;
import com.example.sm.auth.repository.UserRepository;
import com.example.sm.common.constant.MessageConstant;
import com.example.sm.common.decorator.RequestSession;
import com.example.sm.common.decorator.*;
import com.example.sm.common.enums.PasswordEncryptionType;
import com.example.sm.common.enums.Role;
import com.example.sm.common.exception.AlreadyExistException;
import com.example.sm.common.exception.EmptyException;
import com.example.sm.common.exception.InvaildRequestException;
import com.example.sm.common.exception.NotFoundException;
import com.example.sm.common.model.*;
import com.example.sm.common.repository.ImportedDataRepository;
import com.example.sm.common.repository.UserDataRepository;
import com.example.sm.common.service.AdminConfigurationService;
import com.example.sm.common.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.JSONException;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements  UserService {
    private final UserRepository userRepository;
    private final ImportedDataRepository importedDataRepository;

    private final UserDataRepository userDataRepository;

    private final NullAwareBeanUtilsBean nullAwareBeanUtilsBean;

    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordUtils passwordUtils;
    private final AdminConfigurationService adminService;
    private  final  Utils utils;

    private final NotificationParser notificationParser;

    private final UserPublisher userPublisher;
    private final ModelMapper modelMapper;
    private final RequestSession requestSession;
    public UserServiceImpl(UserRepository userRepository, ImportedDataRepository importedDataRepository, UserDataRepository userDataRepository, NullAwareBeanUtilsBean nullAwareBeanUtilsBean, JwtTokenUtil jwtTokenUtil, PasswordUtils passwordUtils, AdminConfigurationService adminService, Utils utils, NotificationParser notificationParser, UserPublisher userPublisher, ModelMapper modelMapper, RequestSession requestSession) {
        this.userRepository = userRepository;
        this.importedDataRepository = importedDataRepository;
        this.userDataRepository = userDataRepository;
        this.nullAwareBeanUtilsBean = nullAwareBeanUtilsBean;
        this.jwtTokenUtil = jwtTokenUtil;
        this.passwordUtils = passwordUtils;
        this.adminService = adminService;
        this.utils = utils;
        this.notificationParser = notificationParser;
        this.userPublisher = userPublisher;
        this.modelMapper = modelMapper;
        this.requestSession = requestSession;
    }

    //1.update
    //pass id, check  if id not null then perform update else add perform
    //check (user entered  id and database id) matched--> setvariable --> save --> copy -->return userResponse
    //2.add
    //pass role
    //copy properties userAddRequest to userModel
    //set role in database
    //save in database
    //copy properties userModel to userResponse
    //return userResponse

    @Override
    public UserResponse addOrUpdateUser(UserAddRequest userAddRequest, String id, Role role) throws InvocationTargetException, IllegalAccessException {
        if (id != null) {
            UserModel userResponse1 = getUserModel(id);
            nullAwareBeanUtilsBean.copyProperties(userResponse1, userAddRequest);
            userRepository.save(userResponse1);
            UserResponse userResponse = modelMapper.map(userResponse1,UserResponse.class);
            return userResponse;
        } else {
            if (role == null)//check user role
                throw new InvaildRequestException(MessageConstant.ROLE_NOT_FOUND);
        }
        checkUserDetails(userAddRequest);//check empty or not
        setAgeFromBirthdate(userAddRequest);//set Age from Birthdate
        UserModel userModel= new UserModel();
        nullAwareBeanUtilsBean.copyProperties(userModel, userAddRequest);
        userModel.setRole(role);//set role in database
        userModel.setFullName();//set fullName
        userModel.setCreatedBy(requestSession.getJwtUser().getId());
        userModel.setPassword(passwordUtils.encryptPassword(userAddRequest.getPassword()));
        userRepository.save(userModel);
        UserResponse userResponse = new UserResponse();
        nullAwareBeanUtilsBean.copyProperties(userResponse, userModel);
        return userResponse;
    }

    @Override
    public List<UserResponse> getAllUser() throws InvocationTargetException, IllegalAccessException {
        System.out.println("inside con");
        List<UserModel> userModels = userRepository.findAllBySoftDeleteFalse();
        System.out.println("userModels:"+userModels);
        List<UserResponse> userResponses = new ArrayList<>();
        if (!CollectionUtils.isEmpty(userModels)) {
            for (UserModel userModel : userModels) {
                UserResponse userResponse = new UserResponse();
                nullAwareBeanUtilsBean.copyProperties(userResponse, userModel);
                userResponses.add(userResponse);
            }
        }
        return userResponses;
    }

    @Override
    public List<UserResponse> getUsers(Set<String> ids) throws InvocationTargetException, IllegalAccessException {
        List<UserModel> userModel= userRepository.findByIdInAndSoftDeleteIsFalse(ids);
        log.info("userModel:{}",userModel);
        //userPublisher.publishToQueue(id);
        List<UserResponse> userResponse = new ArrayList<>();
        nullAwareBeanUtilsBean.copyProperties(userResponse, userModel);
        return userResponse;
    }

    @Override
    public void deleteUser(String id) {
        UserModel userModel = getUserModel(id);
        userModel.setSoftDelete(true);
        userRepository.save(userModel);
    }

    @Override
    public Page<UserModel> getAllUserWithFilterAndSort(UserFilter filter, FilterSortRequest.SortRequest
            <UserSortBy> sort, PageRequest pagination) throws InvocationTargetException, IllegalAccessException {
        return userRepository.findAllUserByFilterAndSortAndPage(filter, sort, pagination);
    }


    //1.pass id
    //2.check user entered id and database id and softDeleteFalse --> matched -->set role
    //JWT token generate
    //response token.
    @Override
    public UserResponse getToken(String id) throws InvocationTargetException, IllegalAccessException {
        UserModel userModel = getUserModel(id);
        UserResponse userResponse = new UserResponse();
        userResponse.setRole(userModel.getRole());
        JWTUser jwtUser = new JWTUser(id, Collections.singletonList(userResponse.getRole().toString()));
        log.info("JWTUser :{}",jwtUser);
        String token = jwtTokenUtil.generateToken(jwtUser);
        log.info("token :{}",token);
        nullAwareBeanUtilsBean.copyProperties(userResponse, userModel);
        userResponse.setToken(token);
        return userResponse;
    }

    //id
    //username n password
    //check password is empty or not
    //if not empty then encrypt (passwordUtils)
    //if empty hen though error
    //if password is found then store the encrypted password to the provided ID's user.
    //save to database
    @Override
    public String getEncryptPassword(String id) throws InvocationTargetException, IllegalAccessException {
        UserModel userModel = getUserModel(id);
        UserResponse userResponse = new UserResponse();
        userResponse.setUserName(userModel.getUserName());
        userResponse.setPassword(userModel.getPassword());
        System.out.println("password:"+userModel.getPassword());
        if (userModel.getPassword() != null) {
            String password = passwordUtils.encryptPassword(userModel.getPassword());
            System.out.println(password);
            userModel.setPassword(password);
            userRepository.save(userModel);
            String passwords = userModel.getPassword();
            return passwords;
        } else {
            throw new NotFoundException(MessageConstant.PASSWORD_EMPTY);
        }
    }

    //check password is valid or not.
    //STEPS
    //1. pass email,password
    //2. check email is vaild or not
    //3. get password from database
    //4. PasswordUtils is PasswordAuthenticated method call
    //5. if true (Provide User Details)
    // else Error -> Password not matched
    @Override
    public UserResponse checkUserAuthentication(String email, String password) throws NoSuchAlgorithmException, InvocationTargetException, IllegalAccessException {
        UserModel userModel = getUserEmail(email);
        getEncryptPassword(userModel.getId());
        System.out.println(userModel.getPassword());
        UserResponse userResponse = new UserResponse();
        userResponse.setPassword(userModel.getPassword());
        String getPassword = userResponse.getPassword();
        System.out.println("password"+getPassword);
        boolean passwords = passwordUtils.isPasswordAuthenticated(password, getPassword, PasswordEncryptionType.BCRYPT);
        System.out.println("passwords"+passwords);
        if (passwords) {
            userRepository.save(userModel);
            nullAwareBeanUtilsBean.copyProperties(userResponse, userModel);
            return userResponse;
        } else {
            throw new NotFoundException(MessageConstant.PASSWORD_NOT_MATCHED);
        }
    }

    //pass token
    //getUserIdFromToken() call from jwtTokenUtil
    //check generate id and database id is matched then show user details
    //if not matched then error
    @Override
    public String getIdFromToken(String token) {
        String Id = jwtTokenUtil.getUserIdFromToken(token);
        boolean exists = userRepository.existsByIdAndSoftDeleteFalse(Id);
        if (exists) {
            return Id;
        } else {
            throw new InvaildRequestException(MessageConstant.INVAILD_TOKEN);
        }
    }

    //pass token
    //method call getIdFromToken()
    //getid from getIdFromToken()
    //Database id set getid
    //validateToken() method call from jwtTokenUtil
    //check if true than show userdetails else error show
    @Override
    public UserResponse getValidityOfToken(String token) throws InvocationTargetException, IllegalAccessException {
        UserResponse userResponse = new UserResponse();
        String validateToken = getIdFromToken(token);
        UserModel userResponse1 = getUserModel(validateToken);
        userResponse.setId(validateToken);
        String tokenId = userResponse.getId();
        JWTUser jwtUser = new JWTUser(tokenId, new ArrayList<>());
        boolean getValidate = jwtTokenUtil.validateToken(token, jwtUser);
        System.out.println(getValidate);
        if (getValidate) {
            nullAwareBeanUtilsBean.copyProperties(userResponse, userResponse1);
            return userResponse;
        } else {
            throw new NotFoundException(MessageConstant.TOKEN_NOT_VAILD);
        }
    }

    //pass email, password
    //check username n password
    //generate random otp and set into emailmodel
    //set message and To into emailmodel
    //call the method sendEmailNow from utils
    //Otp save in userModel

    @Override
    public String login(String email, String password) throws NoSuchAlgorithmException, InvocationTargetException, IllegalAccessException {
        AdminConfiguration adminConfiguration = adminService.getConfiguration();
        UserModel userModel = getUserEmail(email);
        boolean passwords = passwordUtils.isPasswordAuthenticated(password,userModel.getPassword(), PasswordEncryptionType.BCRYPT);
        System.out.println(passwords);
        if (passwords) {
            EmailModel emailModel = new EmailModel();
            String otp = generateOtp();
            emailModel.setMessage(otp);
            emailModel.setTo(userModel.getEmail());
            log.info("email:{}",userModel.getEmail());
            emailModel.setCc(adminConfiguration.getTechAdmins());
            emailModel.setSubject("Otp Verification");
            utils.sendEmailNow(emailModel);
            userModel.setOtp(otp);
            userModel.setLogin(true);
            userModel.setUserStatus(UserStatus.ACTIVE);
            userRepository.save(userModel);
            JWTUser jwtUser = new JWTUser(userModel.getId(), Collections.singletonList(userModel.getRole().toString()));
            String token = jwtTokenUtil.generateToken(jwtUser);
            log.info("token:{}",token);
            return token;
        } else {
            throw new NotFoundException(MessageConstant.PASSWORD_NOT_MATCHED);
        }
    }

    //pass id,otp
    //check existsBy(id,otp)
    //if exists then show all details
    //else error
    @Override
    public UserResponse getOtp(String otp, String id) throws InvocationTargetException, IllegalAccessException {
        boolean exists = userRepository.existsByIdAndOtpAndSoftDeleteFalse(id, otp);
        if (exists) {
            UserModel userResponse1 = getUserModel(id);
            UserResponse userResponse = new UserResponse();
            nullAwareBeanUtilsBean.copyProperties(userResponse, userResponse1);
            return userResponse;
        } else {
            throw new NotFoundException(MessageConstant.INVAILD_OTP);
        }
    }

    //pass email
    //user enter email match to database
    //if it is true then otp send
    @Override
    public void forgotPassword(String email) {
        UserModel userModel = getUserEmail(email);
        EmailModel emailModel = new EmailModel();
        String otp = generateOtp();
        emailModel.setMessage(otp);
        emailModel.setTo("sarthak.j@techroversolutions.com");
        emailModel.setSubject("Otp Verification");
        utils.sendEmailNow(emailModel);
        userModel.setOtp(otp);
        userRepository.save(userModel);
    }

    //pass password, confirm password
    //match password and confirm password is equal or not
    //if match then call login api
    //not match then show error
    @Override
    public void setPassword(String password, String confirmPassword, String id) {
        if (password.equals(confirmPassword)) {
            UserModel userModel = getUserModel(id);
            System.out.println(userModel);
            System.out.println("confirm password:"+confirmPassword);
            userModel.setPassword(passwordUtils.encryptPassword(confirmPassword));
            userRepository.save(userModel);
        } else {
            throw new NotFoundException(MessageConstant.PASSWORD_NOT_MATCHED);
        }
    }

    @Override
    public void otpVerifications(String id, String otp) {
        boolean exists = userRepository.existsByIdAndOtpAndSoftDeleteFalse(id, otp);
        System.out.println(exists);
        if (!exists) {
            throw new NotFoundException(MessageConstant.INVAILD_OTP);
        }
    }

    //pass password
    //check to database password
    //new password , confirm password
    //match --> set to database  else---> error
    @Override
    public void changePassword(String password, String confirmPassword, String newPassword, String id) throws NoSuchAlgorithmException {
        UserModel userModel = getUserModel(id);
        String userPassword = userModel.getPassword();
        boolean passwords = passwordUtils.isPasswordAuthenticated(password, userPassword, PasswordEncryptionType.BCRYPT);
        if (passwords) {
            if (newPassword.equals(confirmPassword)) {
                String confirmPasswords = passwordUtils.encryptPassword(confirmPassword);
                userModel.setPassword(confirmPasswords);
                userRepository.save(userModel);
            } else {
                throw new NotFoundException(MessageConstant.PASSWORD_NOT_MATCHED);
            }
        } else {
            throw new NotFoundException(MessageConstant.INVAILD_PASSWORD);
        }
    }

    //pass id
    //check to database id
    //match then set login false
    @Override
    public void logOut(String id) {
        UserModel userModel = getUserModel(id);
        userModel.setLogin(false);
        Date date = new Date();
        userModel.setLogoutTime(date);
        userRepository.save(userModel);
    }

    //if user pass role as student then
    //show only student data
    @Override
    public List<UserResponse> getUserByRole(UserFilter userFilter) {
        return userRepository.getUser(userFilter);
    }
    @Override
    public UserResponse resultDetail(Result result, String id) throws InvocationTargetException, IllegalAccessException {
        System.out.println(id);
        UserModel userModel = getUserModel(id);
        if (userModel.getRole().equals(Role.STUDENT) && userModel.getRole().equals(Role.ADMIN)) {//role is student or not?
            throw new NotFoundException(MessageConstant.ROLE_NOT_MATCHED);
        }
        checkResultCond(result);   //common condition  check
        result.setDate(new Date());

        List<Result> results = new ArrayList<>();
        if (!CollectionUtils.isEmpty(userModel.getResults())) {
            results = userModel.getResults();
        }

        double cgpi = 0.0;
        if (CollectionUtils.isEmpty(results)) {
            results.add(result);
            cgpi = result.getSpi();
        } else {
            for (Result result1 : results) {
                if (result1.getSemester() == result.getSemester()) {
                    throw new AlreadyExistException(MessageConstant.SEMESTER_EXISTS);
                }
            }
            results.add(result);

            double total = 0, avg = 0;
            for (Result semester : results) {
                total = total + semester.getSpi();
            }
            if (total > 0) {
                avg = total / results.size();
                DecimalFormat df = new DecimalFormat("0.00");
                cgpi = Double.parseDouble(df.format(avg));
            }
        }
        userModel.setCgpi(cgpi);
        userModel.setResults(results);
        userRepository.save(userModel);
        //sendResultEmail(userModel, result);//send email
        UserResponse userResponse = new UserResponse();
        nullAwareBeanUtilsBean.copyProperties(userResponse, userModel);
        return userResponse;
    }

    //create method for mail send to user about result
    private void sendResultEmail(UserModel userModel, Result result) throws InvocationTargetException, IllegalAccessException {
        AdminConfiguration adminConfiguration = adminService.getConfiguration();
        ResultEmailRequest resultEmailRequest = modelMapper.map(userModel, ResultEmailRequest.class);
        resultEmailRequest.setSemester(Integer.toString(result.getSemester()));
        resultEmailRequest.setSpi(Double.toString(result.getSpi()));
        resultEmailRequest.setCgpi(Double.toString(userModel.getCgpi()));
        try {
            TemplateModel templateModel = adminConfiguration.getNotificationConfiguration().getResultTemplate();
            EmailModel emailModel = notificationParser.parseEmailNotification(templateModel, resultEmailRequest, userModel.getEmail());
            emailModel.setCc(adminConfiguration.getTechAdmins());
            utils.sendEmailNow(emailModel);
        } catch (Exception e) {
            log.error("Error happened while sending result to user :{}", e.getMessage());
        }
    }

    @Override
    public List<UserDetailResponse> getUserResult(UserDetail userDetail) {
        return userRepository.getUserResult(userDetail);
    }

    //user pass multiple id and semester
    //match in database, unwind result , and find this result total marks and average // group all of data
    //find total marks and average to this user
    @Override
    public List<UserResultResponse> getUserResultBySemester(UserResult userResult) {
        return userRepository.getUserResultBySemester(userResult);
    }

    @Override
    public List<UserMinMaxMarkSemResponse> getUserResultByMinMaxSem(UserIdsRequest userIdsRequest) {
        return userRepository.getUserResultByMinMaxMark(userIdsRequest);
    }

    @Override
    public void deleteUserResult(String id, int semester) {
        UserModel userModel = getUserModel(id);
        List<Result> results1 = userModel.getResults();
        if (!CollectionUtils.isEmpty(userModel.getResults())) {//check Result empty or not
            for (Result semesters : results1) {
                if (semesters.getSemester() == semester) {
                    //check  users entered semester equals to  database semester
                    System.out.println("inside if con");
                    boolean result = results1.remove(semesters);
                    System.out.println(result);
                    if (result) {
                        userRepository.save(userModel);
                    }
                    break;
                }
            }
        } else {
            throw new NotFoundException(MessageConstant.SEMESTER_NOT_FOUND);
        }
    }

    @Override
    public List<UserResultByDateRespose> getUserResultByDate(UserResultByDate userResultByDate) {
        return userRepository.getUserResultByDate(userResultByDate);
    }

    @Override
    public UserResponse updateUserResult(String id, int semester, Resultupdate result) throws InvocationTargetException, IllegalAccessException {
        UserModel userModel = getUserModel(id);
        List<Result> results1 = userModel.getResults();
        UserResponse userResponse = new UserResponse();
        if (!CollectionUtils.isEmpty(userModel.getResults())) {//check Result empty or not
            for (Result semesters : results1) {
                if (semesters.getSemester() == semester) {
                    semesters.setSemester(semester);
                    if (Double.toString(result.getSpi()) != null) {
                        semesters.setSpi(result.getSpi());
                    }
                    if (Integer.toString(result.getYear()) != null) {
                        semesters.setYear(result.getYear());
                    }
                    semesters.setDate(new Date());
                    nullAwareBeanUtilsBean.copyProperties(userModel, semesters);
                    userRepository.save(userModel);
                    nullAwareBeanUtilsBean.copyProperties(userResponse, userModel);
                }
            }
               return userResponse;
        } else {
            throw new NotFoundException(MessageConstant.RESULT_EMPTY);
        }
    }

    //pass mutiple id
    //aggregation operation(branch switch case)
    //set the user result status like(firstClass, secondClass, fourth,fifth)
    @Override
    public List<UserResultByStatus> getUserResultByStatus(UserIdsRequest userIdsRequest) {
        return userRepository.getUserResultByStatus(userIdsRequest);
    }

    @Override
    public Page<UserResultByStatus> getUserResultStatusByPagination(UserIdsRequest userIdsRequest, FilterSortRequest.SortRequest<UserSortBy> sort, PageRequest pagination) throws InvocationTargetException, IllegalAccessException {
        return userRepository.findUserResultStatusByFilterAndSortAndPage(userIdsRequest, sort, pagination);
    }

    @Override
    public Workbook getUserByExcel(UserFilter userFilter, FilterSortRequest.SortRequest<UserSortBy> sort, PageRequest pagination) throws InvocationTargetException, IllegalAccessException {
        Page<UserModel> userResponses = getUserWithPagination(userFilter,sort,pagination);
        List<UserResponseExcel> userResponseExcel = new ArrayList<>();
        for (UserModel userResponse : userResponses.getContent()) {//useResponses.for
            UserResponseExcel userResponseExcel1 = new UserResponseExcel();
            nullAwareBeanUtilsBean.copyProperties(userResponseExcel1, userResponse);
            userResponseExcel.add(userResponseExcel1);
            log.info("id" + userResponse.getId());
            log.info("name" + userResponse.getFirstName());
            log.info("ids" + userResponseExcel1.getId());
        }
       String title= "student data";
        return ExcelUtil.createWorkbookFromData(userResponseExcel,title);
    }

    @Override
    public void resultDetailByEmail(String id) throws InvocationTargetException, IllegalAccessException {
        UserModel userModel = getUserModel(id);
        //check Result empty or not
        if (!CollectionUtils.isEmpty(userModel.getResults())) {
            sendResultDetails(userModel);
        }
    }

    //update user detail
    //admin can all user update
    //student only update student user
    //department only update department and student user
    //send mail to user that which item user is updated
    @Override
    public void userUpdate(String id, Role role, UserAddRequest userAddRequest) throws InvocationTargetException, IllegalAccessException {
        UserModel usermodel = getUserModel(id);
        HashMap<String,String> changedProperties = new HashMap<>();
        boolean userUpdate = false ;
        if (role == Role.ADMIN) {
            userUpdate =true;
        }
        else if (role == Role.DEPARTMENT) {
            if (!usermodel.getRole().equals(Role.ADMIN)) {
                userUpdate =true;
            } else {
                throw new NotFoundException(MessageConstant.ROLE_NOT_MATCHED);
            }
        } else if (role == Role.STUDENT) {
            if (usermodel.getRole().equals(Role.STUDENT)) {
                userUpdate =true;
            } else {
                throw new NotFoundException(MessageConstant.ROLE_NOT_MATCHED);
            }
        }
        if(userUpdate){
            System.out.println("inside if con ");
            updateUserDetail(id, userAddRequest,usermodel);
            difference(usermodel, userAddRequest,changedProperties);
            emailSend(changedProperties);
        }
        else {
            throw new InvaildRequestException(MessageConstant.ROLE_NOT_MATCHED);
        }
    }


    @Override
    public void userDelete(String id, Role role) throws InvocationTargetException, IllegalAccessException {
        UserModel userModel = getUserModel(id);
        AdminConfiguration adminConfiguration = adminService.getConfiguration();
        if (role == Role.ADMIN) {
            userModel.setSoftDelete(true);
            userRepository.save(userModel);
            try {
                EmailModel emailModel = new EmailModel();
                emailModel.setTo(userModel.getEmail());
                emailModel.setCc(adminConfiguration.getTechAdmins());
                emailModel.setMessage("Delete Successfully...");
                emailModel.setSubject("USer Detail");
                utils.sendEmailNow(emailModel);
            } catch (Exception e) {
                log.error("Error happened while sending result to user :{}", e.getMessage());
            }
        } else {
            throw new NotFoundException(MessageConstant.INVAILD_ROLE);
        }
    }

    public String uploadFile(MultipartFile uploadFile) throws IOException {
        if (uploadFile.isEmpty()) {
            throw new EmptyException(MessageConstant.FILE_IS_EMPTY);
        }
        saveUploadedFiles(Arrays.asList(uploadFile));
        log.info( "Successfully uploaded - " + uploadFile.getOriginalFilename());
        return  "Successfully uploaded - " + uploadFile.getOriginalFilename();
    }

    @Override
    public UserImportResponse importUsers(MultipartFile file, String id) throws IOException, InvocationTargetException, IllegalAccessException {
        InputStream is = file.getInputStream();
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        AdminConfiguration adminConfiguration = adminService.getConfiguration();
        ImportedData data = ImportExcelDataHelper.getDataFromExcel(is,extension);
        UserImportedData importedData = modelMapper.map(data, UserImportedData.class);
        if (!CollectionUtils.isEmpty(importedData.getData())){
            for (Map.Entry<String, List<Object>> map : importedData.getData().entrySet()){
                if (!CollectionUtils.isEmpty(map.getValue())) {
                    if (map.getValue().size()>adminConfiguration.getImportRecordLimit()){
                        throw new InvalidRequestException(MessageConstant.RECORD_SIZE_EXCEED);
                    }
                }
            }
        }
        importedData.setImportDate(new Date());
        importedData = importedDataRepository.save(importedData);
        UserImportResponse importResponse = new UserImportResponse();
        importResponse.setMappingHeaders(adminConfiguration.getUserImportMappingFields());
        importResponse.setExcelHeaders(importedData.getHeaders().stream().map(ImportExcelDataHelper::recoverExcelHeader).collect(Collectors.toList()));
        importResponse.setId(importedData.getId());
        return importResponse;
    }

    //pass FirstName: firstname, as it all header and pass id
    //check  id in importdataUser
    //set the value in database
    //check email is exists or not in imported xlxs data if email is same then throw error
    //add all data in user_imported_data collection(json formt)
    @Override
    public List<UserDataModel> importUsersVerify(UserImportVerifyRequest verifyRequest) {
        List<UserDataModel> users;
        //check id in database
        UserImportedData data = importedDataRepository.findById(verifyRequest.getId()).orElseThrow(()->new NotFoundException(MessageConstant.NO_RECORD_FOUND));
        //set import date
        System.out.println("data"+data);
        Date importDate = new Date();
        users = getUserFromImportedData(data,verifyRequest,importDate);

//        if (userDataRepository.findAllByImportedIdAndSoftDeleteIsFalse(verifyRequest.getId()).isEmpty()){
        setAndGetDuplicateEmailUsers(users);

        users = userDataRepository.saveAll(users);
        if(!CollectionUtils.isEmpty(users)){
            userDataRepository.saveAll(users);
        }
       /* }else{
            users = userDataRepository.findAllByImportedIdAndSoftDeleteIsFalse(verifyRequest.getId());
        }*/

        return users;
    }

    @Override
    public List<UserResponse> importDataInUser(UserIdsRequest userIdsRequest) throws InvocationTargetException, IllegalAccessException {
       List<UserDataModel> userDataModel= getUserDataModel(userIdsRequest.getUserId());
        AdminConfiguration adminConfiguration = adminService.getConfiguration();
       log.info("--------------start---------------");
       List<UserResponse> userResponses = new ArrayList<>();
       log.info("import data added in list:{}",userDataModel.size());
        for (UserDataModel dataModel : userDataModel) {
            log.info("email:{}",dataModel.getEmail());
            boolean exists = userRepository.existsByEmailAndSoftDeleteFalse(dataModel.getEmail());
            if(exists) {
                throw  new AlreadyExistException(MessageConstant.EMAIL_NAME_EXISTS);
            }
            else{
                UserModel userModel1= modelMapper.map(dataModel,UserModel.class);
                userModel1.setUserStatus(UserStatus.INVITED);
                try {
                    EmailModel emailModel = new EmailModel();
                    emailModel.setTo(dataModel.getEmail());
                    emailModel.setCc(adminConfiguration.getTechAdmins());
                    emailModel.setMessage("your details stored in the system");
                    emailModel.setSubject("User Details");
                    utils.sendEmailNow(emailModel);
                } catch (Exception e) {
                    log.error("Error happened while sending result to user :{}", e.getMessage());
                }
                userRepository.save(userModel1);
                UserResponse userResponse = modelMapper.map(userModel1, UserResponse.class);
                userResponses.add(userResponse);
            }
        }
        return userResponses;
    }

    @Override
    public void deleteUserInXls(String id) {
      UserDataModel userDataModel= getUserData(id);
      userDataModel.setSoftDelete(true);
      userDataRepository.save(userDataModel);
    }

    @Override
    public void getUserPassword(String userName, String password, String confirmPassword) throws InvocationTargetException, IllegalAccessException {
        UserModel userModel= getUserName(userName);
        AdminConfiguration adminConfiguration = adminService.getConfiguration();
        if(password.equals(confirmPassword)){
            String passwords = passwordUtils.encryptPassword(confirmPassword);
            log.info("password:{}",passwords);
            userModel.setPassword(passwords);
            userRepository.save(userModel);
            EmailModel emailModel = new EmailModel();
            String otp = generateOtp();
            userModel.setOtp(otp);
            emailModel.setMessage(utils.sendOtp(userModel,confirmPassword));
            emailModel.setTo(userModel.getEmail());
            emailModel.setCc(adminConfiguration.getTechAdmins());
            emailModel.setSubject("Otp Verification");
            utils.sendEmailNow(emailModel);
        }
    }

    @Override
    public void sendMailToInvitedUser(UserStatus userStatus) throws InvocationTargetException, IllegalAccessException {
        AdminConfiguration adminConfiguration = adminService.getConfiguration();
        if(userStatus.equals(UserStatus.INVITED)){
            List<UserModel> userModel= userRepository.findByUserStatusAndSoftDeleteIsFalse(userStatus);
            for (UserModel model : userModel) {
                UserModel userModel1 = modelMapper.map(model, UserModel.class);
                try {
                    EmailModel emailModel = new EmailModel();
                    emailModel.setMessage("your details stored in the system");
                    emailModel.setTo(userModel1.getEmail());
                    emailModel.setCc(adminConfiguration.getTechAdmins());
                    emailModel.setSubject("User detail");
                    utils.sendEmailNow(emailModel);
                } catch (Exception e) {
                    log.error("Error happened while sending result to user :{}", e.getMessage());
                }
            }
        }
    }

    @Override
    public void checkUserPublisherId(String id) {

    }
    @Override
    public String sendMessage(String id) {
        log.info("ID : {}",id);
        return id;
    }

    @Override
    public MonthTitleName getUserDetailByMonth(String year) throws JSONException, InvocationTargetException, IllegalAccessException {
        List<UserDetailByMonth> userDetailByMonths= new ArrayList<>(userRepository.getUserByMonth(year));
        System.out.println("userDetailByMonths"+userDetailByMonths);
        MonthTitleName monthTitleNames= new MonthTitleName();
        double totalCount= 0;
        Set<String> titles= new LinkedHashSet<>();
        HashMap<String,String> title= new LinkedHashMap<>();
        title.put("Jan","1");
        title.put("feb","2");
        title.put("mar","3");
        title.put("apr","4");
        title.put("may","5");
        title.put("jun","6");
        title.put("jul","7");
        title.put("aug","8");
        title.put("sep","9");
        title.put("oct","10");
        title.put("nov","11");
        title.put("dec","12");

        for (Map.Entry<String,String> entry : title.entrySet()) {
            String titleName = entry.getKey() + " - " + year;
            titles.add(titleName);
            System.out.println("title"+titles);
            boolean exist = userDetailByMonths.stream().anyMatch(e -> e.getMonth().equals(entry.getValue()));
            System.out.println(exist);
            if(!exist){// check month is null or not if null then set count 0 and set month name
                System.out.println("inside if con");
                UserDetailByMonth userDetailByMonth1= new UserDetailByMonth();
                userDetailByMonth1.setCount(0.0);
                userDetailByMonth1.setMonth(entry.getValue());
                userDetailByMonths.add(userDetailByMonth1);
                System.out.println("userDetailMon"+userDetailByMonths);
                monthTitleNames.setTitle(titles);
            }
        }
        userDetailByMonths.sort(Comparator.comparing(UserDetailByMonth::getMonth));
        monthTitleNames.setUserDetailByMonths(userDetailByMonths);

        for (UserDetailByMonth userDetailByMonth : userDetailByMonths) {
            totalCount= totalCount+userDetailByMonth.getCount();
            monthTitleNames.setTotalCount(totalCount);
        }
        System.out.println("final"+monthTitleNames);
        return monthTitleNames;
    }

    @Override
    public UserResponse getUser(String id) throws InvocationTargetException, IllegalAccessException {
        UserModel userModel= getUserModel(id);
        System.out.println("userModel:"+userModel);
        UserResponse userResponse= new UserResponse();
        nullAwareBeanUtilsBean.copyProperties(userResponse,userModel);
        System.out.println("userResponse:"+userResponse);
        return userResponse;
    }

    @Override
    public void getAllUserByPagination() {
        Pagination pagination= new Pagination();
        pagination.setLimit(30);
        FilterSortRequest.SortRequest<UserSortBy> sortBySortRequest= new FilterSortRequest.SortRequest<>();
        sortBySortRequest.setOrderBy(Sort.Direction.ASC);
        sortBySortRequest.setSortBy(UserSortBy.EMAIL);
        Page<UserModel> userModels = null;
        List<UserModel> userModels1= new ArrayList<>();
        int i= 0;
        do{
            pagination.setPage(i++);
            PageRequest pageRequest = PageRequest.of(pagination.getPage(), pagination.getLimit());
            userModels= userRepository.getAllUser(new UserFilter(),sortBySortRequest,pageRequest);
            System.out.println("userModels"+userModels);
            userModels1.addAll(userModels.getContent());
        }
        while(userModels.hasNext());
        for (UserModel userModel : userModels1) {
            userModel.setFullName();
            userRepository.save(userModel);
        }
    }

    @Override
    public Page<UserModel> getUserWithPagination(UserFilter filter, FilterSortRequest.SortRequest<UserSortBy> sort, PageRequest pageRequest) {
        return userRepository.getAllUser(filter,sort,pageRequest);
    }
    //common method
    private UserModel getUserModel(String id) {
          return userRepository.findByIdAndSoftDeleteIsFalse(id).orElseThrow(() -> new NotFoundException(MessageConstant.USER_ID_NOT_FOUND));
    }

    private UserModel getUserName(String userName) {
        return userRepository.findByUserNameAndSoftDeleteIsFalse(userName).orElseThrow(() -> new NotFoundException(MessageConstant.USER_ID_NOT_FOUND));
    }

    private List<UserDataModel> getUserDataModel(Set<String> userId){
        return userDataRepository.findByIdInAndSoftDeleteIsFalse(userId);
    }

    private UserDataModel getUserData(String id){
        return userDataRepository.findByIdAndSoftDeleteIsFalse(id).orElseThrow(() -> new NotFoundException(MessageConstant.USER_ID_NOT_FOUND));
    }

    private UserModel getUserEmail(String email) {
        return userRepository.findByEmailAndSoftDeleteIsFalse(email).orElseThrow(() -> new NotFoundException(MessageConstant.EMAIL_NOT_FOUND));
    }

    public String generateOtp() {
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        // this will convert any number sequence into 6 character.
        String otp = String.format("%06d", number);
        return otp;
    }

    public void checkUserDetails(UserAddRequest userAddRequest) throws InvocationTargetException, IllegalAccessException {
        AdminConfiguration adminConfiguration = adminService.getConfiguration();
        if ((StringUtils.isEmpty(userAddRequest.getFirstName()) || (userAddRequest.getFirstName().matches(adminConfiguration.getNameRegex())))) {
            throw new InvaildRequestException(MessageConstant.FIRSTNAME_NOT_EMPTY);
        }
        if ((StringUtils.isEmpty(userAddRequest.getMiddleName()) || (userAddRequest.getMiddleName().matches(adminConfiguration.getNameRegex())))) {
            throw new InvaildRequestException(MessageConstant.MIDDLENAME_NOT_EMPTY);
        }
        if ((StringUtils.isEmpty(userAddRequest.getLastName()) || (userAddRequest.getLastName().matches(adminConfiguration.getNameRegex())))) {
            throw new InvaildRequestException(MessageConstant.LASTNAME_NOT_EMPTY);
        }
        if (StringUtils.isEmpty(userAddRequest.getUserName())) {
            throw new InvaildRequestException(MessageConstant.USERNAME_NOT_EMPTY);
        }
        if (StringUtils.isEmpty(userAddRequest.getEmail())) {
            throw new InvaildRequestException(MessageConstant.EMAIL_NOT_FOUND);
        }
        if (userRepository.existsByEmailAndSoftDeleteFalse(userAddRequest.getEmail())) {
            throw new AlreadyExistException(MessageConstant.EMAIL_NAME_EXISTS);
        }
        if (StringUtils.isEmpty(userAddRequest.getEmail()) &&
                CollectionUtils.isEmpty(adminConfiguration.getRequiredEmailItems())) {
            throw new InvaildRequestException(MessageConstant.EMAIL_EMPTY);
        }
        if (!userAddRequest.getEmail().matches(adminConfiguration.getRegex())) {
            throw new InvaildRequestException(MessageConstant.EMAIL_FORMAT_NOT_VALID);
        }

        if (!userAddRequest.getPassword().matches(adminConfiguration.getPasswordRegex())) {
            throw new InvaildRequestException(MessageConstant.INVAILD_PASSWORD);
        }
        if (!userAddRequest.getMobileNo().matches(adminConfiguration.getMoblieNoRegex())) {
            throw new InvaildRequestException(MessageConstant.INVAILD_MOBILENO);
        }
        if (StringUtils.length(userAddRequest.getAddress().getZipCode()) > 7) {
            throw new InvaildRequestException(MessageConstant.INVAILD_ZIPCODE);
        }
    }
    public void setAgeFromBirthdate(UserAddRequest userAddRequest){
        Date date = userAddRequest.getBirthDate();
        LocalDateTime dates = Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        System.out.println(dates);
        LocalDate curDate = LocalDate.now();
        System.out.println(curDate);

        //set age from birthdate
        UserModel userModel = new UserModel();
        if (userAddRequest.getBirthDate() != null) {
            int age = Period.between(LocalDate.from(dates), curDate).getYears();
            userModel.setAge(age);
            System.out.println(age);
            userRepository.save(userModel);
        }
    }

    public void checkResultCond(Result result) throws InvocationTargetException, IllegalAccessException {
        AdminConfiguration adminConfiguration = adminService.getConfiguration();
        String sem = String.valueOf(result.getSemester());
        if (!sem.matches(adminConfiguration.getSemesterRegex())) {
            throw new InvaildRequestException(MessageConstant.INVAILD_SEMESTER);
        }
        if (result.getSpi() > 10) {
            throw new InvaildRequestException(MessageConstant.INVAILD_SPI);
        }
        int year = Year.now().getValue();
        System.out.println("year" + year);
        if (result.getYear() > year) {
            throw new InvaildRequestException(MessageConstant.INVAILD_YEAR);
        }
    }


    private void sendResultDetails(UserModel userModel) throws InvocationTargetException, IllegalAccessException {
        AdminConfiguration adminConfiguration = adminService.getConfiguration();
        List<Result> results = userModel.getResults();
        List<ResultDetailsResponseByEmail> response = new ArrayList<>();
        ResultDetailsResponseByEmail responseByEmail = new ResultDetailsResponseByEmail();
        for (Result result1 : results) {
            responseByEmail.setCgpi(userModel.getCgpi());
            responseByEmail.setSemester(result1.getSemester());
            responseByEmail.setSpi(result1.getSpi());
            response.add(responseByEmail);
        }

        System.out.println("respone:"+response);
        try {
            EmailModel emailModel = new EmailModel();
            emailModel.setTo(userModel.getEmail());
            emailModel.setCc(adminConfiguration.getTechAdmins());
            emailModel.setMessage(utils.generateReportMessage(userModel.getResults(), userModel.getCgpi()));
            emailModel.setSubject("Result Details");
            utils.sendEmailNow(emailModel);
        } catch (Exception e) {
            log.error("Error happened while sending result to user :{}", e.getMessage());
        }
    }

    public void updateUserDetail(String id, UserAddRequest userAddRequest,UserModel userResponse1) throws InvocationTargetException, IllegalAccessException {
        if (userResponse1 != null) {
            if (userAddRequest.getFirstName() != null) {
                userResponse1.setFirstName(userAddRequest.getFirstName());
            }
            if (userAddRequest.getMiddleName() != null) {
                userResponse1.setMiddleName(userAddRequest.getMiddleName());
            }
            if (userAddRequest.getLastName() != null) {
                userResponse1.setLastName(userAddRequest.getLastName());
            }
            if (userAddRequest.getEmail() != null) {
                userResponse1.setEmail(userAddRequest.getEmail());
            }
            if (userAddRequest.getPassword() != null) {
                userResponse1.setPassword(userAddRequest.getPassword());
            }
            if (userAddRequest.getBirthDate() != null) {
                userResponse1.setBirthDate(userAddRequest.getBirthDate());
            }
            if (userAddRequest.getAddress() != null) {
                userResponse1.setAddress(userAddRequest.getAddress());
            }
            if (userAddRequest.getMobileNo() != null) {
                userResponse1.setMobileNo(userAddRequest.getMobileNo());
            }
            userRepository.save(userResponse1);
        }
    }

    public void emailSend(HashMap<String,String> changedProperties) throws InvocationTargetException, IllegalAccessException {
        AdminConfiguration adminConfiguration = adminService.getConfiguration();
        try {
            EmailModel emailModel = new EmailModel();
            emailModel.setTo("dency05@gmail.com");
            emailModel.setCc(adminConfiguration.getTechAdmins());
            emailModel.setMessage(utils.genearteUpdatedUserDetail(changedProperties));
            emailModel.setSubject("User Details");
            utils.sendEmailNow(emailModel);
            log.info("email send start");
        } catch (Exception e) {
            log.error("Error happened while sending result to user :{}", e.getMessage());
        }
    }

    public HashMap<String,String> difference(UserModel userModel, UserAddRequest userAddRequest,HashMap<String, String> changedProperties) throws IllegalAccessException, InvocationTargetException {
        UserModel userModel1 = new UserModel();
        nullAwareBeanUtilsBean.copyProperties(userModel1, userAddRequest);
        userModel1.setId(userModel.getId());
        for (Field field : userModel.getClass().getDeclaredFields()) {
            // You might want to set modifier to public first (if it is not public yet)
            field.setAccessible(true);
            Object value1 = field.get(userModel);
            Object value2 = field.get(userModel1);
            if (value1 != null && value2 != null) {
                System.out.println(field.getName() + "=" + value1);
                System.out.println(field.getName() + "=" + value2);
                if (!Objects.equals(value1, value2)) {
                    changedProperties.put(field.getName(),value2.toString());
                }
            }
        }
        log.info(changedProperties.toString());
        return changedProperties;
    }

    // Save the uploaded file to this folder
    private static String UPLOADED_FOLDER = "/Downloads";
   public void saveUploadedFiles(List<MultipartFile> files) throws IOException {
        File folder = new File(UPLOADED_FOLDER);
        if (!folder.exists()) {
            folder.mkdir();
        }
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
                // next pls
            }
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
            Files.write(path, bytes);
        }
    }

    private List<UserDataModel> getUserFromImportedData(UserImportedData data, UserImportVerifyRequest verifyRequest, Date importDate) throws IndexOutOfBoundsException{
        //new list
        List<UserDataModel> userDataModels = new ArrayList<>();
        //set
        Set<String> excelKeys = verifyRequest.getMapping().keySet();
        if(CollectionUtils.isEmpty(excelKeys)) {
            return userDataModels;
        }
        List<Object> firstColumnData = data.getData().get(excelKeys.iterator().next());
        for(int i = 0;i< firstColumnData.size();i++){
            Map<String,Object> currentUserData = ImportExcelDataHelper.getMapData(i,data.getData(),excelKeys,verifyRequest.getMapping());
            UserDataModel currentUser = modelMapper.map(currentUserData,UserDataModel.class);
            currentUser.setImportFromExcel(true);
            currentUser.setImportDate(importDate);
            currentUser.setImportedId(verifyRequest.getId());
            //currentUser.setDuplicateEmail(checkDuplicateEmail(currentUser.getEmail()));
            userDataModels.add(currentUser);
        }
        return userDataModels;
    }

    private List<UserDataModel> setAndGetDuplicateEmailUsers(List<UserDataModel> users){
        List<UserDataModel> userList = new LinkedList<>();
        Set<String> uniqueEmails = new HashSet<>();
        Set<String> duplicateEmails = new HashSet<>();
        if(!CollectionUtils.isEmpty(users)) {  //database empty
            for (UserDataModel user : users) { //
                user.setDuplicateEmail(checkDuplicateEmail(user.getEmail()));
                if(!user.isEmptyEmail()){
                    if(!uniqueEmails.contains(user.getEmail().toLowerCase())){
                        uniqueEmails.add(user.getEmail().toLowerCase());
                    }else {
                        duplicateEmails.add(user.getEmail().toLowerCase());
                    }
                }
            }
            for (UserDataModel user : users) {
                if(!user.isEmptyEmail() && !user.isDuplicateEmail()){
                    user.setDuplicateEmail(duplicateEmails.contains(user.getEmail().toLowerCase()));
                }
                if(user.isDuplicateEmail()){
                    userList.add(user);
                }
            }
        }
        System.out.println("userList"+userList);
        return userList;
    }

    void sendEmailLogInTime(UserModel userModel) throws InvocationTargetException, IllegalAccessException {
        AdminConfiguration adminConfiguration = adminService.getConfiguration();
        EmailModel emailModel = new EmailModel();
        String otp = generateOtp();
        emailModel.setMessage(otp);
        emailModel.setTo(userModel.getEmail());
        log.info("email:{}",userModel.getEmail());
        emailModel.setCc(adminConfiguration.getTechAdmins());
        emailModel.setSubject("Otp Verification");
        utils.sendEmailNow(emailModel);
    }

    private boolean checkDuplicateEmail(String email) {
        return userDataRepository.existsByEmailAndSoftDeleteFalse(email);
    }


}


