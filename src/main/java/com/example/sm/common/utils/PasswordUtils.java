package com.example.sm.common.utils;

import com.example.sm.common.enums.PasswordEncryptionType;
import org.mindrot.jbcrypt.BCrypt;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.stereotype.Component;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
@Component
public class PasswordUtils {


    public String encryptPassword(String password) {

        // gensalt's log_rounds parameter determines the complexity
        // the work factor is 2**log_rounds, and the default is 10
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    public boolean isPasswordAuthenticated(String userProvided, String passwordInDatabase, PasswordEncryptionType passwordType) throws NoSuchAlgorithmException {
        // Check that an unencrypted password matches one that has
        // previously been hashed
        // If Password bi s null
        if (passwordInDatabase == null) {
            System.out.println("database password null");
            return false;
        }

        // Check the encryption type
        if (passwordType == null || passwordType.equals(PasswordEncryptionType.BCRYPT)) {
            return BCrypt.checkpw(userProvided, passwordInDatabase);
        } else {
            return encryptPasswordToMD5(userProvided).toLowerCase().equals(passwordInDatabase.toLowerCase());
        }
    }

    private static String encryptPasswordToMD5(String password) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(password.getBytes());
        return DatatypeConverter.printHexBinary(messageDigest.digest());
    }

    /**
     * Generate random password Using one lower case letter, one upper case letter,
     * one digit and one special character
     *
     * @return
     */
    public static String generateRandomPassword(int length) {
        List<CharacterRule> rules = Arrays.asList(new CharacterRule(EnglishCharacterData.UpperCase, 1),
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                new CharacterRule(EnglishCharacterData.Digit, 1),
                new CharacterRule(EnglishCharacterData.Special, 1),
                new CharacterRule(EnglishCharacterData.UpperCase, 1));
        PasswordGenerator generator = new PasswordGenerator();
        return generator.generatePassword(length, rules);
    }
}
