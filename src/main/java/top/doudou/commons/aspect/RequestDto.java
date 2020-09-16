package top.doudou.commons.aspect;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * @Author: 傻男人
 * @Date: 2020/8/18 16:31
 * @Version: 1.0
 * @Description:
 */
@Data
public class RequestDto implements Serializable {

    @ApiModelProperty("请求方法")
    private String requestMethod;

    @ApiModelProperty("请求时间")
    private Long requestTime;

    @ApiModelProperty("请求地址")
    private String url;

    @ApiModelProperty("请求的方法")
    private String ctl;

    @ApiModelProperty("请求参数")
    private String parameter;

    @ApiModelProperty("请求响应")
    private String respond;

    @ApiModelProperty("异常消息")
    private String errorMsg;

    @ApiModelProperty("异常信息")
    private Exception exception;

    public RequestDto(){}

    public RequestDto(String ctl,String url,String requestMethod,String parameter){
        this.ctl = ctl;
        this.url = url;
        this.requestMethod = requestMethod;
        this.parameter = parameter;
    }

    public String format(){
        if(null == this){
            return "";
        }
        StringBuilder result = new StringBuilder();
        String requestMethod = this.getRequestMethod();
        if(StringUtils.isNotEmpty(requestMethod)){
            result.append(result.length() == 0 ? requestMethod : "    "+requestMethod);
        }
        String url = this.getUrl();
        if(StringUtils.isNotEmpty(url)){
            result.append(result.length() == 0 ? url : "    "+url);
        }
        Long requestTime = this.getRequestTime();
        if(null != requestTime){
            result.append(result.length() == 0 ? requestTime : "    "+requestTime);
        }
        String ctl = this.getCtl();
        if(StringUtils.isNotEmpty(ctl)){
            result.append(result.length() == 0 ? ctl : "    "+ctl);
        }
        String parameter = this.getParameter();
        if(StringUtils.isNotEmpty(parameter)){
            result.append(result.length() == 0 ? parameter : "    "+parameter);
        }
        String errorMsg = this.getErrorMsg();
        if(StringUtils.isNotEmpty(requestMethod)){
            result.append(result.length() == 0 ? errorMsg : "    "+errorMsg);
        }
        return result.toString();
    }
}
