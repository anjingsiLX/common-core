package top.doudou.commons.utils;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class BrowserUtils {

    public static boolean isBrowser(HttpServletRequest request){
        String userAgent = request.getHeader("User-Agent");
        if(StringUtils.isNotBlank(userAgent) && userAgent.startsWith("Mozilla/5.0")){
            return true;
        }
        String accept = request.getHeader("Accept");
        if(StringUtils.isNotBlank(accept) && accept.startsWith("text/html")){
            return true;
        }
        return false;
    }
}
