package top.doudou.commons.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author: anyp
 * @Date: 2020/3/30
 * @Description:
 */
@Data
@ApiModel("分页信息")
public class PageInfo implements Serializable {
    private static final long serialVersionUID = 7926666149245633548L;

    @ApiModelProperty(value = "页码",required = true)
    @Min(value = 1,message = "页码必须大于0")
    @NotNull(message = "页码不能为空")
    private Integer pageIndex;

    @ApiModelProperty(value = "每页的条数",required = true)
    @Min(value = 1,message = "每页大小必须大于0")
    @Max(value = 100,message = "每页大小必须小于100")
    @NotNull(message = "分页大小不能为空")
    private Integer pageSize;

    public PageInfo(Integer pageIndex, Integer pageSize){
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }

    public PageInfo(){}

    public PageInfo(Integer pageIndex, Long pageSize){
        this.pageIndex = pageIndex;
        this.pageSize = Integer.valueOf(String.valueOf(pageSize));
    }

}
