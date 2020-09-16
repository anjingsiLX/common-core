package top.doudou.commons.utils;

import com.alibaba.fastjson.JSON;
import javolution.util.FastList;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author chensen
 * @create 2018-09-18-11:49
 */
public class CommonUtil {

    /**
     * 获取ip地址
     *
     * @param request
     * @return
     */
    public static String getRemoteIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip.contains(",")) {
            return ip.split(",")[0];
        } else {
            return ip;
        }
    }

    //6-16位登录账号验证
    public static boolean validLoginAccount(String account) {
        if (StringUtils.isBlank(account)) {
            return false;
        }
        String regex = "^[a-zA-Z0-9]{6,16}$";
        return account.matches(regex);
    }

    //6-16位登录密码
    public static boolean validLoginPassword(String password) {
        if (StringUtils.isBlank(password)) {
            return false;
        }
        String regex = "^[a-zA-Z0-9]{6,16}$";
        return password.matches(regex);
    }

    //身份证号验证
    public static boolean validIdNo(String idNo) {
        if (StringUtils.isBlank(idNo)) {
            return false;
        }
        String regex = "(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)";
        return idNo.matches(regex);
    }

    //手机号码验证
    public static boolean validMobile(String mobile) {
        if (StringUtils.isBlank(mobile)) {
            return false;
        }
        String regex = "^(0|86|17951)?(1)[0-9]{10}$";
        return mobile.matches(regex);
    }

    public static boolean validInt(Integer number) {
        return number != null && number >= 0;
    }

    public static boolean validId(Integer id) {
        return id != null && id > 0;
    }

    /**
     * 字符串数组转换为Integer集合
     *
     * @param arr
     * @return
     */
    public static List<Integer> toIntList(String[] arr) {
        return toIntList(arr, true);
    }

    public static List<Integer> toIntList(String[] arr, boolean distinct) {
        List<Integer> integers = FastList.newInstance();
        for (String id : arr) {
            if (StringUtils.isNotBlank(id)) {
                Integer key = Integer.parseInt(id.trim());
                if (!distinct || (distinct && !integers.contains(key))) {
                    integers.add(key);
                }
            }
        }
        return integers;
    }

    public static List<Long> toLongList(String[] arr) {
        return toLongList(arr, true);
    }

    public static List<Long> toLongList(String[] arr, boolean distinct) {
        List<Long> integers = FastList.newInstance();
        for (String id : arr) {
            if (StringUtils.isNotBlank(id)) {
                Long key = Long.parseLong(id.trim());
                if (!distinct || (distinct && !integers.contains(key))) {
                    integers.add(key);
                }
            }
        }
        return integers;
    }

    /**
     * 是否存在注解，如果存在就获取
     */
    public static <T extends Annotation> T getAnnotation(JoinPoint joinPoint, Class<T> annotationClass) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        if (method != null) {
            return method.getAnnotation(annotationClass);
        }
        return null;
    }

    /**
     * 参数拼装
     */
    public static String requestParamToString(Object[] paramsArray) {
        StringBuilder params = new StringBuilder();
        try {
            if (paramsArray != null && paramsArray.length > 0) {
                for (Object o : paramsArray) {
                    boolean needFilter = isFilterObject(o);
                    if (!needFilter) {
                        Object jsonObj = JSON.toJSON(o);
                        params.append(jsonObj.toString()).append(" ");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params.toString().trim();
    }

    /**
     * 判断是否需要过滤的对象。
     *
     * @param o 对象信息。
     * @return 如果是需要过滤的对象，则返回true；否则返回false。
     */
    private static boolean isFilterObject(Object o) {
        return o == null || o instanceof BindingResult || o instanceof MultipartFile || o instanceof MultipartFile[] || o instanceof HttpServletRequest || o instanceof HttpServletResponse;
    }


}
