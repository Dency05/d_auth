    package com.example.sm.auth;

    import com.example.sm.auth.decorator.*;
    import com.example.sm.auth.enums.UserSortBy;
    import com.example.sm.auth.enums.UserStatus;
    import com.example.sm.auth.model.UserModel;
    import com.example.sm.common.decorator.*;
    import com.example.sm.common.enums.CustomHTTPHeaders;
    import com.example.sm.common.model.JWTUser;
    import com.example.sm.common.utils.JwtTokenUtil;
    import com.example.sm.helper.DataSetUserHelper;
    import com.google.gson.Gson;
    import lombok.extern.slf4j.Slf4j;
    import org.junit.jupiter.api.BeforeEach;
    import org.modelmapper.TypeToken;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
    import org.springframework.boot.test.context.SpringBootTest;
    import org.springframework.core.io.ClassPathResource;
    import org.springframework.data.domain.PageRequest;
    import org.springframework.data.domain.Sort;
    import org.springframework.http.MediaType;
    import org.springframework.mock.web.MockMultipartFile;
    import org.springframework.test.context.ActiveProfiles;
    import org.springframework.test.web.servlet.MockMvc;
    import org.springframework.test.web.servlet.MvcResult;
    import org.junit.jupiter.api.Test;
    import org.junit.jupiter.api.Assertions;
    import org.springframework.web.multipart.MultipartFile;

    import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
    import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
    import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
    import java.util.Collections;
    import java.util.Date;
    import java.util.HashSet;
    import java.util.Set;

    @SpringBootTest
    @ActiveProfiles("test")
    @AutoConfigureMockMvc
    @Slf4j
    public class UserControllerTest {
        @Autowired
        MockMvc mockMvc;
        @Autowired
        JwtTokenUtil jwtTokenUtil;

        @Autowired
        DataSetUserHelper dataSetUserHelper;

        @BeforeEach
        public void setUp(){
          dataSetUserHelper.cleanUp();
          dataSetUserHelper.init();
        }

        @Test
        void addOrUpdateUser() {
            try {
                UserModel userModel = dataSetUserHelper.getUserModel();
                System.out.println(userModel.getId());
                JWTUser jwtUser = new JWTUser(userModel.getId(), Collections.singletonList(userModel.getRole().toString()));
                String token = jwtTokenUtil.generateToken(jwtUser);
                UserAddRequest userAddRequest= new UserAddRequest();
                userAddRequest.setFirstName("denu");
                String json = new Gson().toJson(userAddRequest);
                MvcResult mvcResult = mockMvc.perform(post("/user/addOrUpdate?id="+userModel.getId()+"&role="+userModel.getRole())
                                .header(CustomHTTPHeaders.TOKEN.toString(), token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                        .andExpect(status().isOk()).andReturn();

                String result = mvcResult.getResponse().getContentAsString();
                DataResponse dataResponse = new Gson().fromJson(result, new TypeToken<DataResponse>() {}.getType());
                Assertions.assertEquals("Ok", dataResponse.getStatus().getStatus());
                dataResponse.getStatus().getDescription();

            }
            catch (Exception e) {
                log.error( "response not match :{} ",e.getMessage());
            }
        }

        @Test
        void getUsers() {
            try {
                UserModel userModel = dataSetUserHelper.getUserModel();
                System.out.println(userModel.getId());
                JWTUser jwtUser = new JWTUser(userModel.getId(), Collections.singletonList(userModel.getRole().toString()));
                String token = jwtTokenUtil.generateToken(jwtUser);
                Set<String> stringSet= new HashSet<>();
                stringSet.add(userModel.getId());
                String json = new Gson().toJson(stringSet);
                MvcResult mvcResult = mockMvc.perform(post("/user/get/users")
                                .header(CustomHTTPHeaders.TOKEN.toString(), token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                        .andExpect(status().isOk()).andReturn();

                String result = mvcResult.getResponse().getContentAsString();
                DataResponse dataResponse = new Gson().fromJson(result, new TypeToken<DataResponse>() {}.getType());
                Assertions.assertEquals("Ok", dataResponse.getStatus().getStatus());
                dataResponse.getStatus().getDescription();

            }
            catch (Exception e) {
                log.error( "response not match :{} ",e.getMessage());
            }
        }


       @Test
        void getUserFromId() {
            try {
                UserModel userModel = dataSetUserHelper.getUserModel();
                System.out.println(userModel.getId());
                JWTUser jwtUser = new JWTUser(userModel.getId(), Collections.singletonList(userModel.getRole().toString()));
                String token = jwtTokenUtil.generateToken(jwtUser);
                MvcResult mvcResult = mockMvc.perform(get("/user/get?id=" + userModel.getId())
                        .header(CustomHTTPHeaders.TOKEN.toString(), token)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk()).andReturn();

                String result = mvcResult.getResponse().getContentAsString();
                DataResponse dataResponse = new Gson().fromJson(result, new TypeToken<DataResponse>() {}.getType());
                Assertions.assertEquals("Ok", dataResponse.getStatus().getStatus());
                dataResponse.getStatus().getDescription();

            }
            catch (Exception e) {
                log.error( "response not match :{} ",e.getMessage());
            }
        }

        @Test
        void deleteUser(){
            try {
                UserModel userModel = dataSetUserHelper.getUserModel();
                JWTUser jwtUser = new JWTUser(userModel.getId(), Collections.singletonList(userModel.getRole().toString()));
                String token = jwtTokenUtil.generateToken(jwtUser);
                MvcResult mvcResult = mockMvc.perform(get("/user/delete/?id="+ userModel.getId())
                                .header(CustomHTTPHeaders.TOKEN.toString(), token)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk()).andReturn();

                String result = mvcResult.getResponse().getContentAsString();
                DataResponse dataResponse = new Gson().fromJson(result, new TypeToken<DataResponse>() {
                }.getType());

                Assertions.assertEquals("Ok", dataResponse.getStatus().getStatus());
                dataResponse.getStatus().getDescription();
                log.info("ohk success...");

            }
            catch (Exception e) {
                log.error( "response not match :{} ",e.getMessage());
            }
        }


        @Test
        void userLogIn(){
            try {
                UserModel userModel = dataSetUserHelper.getUserModel();
                JWTUser jwtUser = new JWTUser(userModel.getId(), Collections.singletonList(userModel.getRole().toString()));
                String token = jwtTokenUtil.generateToken(jwtUser);
                log.info("emial:{}",userModel.getEmail());
                MvcResult mvcResult = mockMvc.perform(get("/user/login?email="+userModel.getEmail()+"&password="+userModel.getPassword())
                                .header(CustomHTTPHeaders.TOKEN.toString(), token)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk()).andReturn();

                String result = mvcResult.getResponse().getContentAsString();
                DataResponse dataResponse = new Gson().fromJson(result, new TypeToken<DataResponse>() {
                }.getType());
                System.out.println(dataResponse);

                Assertions.assertEquals("Ok", dataResponse.getStatus().getStatus());
                dataResponse.getStatus().getDescription();
                log.info("ohk success...");
            }
            catch (Exception e) {
                log.error( "response not match :{} ",e.getMessage());
            }
        }

        @Test
        void addResult(){
            try {
                UserModel userModel = dataSetUserHelper.getUserModel();
                JWTUser jwtUser = new JWTUser(userModel.getId(), Collections.singletonList(userModel.getRole().toString()));
                String token = jwtTokenUtil.generateToken(jwtUser);

                Result results = new Result();
                results.setSemester(7);
                results.setSpi(9.6);
                //results.setDate(new Date());
                results.setYear(2022);
                String json = new Gson().toJson(results);
                MvcResult mvcResult = mockMvc.perform(post("/user/add/Result?id="+userModel.getId())
                                .header(CustomHTTPHeaders.TOKEN.toString(), token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                        .andExpect(status().isOk()).andReturn();

                String result = mvcResult.getResponse().getContentAsString();
                DataResponse dataResponse = new Gson().fromJson(result, new TypeToken<DataResponse>() {
                }.getType());
                System.out.println(dataResponse.getData());
                Assertions.assertEquals("Ok", dataResponse.getStatus().getStatus());
                dataResponse.getStatus().getDescription();
                log.info("ok success..");
            }
            catch (Exception e) {
                log.error( "response not match :{} ",e.getMessage());
            }
        }

        @Test
        void getUserResultByStatus(){
            try {
                UserModel userModel = dataSetUserHelper.getUserModel();
                JWTUser jwtUser = new JWTUser(userModel.getId(), Collections.singletonList(userModel.getRole().toString()));
                String token = jwtTokenUtil.generateToken(jwtUser);
                UserIdsRequest userIdsRequest= new UserIdsRequest();
                userIdsRequest.setUserId(Collections.singleton(userModel.getId()));
                String json = new Gson().toJson(userIdsRequest);
                MvcResult mvcResult = mockMvc.perform(post("/user/add/Result?id="+userModel.getId())
                                .header(CustomHTTPHeaders.TOKEN.toString(), token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                        .andExpect(status().isOk()).andReturn();


                String result = mvcResult.getResponse().getContentAsString();
                ListResponse listResponse = new Gson().fromJson(result, new TypeToken<ListResponse>() {
                }.getType());

                Assertions.assertEquals("Ok", listResponse.getStatus().getStatus());
                listResponse.getStatus().getDescription();
            }
            catch (Exception e) {
                log.error( "response not match :{} ",e.getMessage());
            }
        }

        @Test
        void getAllUser(){
            try {
                UserModel userModel = dataSetUserHelper.getUserModel();
                JWTUser jwtUser = new JWTUser(userModel.getId(), Collections.singletonList(userModel.getRole().toString()));
                String token = jwtTokenUtil.generateToken(jwtUser);
                System.out.println("token"+token);
                MvcResult mvcResult = mockMvc.perform(get("/user/getAll")
                                .header(CustomHTTPHeaders.TOKEN.toString(), token)
                                .contentType(MediaType.APPLICATION_JSON))//requestBody
                        .andExpect(status().isOk()).andReturn();

                String result = mvcResult.getResponse().getContentAsString();
                System.out.println(result);
                DataResponse dataResponse = new Gson().fromJson(result, new TypeToken<DataResponse>() {
                }.getType());

                Assertions.assertEquals("Success", dataResponse.getStatus().getStatus());
                dataResponse.getStatus().getDescription();
                log.info("ok.....");
            }
            catch (Exception e) {
                log.error( "response not match :{} ",e.getMessage());
            }
        }


        @Test
        void getUserByPagination(){
            try {
                UserModel userModel = dataSetUserHelper.getUserModel();
                JWTUser jwtUser = new JWTUser(userModel.getId(), Collections.singletonList(userModel.getRole().toString()));
                String token = jwtTokenUtil.generateToken(jwtUser);
                FilterSortRequest<UserFilter,UserSortBy> filterSortRequest= new FilterSortRequest<>();
                UserFilter userFilter= new UserFilter();
                FilterSortRequest.SortRequest<UserSortBy> sortBySortRequest= new FilterSortRequest.SortRequest<>();
                sortBySortRequest.setOrderBy(Sort.Direction.ASC);
                sortBySortRequest.setSortBy(UserSortBy.EMAIL);
                Pagination pagination= new Pagination();
                pagination.setLimit(10);
                pagination.setPage(0);
                PageRequest pageRequest = PageRequest.of(pagination.getPage(), pagination.getLimit());
                filterSortRequest.setFilter(userFilter);
                filterSortRequest.setSort(sortBySortRequest);
                filterSortRequest.setPage(pagination);
                String json = new Gson().toJson(filterSortRequest);

                MvcResult mvcResult = mockMvc.perform(post("/user/get/all/user/by/pagination")
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
        void getToken(){
            try {
                UserModel userModel = dataSetUserHelper.getUserModel();
                JWTUser jwtUser = new JWTUser(userModel.getId(), Collections.singletonList(userModel.getRole().toString()));
                String token = jwtTokenUtil.generateToken(jwtUser);
                MvcResult mvcResult = mockMvc.perform(get("/user/generateToken?id="+ userModel.getId())
                                .header(CustomHTTPHeaders.TOKEN.toString(), token)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk()).andReturn();

                String result = mvcResult.getResponse().getContentAsString();
                TokenResponse tokenResponse = new Gson().fromJson(result, new TypeToken<TokenResponse>() {
                }.getType());

                Assertions.assertEquals("Ok", tokenResponse.getStatus().getStatus());
                tokenResponse.getStatus().getDescription();
                log.info("ohk success...");

            }
            catch (Exception e) {
                log.error( "response not match :{} ",e.getMessage());
            }
        }

        @Test
        void getEncryptPassword(){
            try {
                UserModel userModel = dataSetUserHelper.getUserModel();
                JWTUser jwtUser = new JWTUser(userModel.getId(), Collections.singletonList(userModel.getRole().toString()));
                String token = jwtTokenUtil.generateToken(jwtUser);
                System.out.println(jwtUser);
                MvcResult mvcResult = mockMvc.perform(get("/user/getEncryptPassword?id="+userModel.getId())
                                .header(CustomHTTPHeaders.TOKEN.toString(), token)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk()).andReturn();

                String result = mvcResult.getResponse().getContentAsString();
                DataResponse dataResponse = new Gson().fromJson(result, new TypeToken<DataResponse>() {
                }.getType());
                System.out.println(dataResponse);

                Assertions.assertEquals("Ok", dataResponse.getStatus().getStatus());
                dataResponse.getStatus().getDescription();
                log.info("ohk success...");
            }
            catch (Exception e) {
                log.error( "response not match :{} ",e.getMessage());
            }
        }

        @Test
        void checkUserAuthentication(){
            try {
                UserModel userModel = dataSetUserHelper.getUserModel();
                JWTUser jwtUser = new JWTUser(userModel.getId(), Collections.singletonList(userModel.getRole().toString()));
                String token = jwtTokenUtil.generateToken(jwtUser);
                MvcResult mvcResult = mockMvc.perform(get("/user/getPasswords?email="+userModel.getEmail()+"&password="+userModel.getPassword())
                                .header(CustomHTTPHeaders.TOKEN.toString(), token)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk()).andReturn();

                String result = mvcResult.getResponse().getContentAsString();
                DataResponse dataResponse = new Gson().fromJson(result, new TypeToken<DataResponse>() {
                }.getType());
                System.out.println(dataResponse);

                Assertions.assertEquals("Ok", dataResponse.getStatus().getStatus());
                dataResponse.getStatus().getDescription();
                log.info("ohk success...");
            }
            catch (Exception e) {
                log.error( "response not match :{} ",e.getMessage());
            }
        }

        @Test
        void getIdFromToken(){
            try {
                UserModel userModel = dataSetUserHelper.getUserModel();
                System.out.println(userModel.getId());
                JWTUser jwtUser = new JWTUser(userModel.getId(), Collections.singletonList(userModel.getRole().toString()));
                String token = jwtTokenUtil.generateToken(jwtUser);
                MvcResult mvcResult = mockMvc.perform(get("/user/getIdFromToken?token=" +token)
                                .header(CustomHTTPHeaders.TOKEN.toString(), token)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk()).andReturn();

                String result = mvcResult.getResponse().getContentAsString();
                DataResponse dataResponse = new Gson().fromJson(result, new TypeToken<DataResponse>() {}.getType());
                Assertions.assertEquals("Ok", dataResponse.getStatus().getStatus());
                dataResponse.getStatus().getDescription();
            }
            catch (Exception e) {
                log.error( "response not match :{} ",e.getMessage());
            }
        }
        @Test
        void getValidateToken(){
            try {
                UserModel userModel = dataSetUserHelper.getUserModel();
                System.out.println(userModel.getId());
                JWTUser jwtUser = new JWTUser(userModel.getId(), Collections.singletonList(userModel.getRole().toString()));
                String token = jwtTokenUtil.generateToken(jwtUser);
                MvcResult mvcResult = mockMvc.perform(get("/user/validate/token?token=" +token)
                                .header(CustomHTTPHeaders.TOKEN.toString(), token)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk()).andReturn();

                String result = mvcResult.getResponse().getContentAsString();
                DataResponse dataResponse = new Gson().fromJson(result, new TypeToken<DataResponse>() {}.getType());
                Assertions.assertEquals("Ok", dataResponse.getStatus().getStatus());
                dataResponse.getStatus().getDescription();
            }
            catch (Exception e) {
                log.error( "response not match :{} ",e.getMessage());
            }
        }

        @Test
        void getOtpVerification(){
            try {
                UserModel userModel = dataSetUserHelper.getUserModel();
                System.out.println(userModel.getId());
                JWTUser jwtUser = new JWTUser(userModel.getId(), Collections.singletonList(userModel.getRole().toString()));
                String token = jwtTokenUtil.generateToken(jwtUser);
                MvcResult mvcResult = mockMvc.perform(get("/user/verification/Otp/?id=" +userModel.getId()+"&otp="+userModel.getOtp())
                                .header(CustomHTTPHeaders.TOKEN.toString(), token)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk()).andReturn();

                String result = mvcResult.getResponse().getContentAsString();
                DataResponse dataResponse = new Gson().fromJson(result, new TypeToken<DataResponse>() {}.getType());
                Assertions.assertEquals("Ok", dataResponse.getStatus().getStatus());
                dataResponse.getStatus().getDescription();
            }
            catch (Exception e) {
                log.error( "response not match :{} ",e.getMessage());
            }
        }

        @Test
        void forgotPassword(){
            try {
                UserModel userModel = dataSetUserHelper.getUserModel();
                System.out.println(userModel.getId());
                JWTUser jwtUser = new JWTUser(userModel.getId(), Collections.singletonList(userModel.getRole().toString()));
                String token = jwtTokenUtil.generateToken(jwtUser);
                MvcResult mvcResult = mockMvc.perform(get("/user/forgot/password/?email="+userModel.getEmail())
                                .header(CustomHTTPHeaders.TOKEN.toString(), token)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk()).andReturn();

                String result = mvcResult.getResponse().getContentAsString();
                DataResponse dataResponse = new Gson().fromJson(result, new TypeToken<DataResponse>() {}.getType());
                Assertions.assertEquals("Ok", dataResponse.getStatus().getStatus());
                dataResponse.getStatus().getDescription();
            }
            catch (Exception e) {
                log.error( "response not match :{} ",e.getMessage());
            }
        }

        @Test
        void setPassword(){
            try {
                UserModel userModel = dataSetUserHelper.getUserModel();
                System.out.println(userModel.getId());
                JWTUser jwtUser = new JWTUser(userModel.getId(), Collections.singletonList(userModel.getRole().toString()));
                String token = jwtTokenUtil.generateToken(jwtUser);
                MvcResult mvcResult = mockMvc.perform(get("/user/setPassword?id="+userModel.getId()+"&password="+userModel.getPassword()+"&confirmPassword="+userModel.getPassword())
                                .header(CustomHTTPHeaders.TOKEN.toString(), token)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk()).andReturn();

                String result = mvcResult.getResponse().getContentAsString();
                DataResponse dataResponse = new Gson().fromJson(result, new TypeToken<DataResponse>() {}.getType());
                Assertions.assertEquals("Ok", dataResponse.getStatus().getStatus());
                dataResponse.getStatus().getDescription();
            }
            catch (Exception e) {
                log.error( "response not match :{} ",e.getMessage());
            }
        }


        @Test
        void changePassword(){
            try {
                UserModel userModel = dataSetUserHelper.getUserModel();
                System.out.println(userModel.getId());
                JWTUser jwtUser = new JWTUser(userModel.getId(), Collections.singletonList(userModel.getRole().toString()));
                String token = jwtTokenUtil.generateToken(jwtUser);
                MvcResult mvcResult = mockMvc.perform(get("/user/change/user/Password?id="+userModel.getId()+"&password="+userModel.getPassword()+"&confirmPassword="+"Dency05"+"&newPassword="+"Dency05")
                                .header(CustomHTTPHeaders.TOKEN.toString(), token)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk()).andReturn();

                String result = mvcResult.getResponse().getContentAsString();
                DataResponse dataResponse = new Gson().fromJson(result, new TypeToken<DataResponse>() {}.getType());
                System.out.println(dataResponse);
                Assertions.assertEquals("Ok", dataResponse.getStatus().getStatus());
                dataResponse.getStatus().getDescription();
            }
            catch (Exception e) {
                log.error( "response not match :{} ",e.getMessage());
            }
        }

        @Test
        void logOut(){
            try {
                UserModel userModel = dataSetUserHelper.getUserModel();
                System.out.println(userModel.getId());
                JWTUser jwtUser = new JWTUser(userModel.getId(), Collections.singletonList(userModel.getRole().toString()));
                String token = jwtTokenUtil.generateToken(jwtUser);
                MvcResult mvcResult = mockMvc.perform(get("/user/logOut?id="+userModel.getId())
                                .header(CustomHTTPHeaders.TOKEN.toString(), token)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk()).andReturn();

                String result = mvcResult.getResponse().getContentAsString();
                DataResponse dataResponse = new Gson().fromJson(result, new TypeToken<DataResponse>() {}.getType());
                Assertions.assertEquals("Ok", dataResponse.getStatus().getStatus());
                dataResponse.getStatus().getDescription();
            }
            catch (Exception e) {
                log.error( "response not match :{} ",e.getMessage());
            }
        }


        @Test
        void getUserFromRole() {
            try {
                UserModel userModel = dataSetUserHelper.getUserModel();
                System.out.println(userModel.getId());
                UserFilter userFilter = new UserFilter();
                userFilter.setRole(userModel.getRole());
                String json = new Gson().toJson(userFilter);
                JWTUser jwtUser = new JWTUser(userModel.getId(), Collections.singletonList(userModel.getRole().toString()));
                String token = jwtTokenUtil.generateToken(jwtUser);
                MvcResult mvcResult = mockMvc.perform(post("/user/get/by/role")
                                .header(CustomHTTPHeaders.TOKEN.toString(), token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                        .andExpect(status().isOk()).andReturn();

                String result = mvcResult.getResponse().getContentAsString();
                DataResponse dataResponse = new Gson().fromJson(result, new TypeToken<DataResponse>() {
                }.getType());
                Assertions.assertEquals("Ok", dataResponse.getStatus().getStatus());
                dataResponse.getStatus().getDescription();
            } catch (Exception e) {
                log.error("response not match :{} ", e.getMessage());
            }
        }

            @Test
            void getUserResult(){
                try {
                    UserModel userModel = dataSetUserHelper.getUserModel();
                    System.out.println(userModel.getId());
                    JWTUser jwtUser = new JWTUser(userModel.getId(), Collections.singletonList(userModel.getRole().toString()));
                    String token = jwtTokenUtil.generateToken(jwtUser);
                    UserDetail userDetail= new UserDetail();
                    userDetail.setUserIds(Collections.singleton(userModel.getId()));
                    userDetail.setSemester(5);
                    String json = new Gson().toJson(userDetail);
                    MvcResult mvcResult = mockMvc.perform(post("/user/get/result/semester")
                                    .header(CustomHTTPHeaders.TOKEN.toString(), token)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(json))
                            .andExpect(status().isOk()).andReturn();

                    String result = mvcResult.getResponse().getContentAsString();
                    DataResponse dataResponse = new Gson().fromJson(result, new TypeToken<DataResponse>() {}.getType());
                    Assertions.assertEquals("Ok", dataResponse.getStatus().getStatus());
                    dataResponse.getStatus().getDescription();
                }
                catch (Exception e) {
                    log.error( "response not match :{} ",e.getMessage());
                }
            }

        @Test
        void getUserResultBySemester(){
            try {
                UserModel userModel = dataSetUserHelper.getUserModel();
                System.out.println(userModel.getId());
                JWTUser jwtUser = new JWTUser(userModel.getId(), Collections.singletonList(userModel.getRole().toString()));
                String token = jwtTokenUtil.generateToken(jwtUser);
                UserResult userResult= new UserResult();
                userResult.setUserIds(Collections.singleton(userModel.getId()));
                userResult.setSemester(Collections.singleton(5));
                String json = new Gson().toJson(userResult);
                MvcResult mvcResult = mockMvc.perform(post("/user/result/by/semester")
                                .header(CustomHTTPHeaders.TOKEN.toString(), token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                        .andExpect(status().isOk()).andReturn();

                String result = mvcResult.getResponse().getContentAsString();
                DataResponse dataResponse = new Gson().fromJson(result, new TypeToken<DataResponse>() {}.getType());
                Assertions.assertEquals("Ok", dataResponse.getStatus().getStatus());
                dataResponse.getStatus().getDescription();
            }
            catch (Exception e) {
                log.error( "response not match :{} ",e.getMessage());
            }
        }

        @Test
        void getUserResultByMinMaxMark(){
            try {
                UserModel userModel = dataSetUserHelper.getUserModel();
                System.out.println(userModel.getId());
                JWTUser jwtUser = new JWTUser(userModel.getId(), Collections.singletonList(userModel.getRole().toString()));
                String token = jwtTokenUtil.generateToken(jwtUser);
                UserIdsRequest userIdsRequest =new UserIdsRequest();
                userIdsRequest.setUserId(Collections.singleton(userModel.getId()));
                String json = new Gson().toJson(userIdsRequest);
                MvcResult mvcResult = mockMvc.perform(post("/user/result/get/by/minMaxMark")
                                .header(CustomHTTPHeaders.TOKEN.toString(), token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                        .andExpect(status().isOk()).andReturn();

                String result = mvcResult.getResponse().getContentAsString();
                DataResponse dataResponse = new Gson().fromJson(result, new TypeToken<DataResponse>() {}.getType());
                Assertions.assertEquals("Ok", dataResponse.getStatus().getStatus());
                dataResponse.getStatus().getDescription();
            }
            catch (Exception e) {
                log.error( "response not match :{} ",e.getMessage());
            }
        }

        @Test
        void deleteUserResult(){
            try {
                UserModel userModel = dataSetUserHelper.getUserModel();
                System.out.println(userModel.getId());
                JWTUser jwtUser = new JWTUser(userModel.getId(), Collections.singletonList(userModel.getRole().toString()));
                String token = jwtTokenUtil.generateToken(jwtUser);
                MvcResult mvcResult = mockMvc.perform(get("/user/result/delete/by/semester?semester="+5 +"&id="+userModel.getId())
                                .header(CustomHTTPHeaders.TOKEN.toString(), token)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk()).andReturn();

                String result = mvcResult.getResponse().getContentAsString();
                DataResponse dataResponse = new Gson().fromJson(result, new TypeToken<DataResponse>() {}.getType());
                Assertions.assertEquals("Ok", dataResponse.getStatus().getStatus());
                dataResponse.getStatus().getDescription();
            }
            catch (Exception e) {
                log.error( "response not match :{} ",e.getMessage());
            }
        }

        @Test
        void getUserResultByDate(){
            try {
                UserModel userModel = dataSetUserHelper.getUserModel();
                System.out.println(userModel.getId());
                JWTUser jwtUser = new JWTUser(userModel.getId(), Collections.singletonList(userModel.getRole().toString()));
                String token = jwtTokenUtil.generateToken(jwtUser);
                UserResultByDate userResultByDate = new UserResultByDate();
                userResultByDate.setDate(String.valueOf(new Date()));
                String json = new Gson().toJson(userResultByDate);
                MvcResult mvcResult = mockMvc.perform(post("/user/result/get/by/date")
                                .header(CustomHTTPHeaders.TOKEN.toString(), token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                        .andExpect(status().isOk()).andReturn();

                String result = mvcResult.getResponse().getContentAsString();
                DataResponse dataResponse = new Gson().fromJson(result, new TypeToken<DataResponse>() {}.getType());
                Assertions.assertEquals("Ok", dataResponse.getStatus().getStatus());
                dataResponse.getStatus().getDescription();
            }
            catch (Exception e) {
                log.error( "response not match :{} ",e.getMessage());
            }
        }

        @Test
        void updateUserResult(){
            try {
                UserModel userModel = dataSetUserHelper.getUserModel();
                System.out.println(userModel.getId());
                JWTUser jwtUser = new JWTUser(userModel.getId(), Collections.singletonList(userModel.getRole().toString()));
                String token = jwtTokenUtil.generateToken(jwtUser);
                Resultupdate resultupdate= new Resultupdate();
                resultupdate.setSpi(9.8);
                resultupdate.setDate(new Date());
                String json = new Gson().toJson(resultupdate);
                MvcResult mvcResult = mockMvc.perform(post("/user/result/update?id="+userModel.getId()+"&semester="+5)
                                .header(CustomHTTPHeaders.TOKEN.toString(), token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                        .andExpect(status().isOk()).andReturn();

                String result = mvcResult.getResponse().getContentAsString();
                DataResponse dataResponse = new Gson().fromJson(result, new TypeToken<DataResponse>() {}.getType());
                Assertions.assertEquals("Ok", dataResponse.getStatus().getStatus());
                dataResponse.getStatus().getDescription();
            }
            catch (Exception e) {
                log.error( "response not match :{} ",e.getMessage());
            }
        }

       @Test
        void getAllUserAndSetFullName(){
           try {
               UserModel userModel = dataSetUserHelper.getUserModel();
               System.out.println(userModel.getId());
               JWTUser jwtUser = new JWTUser(userModel.getId(), Collections.singletonList(userModel.getRole().toString()));
               String token = jwtTokenUtil.generateToken(jwtUser);
               MvcResult mvcResult = mockMvc.perform(get("/user/setFullName")
                               .header(CustomHTTPHeaders.TOKEN.toString(), token)
                               .contentType(MediaType.APPLICATION_JSON))
                       .andExpect(status().isOk()).andReturn();

               String result = mvcResult.getResponse().getContentAsString();
               DataResponse dataResponse = new Gson().fromJson(result, new TypeToken<DataResponse>() {}.getType());
               Assertions.assertEquals("Ok", dataResponse.getStatus().getStatus());
               dataResponse.getStatus().getDescription();
           }
           catch (Exception e) {
               log.error( "response not match :{} ",e.getMessage());
           }
       }


       @Test
       void getUserDetailByMonth(){
           try {
               UserModel userModel = dataSetUserHelper.getUserModel();
               System.out.println(userModel.getId());
               JWTUser jwtUser = new JWTUser(userModel.getId(), Collections.singletonList(userModel.getRole().toString()));
               String token = jwtTokenUtil.generateToken(jwtUser);
               MvcResult mvcResult = mockMvc.perform(get("/user/getDetail/by/month?year="+"2022")
                               .header(CustomHTTPHeaders.TOKEN.toString(), token)
                               .contentType(MediaType.APPLICATION_JSON))
                       .andExpect(status().isOk()).andReturn();

               String result = mvcResult.getResponse().getContentAsString();
               DataResponse dataResponse = new Gson().fromJson(result, new TypeToken<DataResponse>() {}.getType());
               Assertions.assertEquals("Ok", dataResponse.getStatus().getStatus());
               dataResponse.getStatus().getDescription();
           }
           catch (Exception e) {
               log.error( "response not match :{} ",e.getMessage());
           }
       }


       @Test
        void sendMailToInvitedUser(){
           try {
               UserModel userModel = dataSetUserHelper.getUserModel();
               System.out.println(userModel.getId());
               JWTUser jwtUser = new JWTUser(userModel.getId(), Collections.singletonList(userModel.getRole().toString()));
               String token = jwtTokenUtil.generateToken(jwtUser);
               MvcResult mvcResult = mockMvc.perform(get("/user/sendMail/to/invitedUser?userStatus="+UserStatus.INVITED)
                               .header(CustomHTTPHeaders.TOKEN.toString(), token)
                               .contentType(MediaType.APPLICATION_JSON))
                       .andExpect(status().isOk()).andReturn();

               String result = mvcResult.getResponse().getContentAsString();
               DataResponse dataResponse = new Gson().fromJson(result, new TypeToken<DataResponse>() {}.getType());
               Assertions.assertEquals("Ok", dataResponse.getStatus().getStatus());
               dataResponse.getStatus().getDescription();
           }
           catch (Exception e) {
               log.error( "response not match :{} ",e.getMessage());
           }
       }

       @Test
        void getUserPassword(){
           try {
               UserModel userModel = dataSetUserHelper.getUserModel();
               System.out.println(userModel.getId());
               JWTUser jwtUser = new JWTUser(userModel.getId(), Collections.singletonList(userModel.getRole().toString()));
               String token = jwtTokenUtil.generateToken(jwtUser);
               MvcResult mvcResult = mockMvc.perform(get("/user/get/password?userName="+userModel.getUserName()+"&password="+userModel.getPassword()+"&confirmPassword="+userModel.getPassword())
                               .header(CustomHTTPHeaders.TOKEN.toString(), token)
                               .contentType(MediaType.APPLICATION_JSON))
                       .andExpect(status().isOk()).andReturn();

               String result = mvcResult.getResponse().getContentAsString();
               DataResponse dataResponse = new Gson().fromJson(result, new TypeToken<DataResponse>() {}.getType());
               Assertions.assertEquals("Ok", dataResponse.getStatus().getStatus());
               dataResponse.getStatus().getDescription();
           }
           catch (Exception e) {
               log.error( "response not match :{} ",e.getMessage());
           }
       }

       @Test
        void deleteUserInXls(){
           try {
               UserModel userModel = dataSetUserHelper.getUserModel();
               System.out.println(userModel.getId());
               JWTUser jwtUser = new JWTUser(userModel.getId(), Collections.singletonList(userModel.getRole().toString()));
               String token = jwtTokenUtil.generateToken(jwtUser);
               MvcResult mvcResult = mockMvc.perform(get("/user/delete/user/xls?id="+userModel.getId())
                               .header(CustomHTTPHeaders.TOKEN.toString(), token)
                               .contentType(MediaType.APPLICATION_JSON))
                       .andExpect(status().isOk()).andReturn();

               String result = mvcResult.getResponse().getContentAsString();
               DataResponse dataResponse = new Gson().fromJson(result, new TypeToken<DataResponse>() {}.getType());
               Assertions.assertEquals("Ok", dataResponse.getStatus().getStatus());
               dataResponse.getStatus().getDescription();
           }
           catch (Exception e) {
               log.error( "response not match :{} ",e.getMessage());
           }
       }
        @Test
        void  importedDataInUse(){
            try {
                UserModel userModel = dataSetUserHelper.getUserModel();
                System.out.println(userModel.getId());
                JWTUser jwtUser = new JWTUser(userModel.getId(), Collections.singletonList(userModel.getRole().toString()));
                String token = jwtTokenUtil.generateToken(jwtUser);
                UserIdsRequest userIdsRequest= new UserIdsRequest();
                userIdsRequest.setUserId(Collections.singleton(userModel.getId()));
                String json = new Gson().toJson(userIdsRequest);
                MvcResult mvcResult = mockMvc.perform(post("/user/imported/data/collection")
                                .header(CustomHTTPHeaders.TOKEN.toString(), token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                        .andExpect(status().isOk()).andReturn();

                String result = mvcResult.getResponse().getContentAsString();
                DataResponse dataResponse = new Gson().fromJson(result, new TypeToken<DataResponse>() {}.getType());
                Assertions.assertEquals("Ok", dataResponse.getStatus().getStatus());
                dataResponse.getStatus().getDescription();
            }
            catch (Exception e) {
                log.error( "response not match :{} ",e.getMessage());
            }
        }

        @Test
        void UserImportVerifyRequest(){
            try {
                UserModel userModel = dataSetUserHelper.getUserModel();
                System.out.println(userModel.getId());
                JWTUser jwtUser = new JWTUser(userModel.getId(), Collections.singletonList(userModel.getRole().toString()));
                String token = jwtTokenUtil.generateToken(jwtUser);
                UserImportVerifyRequest userImportVerifyRequest= new UserImportVerifyRequest();
                userImportVerifyRequest.setId(userModel.getId());
                String json = new Gson().toJson(userImportVerifyRequest);
                MvcResult mvcResult = mockMvc.perform(post("/user/imported/user/verify")
                                .header(CustomHTTPHeaders.TOKEN.toString(), token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                        .andExpect(status().isOk()).andReturn();

                String result = mvcResult.getResponse().getContentAsString();
                DataResponse dataResponse = new Gson().fromJson(result, new TypeToken<DataResponse>() {}.getType());
                Assertions.assertEquals("Ok", dataResponse.getStatus().getStatus());
                dataResponse.getStatus().getDescription();
            }
            catch (Exception e) {
                log.error( "response not match :{} ",e.getMessage());
            }
        }

        @Test
        void importUsers(){
            try {
                UserModel userModel = dataSetUserHelper.getUserModel();
                System.out.println(userModel.getId());
                JWTUser jwtUser = new JWTUser(userModel.getId(), Collections.singletonList(userModel.getRole().toString()));
                String token = jwtTokenUtil.generateToken(jwtUser);
                MultipartFile file = new MockMultipartFile("filename", "AuthData.xlsx", "application/vnd.ms-excel", new ClassPathResource("AuthData.xlsx").getInputStream());
                System.out.println("file"+file);
                MvcResult mvcResult = mockMvc.perform(get("/user/import/from/excel/?file="+file)
                                .header(CustomHTTPHeaders.TOKEN.toString(), token)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk()).andReturn();

                String result = mvcResult.getResponse().getContentAsString();
                DataResponse dataResponse = new Gson().fromJson(result, new TypeToken<DataResponse>() {}.getType());
                Assertions.assertEquals("Ok", dataResponse.getStatus().getStatus());
                dataResponse.getStatus().getDescription();
            }
            catch (Exception e) {
                log.error( "response not match :{} ",e.getMessage());
            }
        }

        @Test
        void userDelete(){
            try {
                UserModel userModel = dataSetUserHelper.getUserModel();
                System.out.println(userModel.getId());
                JWTUser jwtUser = new JWTUser(userModel.getId(), Collections.singletonList(userModel.getRole().toString()));
                String token = jwtTokenUtil.generateToken(jwtUser);
                MvcResult mvcResult = mockMvc.perform(get("/user/delete/user/id?id="+userModel.getId()+"&role="+userModel.getRole())
                                .header(CustomHTTPHeaders.TOKEN.toString(), token)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk()).andReturn();

                String result = mvcResult.getResponse().getContentAsString();
                DataResponse dataResponse = new Gson().fromJson(result, new TypeToken<DataResponse>() {}.getType());
                Assertions.assertEquals("Ok", dataResponse.getStatus().getStatus());
                dataResponse.getStatus().getDescription();
            }
            catch (Exception e) {
                log.error( "response not match :{} ",e.getMessage());
            }
        }

        @Test
        void userUpdate(){
            try {
                UserModel userModel = dataSetUserHelper.getUserModel();
                System.out.println(userModel.getId());
                JWTUser jwtUser = new JWTUser(userModel.getId(), Collections.singletonList(userModel.getRole().toString()));
                String token = jwtTokenUtil.generateToken(jwtUser);
                UserAddRequest userAddRequest= new UserAddRequest();
                userAddRequest.setUserName("dency");
                userAddRequest.setFirstName("Denuu");
                String json = new Gson().toJson(userAddRequest);
                MvcResult mvcResult = mockMvc.perform(post("/user/update/send/email?id="+userModel.getId()+"&role="+userModel.getRole())
                                .header(CustomHTTPHeaders.TOKEN.toString(), token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                        .andExpect(status().isOk()).andReturn();

                String result = mvcResult.getResponse().getContentAsString();
                System.out.println(result);
                DataResponse dataResponse = new Gson().fromJson(result, new TypeToken<DataResponse>() {}.getType());
                Assertions.assertEquals("Ok", dataResponse.getStatus().getStatus());
                System.out.println(userModel.getFirstName());
                dataResponse.getStatus().getDescription();
            }
            catch (Exception e) {
                log.error( "response not match :{} ",e.getMessage());
            }
        }

        @Test
        void sendUserResultEmail(){
            try {
                UserModel userModel = dataSetUserHelper.getUserModel();
                System.out.println(userModel.getId());
                JWTUser jwtUser = new JWTUser(userModel.getId(), Collections.singletonList(userModel.getRole().toString()));
                String token = jwtTokenUtil.generateToken(jwtUser);
                MvcResult mvcResult = mockMvc.perform(get("/user/send/result/email?id="+userModel.getId())
                                .header(CustomHTTPHeaders.TOKEN.toString(), token)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk()).andReturn();

                String result = mvcResult.getResponse().getContentAsString();
                DataResponse dataResponse = new Gson().fromJson(result, new TypeToken<DataResponse>() {}.getType());
                Assertions.assertEquals("Ok", dataResponse.getStatus().getStatus());
                dataResponse.getStatus().getDescription();
            }
            catch (Exception e) {
                log.error( "response not match :{} ",e.getMessage());
            }
        }

        @Test
        void getUserByExcel(){
            try {
                UserModel userModel = dataSetUserHelper.getUserModel();
                JWTUser jwtUser = new JWTUser(userModel.getId(), Collections.singletonList(userModel.getRole().toString()));
                String token = jwtTokenUtil.generateToken(jwtUser);
                FilterSortRequest<UserFilter,UserSortBy> filterSortRequest= new FilterSortRequest<>();
                UserFilter userFilter= new UserFilter();
                FilterSortRequest.SortRequest<UserSortBy> sortBySortRequest= new FilterSortRequest.SortRequest<>();
                sortBySortRequest.setOrderBy(Sort.Direction.ASC);
                sortBySortRequest.setSortBy(UserSortBy.EMAIL);
                Pagination pagination= new Pagination();
                pagination.setLimit(10);
                pagination.setPage(0);
                PageRequest pageRequest = PageRequest.of(pagination.getPage(), pagination.getLimit());
                filterSortRequest.setFilter(userFilter);
                filterSortRequest.setSort(sortBySortRequest);
                filterSortRequest.setPage(pagination);
                String json = new Gson().toJson(filterSortRequest);
                System.out.println("json:"+json);

                MvcResult mvcResult = mockMvc.perform(post("/user/detail/get/inExcel")
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
        void  getUserResultStatusByPagination(){
            try {
                UserModel userModel = dataSetUserHelper.getUserModel();
                JWTUser jwtUser = new JWTUser(userModel.getId(), Collections.singletonList(userModel.getRole().toString()));
                String token = jwtTokenUtil.generateToken(jwtUser);
                FilterSortRequest<UserIdsRequest,UserSortBy> filterSortRequest= new FilterSortRequest<>();
                UserIdsRequest userIdsRequest = new UserIdsRequest();
                userIdsRequest.setUserId(Collections.singleton(userModel.getId()));
                FilterSortRequest.SortRequest<UserSortBy> sortBySortRequest= new FilterSortRequest.SortRequest<>();
                sortBySortRequest.setOrderBy(Sort.Direction.ASC);
                sortBySortRequest.setSortBy(UserSortBy.EMAIL);
                Pagination pagination= new Pagination();
                pagination.setLimit(10);
                pagination.setPage(0);
                PageRequest pageRequest = PageRequest.of(pagination.getPage(), pagination.getLimit());
                filterSortRequest.setFilter(userIdsRequest);
                filterSortRequest.setSort(sortBySortRequest);
                filterSortRequest.setPage(pagination);
                String json = new Gson().toJson(filterSortRequest);

                MvcResult mvcResult = mockMvc.perform(post("/user/get/resultStatus/by/pagination")
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





    }


