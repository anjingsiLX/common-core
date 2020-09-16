package top.doudou.commons.excel;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 文档的摘要信息
 * @author anjingsi
 * @date 2020-03-26
 */
@Data
@ApiModel("文档的摘要信息")
public class DocumentSummaryInfo implements Serializable{

    @ApiModelProperty("文档类别")
    private String category;

    @ApiModelProperty("文档管理员")
    private String manager;

    @ApiModelProperty("组织机构")
    private String company;

    @ApiModelProperty("文档主题")
    private String subject;

    @ApiModelProperty("文档标题")
    private String titile;

    @ApiModelProperty("文档作者")
    private String author;

    @ApiModelProperty("文档备注")
    private String comments;

    @ApiModelProperty("文档名字")
    private String sheetName;

}
