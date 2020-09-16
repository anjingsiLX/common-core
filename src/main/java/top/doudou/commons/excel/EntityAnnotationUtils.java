package top.doudou.commons.excel;

import com.google.common.collect.Maps;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;

/**
 * 获取实体类注解属性
 * @author anyp
 * @create 2020-3-26
 */
public class EntityAnnotationUtils<T> {

    /**
     * 获取实体类注解的属性与对应的字段名
     * @param target
     * @param <T>
     * @return
     */
    public static <T>LinkedHashMap<String, String> getAnnotationProperty(Class<T> target)  {
        LinkedHashMap<String, String> map = Maps.newLinkedHashMap();
        Field[] fields = target.getDeclaredFields();
        for (Field field : fields) {
            ExcelMapping annotation = field.getAnnotation(ExcelMapping.class);
            if(annotation != null){
                map.put(annotation.value(),field.getName());
            }
        }
        return map;
    }

    /**
     * 获取实体类对应的写入属性
     * @param target
     * @param <T>
     * @return
     */
    public static <T>LinkedHashMap<Integer, ExcelWriteDto> getExcelWriteProperty(Class<T> target)  {
        LinkedHashMap<Integer, ExcelWriteDto> map = Maps.newLinkedHashMap();
        Field[] fields = target.getDeclaredFields();
        boolean flag = false;
        int position = 0;
        for (Field field : fields) {
            ExcelMapping annotation = field.getAnnotation(ExcelMapping.class);
            if(annotation != null){
                ExcelWriteDto excelWriteDto = new ExcelWriteDto();
                excelWriteDto.setColumnWidth(annotation.columnWidth());
                excelWriteDto.setFieldName(field.getName());
                if(!flag){
                    if(annotation.position() == 0){
                        excelWriteDto.setPosition(position);
                        flag = true;
                    }
                }else {
                    excelWriteDto.setPosition(position);
                }
                excelWriteDto.setValue(annotation.value());
                map.put(excelWriteDto.getPosition(),excelWriteDto);
                position ++;
            }
        }
        return map;
    }

}
