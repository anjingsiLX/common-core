package top.doudou.commons.aspect;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.StringJoiner;

/**
 * @Author: 傻男人
 * @Date: 2020/8/21 13:25
 * @Version: 1.0
 * @Description: 切面的工具类
 */
public class AspectUtils {

    /**
     * 获取参数列表
     *
     * @param joinPoint
     * @return
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     */
    public static Map<String, String> getParameters(JoinPoint joinPoint) {
        Map<String, String> result = Maps.newHashMap();
        Object[] args = joinPoint.getArgs();
        ParameterNameDiscoverer pnd = new DefaultParameterNameDiscoverer();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String[] parameterNames = pnd.getParameterNames(method);
        Gson gson = new Gson();
        for (int i = 0; i < parameterNames.length; i++) {
            Object arg = args[i];
            if(isFilterObject(arg)){
                continue;
            }
            if(arg instanceof MultipartFile){
                result.put(parameterNames[i], ((MultipartFile)arg).getOriginalFilename());
                continue;
            }
            if(arg instanceof MultipartFile[]){
                MultipartFile[] list = (MultipartFile[])arg;
                StringJoiner sj = new StringJoiner(",","","");
                Arrays.stream(list).forEach(item->{
                    sj.add(item.getOriginalFilename());
                });
                result.put(parameterNames[i], sj.toString());
                continue;
            }
            result.put(parameterNames[i], gson.toJson(arg));
        }
        return result;
    }

    /**
     * 判断是否需要过滤的对象。
     *
     * @param o 对象信息。
     * @return 如果是需要过滤的对象，则返回true；否则返回false。
     */
    private static boolean isFilterObject(Object o) {
        return null == o || o instanceof BindingResult || o instanceof HttpServletRequest || o instanceof HttpServletResponse;
    }
}
