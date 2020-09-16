package top.doudou.commons.aspect;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.File;

/**
 * @Author: 傻男人
 * @Date: 2020/8/25 14:06
 * @Version: 1.0
 * @Description: 请求日志的配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "request.log")
public class RequestLogProperties {
    /**
     * 错误日志名字
     */
    public String errorLogName = "error.log";

    /**
     * 超时日志名字
     */
    public String overtimeLogName = "overtime.log";

    /**
     * 超时时间
     */
    public Long timeOut = 500L;

    /**
     * 接口请求日志名字
     */
    public String requestLogName = "request.log";

    /**
     * 基本的文件位置
     */
    public String basePath = "."+File.separator+"customLog"+File.separator;

}