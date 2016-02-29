package cn.dataup.mgr.web.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilterUtil {
    private static String injStr = "'|and|exec|insert|select|delete|update|count|*|%|chr|mid|master|truncate|char|declare|or|=|like";

    /**
     * <pre>
     * 验证字符串的是否包含非法字符 
     * 字符串验验证成功：true;否则 false;
     * 
     * &#064;param msg
     * &#064;return
     * 
     */
    public static boolean isValidate(String msg) {
        if (hasInj(msg)) {
            return false;
        }
        return true;
    }

    private static boolean hasInj(String str) {
        String[] inj_stra = injStr.split("\\|");
        for (int i = 0; i < inj_stra.length; i++) {
            if (str.equalsIgnoreCase(inj_stra[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * 验证手机号
     * 
     * @param mobiles
     * @return
     */
    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^(13[0-9]|15[^4,\\D]|18[0-3,5-9]|14[57])\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 验证email
     * 
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9]*[-_\\.]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }
}
