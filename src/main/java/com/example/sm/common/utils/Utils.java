package com.example.sm.common.utils;

import com.example.sm.auth.decorator.Result;
import com.example.sm.auth.model.UserModel;
import com.example.sm.common.decorator.RequestSession;
import com.example.sm.common.decorator.RestAPI;
import com.example.sm.common.enums.CustomHTTPHeaders;
import com.example.sm.common.exception.NotFoundException;
import com.example.sm.common.model.AdminConfiguration;
import com.example.sm.common.model.EmailModel;
import com.example.sm.common.service.AdminConfigurationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class Utils {

    @Autowired
    MessageSource messageSource;

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    AdminConfigurationService configurationService;
    @Autowired
    RequestSession requestSession;

    private static final String TOKEN_NOT_FOUND = "Token not found";
    public static String generateVerificationToken(int length) {
        // Using random method
        Random random_method = new Random();
        StringBuilder otp = new StringBuilder();
        for(int i =0; i< length; i++){
            otp.append(random_method.nextInt(10));
        }
        return otp.toString();
    }
    public static String getTokenFromHeaders(HttpServletRequest request) throws NotFoundException {
        String jwtToken = request.getHeader(CustomHTTPHeaders.TOKEN.toString());
        if(jwtToken ==null){
            throw new NotFoundException(TOKEN_NOT_FOUND);
        }
        return jwtToken;
    }
    public static String encodeBase64(String password) {
        byte[] pass = Base64.encodeBase64(password.getBytes());
        String actualString = new String(pass);
        log.info("actual string:{}",actualString);
        return actualString;
    }
    public static String decodeBase64(String password) {
        byte[] actualByte = Base64.decodeBase64(password);
        String actualString = new String(actualByte);
        System.out.println(actualString);
        return actualString;
    }
    /*
    public static List<com.example.auth.enumUser.Role> getAllRoles(Class<com.example.auth.enumUser.Role> authorizationClass) {
        List<com.example.auth.common.model.Role> roleList = new ArrayList<>();
        com.example.auth.enumUser.Role[] authList = com.example.auth.enumUser.Role.values();
        for (com.example.auth.enumUser.Role authorization : authList) {
            com.example.auth.common.model.Role role = new com.example.auth.common.model.Role();
            role.setSoftDelete(false);
            role.setRoleDescription(authorization.name());
            role.setSpecificRole(authorization.name());
            roleList.add(role);
        }
        return roleList;
    }

     */

    public void sendEmailNow( EmailModel emailModel) {
        try {

            if(StringUtils.isEmpty(emailModel.getSubject())){
                emailModel.setSubject(emailModel.getSubject());
            }

            // Recipient's email ID needs to be mentioned.
            // Sender's email ID needs to be mentioned
            AdminConfiguration adminConfiguration= configurationService.getConfiguration();
            Properties props = new Properties();
            props.put("mail.smtp.auth", adminConfiguration.isSmptAuth());//true
            props.put("mail.smtp.starttls.enable", adminConfiguration.isStarttls());//true
            props.put("mail.smtp.host", adminConfiguration.getHost());//smtp.office365.com
            props.put("mail.smtp.port", adminConfiguration.getPort());//587
            // Get the Session object.
            log.info("getting session object:");
            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(adminConfiguration.getUsername(), adminConfiguration.getPassword());
                }
            });
            log.info("got session object:");

            try {
                // Create a default MimeMessage object.
                MimeMessage message = new MimeMessage(session);
                log.info("helper object initialization");
                MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
                // Set From: header field of the header.
                helper.setFrom(new InternetAddress(adminConfiguration.getFrom()));
                log.info("helper from set");
                // Set To: header field of the header.
                helper.setTo(emailModel.getTo());
                log.info("helper to  set");
                if(emailModel.getCc() != null && emailModel.getCc().size() != 0){
                    String[] cc = new String[emailModel.getCc().size()];
                    emailModel.getCc().toArray(cc);
                    helper.setCc(cc);
                }
              /*  if(emailModel.getBcc() != null && emailModel.getBcc().size() != 0){
                    String[] bcc = new String[emailModel.getBcc().size()];
                    emailModel.getBcc().toArray(bcc);
                    helper.setBcc(bcc);
                }*/


                // Set Subject: header field
                helper.setSubject(emailModel.getSubject());
                log.info("helper set subject ");

                // Now set the actual message
              if(emailModel.getFile()!=null) {
                  DataSource source = new FileDataSource(emailModel.getFile().getName());
                  Multipart multipart = new MimeMultipart();
                  MimeBodyPart messageBodyPart = new MimeBodyPart();
                  MimeBodyPart messageBodyPart1 = new MimeBodyPart();
                  messageBodyPart.setDataHandler(new DataHandler(source));
                  messageBodyPart.setFileName(emailModel.getFile().getName());
                  multipart.addBodyPart(messageBodyPart);
                  message.setContent(multipart);
                 /* FileSystemResource fileSystemResource= new FileSystemResource("C:\\excelFiles\\"+emailModel.getFile().getName());
                  helper.addAttachment(Objects.requireNonNull(fileSystemResource.getFilename()),fileSystemResource);*/
                  log.info("helper set attachment");
              }

                /*log.info("email model attachment list size:{}",emailModel.getAttachmentList().size());
                log.info("email model attachment list size:{}",emailModel.getAttachmentList().toString());
                if(emailModel.getAttachmentList() != null && emailModel.getAttachmentList().size() != 0){
                    emailModel.getAttachmentList().forEach(attachment->{
                        try {
                            helper.addAttachment(attachment.getFileName(), new URLDataSource(new URL(attachment.getAttachmentUrl())));
                            log.info("helper set attachment");
                        } catch (MessagingException | IOException e) {
                            log.info("Unable to attach File");
                            e.printStackTrace();
                        }
                    });
                }*/



                // Send message
                new Thread(() ->{
                    try {
                        log.info("email send start...");
                        Transport.send(message);
                        log.info("email send ended");
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }).start();
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        } catch (Exception ignored) {}
    }

    public static List<RestAPI> getAllMethodNames(Class className) {
        Method[] allMethods = className.getDeclaredMethods();
        List<RestAPI> apis = new ArrayList<>();
        for (Method method : allMethods) {
            if (Modifier.isPublic(method.getModifiers())) {
               Access a = method.getAnnotation(Access.class);
                RequestMapping rm = method.getAnnotation(RequestMapping.class);
                log.info("Name : {} , Access : {} ", rm.name(), a);
                if (a != null) {
                    List<String> authList = new ArrayList<>(Arrays.asList(a.levels()))
                            .stream()
                            .map(Enum::toString)
                            .collect(Collectors.toList());
                    RestAPI api = new RestAPI();
                    api.setName(rm.name());
                    api.setRoles(authList);
                    apis.add(api);
                }
            }
        }
        return apis;

    }

    public String generateReportMessage(List<Result> result,double cgpi) {
        StringBuilder stringBuilder = generateCommonHtmlHead();
        for (Result result1 : result) {
            stringBuilder.append("<tr>");
            stringBuilder.append("<style> td { font-size: 12px; }  </style>");
            stringBuilder.append("<td>").append(result1.getSemester()).append("</td>");
            stringBuilder.append("<td>").append(result1.getSpi()).append("</td>");
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String strDate= formatter.format(result1.getDate());
            log.info("date"+strDate);
            stringBuilder.append("<td>").append(strDate).append("</td>");
            stringBuilder.append("</tr>");
        }
        log.info(stringBuilder.toString());
        generateCommonFooter(stringBuilder);
        System.out.println("string builder"+stringBuilder.toString());
        return stringBuilder.toString();
    }

    private StringBuilder generateCommonHtmlHead() {
        StringBuilder stringBuilder = new StringBuilder();
        return stringBuilder.append("<head>")
                .append("<h3>Result Details<h3>")
                .append("</head>")
                .append("<body>")
                .append("<table border=1>")
                .append("<tr>")
                .append(("<style> th { font-size: 14px; } </style>"))
                .append("<th>semester</th><th>spi</th><th>Date</th>")
                .append("</tr>");
    }

    private void generateCommonFooter(StringBuilder stringBuilder) {
        stringBuilder.append("</table></body>");
    }

    public String genearteUpdatedUserDetail(HashMap<String, String> changedProperties ) {
        StringBuilder stringBuilder = new StringBuilder();
        if(!CollectionUtils.isEmpty(changedProperties)) {
            stringBuilder.append("You updated following fields");
            stringBuilder.append("br/");
            stringBuilder.append(changedProperties);
            stringBuilder.append("br/");
            stringBuilder.append("Updated By:");
            stringBuilder.append(requestSession.getJwtUser().getRole());
        }
        log.info(stringBuilder.toString());
        return stringBuilder.toString();
    }


    public String sendOtp(UserModel userModel,String confirmPassword) {
        StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Your email is   :");
            stringBuilder.append(userModel.getEmail());
            stringBuilder.append("<br/>");
            stringBuilder.append("Your Password is  :");
            stringBuilder.append(" " +confirmPassword);
            stringBuilder.append("<br/>");
            stringBuilder.append("Otp is  :");
            stringBuilder.append("" +userModel.getOtp());
            stringBuilder.append("<br/>");
            stringBuilder.append("Created by :");
            stringBuilder.append(requestSession.getJwtUser().getId());
        log.info(stringBuilder.toString());
        return stringBuilder.toString();
    }
}
