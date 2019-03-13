package com.tonfay.submit.util;

public class StringUtils {
	public static boolean isNotBlank(final CharSequence cs) {
        return !isBlank(cs);
    }
	public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        if(cs.toString().trim().equals("null")) {
        	return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }
}
