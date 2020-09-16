package top.doudou.commons.excel;

import lombok.Data;

import java.io.Serializable;

/**
 * excel写入实体类
 * @author anjingsi
 * @date 2020-03-26
 */
@Data
public class ExcelWriteDto implements Serializable {

    /**
     * 写入到excel的行宽
     */
    private int columnWidth;

    /**
     * 表头的位置
     * @return
     */
    private int position;

    /**
     * 对应实体类的属性值
     */
    private String fieldName;

    /**
     * 表头的值
     */
    private String value;
}
