package top.doudou.commons.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.BindingResult;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 验证参数
 *
 * @author anjingsi
 * @date 2020-04-03
 */
@Slf4j
public class CheckParamUtil {

    private static String EMAIL_REGEX = "\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?";

    private static String EXCLUDE_VALUE = "serialVersionUID";

    /**
     * 检查入参是否有错误
     * @param result
     */
    public static void hasError(BindingResult result){
        if (result.hasErrors()) {
            throw new BizException(result.getFieldError().getDefaultMessage());
        }
    }

    /**
     * 参数验证
     *
     * @param t
     * @param <T>
     */
    public static <T> void checkParam(T t) {
        if (t == null) {
            throw new BizException("参数不能为空");
        }
        Class<?> clazz = t.getClass();
        Field[] fields = clazz.getDeclaredFields();
        try {
            for (Field field : fields) {
                if (EXCLUDE_VALUE.equals(field.getName())) {
                    continue;
                }
                Annotation[] annotations = field.getDeclaredAnnotations();
                if (annotations.length == 0) {
                    continue;
                }
                if (!field.isAnnotationPresent(NotBlank.class) && !field.isAnnotationPresent(NotNull.class) && !field.isAnnotationPresent(Pattern.class) && !field.isAnnotationPresent(Email.class)) {
                    continue;
                }
                NotBlank notblank = field.getDeclaredAnnotation(NotBlank.class);
                Method method = clazz.getMethod("get" + acronymToUpper(field.getName()));
                Object value = method.invoke(t);
                if (notblank != null && null != value && StringUtils.isEmpty(value.toString())) {
                    throw new BizException(notblank.message());
                }
                NotNull notNull = field.getDeclaredAnnotation(NotNull.class);
                if (notNull != null && null == value) {
                    throw new BizException(notNull.message());
                }
                Pattern pattern = field.getDeclaredAnnotation(Pattern.class);
                if (pattern != null) {
                    if (value == null) {
                        throw new BizException(pattern.message());
                    }
                    boolean matches = value.toString().matches(pattern.regexp());
                    if (!matches) {
                        throw new BizException(pattern.message());
                    }
                }
                Email email = field.getDeclaredAnnotation(Email.class);
                if (email != null) {
                    if (value == null) {
                        throw new BizException(email.message());
                    }
                    boolean matches = value.toString().matches(EMAIL_REGEX);
                    if (!matches) {
                        throw new BizException("邮箱格式不正确");
                    }
                }
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new BizException(e.getMessage());
        }
    }


    private static String acronymToUpper(String str) {
        char[] chars = str.toCharArray();
        if (chars[0] >= 'a' && chars[0] <= 'z') {
            chars[0] = (char) (chars[0] - 32);
        }
        return new String(chars);
    }
}
