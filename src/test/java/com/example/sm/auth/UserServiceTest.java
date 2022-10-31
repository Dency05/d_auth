package com.example.sm.auth;

import com.example.sm.auth.decorator.*;
import com.example.sm.auth.enums.UserSortBy;
import com.example.sm.auth.enums.UserStatus;
import com.example.sm.auth.model.UserModel;
import com.example.sm.auth.rabbitmq.UserPublisher;
import com.example.sm.auth.repository.UserRepository;
import com.example.sm.auth.service.UserService;
import com.example.sm.auth.service.UserServiceImpl;
import com.example.sm.common.decorator.*;
import com.example.sm.common.enums.Role;
import com.example.sm.common.model.AdminConfiguration;
import com.example.sm.common.model.JWTUser;
import com.example.sm.common.model.UserDataModel;
import com.example.sm.common.model.UserImportedData;
import com.example.sm.common.repository.ImportedDataRepository;
import com.example.sm.common.repository.UserDataRepository;
import com.example.sm.common.service.AdminConfigurationService;
import com.example.sm.common.utils.JwtTokenUtil;
import com.example.sm.common.utils.PasswordUtils;
import com.example.sm.common.utils.Utils;
import com.example.sm.helper.DataSetUserHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
@ContextConfiguration
@Slf4j
public class UserServiceTest {

   private static final String Id = "123";
   private  final String email = "dency09@gamil.com";
   private final String firstName= "Dency";
   public  final String password= "Dency05";
   private  final Role role = Role.ADMIN;
   private final boolean softDelete= false;
   private  final String otp ="456789";
   private  final UserRepository userRepository = mock(UserRepository.class);
   private final ImportedDataRepository importedDataRepository = mock(ImportedDataRepository.class);

    private final UserDataRepository userDataRepository = mock(UserDataRepository.class);

    private final NullAwareBeanUtilsBean nullAwareBeanUtilsBean= mock(NullAwareBeanUtilsBean.class);

    private final JwtTokenUtil jwtTokenUtil= mock(JwtTokenUtil.class);
    private final PasswordUtils passwordUtil = mock(PasswordUtils.class);
    private final AdminConfigurationService adminService= mock(AdminConfigurationService.class);
    private  final  Utils utils= mock(Utils.class);

    private final NotificationParser notificationParser= mock(NotificationParser.class);

    private final UserPublisher userPublisher= mock(UserPublisher.class);
    private final ModelMapper modelMapper= mock(ModelMapper.class);
    private final RequestSession requestSession= mock(RequestSession.class);

    public  UserService userService= new UserServiceImpl(userRepository,importedDataRepository ,userDataRepository,nullAwareBeanUtilsBean,jwtTokenUtil,
            passwordUtil,adminService,utils,notificationParser, userPublisher,modelMapper,requestSession);
    @Autowired
    DataSetUserHelper dataSetUserHelper;

    @BeforeEach
    public void setUp(){
        dataSetUserHelper.cleanUp();
        dataSetUserHelper.init();
    }

    @Test
    public void getValidUserId(){
        try{
            //given
        var userModel = UserModel.builder()
                .id(Id)
                .email(email)
                .softDelete(softDelete)
                .password(password)
                .firstName(firstName).build();

        var expectedUserResponse = UserResponse.builder()
                .id(Id)
                .email(email)
                .softDelete(softDelete)
                .password(password)
                .firstName(firstName).build();

        //when
        when(userRepository.findByIdAndSoftDeleteIsFalse(Id)).thenReturn(Optional.of(userModel));

         //then
          assertEquals(expectedUserResponse,userService.getUser(Id));
          verify(userRepository,times(1)).findByIdAndSoftDeleteIsFalse(Id);
        }

        catch (Exception e){
            fail(e.getMessage());
        }
    }

    @Test
    public void getInValidUserId() throws InvocationTargetException, IllegalAccessException {
        try{
            var userModel = UserModel.builder()
                    .id(Id)
                    .email(email)
                    .softDelete(softDelete)
                    .password(password)
                    .firstName(firstName).build();

            var userResponse = UserResponse.builder()
                    .id(Id)
                    .email(email)
                    .softDelete(softDelete)
                    . password(password)
                    .firstName(firstName).build();
            System.out.println(userResponse);

            //when
            when(userRepository.findByIdAndSoftDeleteIsFalse("789")).thenReturn(Optional.of(userModel));

            //then
            assertEquals(userResponse,userService.getUser("789"));
            verify(userRepository, times(2)).findByIdAndSoftDeleteIsFalse("789");
        }
        catch (Exception e){
            fail(e.getMessage());
        }
    }


    @Test
    public void getAllUser() {
      try {
          //given
        var userModel = List.of(UserModel.builder()
                .id(Id)
                .email(email)
                .softDelete(softDelete)
                .password(password)
                .firstName(firstName).build());

        System.out.println(userModel);

        var userResponse = List.of(UserResponse.builder()
                .id(Id)
                .email(email)
                .softDelete(softDelete).
                password(password).
                firstName(firstName).build());

        when(userRepository.findAllBySoftDeleteFalse()).thenReturn(userModel);

        userResponse = userService.getAllUser();

        assertEquals(1,userResponse.size());
        verify(userRepository, times(1)).findAllBySoftDeleteFalse();
      }
       catch (Exception e){
           log.error( "response not match :{} ",e.getMessage());
       }
    }

    @Test
    public void logInValid(){
        try {
            //given
            var userModel = UserModel.builder()
                    .id(Id)
                    .email(email)
                    .softDelete(softDelete)
                    .password(password)
                    .role(role)
                    .firstName(firstName).build();

            JWTUser jwtUser = new JWTUser(userModel.getId(), Collections.singletonList(userModel.getRole().toString()));
            String token = jwtTokenUtil.generateToken(jwtUser);

            //when
            when(userRepository.findByEmailAndSoftDeleteIsFalse(email)).thenReturn(Optional.of(userModel));

            //then
            Assertions.assertEquals(token,userService.login(email,password));
        }
        catch (Exception e){
            log.error( "response not match :{} ",e.getMessage());
        }
    }

    @Test
    public void logInInValid(){
        try {
            //given
            var userModel = UserModel.builder()
                    .id(Id)
                    .email(email)
                    .softDelete(softDelete)
                    .password(password)
                    .role(role)
                    .firstName(firstName).build();

            JWTUser jwtUser = new JWTUser(userModel.getId(), Collections.singletonList(userModel.getRole().toString()));
            String token = jwtTokenUtil.generateToken(jwtUser);

            //when
            when(userRepository.findByEmailAndSoftDeleteIsFalse("dency@gmail.com")).thenReturn(Optional.of(userModel));

            //then
            Assertions.assertEquals(token,userService.login("dency@gmail.com",password));
        }
        catch (Exception e){
            log.error( "response not match :{} ",e.getMessage());
        }
    }


   @Test
    void otpVerification() throws InvocationTargetException, IllegalAccessException {
       try {
               var userModel = UserModel.builder()
                       .id(Id)
                       .email(email)
                       .softDelete(softDelete)
                       .password(password)
                       .otp(otp)
                       .firstName(firstName).build();

               System.out.println(userModel);

               //when
               when(userRepository.existsByIdAndOtpAndSoftDeleteFalse(Id,otp)).thenReturn(true);

               userService.otpVerifications(Id,otp);

               //then
                verify(userRepository,times(1)).existsByIdAndOtpAndSoftDeleteFalse(Id,otp);
       }
       catch (Exception e){
           fail(e.getMessage());
       }
   }

    @Test
    void otpVerificationInValid() {
        try {
            var userModel = UserModel.builder()
                    .id(Id)
                    .email(email)
                    .softDelete(softDelete)
                    .password(password)
                    .otp(otp)
                    .firstName(firstName).build();

            System.out.println(userModel);

            //when
            when(userRepository.existsByIdAndOtpAndSoftDeleteFalse(Id,"477777")).thenReturn(true);

            userService.otpVerifications(Id,otp);

            //then
            verify(userRepository,times(1)).existsByIdAndOtpAndSoftDeleteFalse(Id,otp);
        }
        catch (Exception e){
            fail(e.getMessage());
        }
    }

    @Test
    void deleteUser(){
        try {
            //given
            var userModel = UserModel.builder()
                        .id(Id)
                        .email(email)
                        .softDelete(softDelete)
                        .password(password)
                        .firstName(firstName).build();

            var expectedUserModel1 = UserModel.builder()
                         .id(Id)
                         .email(email)
                         .softDelete(true)
                         .password(password)
                         .firstName(firstName).build();

            System.out.println(userModel);

            //when
            when(userRepository.findByIdAndSoftDeleteIsFalse(Id)).thenReturn(Optional.of(userModel));
            userService.deleteUser(Id);

           //then
            verify(userRepository,times(1)).save(expectedUserModel1);
        }
        catch (Exception e){
                log.error( "response not match :{} ",e.getMessage());
        }
    }

    @Test
    void deleteUserInvalid() {
        try {
            //given
            var userModel = UserModel.builder()
                    .id(Id)
                    .email(email)
                    .softDelete(softDelete)
                    .password(password)
                    .firstName(firstName).build();

            var exceptedUserModel = UserModel.builder()
                    .id(Id)
                    .email(email)
                    .softDelete(true)
                    .password(password)
                    .firstName(firstName).build();

            System.out.println(userModel);

            //when
            when(userRepository.findByIdAndSoftDeleteIsFalse("789")).thenReturn(Optional.of(userModel));
            userService.deleteUser(Id);

            //then
            verify(userRepository,times(1)).save(exceptedUserModel);
        }
        catch (Exception e){
            log.error( "response not match :{} ",e.getMessage());
        }
    }


    @Test
    void addOrUpdateUser() {
        try{
           var userModel = UserModel.builder()
                        .id(Id)
                        .email(email)
                        .softDelete(softDelete)
                        .password(password)
                        .firstName(firstName).build();

                System.out.println(userModel);

           var userResponse = UserResponse.builder()
                        .id(Id)
                        .email(email)
                        .softDelete(softDelete)
                        .password(password)
                        .firstName("Dencyyy").lastName("Gevariya").build();

           var userAddRequest = UserAddRequest.builder()
                   .firstName("Dencyyy")
                   .lastName("Gevariya").build();
           //when
            when(userRepository.findByIdAndSoftDeleteIsFalse(Id)).thenReturn(Optional.of(userModel));

           //then
            Assertions.assertEquals(userResponse,userService.addOrUpdateUser(userAddRequest,Id,role));

        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void getAllUserWithFilterAndSort(){
        var userModel = List.of(UserModel.builder()
                .id(Id)
                .email(email)
                .softDelete(softDelete)
                .password(password)
                .firstName(firstName).build());

        Page<UserModel> page = new PageImpl<>(userModel);//list convert to page
        System.out.println(page.getContent());

        FilterSortRequest<UserFilter,UserSortBy> filterSortRequest= new FilterSortRequest<>();
        UserFilter filter = filterSortRequest.getFilter();
        FilterSortRequest.SortRequest<UserSortBy> sortBySortRequest= new FilterSortRequest.SortRequest<>();
        sortBySortRequest.setOrderBy(Sort.Direction.ASC);
        sortBySortRequest.setSortBy(UserSortBy.EMAIL);
        Pagination pagination= new Pagination();
        pagination.setLimit(10);
        pagination.setPage(0);
        PageRequest pageRequest = PageRequest.of(pagination.getPage(), pagination.getLimit());
        filterSortRequest.setFilter(filter);
        filterSortRequest.setSort(sortBySortRequest);
        filterSortRequest.setPage(pagination);
        try {
            when(userRepository.getAllUser(filter,sortBySortRequest,pageRequest)).thenReturn(page);
            userService.getUserWithPagination(filter,sortBySortRequest,pageRequest);
            System.out.println("User Successfully");

            Assertions.assertEquals(page,userService.getUserWithPagination(filter,sortBySortRequest,pageRequest));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void getToken() throws InvocationTargetException, IllegalAccessException {
        try{
            var userModel = UserModel.builder()
                    .id(Id)
                    .email(email)
                    .softDelete(softDelete)
                    .password(password)
                    .role(role)
                    .firstName(firstName).build();

            JWTUser jwtUser = new JWTUser(userModel.getId(), Collections.singletonList(userModel.getRole().toString()));
            String token = jwtTokenUtil.generateToken(jwtUser);

            var userResponse = UserResponse.builder()
                    .id(Id)
                    .email(email)
                    .softDelete(softDelete)
                    .password(password)
                    .token(token)
                    .firstName("Dencyyy").lastName("Gevariya").build();


            //when
            when(userRepository.findByIdAndSoftDeleteIsFalse(Id)).thenReturn(Optional.of(userModel));

            //then
            Assertions.assertEquals(userResponse,userService.getToken(Id));

        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void getTokenInvalid(){
        try{
            var userModel = UserModel.builder()
                    .id(Id)
                    .email(email)
                    .softDelete(softDelete)
                    .password(password)
                    .role(role)
                    .firstName(firstName).build();

            JWTUser jwtUser = new JWTUser(userModel.getId(), Collections.singletonList(userModel.getRole().toString()));
            String token = jwtTokenUtil.generateToken(jwtUser);

            var userResponse = UserResponse.builder()
                    .id(Id)
                    .email(email)
                    .softDelete(softDelete)
                    .password(password)
                    .token(token)
                    .firstName("Dencyyy").lastName("Gevariya").build();


            //when
            when(userRepository.findByIdAndSoftDeleteIsFalse("678")).thenReturn(Optional.of(userModel));

            //then
            Assertions.assertEquals(userResponse,userService.getToken(Id));

        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void setPassword(){
        try{
            var userModel = UserModel.builder()
                    .id(Id)
                    .email(email)
                    .softDelete(softDelete)
                    .password(password)
                    .role(role)
                    .firstName(firstName).build();


           String password =  passwordUtil.encryptPassword(userModel.getPassword());

            var exceptedUserModel = UserModel.builder()
                    .id(Id)
                    .email(email)
                    .softDelete(softDelete)
                    .password(password)
                    .firstName(firstName).build();

            //when
            when(userRepository.findByIdAndSoftDeleteIsFalse(Id)).thenReturn(Optional.of(userModel));

             userService.setPassword(userModel.getPassword(),userModel.getPassword(),userModel.getId());
            //then
            verify(userRepository,times(1)).save(exceptedUserModel);
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void setPasswordInvalid(){
        try{
            var userModel = UserModel.builder()
                    .id(Id)
                    .email(email)
                    .softDelete(softDelete)
                    .password(password)
                    .role(role)
                    .firstName(firstName).build();


            String password =  passwordUtil.encryptPassword(userModel.getPassword());

            var exceptedUserModel = UserModel.builder()
                    .id(Id)
                    .email(email)
                    .softDelete(softDelete)
                    .password(password)
                    .firstName(firstName).build();

            //when
            when(userRepository.findByIdAndSoftDeleteIsFalse(Id)).thenReturn(Optional.of(userModel));

            userService.setPassword("Dency",userModel.getPassword(),userModel.getId());
            //then
            verify(userRepository,times(1)).save(exceptedUserModel);
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void changePasswordInvalid(){
        try{
            var userModel = UserModel.builder()
                    .id(Id)
                    .email(email)
                    .softDelete(softDelete)
                    .password(password)
                    .role(role)
                    .firstName(firstName).build();


            String password =  passwordUtil.encryptPassword(userModel.getPassword());

            var exceptedUserModel = UserModel.builder()
                    .id(Id)
                    .email(email)
                    .softDelete(softDelete)
                    .password(password)
                    .firstName(firstName).build();

            //when
            when(userRepository.findByIdAndSoftDeleteIsFalse(Id)).thenReturn(Optional.of(userModel));

            userService.changePassword(userModel.getPassword(),"Dency","Dency",userModel.getId());
            //then
            verify(userRepository,times(1)).save(exceptedUserModel);
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void logOut(){
        try {
            //given
            var userModel = UserModel.builder()
                    .id(Id)
                    .email(email)
                    .softDelete(softDelete)
                    .password(password)
                    .firstName(firstName).build();

            var exceptedUserModel = UserModel.builder()
                    .id(Id)
                    .email(email)
                    .softDelete(false)
                    .password(password).login(false).logoutTime(new Date())
                    .firstName(firstName).build();

            System.out.println(userModel);

            //when
            when(userRepository.findByIdAndSoftDeleteIsFalse("789")).thenReturn(Optional.of(userModel));
            userService.logOut(Id);

            //then
            verify(userRepository,times(1)).save(exceptedUserModel);
        }
        catch (Exception e){
            log.error( "response not match :{} ",e.getMessage());
        }
    }

    @Test
    void getUserByRole(){
        try {
            //given
            var userModel = UserModel.builder()
                    .id(Id)
                    .email(email)
                    .softDelete(softDelete)
                    .password(password)
                    .firstName(firstName).build();

            var userFilter = UserFilter.builder()
                    .role(Role.ADMIN).build();

            var expectedUserResponse = List.of(UserResponse.builder()
                    .id(Id)
                    .email(email)
                    .softDelete(false)
                    .password(password)
                    .firstName(firstName).build());

            System.out.println(userModel);

            //when
            when(userRepository.getUser(userFilter)).thenReturn(expectedUserResponse);
            userService.getUserByRole(userFilter);
        }
        catch (Exception e){
            log.error( "response not match :{} ",e.getMessage());
        }
    }

    @Test
    void getUserByRoleInValid(){
            try {
                //given
                var userModel = UserModel.builder()
                        .id(Id)
                        .email(email)
                        .softDelete(softDelete)
                        .password(password)
                        .firstName(firstName).build();

                var userFilter = UserFilter.builder()
                        .role(Role.ANONYMOUS).build();

                var expectedUserResponse = List.of(UserResponse.builder()
                        .id(Id)
                        .email(email)
                        .softDelete(false)
                        .password(password)
                        .firstName(firstName).build());

                System.out.println(userModel);

                //when
                when(userRepository.getUser(userFilter)).thenReturn(expectedUserResponse);
                userService.getUserByRole(userFilter);
            }
            catch (Exception e){
                log.error( "response not match :{} ",e.getMessage());
            }
        }

    @Test
    void  resultDetail(){
       try{
            var userModel = UserModel.builder()
                    .id(Id)
                    .email(email)
                    .softDelete(softDelete)
                    .password(password)
                    .role(role)
                    .firstName(firstName).build();

            var userResponse = UserResponse.builder()
                    .id(Id)
                    .email(email)
                    .softDelete(softDelete)
                    .password(password)
                    .firstName(firstName).build();

            var result =Result.builder().spi(9.0).year(2022).semester(5).build();

            //when
            when(userRepository.findByIdAndSoftDeleteIsFalse(Id)).thenReturn(Optional.of(userModel));

            //then
            Assertions.assertEquals(userResponse, userService.resultDetail(result,userModel.getId()));

        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void getEncryptPassword(){
        try{
            var userModel = UserModel.builder()
                    .id(Id)
                    .email(email)
                    .softDelete(softDelete)
                    .password(password)
                    .role(role)
                    .firstName(firstName).build();

            String password = passwordUtil.encryptPassword(userModel.getPassword());

            //when
            when(userRepository.findByIdAndSoftDeleteIsFalse(Id)).thenReturn(Optional.of(userModel));

            //then
            Assertions.assertEquals(password, userService.getEncryptPassword(userModel.getId()));

        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }@Test
    void getEncryptPasswordInvalid(){
        try{
            var userModel = UserModel.builder()
                    .id(Id)
                    .email(email)
                    .softDelete(softDelete)
                    .password(password)
                    .role(role)
                    .firstName(firstName).build();

            String password = passwordUtil.encryptPassword(userModel.getPassword());

            //when
            when(userRepository.findByIdAndSoftDeleteIsFalse(Id)).thenReturn(Optional.of(userModel));

            //then
            Assertions.assertEquals(password, userService.getEncryptPassword("789"));

        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }


    @Test
    void getUserResult (){
        try{
            var result = Result.builder().date(new Date()).spi(9.7).semester(5).build();

            var results = List.of(Result.builder().date(new Date()).spi(9.7).semester(5).build());

            var userModel = UserModel.builder()
                    .id(Id)
                    .email(email)
                    .softDelete(softDelete)
                    .password(password)
                    .role(role)
                    .results(results)
                    .firstName(firstName).build();

            var userDetail = UserDetail.builder().semester(result.getSemester()).userIds(Collections.singleton(userModel.getId())).build();

            var userDetailResponse =List.of(UserDetailResponse.builder()
                    .firstName(userModel.getFirstName())
                    .lastName(userModel.getLastName())
                    .results(result).build());

            //when
            when(userRepository.getUserResult(userDetail)).thenReturn(userDetailResponse);

            //then
            Assertions.assertEquals(userDetailResponse, userService.getUserResult(userDetail));

        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }
    @Test
    void getUserResultInvalid (){
        try{
            var result = Result.builder().date(new Date()).spi(9.7).semester(5).build();

            var results = List.of(Result.builder().date(new Date()).spi(9.7).semester(5).build());

            var userModel = UserModel.builder()
                    .id(Id)
                    .email(email)
                    .softDelete(softDelete)
                    .password(password)
                    .role(role)
                    .results(results)
                    .firstName(firstName).build();

            var userDetail = UserDetail.builder().semester(result.getSemester()).userIds(Collections.singleton("456")).build();

            var userDetailResponse =List.of(UserDetailResponse.builder()
                    .firstName(userModel.getFirstName())
                    .lastName(userModel.getLastName())
                    .results(result).build());

            //when
            when(userRepository.getUserResult(userDetail)).thenReturn(userDetailResponse);

            //then
            Assertions.assertEquals(userDetailResponse, userService.getUserResult(userDetail));

        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void getUserResultBySemester (){
        try{
            var result = Result.builder().date(new Date()).spi(9.7).semester(5).build();

            var results = List.of(Result.builder().date(new Date()).spi(9.7).semester(5).build());

            var userModel = UserModel.builder()
                    .id(Id)
                    .email(email)
                    .softDelete(softDelete)
                    .password(password)
                    .role(role)
                    .results(results)
                    .firstName(firstName).build();

            var userResult= UserResult.builder().userIds(Collections.singleton(userModel.getId()))
                    .semester(Collections.singleton(result.getSemester())).build();

            var userResultResponse =List.of(UserResultResponse.builder()
                            .average(7.8)
                            .totalMark(9.9)
                            .fullName("Dency Gevariya").results(results).build());

            //when
            when(userRepository.getUserResultBySemester(userResult)).thenReturn(userResultResponse);

            //then
            Assertions.assertEquals(userResultResponse, userService.getUserResultBySemester(userResult));

        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void getUserResultByMinMaxSem(){
        try{
            var result = Result.builder().date(new Date()).spi(9.7).semester(5).build();

            var results = List.of(Result.builder().date(new Date()).spi(9.7).semester(5).build());

            var userModel = UserModel.builder()
                    .id(Id)
                    .email(email)
                    .softDelete(softDelete)
                    .password(password)
                    .role(role)
                    .results(results)
                    .firstName(firstName).build();

           var userMinMaxMarkSemResponse = List.of(UserMinMaxMarkSemResponse.builder()
                   .min(8.8).max(8.9).maxArray(result).minArray(result).build());

            var userIdsRequest = UserIdsRequest.builder().userId(Collections.singleton(userModel.getId())).build();

            //when
            when(userRepository.getUserResultByMinMaxMark(userIdsRequest)).thenReturn(userMinMaxMarkSemResponse);

            //then
            Assertions.assertEquals(userMinMaxMarkSemResponse, userService.getUserResultByMinMaxSem(userIdsRequest));
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void  deleteUserResult(){
        try{
            var result = Result.builder().date(new Date()).spi(9.7).semester(5).build();

            var results = List.of(Result.builder().date(new Date()).spi(9.7).semester(5).build());

            var userModel = UserModel.builder()
                    .id(Id)
                    .email(email)
                    .softDelete(softDelete)
                    .password(password)
                    .role(role)
                    .results(results)
                    .firstName(firstName).build();

            var exceptedUserModel = UserModel.builder()
                    .id(Id)
                    .email(email)
                    .softDelete(softDelete)
                    .password(password)
                    .role(role)
                    .firstName(firstName).build();

            //when
            when(userRepository.findByIdAndSoftDeleteIsFalse(userModel.getId())).thenReturn(Optional.of(userModel));
            userService.deleteUserResult(userModel.getId(),result.getSemester());
            //then
            verify(userRepository,times(3)).save(exceptedUserModel);

        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void getUserResultByDate(){
        try{
            var result = Result.builder().date(new Date()).spi(9.7).semester(5).build();

            var results = List.of(Result.builder().date(new Date()).spi(9.7).semester(5).build());

            var userModel = UserModel.builder()
                    .id(Id)
                    .email(email)
                    .softDelete(softDelete)
                    .password(password)
                    .role(role)
                    .results(results)
                    .firstName(firstName).build();

            var userResultByDate= UserResultByDate.builder().date("21-10-2022").build();

            var userResultByDateRespose =List.of(UserResultByDateRespose.builder()
                            .resultOfDate(9.8).resultDate("21-10-2022").result(results).build());
            //when
            when(userRepository.getUserResultByDate(userResultByDate)).thenReturn(userResultByDateRespose);

            //then
            Assertions.assertEquals(userResultByDateRespose, userService.getUserResultByDate(userResultByDate));

        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void updateUserResult(){
        try{
            var result = Result.builder().date(new Date()).spi(9.7).semester(5).build();

            var results = List.of(Result.builder().date(new Date()).spi(9.7).semester(5).build());
            var resultList = List.of(Result.builder().date(new Date()).spi(9.8).semester(5).year(2022).build());
            var userModel = UserModel.builder()
                    .id(Id)
                    .email(email)
                    .softDelete(softDelete)
                    .password(password)
                    .role(role)
                    .results(results)
                    .firstName(firstName).build();


            var userResponse = UserResponse.builder()
                    .id(Id)
                    .email(email)
                    .softDelete(softDelete)
                    .password(password)
                    .role(role)
                    .results(resultList)
                    .firstName(firstName).build();

            var resultupdate= Resultupdate.builder().date(new Date()).spi(9.8).year(2022).build();
            //when
            when(userRepository.findByIdAndSoftDeleteIsFalse(userModel.getId())).thenReturn(Optional.of(userModel));

            //then
            Assertions.assertEquals(userResponse, userService.updateUserResult(userModel.getId(),result.getSemester(),resultupdate));
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void getUserResultByStatus(){
        try{
            var result = Result.builder().date(new Date()).spi(9.7).semester(5).build();

            var results = List.of(Result.builder().date(new Date()).spi(9.7).semester(5).build());

            var userModel = UserModel.builder()
                    .id(Id)
                    .email(email)
                    .softDelete(softDelete)
                    .password(password)
                    .role(role)
                    .results(results)
                    .firstName(firstName).build();

            var userResultByStatus= List.of(UserResultByStatus.builder().result(results).build());

            var userIdRequest= UserIdsRequest.builder().userId(Collections.singleton(userModel.getId())).build();

            //when
            when(userRepository.getUserResultByStatus(userIdRequest)).thenReturn(userResultByStatus);

            //then
            Assertions.assertEquals(userResultByStatus, userService.getUserResultByStatus(userIdRequest));

        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }


    @Test
    void getUserResultStatusByPagination(){
        FilterSortRequest<UserIdsRequest,UserSortBy> filterSortRequest= new FilterSortRequest<>();
        UserIdsRequest filter = filterSortRequest.getFilter();
        FilterSortRequest.SortRequest<UserSortBy> sortBySortRequest= new FilterSortRequest.SortRequest<>();
        sortBySortRequest.setOrderBy(Sort.Direction.ASC);
        sortBySortRequest.setSortBy(UserSortBy.EMAIL);
        Pagination pagination= new Pagination();
        pagination.setLimit(10);
        pagination.setPage(0);
        PageRequest pageRequest = PageRequest.of(pagination.getPage(), pagination.getLimit());
        filterSortRequest.setFilter(filter);
        filterSortRequest.setSort(sortBySortRequest);
        filterSortRequest.setPage(pagination);
        try{
            userService.getUserResultStatusByPagination(filter,sortBySortRequest,pageRequest);
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void resultDetailByEmail() throws InvocationTargetException, IllegalAccessException {
        AdminConfiguration adminConfiguration = adminService.getConfiguration();
        try{
            var result = Result.builder().date(new Date()).spi(9.7).semester(5).build();

            var results = List.of(Result.builder().date(new Date()).spi(9.7).semester(5).build());

            var userModel = UserModel.builder()
                .id(Id)
                .email(email)
                .softDelete(softDelete)
                .password(password)
                 .results(results)
                  .cgpi(9.8)
                .role(role)
                .firstName(firstName).build();
        //when
        when(userRepository.findByIdAndSoftDeleteIsFalse(userModel.getId())).thenReturn(Optional.of(userModel));
        //then
        userService.resultDetailByEmail(Id);
        verify(userRepository,times(1)).findByIdAndSoftDeleteIsFalse(Id);
    }
     catch (Exception e) {
        fail(e.getMessage());
    }

    }
    @Test
    void resultDetailByEmailInValid() throws InvocationTargetException, IllegalAccessException {
        AdminConfiguration adminConfiguration = adminService.getConfiguration();
        try{
            var result = Result.builder().date(new Date()).spi(9.7).semester(5).build();

            var results = List.of(Result.builder().date(new Date()).spi(9.7).semester(5).build());

            var userModel = UserModel.builder()
                    .id(Id)
                    .email(email)
                    .softDelete(softDelete)
                    .password(password)
                    .results(results)
                    .cgpi(9.8)
                    .role(role)
                    .firstName(firstName).build();
            //when
            when(userRepository.findByIdAndSoftDeleteIsFalse("7568958")).thenReturn(Optional.of(userModel));
            //then
            userService.resultDetailByEmail(Id);
            verify(userRepository,times(1)).findByIdAndSoftDeleteIsFalse("689879");
        }
        catch (Exception e) {
            fail(e.getMessage());
        }

    }

    @Test
    void  getAllUserByPagination() {
        try {
            var results = List.of(Result.builder().date(new Date()).spi(9.7).semester(5).build());

            var userModel = List.of(UserModel.builder()
                    .id(Id)
                    .email(email)
                    .softDelete(softDelete)
                    .password(password)
                    .role(role)
                    .results(results)
                    .firstName(firstName).build());


            var exceptedUserModel = UserModel.builder()
                    .id(Id)
                    .email(email)
                    .softDelete(softDelete)
                    .password(password)
                    .role(role)
                    .results(results)
                    .fullName("Dency")
                    .firstName(firstName).build();

            var pagination= Pagination.builder().limit(10).page(0).build();

            FilterSortRequest.SortRequest<UserSortBy> sortBySortRequest = new FilterSortRequest.SortRequest<>();
            sortBySortRequest.setOrderBy(Sort.Direction.ASC);

            PageRequest pageRequest = PageRequest.of(pagination.getPage(), pagination.getLimit());
            Page<UserModel> page = new PageImpl<>(userModel);
            System.out.println("page" +page.getContent());


            //when
            when(userRepository.getAllUser(new UserFilter(), sortBySortRequest, pageRequest)).thenReturn(page);

            userService.getAllUserByPagination();

            verify(userRepository,times(1)).save(exceptedUserModel);
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void getUserDetailByMonth(){
        try{
           var userDetailByMonth = List.of(UserDetailByMonth.builder()
                   .dateOfMonth("7")
                           .month("7").count(1.0).userIds(Collections.singleton(Id)).id(Id).build());


           var  monthTitleName = MonthTitleName.builder().userDetailByMonths(userDetailByMonth).title(setTitle()).totalCount(1.0).build();

           when(userRepository.getUserByMonth("2022")).thenReturn(userDetailByMonth);

            assertEquals(monthTitleName,userService.getUserDetailByMonth("2022"));

        }
        catch (Exception e){
            fail(e.getMessage());
        }
    }

    private Set<String> setTitle() {
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
            String titleName = entry.getKey() + " - " + "2022";
            titles.add(titleName);
            System.out.println("title" + titles);
        }
        return titles;
    }


    @Test
    void sendMailToInvitedUser(){
        try{
            userService.sendMailToInvitedUser(UserStatus.INVITED);
        }
        catch (Exception e){
            fail(e.getMessage());
        }
    }

    @Test
    void sendMailToInvitedUserInvalid(){
        try{
            userService.sendMailToInvitedUser(UserStatus.ACTIVE);
        }
        catch (Exception e){
            fail(e.getMessage());
        }
    }


    @Test
    void getUserPassword(){
        UserModel userModel= dataSetUserHelper.getUserModel();
        try{
            userService.getUserPassword(userModel.getUserName(),userModel.getPassword(),userModel.getPassword());
        }
        catch (Exception e){
            fail(e.getMessage());
        }
    }


    @Test
    void getUserPasswordInvalid(){
        UserModel userModel= dataSetUserHelper.getUserModel();
        try{
            userService.getUserPassword(userModel.getUserName(),userModel.getPassword(),"Dencfnknk");
        }
        catch (Exception e){
            fail(e.getMessage());
        }
    }

    @Test
    void deleteUserInXls(){
        UserModel userModel= dataSetUserHelper.getUserModel();
        try{
            userService.deleteUserInXls(userModel.getId());}
        catch (Exception e){
            fail(e.getMessage());
        }
    }

    @Test
    void deleteUserInXlsInvalid(){
        UserModel userModel= dataSetUserHelper.getUserModel();
        try{
            userService.deleteUserInXls("264673758");}
        catch (Exception e){
            fail(e.getMessage());
        }
    }

    @Test
    void importDataInUser(){
        UserModel userModel= dataSetUserHelper.getUserModel();
        UserIdsRequest userIdsRequest= new UserIdsRequest();
        userIdsRequest.setUserId(Collections.singleton(userModel.getId()));
        try {
            userService.importDataInUser(userIdsRequest);
        }
        catch (Exception e){
            fail(e.getMessage());
        }
    }

    @Test
    void importUsersVerify(){
        try{

       Map<String,String> mapping = new HashMap<>();
        mapping.put("firstName","firstName");

       var userImportVerifyRequest= UserImportVerifyRequest.builder().id(Id).mapping(mapping).build();

       var exceptedUserDataModel = UserDataModel.builder().firstName(firstName).email(email).ImportDate(new Date()).importedId(Id).importFromExcel(true).build();

       var userImportedData= UserImportedData.builder().id(Id).build();
       //when
        when(importedDataRepository.findById(Id)).thenReturn(Optional.of(userImportedData));
       //then
        userService.importUsersVerify(userImportVerifyRequest);
        Assertions.assertEquals(exceptedUserDataModel,userService.importUsersVerify(userImportVerifyRequest));

        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }


    @Test
    void importUsersVerifyInvalid(){
        try{

            Map<String,String> mapping = new HashMap<>();
            mapping.put("firstName","firstName");
            mapping.put("email","email");

            var userImportVerifyRequest= UserImportVerifyRequest.builder().id(Id).mapping(mapping).build();

            var exceptedUserDataModel = UserDataModel.builder().firstName(firstName).email(email).ImportDate(new Date()).importedId(Id).importFromExcel(true).build();

            var userImportedData= UserImportedData.builder().id(Id).build();
            //when
            when(importedDataRepository.findById(Id)).thenReturn(Optional.of(userImportedData));
            //then
            userService.importUsersVerify(userImportVerifyRequest);
            Assertions.assertEquals(exceptedUserDataModel,userService.importUsersVerify(userImportVerifyRequest));

        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void  userUpdate(){
        try{
            var results = List.of(Result.builder().date(new Date()).spi(9.7).semester(5).build());

            var userModel = UserModel.builder()
                    .id(Id)
                    .email(email)
                    .softDelete(softDelete)
                    .password(password)
                    .role(role)
                    .results(results)
                    .firstName(firstName).build();

            var expectedUserModel = UserModel.builder()
                    .id(Id)
                    .email(email)
                    .softDelete(softDelete)
                    .password(password)
                    .role(role)
                    .results(results)
                    .firstName("Dencyyy").lastName("Gevariya").build();

            var userAddRequest = UserAddRequest.builder()
                    .firstName("Dencyyy")
                    .lastName("Gevariya").build();

            //when
            when(userRepository.findByIdAndSoftDeleteIsFalse(userModel.getId())).thenReturn(Optional.of(userModel));

            //then
            userService.userUpdate(userModel.getId(),userModel.getRole(),userAddRequest);

            verify(userRepository,times(1)).save(expectedUserModel);
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void  userUpdateInvalid(){
        try{
            var results = List.of(Result.builder().date(new Date()).spi(9.7).semester(5).build());

            var userModel = UserModel.builder()
                    .id(Id)
                    .email(email)
                    .softDelete(softDelete)
                    .password(password)
                    .role(role)
                    .results(results)
                    .firstName(firstName).build();

            var expectedUserModel = UserModel.builder()
                    .id(Id)
                    .email(email)
                    .softDelete(softDelete)
                    .password(password)
                    .role(role)
                    .results(results)
                    .firstName("Dencyyy").lastName("Gevariya").build();

            var userAddRequest = UserAddRequest.builder()
                    .firstName("Dencyyy")
                    .lastName("Gevariya").build();

            //when
            when(userRepository.findByIdAndSoftDeleteIsFalse("789")).thenReturn(Optional.of(userModel));

            //then
            userService.userUpdate("689879",userModel.getRole(),userAddRequest);

            verify(userRepository,times(1)).save(expectedUserModel);
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void userDelete(){
        try{
            var result = Result.builder().date(new Date()).spi(9.7).semester(5).build();

            var results = List.of(Result.builder().date(new Date()).spi(9.7).semester(5).build());

            var userModel = UserModel.builder()
                    .id(Id)
                    .email(email)
                    .softDelete(softDelete)
                    .password(password)
                    .role(role)
                    .results(results)
                    .firstName(firstName).build();

            var exceptedUserModel = UserModel.builder()
                    .id(Id)
                    .email(email)
                    .softDelete(true)
                    .password(password)
                    .role(role)
                    .results(results)
                    .firstName(firstName).build();

            //when
            when(userRepository.findByIdAndSoftDeleteIsFalse(userModel.getId())).thenReturn(Optional.of(userModel));

            //then
            userService.userDelete(userModel.getId(),userModel.getRole());

            verify(userRepository,times(1)).save(exceptedUserModel);
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }


    @Test
    void userDeleteInvalid(){
        try{

            var results = List.of(Result.builder().date(new Date()).spi(9.7).semester(5).build());

            var userModel = UserModel.builder()
                    .id(Id)
                    .email(email)
                    .softDelete(softDelete)
                    .password(password)
                    .role(role)
                    .results(results)
                    .firstName(firstName).build();

            var exceptedUserModel = UserModel.builder()
                    .id(Id)
                    .email(email)
                    .softDelete(true)
                    .password(password)
                    .role(role)
                    .results(results)
                    .firstName(firstName).build();

            //when
            when(userRepository.findByIdAndSoftDeleteIsFalse("8678659utyo")).thenReturn(Optional.of(userModel));

            //then
            userService.userDelete("db6987",userModel.getRole());

            verify(userRepository,times(1)).save(exceptedUserModel);
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }


    @Test
    void getUserByExcel(){
        UserModel userModel= dataSetUserHelper.getUserModel();
        FilterSortRequest<UserFilter,UserSortBy> filterSortRequest= new FilterSortRequest<>();
        UserFilter filter = filterSortRequest.getFilter();
        FilterSortRequest.SortRequest<UserSortBy> sortBySortRequest= new FilterSortRequest.SortRequest<>();
        sortBySortRequest.setOrderBy(Sort.Direction.ASC);
        sortBySortRequest.setSortBy(UserSortBy.EMAIL);
        Pagination pagination= new Pagination();
        pagination.setLimit(10);
        pagination.setPage(0);
        PageRequest pageRequest = PageRequest.of(pagination.getPage(), pagination.getLimit());
        filterSortRequest.setFilter(filter);
        filterSortRequest.setSort(sortBySortRequest);
        filterSortRequest.setPage(pagination);
        try {
            userService.getUserByExcel(filter,sortBySortRequest,pageRequest);
        }
        catch (Exception e){
            fail(e.getMessage());
        }
    }

}


