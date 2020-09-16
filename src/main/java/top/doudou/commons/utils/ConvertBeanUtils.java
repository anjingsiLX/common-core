package top.doudou.commons.utils;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Consumer;

/**
 * bean属性的转换
 *
 * @author anyp
 * @date 2020-03-25
 */
@Slf4j
public class ConvertBeanUtils {

    public static <T> T copyProperties(Object entity, Class<T> doClass) {
        if (entity == null) {
            return null;
        } else if (doClass == null) {
            log.info("转换的class不能为null");
            throw new BizException("转换的class不能为null");
        }
        try {
            T newInstance = doClass.newInstance();
            BeanUtils.copyProperties(entity, newInstance);
            return newInstance;
        } catch (Exception e) {
            log.info("属性转换异常！");
            throw new BizException("bean属性转换异常");
        }
    }

    /**
     * 属性复制
     * @param source 源
     * @param target 目标
     */
    public static void copyProperties(Object source, Object target) {
        if (source == null || target == null) {
            throw new BizException("源或者目标都不能为空");
        }
        try {
            BeanUtils.copyProperties(source, target);
        } catch (Exception e) {
            log.info("属性转换异常！");
            throw new BizException("bean属性转换异常");
        }
    }

    /**
     * Map转实体类
     * @param map
     * @param beanClass
     * @return
     */
    public static <T> T mapToObject(Map<String, Object> map, Class<T> beanClass) {
        if (map == null) return null;
        try {
            T t = beanClass.newInstance();
            BeanInfo beanInfo = Introspector.getBeanInfo(t.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                Method setter = property.getWriteMethod();
                if (setter != null) {
                    setter.invoke(t, map.get(property.getName()));
                }
            }
            return t;
        }catch (Exception e){
            e.printStackTrace();
            throw new BizException("bean属性转换异常");
        }
    }

    /**
     * bean属性复制(bean中包含list)
     */
    public static <T> T copyPropertiesContainList(Object source, Class<T> doClass) {
        if (source == null) {
            return null;
        } else if (doClass == null) {
            log.info("转换的class不能为null");
            throw new BizException("转换的class不能为null");
        }

        Field[] sourFields = source.getClass().getDeclaredFields();

        // 将 source 的属性对象为 value，属性名为 key 封装为 map
        Map<String, Field> sourFieldMap = Maps.newHashMap();
        Arrays.stream(sourFields).forEach(sourField -> sourFieldMap.put(sourField.getName(), sourField));
        try {
            T target = doClass.newInstance();
            Field[] tarFields = target.getClass().getDeclaredFields();
            for (Field tarField : tarFields) {
                // 从 sourFieldMap 中通过 fieldName 取值，若能取出表示存在相同的属性名
                Field sourField = sourFieldMap.get(tarField.getName());
                if (sourField == null || "serialVersionUID".equals(sourField.getName())) {
                    continue;
                }
                Class tarGenericClass = getGenericClass(tarField);
                Class sourGenericClass = getGenericClass(sourField);

                // 获取属性声明的类型 Class 对象
                Class<?> sourType = sourField.getType();
                Class<?> tarType = tarField.getType();

                sourField.setAccessible(true);
                tarField.setAccessible(true);

                // 若不是 List 或其父接口类型，为 null
                if (tarGenericClass == null && sourGenericClass == null) {
                    // target 属性类型是 source 属性类型的子类或子接口
                    if (sourType.isAssignableFrom(tarType)) {
                        tarField.set(target, sourField.get(source));
                    }
                } else if (tarGenericClass != null && sourGenericClass != null) {
                    // Iterable 是 List 最顶层接口，在 sourGenericClass 中判断是声明类型是 List 或其父接口
                    Iterable sourList = (Iterable) sourField.get(source);
                    if (sourList == null) {
                        continue;
                    }
                    // 该集合用于添加 target 拷贝的对象
                    List tarList = Lists.newArrayList();
                    for (Object sourObj : sourList) {
                        tarList.add(copyPropertiesContainList(sourObj, tarGenericClass.newInstance().getClass()));
                    }
                    tarField.set(target, tarList);
                }
            }
            return target;
        } catch (Exception e) {
            log.info("复制属性出错");
            throw new BizException("bean属性转换异常");
        }
    }

    /**
     * 获取属性类型中的泛型类型，获取泛型的过程适用于其他类型，但是本方法仅适用于 List 集合，
     * 并且只有一个泛型的情况，若有需求后续增加功能。
     *
     * @param field
     * @return
     * @throws Exception
     */
    private static Class getGenericClass(Field field) throws Exception {
        // 获取属性声明的类型 Class 对象
        Class<?> type = field.getType();
        // 判断该 type 是否是 List 或其父接口
        if (type.isAssignableFrom(List.class)) {
            // 获取该属性声明的类型（Type 接口），getType()：是获取属性声明的类型 Class 对象
            Type genericType = field.getGenericType();
            if (genericType == null) {
                return null;
            }
            // 判断 geneicType 是否是参数化类型，即明确指定泛型的类型，如：java.util.List<java.lang.String>
            if (genericType instanceof ParameterizedType) {
                //因为 genericType 是 ParameterizedType 的实例，故可以强转
                ParameterizedType pt = (ParameterizedType) genericType;
                // 获取泛型的类型数组
                Type[] genericArgs = pt.getActualTypeArguments();
                // 当泛型类型大于 1 个时，抛出异常
                if (genericArgs.length > 1) {
                    throw new Exception(genericType + "  " + field.getName() + ": 泛型不只一个无法赋值");
                }
                // 当只有一个泛型类型时，获取泛型类型
                return (Class) genericArgs[0];
            }
        }
        return null;
    }

    /**
     * 复制非空属性
     * @param source
     * @param target
     */
    public static void copyNotNullProperties(Object source, Object target){
        copyNotNullProperties(source, target,null);
    }

    /**
     * 复制非空属性
     * @param source
     * @param target
     * @param excludeFields 排除字段名称
     */
    public static void copyNotNullProperties(Object source, Object target,FieldName... excludeFields) {
        if (source == null || target == null) {
            throw new BizException("源或者目标都不能为空");
        }
        try {
            if (excludeFields!=null){
                BeanUtils.copyProperties(source,target,getNoNullProperties(source,excludeFields));
            }else {
                BeanUtils.copyProperties(source, target,getNoNullProperties(source));
            }
        } catch (Exception e) {
            log.info("属性转换异常！");
            throw new BizException("bean属性转换异常");
        }
    }

    private static String[] getNoNullProperties(Object target,FieldName... excludeFields){
        String[] targetNames = getNoNullProperties(target);

        String[] excludeNames = new String[excludeFields.length];

        for (int i = 0; i < excludeFields.length; i++) {

            excludeNames[i]  = excludeFields[i].getName();
        }
        String[] names = new String[targetNames.length+excludeNames.length];
        System.arraycopy(targetNames, 0, names, 0, targetNames.length);
        System.arraycopy(excludeNames, 0, names, targetNames.length, excludeNames.length);

        return names;
    }

    private static String[] getNoNullProperties(Object target) {
        BeanWrapper srcBean = new BeanWrapperImpl(target);
        PropertyDescriptor[] pds = srcBean.getPropertyDescriptors();
        Set<String> noEmptyName = new HashSet<>();
        for (PropertyDescriptor p : pds) {
            Object value = srcBean.getPropertyValue(p.getName());
            if (value == null) noEmptyName.add(p.getName());
        }
        String[] result = new String[noEmptyName.size()];
        return noEmptyName.toArray(result);
    }

    public static String[] getName(Consumer<String> methodParam){

        return null;
    }

}
