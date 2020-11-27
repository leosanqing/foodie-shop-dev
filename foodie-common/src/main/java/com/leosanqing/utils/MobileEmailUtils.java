package com.leosanqing.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MobileEmailUtils {

    public static boolean checkMobileIsOk(String mobile) {
        String regex = "";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(mobile);
        boolean isMatch = m.matches();
        return isMatch;
    }

    public static boolean checkEmailIsOk(String email) {
        boolean isMatch = true;
        if (!email.matches("[\\w\\.\\-]+@([\\w\\-]+\\.)+[\\w\\-]+")) {
            isMatch = false;
        }
        return isMatch;
    }
}

