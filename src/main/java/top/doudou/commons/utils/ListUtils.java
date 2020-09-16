package top.doudou.commons.utils;

import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: anyp
 * @Date: 2020/3/27
 * @Description:
 */
public class ListUtils {

    public static <T> List<T> copyList(Collection source, Class<T> target) {
        return CollectionUtils.isEmpty(source) ? Collections.emptyList() : (List)source.stream().map((s) -> ConvertBeanUtils.copyProperties(s, target)).collect(Collectors.toList());
    }

    public static List<Long> stringToList(String source,String split){
        List<Long> list = Lists.newArrayList();
        if(!source.contains(split)){
            list.add(Long.valueOf(source));
            return list;
        }
        if(StringUtils.isBlank(split)){
            split = ",";
        }
        Arrays.asList(source.split(split)).stream().mapToLong(t-> Long.valueOf(t).longValue()).forEach(str->list.add(str));
        return list;
    }

    public static List<Long> stringToList(String source){
        return stringToList(source,",");
    }

    public static <T extends Number>String listToString(List<T> list){
        if(CollectionUtils.isEmpty(list)){
            return null;
        }
        return StringUtils.join(list.toArray(),",");
    }


    public static List<String> stringToStrList(String source,String split){
        List<String> list = Lists.newArrayList();
        if(!source.contains(split)){
            list.add(source);
            return list;
        }
        if(StringUtils.isBlank(split)){
            split = ",";
        }
        Arrays.asList(source.split(split)).stream().forEach(str->list.add(str));
        return list;
    }

    public static List<String> stringToStrList(String source){
        return stringToStrList(source,",");
    }

}
