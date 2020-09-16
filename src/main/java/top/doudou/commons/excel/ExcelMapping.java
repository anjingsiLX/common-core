package top.doudou.commons.excel;

import java.lang.annotation.*;

/**
 * 解析excel时属性的值
 * @author anyp
 * @create 2020-3-26
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ExcelMapping {

    /**
     * 属性对应到excel中的表头
     */
    String value();

    /**
     * 写入到excel的行宽
     */
    int columnWidth() default 5000;

    /**
     * 写入到表头的位置
     * @return
     */
    int position() default 0;
}
