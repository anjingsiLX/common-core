package top.doudou.commons.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * @Author: 傻男人
 * @Date: 2020/4/30 14:57
 * @Version: 1.0
 * @Description:
 */
public class RandomUtils {

    private static String numCode = "0123456789";

    /**
     * 随机数字验证码
     * @param n
     * @return
     */
    public static String getNumCode(int n) {
        char[] ch = new char[n];
        for (int i = 0; i < n; i++) {
            Random random = new Random();
            int index = random.nextInt(numCode.length());
            ch[i] = numCode.charAt(index);
        }
        String result = String.valueOf(ch);
        return result;
    }

    public static String randomTime(){
        return new SimpleDateFormat("yyyyMMdd").format(new Date());
    }

}
