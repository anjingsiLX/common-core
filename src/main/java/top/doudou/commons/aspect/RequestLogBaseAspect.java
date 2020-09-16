package top.doudou.commons.aspect;

import top.doudou.commons.config.entity.ConfigConstant;
import top.doudou.commons.redis.RedisUtil;
import top.doudou.commons.utils.JsonUtils;
import top.doudou.commons.utils.ServletUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executor;

import static top.doudou.commons.aspect.AspectUtils.getParameters;

/**
 * @Author: 傻男人
 * @Date: 2020/8/25 14:06
 * @Version: 1.0
 * @Description:  请求日志的记录抽象类
 */
@Slf4j
public class RequestLogBaseAspect {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private Executor executorService;

    @Autowired
    private RequestLogProperties requestLogProperties;

    /**
     * 打印请求日志
     * @param joinPoint 切点
     */
    @Before(value = "logPrintPointCut()")
    public void beforeLogPoints(JoinPoint joinPoint) {
        HttpServletRequest request = ServletUtils.getRequest();
        String requestUri = request.getRequestURI();
        String requestMethod = request.getMethod();
        Map<String, String> mapParameter = getParameters(joinPoint);
        String requestDataStr = MapUtils.isNotEmpty(mapParameter) ? JsonUtils.toJsonString(mapParameter) : "";
        String requestExecutor = joinPoint.getSignature().getDeclaringTypeName() +"."+joinPoint.getSignature().getName();
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(15);
        request.setAttribute(ConfigConstant.REQUEST_UUID, uuid);
        request.setAttribute(ConfigConstant.REQUEST_START_TIME, System.currentTimeMillis());
        log.info("[{}]  ================ START ================", uuid);
        log.info("[{}]  -Url：{}  {}", uuid, requestMethod, requestUri);
        log.info("[{}]  -Ctl：{}", uuid, requestExecutor);
        log.info("[{}]  -Parameter：{}", uuid, requestDataStr);
        RequestDto requestDto = new RequestDto(requestExecutor,requestUri,requestMethod,requestDataStr);
        redisUtil.set(uuid,requestDto,60);
    }

    /**
     * 打印请求时间
     */
    @After(value = "logPrintPointCut()")
    public void afterLogPoints() {
        HttpServletRequest request= ServletUtils.getRequest();
        String uuid=Optional.ofNullable(request.getAttribute(ConfigConstant.REQUEST_UUID)).map(Object::toString).orElse("");
        Long startTime = Optional.ofNullable(request.getAttribute(ConfigConstant.REQUEST_START_TIME)).map(start->Long.parseLong(start.toString())).orElse(0L);
        Long requestTime = System.currentTimeMillis()-startTime;
        log.info("[{}]  -End：[request time：{} ms]",uuid,requestTime);
        executorService.execute(()->{
            RequestDto requestDto = (RequestDto)redisUtil.get(uuid);
            if(null != requestDto){
                requestDto.setRequestTime(requestTime);
                if(requestTime >= requestLogProperties.getTimeOut()){
                    writeLogToFile(requestDto,requestLogProperties.getBasePath()+requestLogProperties.getOvertimeLogName());
                }
                writeLogToFile(requestDto,requestLogProperties.getBasePath()+requestLogProperties.getRequestLogName());
            }
        });

    }

    /**
     * 打印响应日志
     *0
     */
    @AfterReturning(pointcut = "logPrintPointCut()", returning = "result")
    public void afterReturningLogPoints(JsonResponse result) {
        HttpServletRequest request= ServletUtils.getRequest();
        String uuid= Optional.ofNullable(request.getAttribute("request-uuid")).map(Object::toString).orElse("");
        log.info("[{}]  -Respond：{}",uuid, JsonUtils.toJsonString(result));
        log.info("[{}]  ========== END ==========",uuid);
    }

    /**
     * 打印异常信息
     *
     * @param e  异常
     */
    @AfterThrowing(value = "logPrintPointCut()", throwing = "e")
    public void afterThrowingLogPoints(Exception e) {
        HttpServletRequest request= ServletUtils.getRequest();
        String uuid=Optional.ofNullable(request.getAttribute("request-uuid")).map(Object::toString).orElse("");
        log.info("[{}]  -Error：{}",uuid,e.getMessage());
        log.info("[{}]  ========== END ==========",uuid);
        executorService.execute(()->{
            RequestDto requestDto = (RequestDto)redisUtil.get(uuid);
            if(null != requestDto){
                requestDto.setErrorMsg(e.getMessage());
                requestDto.setException(e);
                writeLogToFile(requestDto,requestLogProperties.getBasePath()+requestLogProperties.getErrorLogName());
            }
        });
    }

    private void writeLogToFile(RequestDto requestDto, String filePath){
        WriteLogToFile.logToFile(filePath,requestDto.format(),requestDto.getException());
    }

}
