package com.example.sm.auth.service;

import com.example.sm.auth.decorator.*;
import com.example.sm.auth.enums.UserStatus;
import com.example.sm.auth.model.UserModel;
import com.example.sm.common.decorator.FilterSortRequest;
import com.example.sm.common.decorator.UserImportResponse;
import com.example.sm.common.decorator.UserImportVerifyRequest;
import com.example.sm.common.enums.Role;
import com.example.sm.auth.enums.UserSortBy;
import com.example.sm.common.model.UserDataModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.JSONException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Set;


public interface UserService {

 UserResponse addOrUpdateUser(UserAddRequest userAddRequest, String id, Role role) throws InvocationTargetException, IllegalAccessException;

 List<UserResponse> getAllUser() throws InvocationTargetException, IllegalAccessException;

 List<UserResponse> getUsers(Set<String> ids) throws InvocationTargetException, IllegalAccessException;


 void deleteUser(String id);

 Page<UserModel> getAllUserWithFilterAndSort(UserFilter filter, FilterSortRequest.SortRequest<UserSortBy> sort, PageRequest pagination) throws InvocationTargetException, IllegalAccessException;

 UserResponse getToken(String id) throws InvocationTargetException, IllegalAccessException;

 String getEncryptPassword(String id) throws InvocationTargetException, IllegalAccessException;

 UserResponse checkUserAuthentication(String email, String password) throws NoSuchAlgorithmException, InvocationTargetException, IllegalAccessException;

 String getIdFromToken(String token) throws InvocationTargetException, IllegalAccessException;

 UserResponse getValidityOfToken(String token) throws InvocationTargetException, IllegalAccessException;

String login(String email, String password) throws InvocationTargetException, IllegalAccessException, NoSuchAlgorithmException;

UserResponse getOtp(String otp,String id) throws InvocationTargetException, IllegalAccessException;

void forgotPassword(String email);

void setPassword(String password, String confirmPassword,String id);

void otpVerifications(String id, String otp) throws InvocationTargetException, IllegalAccessException;

 void changePassword(String password, String confirmPassword, String newPassword,String id) throws NoSuchAlgorithmException;

 void logOut(String id);

 List<UserResponse> getUserByRole(UserFilter userFilter);

 UserResponse resultDetail(Result result,String id) throws InvocationTargetException, IllegalAccessException;

 List<UserDetailResponse> getUserResult(UserDetail userDetail) throws InvocationTargetException, IllegalAccessException;

 List<UserResultResponse> getUserResultBySemester(UserResult userResult);

 List<UserMinMaxMarkSemResponse> getUserResultByMinMaxSem(UserIdsRequest userIdsRequest);

 void deleteUserResult(String id, int semester);

 List<UserResultByDateRespose> getUserResultByDate(UserResultByDate userResultByDate);

 UserResponse updateUserResult(String id, int semester,Resultupdate result) throws InvocationTargetException, IllegalAccessException;

 List<UserResultByStatus> getUserResultByStatus(UserIdsRequest userIdsRequest);

 Page<UserResultByStatus> getUserResultStatusByPagination(UserIdsRequest userIdsRequest, FilterSortRequest.SortRequest<UserSortBy> sort, PageRequest pagination) throws InvocationTargetException, IllegalAccessException;

Workbook getUserByExcel(UserFilter userFilter, FilterSortRequest.SortRequest<UserSortBy> sort, PageRequest pagination) throws InvocationTargetException, IllegalAccessException;

void resultDetailByEmail(String id) throws InvocationTargetException, IllegalAccessException;

void userUpdate(String id, Role role, UserAddRequest userAddRequest) throws InvocationTargetException, IllegalAccessException;

 void userDelete(String id, Role role) throws InvocationTargetException, IllegalAccessException;

 UserImportResponse importUsers(MultipartFile file, String id) throws IOException, InvocationTargetException, IllegalAccessException;

 List<UserDataModel> importUsersVerify(UserImportVerifyRequest verifyRequest);

List<UserResponse> importDataInUser(UserIdsRequest userIdsRequest) throws InvocationTargetException, IllegalAccessException;

 void deleteUserInXls(String id);

 void getUserPassword(String userName, String password, String confirmPassword) throws InvocationTargetException, IllegalAccessException;

 void sendMailToInvitedUser(UserStatus userStatus) throws InvocationTargetException, IllegalAccessException;

 void checkUserPublisherId(String id);
 String sendMessage(String id);

 MonthTitleName getUserDetailByMonth(String year) throws JSONException, InvocationTargetException, IllegalAccessException;

 UserResponse getUser(String id) throws InvocationTargetException, IllegalAccessException;

void getAllUserByPagination() throws JSONException, InvocationTargetException, IllegalAccessException;

 Page<UserModel> getUserWithPagination(UserFilter filter, FilterSortRequest.SortRequest<UserSortBy> sort, PageRequest pageRequest);
}
