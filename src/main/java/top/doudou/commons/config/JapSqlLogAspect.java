package top.doudou.commons.config;

import top.doudou.commons.aspect.WriteLogToFile;
import top.doudou.commons.config.entity.SqlLogDto;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect    //定义为切面
@Component     //加component注解和bean注解才能被容器加载
@Slf4j
public class JapSqlLogAspect {

    private static final String SQL_LOG = "jpa_sql_log.log";
    @Around("crud()")
    public Object logPerformance(ProceedingJoinPoint pjp) throws Throwable{
        long start = System.currentTimeMillis();
        SqlLogDto sqlLogDto = new SqlLogDto();
        String name="-";
        String result="Y";
        try{
            name = pjp.getSignature().toShortString();
            sqlLogDto.setSqlId(name);
            return pjp.proceed();
        }catch (Throwable t){
            result = "N";
            throw t;
        }finally {
            long end =System.currentTimeMillis();
            log.info("{};{};{}ms",name,result,end-start);
            sqlLogDto.setCost(end);
            printSql(sqlLogDto);
        }

    }

    @Pointcut("execution(* com.zhcf..*.*repository..*(..))")
    private void crud(){}

    private void printSql(SqlLogDto sqlLogDto){
        if(null == sqlLogDto){
            return;
        }
        System.out.println("==>  sql id                " + sqlLogDto.getSqlId());
        System.out.println("==>  sql sentence          " + sqlLogDto.getSentence());
        System.out.println("==>  sql cost              " + sqlLogDto.getCost());
        WriteLogToFile.logToFile(SQL_LOG,sqlLogDto.toString());
    }
}