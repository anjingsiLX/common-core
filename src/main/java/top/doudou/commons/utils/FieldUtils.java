package top.doudou.commons.utils;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @Author: 傻男人
 * @Date: 2020/9/7 17:22
 * @Version: 1.0
 * @Description:
 */
@Slf4j
@Data
public class FieldUtils{

    /**
     * 获取private修饰的成员变量  获得某个类的所有声明的字段，即包括public、private和proteced也包括父类的申明字段。
     * @param target
     * @return
     */
    public static  List<Field> getAllFields(Class<?> target){
        List<Field> superField = getSuperField(target);
        Iterator<Field> iterator = superField.iterator();
        while (iterator.hasNext()){
            Field next = iterator.next();
            if ("serialVersionUID".equals(next.getName())) {
                iterator.remove();
            }
        }
        return superField;
    }

    private static List<Field> getSuperField(Class target){
        if(target.equals(Object.class)){
            return Lists.newArrayList();
        }
        List<Field> list = Lists.newArrayList();
        Field[] declaredFields = target.getDeclaredFields();
        list.addAll(Arrays.asList(declaredFields));
        Class<?> superclass = target.getSuperclass();
        list.addAll(getSuperField(superclass));
        return list;
    }

    public static Object getFieldValue(Field field,Class<?> clazz,Object obj) {
        try {
            Method method = clazz.getMethod("get" + acronymToUpper(field.getName()));
            return method.invoke(obj);
        }catch (NoSuchMethodException e){
            throw new RuntimeException("方法未找到：get"+acronymToUpper(field.getName()));
        }catch (Exception e) {
            throw new RuntimeException("获取值失败");
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
