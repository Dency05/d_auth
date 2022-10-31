package com.example.sm.auth.controller;

import com.example.sm.auth.decorator.*;
import com.example.sm.auth.enums.UserSortBy;
import com.example.sm.auth.enums.UserStatus;
import com.example.sm.auth.model.UserModel;
import com.example.sm.auth.service.UserService;
import com.example.sm.common.decorator.*;
import com.example.sm.common.enums.Role;
import com.example.sm.common.model.UserDataModel;
import com.example.sm.common.utils.Access;
import com.example.sm.common.utils.ExcelUtil;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    GeneralHelper generalHelper;

    @Autowired
    RequestSession requestSession;

    @SneakyThrows
    @RequestMapping(name = "addOrUpdateUser", value = "/addOrUpdate", method = RequestMethod.POST)
    @Access(levels = {Role.ADMIN, Role.STUDENT, Role.DEPARTMENT})
    public DataResponse<UserResponse> addOrUpdateUser(@RequestBody UserAddRequest userAddRequest, @RequestParam(required = false) String id, @RequestParam(required = false) Role role) {
        DataResponse<UserResponse> dataResponse = new DataResponse<>();
        dataResponse.setData(userService.addOrUpdateUser(userAddRequest, id, role));
        dataResponse.setStatus(Response.getOkResponse());
        return dataResponse;
    }

    @SneakyThrows
    @Access(levels = Role.ANONYMOUS)
    @RequestMapping(name = "getAllUser", value = "/getAll", method = RequestMethod.GET)
    public ListResponse<UserResponse> getAllUser() {
        ListResponse<UserResponse> listResponse = new ListResponse<>();
        listResponse.setData(userService.getAllUser());
        listResponse.setStatus(Response.getSuccessResponse());
        return listResponse;
    }

    @SneakyThrows
    @Access(levels = Role.ANONYMOUS)
    @RequestMapping(name = "getUsers", value = "/get/users", method = RequestMethod.POST)
    public ListResponse<UserResponse> getUsers(@RequestBody Set<String> ids) {
        ListResponse<UserResponse> listResponse = new ListResponse<>();
        listResponse.setData(userService.getUsers(ids));
        listResponse.setStatus(Response.getOkResponse());
        return listResponse;
    }

    @SneakyThrows
    @Access(levels = Role.ANONYMOUS)
    @RequestMapping(name = "getUser", value = "/get", method = RequestMethod.GET)
    public DataResponse<UserResponse> getUser(@RequestParam String id) {
        DataResponse<UserResponse> dataResponse = new DataResponse<>();
        dataResponse.setData(userService.getUser(id));
        dataResponse.setStatus(Response.getOkResponse());
        return dataResponse;
    }

    @SneakyThrows
    @Access(levels = {Role.ANONYMOUS})
    @RequestMapping(name = "deleteUser", value = "/delete", method = RequestMethod.GET)
    public DataResponse<Object> deleteUser(@RequestParam String id) {
        DataResponse<Object> dataResponse = new DataResponse<>();
        userService.deleteUser(id);
        dataResponse.setStatus(Response.getOkResponse());
        return dataResponse;
    }
    @SneakyThrows
    @Access(levels = Role.STUDENT)
    @RequestMapping(name = "getUserByPagination", value = "/getAll/filter", method = RequestMethod.POST)
    public PageResponse<UserModel> getUserByPagination(@RequestBody FilterSortRequest<UserFilter, UserSortBy> filterSortRequest) {
        PageResponse<UserModel> listResponse = new PageResponse<>();
        UserFilter filter = filterSortRequest.getFilter();
        FilterSortRequest.SortRequest<UserSortBy> sort = filterSortRequest.getSort();
        Pagination pagination = filterSortRequest.getPage();
        PageRequest pageRequest = PageRequest.of(pagination.getPage(), pagination.getLimit());
        Page<UserModel> userResponses = userService.getAllUserWithFilterAndSort(filter, sort, pageRequest);
        listResponse.setData(userResponses);
        listResponse.setStatus(Response.getOhkResponse());
        return listResponse;
    }
    @SneakyThrows
    @Access(levels = Role.ANONYMOUS)
    @RequestMapping(name = "getToken", value = "/generateToken", method = RequestMethod.GET)
    public TokenResponse<UserResponse> getToken(@RequestParam String id) {
        TokenResponse<UserResponse> tokenResponse = new TokenResponse<>();
        UserResponse userResponse = userService.getToken(id);
        tokenResponse.setData(userResponse);
        tokenResponse.setStatus(Response.getOkResponse());
        tokenResponse.setToken(userResponse.getToken());
        return tokenResponse;
    }

    @SneakyThrows
    @Access(levels = Role.ADMIN)
    @RequestMapping(name = " getEncryptPassword", value = "/getEncryptPassword", method = RequestMethod.GET)
    public DataResponse<String> getEncryptPassword(@RequestParam String id) {
        DataResponse<String> dataResponse = new DataResponse<>();
        dataResponse.setData(userService.getEncryptPassword(id));
        dataResponse.setStatus(Response.getOkResponse());
        return dataResponse;
    }

    @SneakyThrows
    @Access(levels = Role.ADMIN)
    @RequestMapping(name = "checkUserAuthentication", value = "/getPasswords", method = RequestMethod.GET)
    public DataResponse<UserResponse> checkUserAuthentication(@RequestParam String email, @RequestParam String password) {
        DataResponse<UserResponse> dataResponse = new DataResponse<>();
        dataResponse.setData(userService.checkUserAuthentication(email, password));
        dataResponse.setStatus(Response.getOkResponse());
        return dataResponse;
    }

    @SneakyThrows
    @Access(levels = Role.ADMIN)
    @RequestMapping(name = "getIdFromToken", value = "/getIdFromToken", method = RequestMethod.GET)
    public TokenResponse<String> getIdFromToken(@RequestParam String token) {
        TokenResponse<String> tokenResponse = new TokenResponse<>();
        tokenResponse.setData(userService.getIdFromToken(token));
        tokenResponse.setStatus(Response.getOkResponse());
        tokenResponse.setToken(token);
        return tokenResponse;
    }

    @SneakyThrows
    @Access(levels = Role.ANONYMOUS)
    @RequestMapping(name = "getValidityOfToken", value = "/validate/token", method = RequestMethod.GET)
    public TokenResponse<UserResponse> getValidityOfToken(@RequestParam String token) {
        TokenResponse<UserResponse> tokenResponse = new TokenResponse<>();
        tokenResponse.setData(userService.getValidityOfToken(token));
        tokenResponse.setStatus(Response.getTokensucessResponse());
        tokenResponse.setToken(token);
        return tokenResponse;
    }

    @SneakyThrows
    @Access(levels = Role.ANONYMOUS)
    @RequestMapping(name = "uerLogin", value = "/login", method = RequestMethod.GET)
    public DataResponse<String> uerLogin(@RequestParam String email, String password) {
        DataResponse<String> dataResponse = new DataResponse<>();
        dataResponse.setData(userService.login(email, password));
        dataResponse.setStatus(Response.getLoginResponse());
        return dataResponse;
    }

    @SneakyThrows
    @Access(levels = Role.ANONYMOUS)
    @RequestMapping(name = "otpVerification", value = "/verification/Otp", method = RequestMethod.GET)
    public DataResponse<UserResponse> otpVerification(@RequestParam String otp, @RequestParam String id) {
        DataResponse<UserResponse> dataResponse = new DataResponse<>();
        dataResponse.setData(userService.getOtp(otp, id));
        dataResponse.setStatus(Response.getOtpResponse());
        return dataResponse;
    }

    @SneakyThrows
    @Access(levels = Role.ADMIN)
    @RequestMapping(name = "forgotPassword", value = "/forgot/password", method = RequestMethod.GET)
    public DataResponse<Object> forgotPassword(@RequestParam String email) {
        DataResponse<Object> dataResponse = new DataResponse<>();
        userService.forgotPassword(email);
        dataResponse.setStatus(Response.getOkResponse());
        return dataResponse;
    }

    @SneakyThrows
    @Access(levels = Role.ANONYMOUS)
    @RequestMapping(name = "otpVerifications", value = "/otp/verification", method = RequestMethod.GET)
    public DataResponse<Object> otpVerifications(@RequestParam String otp, @RequestParam String id) {
        DataResponse<Object> dataResponse = new DataResponse<>();
        userService.otpVerifications(otp, id);
        dataResponse.setStatus(Response.getOtpResponses());
        return dataResponse;
    }

    @SneakyThrows
    @Access(levels = Role.ANONYMOUS)
    @RequestMapping(name = "setPassword", value = "/setPassword", method = RequestMethod.GET)
    public DataResponse<Object> setPassword(@RequestParam String password, @RequestParam String confirmPassword, @RequestParam String id) {
        DataResponse<Object> dataResponse = new DataResponse<>();
        userService.setPassword(password, confirmPassword, id);
        dataResponse.setStatus(Response.getPasswordResponse());
        return dataResponse;
    }

    @SneakyThrows
    @Access(levels = Role.ANONYMOUS)
    @RequestMapping(name = "changePassword", value = "/change/user/Password", method = RequestMethod.GET)
    public DataResponse<Object> changePassword(@RequestParam String password, @RequestParam String confirmPassword, @RequestParam String newPassword, @RequestParam String id) {
        DataResponse<Object> dataResponse = new DataResponse<>();
        userService.changePassword(password, confirmPassword, newPassword, id);
        dataResponse.setStatus(Response.getPasswordResponse());
        return dataResponse;
    }

    @SneakyThrows
    @Access(levels = Role.ANONYMOUS)
    @RequestMapping(name = "logOut", value = "/logOut", method = RequestMethod.GET)
    public DataResponse<Object> logOut(@RequestParam String id) {
        DataResponse<Object> dataResponse = new DataResponse<>();
        userService.logOut(id);
        dataResponse.setStatus(Response.logOutResponse());
        return dataResponse;
    }

    @RequestMapping(name = "getUserByRole", value = "get/by/role", method = RequestMethod.POST)
    @Access(levels = Role.ANONYMOUS)
    public ListResponse<UserResponse> getUserByRole(@RequestBody UserFilter userFilter) {
        ListResponse<UserResponse> listResponse = new ListResponse<>();
        listResponse.setData(userService.getUserByRole(userFilter));
        listResponse.setStatus(Response.getOkResponse());
        return listResponse;
    }

    @SneakyThrows
    @Access(levels = {Role.ADMIN, Role.STUDENT})
    @RequestMapping(name = "addResult", value = "add/Result", method = RequestMethod.POST)
    public DataResponse<UserResponse> addResult(@RequestBody Result result, @RequestParam String id) {
        DataResponse<UserResponse> dataResponse = new DataResponse<>();
        dataResponse.setData(userService.resultDetail(result, id));
        dataResponse.setStatus(Response.getOkResponse());
        return dataResponse;
    }

    @SneakyThrows
    @Access(levels = Role.ANONYMOUS)
    @RequestMapping(name = "getUserResult", value = "get/result/semester", method = RequestMethod.POST)
    public ListResponse<UserDetailResponse> getUserResult(@RequestBody UserDetail userDetail) {
        ListResponse<UserDetailResponse> listResponse = new ListResponse<>();
        listResponse.setData(userService.getUserResult(userDetail));
        listResponse.setStatus(Response.getOkResponse());
        return listResponse;
    }

    @SneakyThrows
    @Access(levels = Role.ANONYMOUS)
    @RequestMapping(name = "getUserResultBySemester", value = "result/by/semester", method = RequestMethod.POST)
    public ListResponse<UserResultResponse> getUserResultBySemester(@RequestBody UserResult userResult) {
        ListResponse<UserResultResponse> listResponse = new ListResponse<>();
        listResponse.setData(userService.getUserResultBySemester(userResult));
        listResponse.setStatus(Response.getOkResponse());
        return listResponse;
    }

    @SneakyThrows
    @Access(levels = Role.ANONYMOUS)
    @RequestMapping(name = "getUserResultByMinMaxMark", value = "result/get/by/minMaxMark", method = RequestMethod.POST)
    public ListResponse<UserMinMaxMarkSemResponse> getUserResultByMinMaxMark(@RequestBody UserIdsRequest userIdsRequest) {
        ListResponse<UserMinMaxMarkSemResponse> listResponse = new ListResponse<>();
        listResponse.setData(userService.getUserResultByMinMaxSem(userIdsRequest));
        listResponse.setStatus(Response.getOkResponse());
        return listResponse;
    }

    @SneakyThrows
    @Access(levels = {Role.ADMIN, Role.DEPARTMENT})
    @RequestMapping(name = "deleteUserResult", value = "/result/delete/by/semester", method = RequestMethod.GET)
    public DataResponse<Object> deleteUserResult(@RequestParam String id, @RequestParam int semester) {
        DataResponse<Object> dataResponse = new DataResponse<>();
        userService.deleteUserResult(id, semester);
        dataResponse.setStatus(Response.getOkResponse());
        return dataResponse;
    }

    @SneakyThrows
    @Access(levels = Role.ANONYMOUS)
    @RequestMapping(name = "getUserResultByDate", value = "result/get/by/date", method = RequestMethod.POST)
    public ListResponse<UserResultByDateRespose> getUserResultByDate(@RequestBody UserResultByDate userResultByDate) {
        ListResponse<UserResultByDateRespose> listResponse = new ListResponse<>();
        listResponse.setData(userService.getUserResultByDate(userResultByDate));
        listResponse.setStatus(Response.getOkResponse());
        return listResponse;
    }

    @SneakyThrows
    @Access(levels = {Role.ADMIN, Role.DEPARTMENT})
    @RequestMapping(name = "updateUserResult", value = "/result/update", method = RequestMethod.POST)
    public ResultResponse<UserResponse> updateUserResult(@RequestParam String id, @RequestParam int semester, @RequestBody Resultupdate result) {
        ResultResponse<UserResponse> resultResponse = new ResultResponse<>();
        resultResponse.setData(userService.updateUserResult(id, semester, result));
        resultResponse.setStatus(Response.getOkResponse());
        resultResponse.setResult(result);
        return resultResponse;
    }
    @SneakyThrows
    @Access(levels = Role.ANONYMOUS)
    @RequestMapping(name = "getUserResultByStatus", value = "resultByStatus", method = RequestMethod.POST)
    public ListResponse<UserResultByStatus> getUserResultByStatus(@RequestBody UserIdsRequest userIdsRequest) {
        ListResponse<UserResultByStatus> listResponse = new ListResponse<>();
        listResponse.setData(userService.getUserResultByStatus(userIdsRequest));
        listResponse.setStatus(Response.getOkResponse());
        return listResponse;
    }

    @SneakyThrows
    @Access(levels = Role.ANONYMOUS)
    @RequestMapping(name = "getUserResultStatusByPagination", value = "/get/resultStatus/by/pagination", method = RequestMethod.POST)
    public PageResponse<UserResultByStatus> getUserResultStatusByPagination(@RequestBody FilterSortRequest<UserIdsRequest, UserSortBy> filterSortRequest) {
        PageResponse<UserResultByStatus> pageResponse = new PageResponse<>();
        UserIdsRequest userIdsRequest = filterSortRequest.getFilter();
        Page<UserResultByStatus> userResponse = userService.getUserResultStatusByPagination(userIdsRequest,
                filterSortRequest.getSort(),
                generalHelper.getPagination(filterSortRequest.getPage().getPage(), filterSortRequest.getPage().getLimit()));
        pageResponse.setData(userResponse);
        pageResponse.setStatus(Response.getOhkResponse());
        return pageResponse;
    }

    @SneakyThrows
    @RequestMapping(name = "getUserByExcel", value = "/detail/get/inExcel", method = RequestMethod.POST)
    @Access(levels = {Role.ANONYMOUS})
    public ResponseEntity<Resource> getUserByExcel(@RequestBody FilterSortRequest<UserFilter, UserSortBy> filterSortRequest) {
        UserFilter userFilter = filterSortRequest.getFilter();
        Workbook workbook = userService.getUserByExcel(userFilter,
                filterSortRequest.getSort(),
                generalHelper.getPagination(filterSortRequest.getPage().getPage(), filterSortRequest.getPage().getLimit()));
        assert workbook != null;
        ByteArrayResource resource = ExcelUtil.getBiteResourceFromWorkbook(workbook);
        return ResponseEntity.ok()
                .contentLength(resource.contentLength())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "exported_data_xlsx" + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(resource);
    }

    @SneakyThrows
    @Access(levels = {Role.ANONYMOUS})
    @RequestMapping(name = "getUserWithPagination", value = "get/all/user/by/pagination", method = RequestMethod.POST)
    public PageResponse<UserModel> getUserWithPagination(@RequestBody FilterSortRequest<UserFilter, UserSortBy> filterSortRequest) {
        PageResponse<UserModel> pageResponse = new PageResponse<>();
        UserFilter filter = filterSortRequest.getFilter();
        FilterSortRequest.SortRequest<UserSortBy> sort = filterSortRequest.getSort();
        Pagination pagination = filterSortRequest.getPage();
        PageRequest pageRequest = PageRequest.of(pagination.getPage(), pagination.getLimit());
        pageResponse.setData(userService.getUserWithPagination(filter,sort,pageRequest));
        pageResponse.setStatus(Response.getOkResponse());
        return pageResponse;
    }

    @SneakyThrows
    @Access(levels = Role.ANONYMOUS)
    @RequestMapping(name = "sendUserResultEmail", value = "/send/result/email", method = RequestMethod.GET)
    public DataResponse<Object> sendUserResultEmail(@RequestParam String id) {
        DataResponse<Object> dataResponse = new DataResponse<>();
        userService.resultDetailByEmail(id);
        dataResponse.setStatus(Response.getOkResponse());
        return dataResponse;
    }

    @SneakyThrows
    @Access(levels = Role.ANONYMOUS)
    @RequestMapping(name = "userUpdate", value = "update/send/email", method = RequestMethod.POST)
    public DataResponse<Object> userUpdate(@RequestParam String id, @RequestParam Role role, @RequestBody UserAddRequest userAddRequest) {
        DataResponse<Object> dataResponse = new DataResponse<>();
        userService.userUpdate(id, role, userAddRequest);
        dataResponse.setStatus(Response.getOkResponse());
        return dataResponse;
    }

    @SneakyThrows
    @Access(levels = Role.ADMIN)
    @RequestMapping(name = "userDelete", value = "/delete/user/id", method = RequestMethod.GET)
    public DataResponse<Object> userDelete(@RequestParam String id, @RequestParam Role role) {
        DataResponse<Object> dataResponse = new DataResponse<>();
        userService.userDelete(id, role);
        dataResponse.setStatus(Response.getOkResponse());
        return dataResponse;
    }


    @SneakyThrows
    @RequestMapping(name = "importUsers", value = {"/import/from/excel"}, method = RequestMethod.POST, consumes = {"multipart/form-data"})
    @Access(levels = {Role.ADMIN})
    public DataResponse<UserImportResponse> importUsers(@RequestParam(value = "file") MultipartFile file) {
        DataResponse<UserImportResponse> dataResponse = new DataResponse<>();
        dataResponse.setData(userService.importUsers(file, requestSession.getJwtUser().getId()));
        dataResponse.setStatus(Response.getSuccessResponse());
        return dataResponse;
    }

    @RequestMapping(name = "importedUsersVerify", value = {"/imported/user/verify"}, method = RequestMethod.POST)
    @Access(levels = {Role.ADMIN})
    public ListResponse<UserDataModel> importedUsersVerify(@RequestBody UserImportVerifyRequest verifyRequest) {
        ListResponse<UserDataModel> listResponse = new ListResponse<>();
        listResponse.setData(userService.importUsersVerify(verifyRequest));
        listResponse.setStatus(Response.getOkResponse());
        return listResponse;
    }
    @SneakyThrows
    @Access(levels = Role.ADMIN)
    @RequestMapping(name = "importedDataInUser", value = "/imported/data/collection", method = RequestMethod.POST)
    public ListResponse<UserResponse> importedDataInUser(@RequestBody UserIdsRequest userIdsRequest) {
        ListResponse<UserResponse> listResponse = new ListResponse<>();
        listResponse.setData(userService.importDataInUser(userIdsRequest));
        listResponse.setStatus(Response.getOtpResponse());
        return listResponse;
    }

    @SneakyThrows
    @Access(levels = {Role.ADMIN})
    @RequestMapping(name = "deleteUserInXls", value = "/delete/user/xls", method = RequestMethod.GET)
    public DataResponse<Object> deleteUserInXls(@RequestParam String id) {
        DataResponse<Object> dataResponse = new DataResponse<>();
        userService.deleteUserInXls(id);
        dataResponse.setStatus(Response.getOkResponse());
        return dataResponse;
    }

    @SneakyThrows
    @Access(levels = {Role.ADMIN})
    @RequestMapping(name = "getUserPassword", value = "/get/password", method = RequestMethod.GET)
    public DataResponse<Object> getUserPassword(@RequestParam String userName, @RequestParam String password, @RequestParam String confirmPassword) {
        DataResponse<Object> dataResponse = new DataResponse<>();
        userService.getUserPassword(userName, password, confirmPassword);
        dataResponse.setStatus(Response.getOkResponse());
        return dataResponse;
    }

    @SneakyThrows
    @Access(levels = {Role.ANONYMOUS})
    @RequestMapping(name = "sendMailToInvitedUser", value = "/sendMail/to/invitedUser", method = RequestMethod.GET)
    public DataResponse<Object> sendMailToInvitedUser(UserStatus userStatus) {
        DataResponse<Object> dataResponse = new DataResponse<>();
        userService.sendMailToInvitedUser(userStatus);
        dataResponse.setStatus(Response.getOkResponse());
        return dataResponse;
    }
    @SneakyThrows
    @Access(levels = {Role.ANONYMOUS})
    @RequestMapping(name = "getUserDetailByMonth", value = "getDetail/by/month", method = RequestMethod.GET)
    public DataResponse<MonthTitleName> getUserDetailByMonth(@RequestParam String year) {
        DataResponse<MonthTitleName> dataResponse = new DataResponse<>();
        dataResponse.setData(userService.getUserDetailByMonth(year));
        dataResponse.setStatus(Response.getOkResponse());
        return dataResponse;
    }
    @SneakyThrows
    @Access(levels = {Role.ADMIN})
    @RequestMapping(name = "getAllUserAndSetFullName", value = "/setFullName", method = RequestMethod.GET)
    public DataResponse<Object> getAllUserAndSetFullName()  {
        DataResponse<Object> dataResponse = new DataResponse<>();
         userService.getAllUserByPagination();
        dataResponse.setStatus(Response.getOhkResponse());
        return dataResponse;
    }


}
