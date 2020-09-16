package top.doudou.commons.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: 傻男人
 * @Date: 2020/8/27 13:51
 * @Version: 1.0
 * @Description:
 */
@Slf4j
public class StringCommonUtils {

    private static final String REPLACE_STR = "\\{\\}";

    /**
     *  字符替换
     * @param format  需要替换的源字符
     * @param arguments  替换的参数
     * @return  替换后的字符
     */
    public static String strReplace(String format,Object... arguments){
        return strReplace(format,REPLACE_STR,arguments);
    }

    /**
     *  字符替换
     * @param format  需要替换的源字符
     * @param replace  需要替换源字符的哪个字符
     * @param arguments  替换的参数
     * @return  替换后的字符
     */
    public static String strReplace(String format,String replace,Object... arguments){
        if(StringUtils.isBlank(format)){
            return format;
        }
        Matcher m = Pattern.compile(replace).matcher(format);
        int i = 0;
        while (m.find() && i < arguments.length) {
            format = format.replaceFirst(replace, String.valueOf(arguments[i++]));
        }
        return format;
    }

}
