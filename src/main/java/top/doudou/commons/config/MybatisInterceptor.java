package top.doudou.commons.config;


import com.baomidou.mybatisplus.annotation.TableField;
import com.google.common.collect.Lists;
import top.doudou.commons.aspect.WriteLogToFile;
import top.doudou.commons.config.entity.ConfigConstant;
import top.doudou.commons.config.entity.SqlLogDto;
import top.doudou.commons.utils.FieldUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.ibatis.binding.MapperProxyFactory;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.*;
import java.util.regex.Matcher;

/**
 * @author 傻男人
 * @description 自定义mybatis拦截器,格式化SQL输出（只对查询和更新语句做了格式化，其它语句需要手动添加）
 * @date 2020-07-27
 */
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class,
                ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class})
})
@Component
@Slf4j
@ConditionalOnClass(MapperProxyFactory.class)
public class MybatisInterceptor implements Interceptor {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private java.util.concurrent.Executor executorService;

    private static final String SQL_LOG = "."+File.separator+"customLog"+ File.separator+"sql_log.log";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object statement = invocation.getArgs()[0];
        if(statement instanceof Statement){
            return showResult(invocation,statement);
        }
        if(statement instanceof MappedStatement){
            return showSql(invocation,statement);
        }
        return invocation.proceed();
    }

    private Object showSql(Invocation invocation, Object statement) throws InvocationTargetException, IllegalAccessException {
        final SqlLogDto sqlLogDto = new SqlLogDto();
        try {
            MappedStatement mappedStatement = (MappedStatement) statement;
            Object parameter = null;
            if (invocation.getArgs().length > 1) {
                parameter = invocation.getArgs()[1];
            }
            BoundSql boundSql = mappedStatement.getBoundSql(parameter);
            Configuration configuration = mappedStatement.getConfiguration();
            sqlLogDto.setSentence(showSql(configuration, boundSql));
            sqlLogDto.setSqlId(mappedStatement.getId());
        } catch (Exception localException) {
        }
        long start = System.currentTimeMillis();
        Object result = invocation.proceed();
        long cost = System.currentTimeMillis() - start;
        sqlLogDto.setCost(cost);
        String uuid = Optional.ofNullable(request.getAttribute(ConfigConstant.REQUEST_UUID)).map(Object::toString).orElse("");
        sqlLogDto.setRequestId(uuid);
        if(result instanceof List){
            sqlLogDto.setRows(((List)result).size());
        }else {
            sqlLogDto.setResult(result);
        }
        executorService.execute(()->printSql(sqlLogDto));
        return result;
    }

    private String getParameterValue(Object obj) {
        String value = null;
        if ((obj instanceof String)) {
            value = "'" + obj.toString() + "'";
        } else if ((obj instanceof Date)) {
            DateFormat formatter = DateFormat.getDateTimeInstance(2, 2, Locale.CHINA);
            value = "'" + formatter.format(new Date()) + "'";
        } else if (obj != null) {
            value = obj.toString();
        } else {
            value = "";
        }
        return value;
    }

    private String showSql(Configuration configuration, BoundSql boundSql) {
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
        MetaObject metaObject;
        if ((CollectionUtils.isNotEmpty(parameterMappings)) && (parameterObject != null)) {
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(parameterObject)));
            } else {
                metaObject = configuration.newMetaObject(parameterObject);
                for (ParameterMapping parameterMapping : parameterMappings) {
                    String propertyName = parameterMapping.getProperty();
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(obj)));
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(obj)));
                    } else {
                        sql = sql.replaceFirst("\\?", "缺失");
                    }
                }
            }
        }
        return sql;
    }

    @Override
    public Object plugin(Object target) {
        if(target instanceof ResultSetHandler){
            return Plugin.wrap(target, this);
        }
        if(target instanceof Executor){
            return Plugin.wrap(target,this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {
    }

    private Object showResult(Invocation invocation, Object statementObj) throws InvocationTargetException, IllegalAccessException {
        List<Object> results = (List<Object>)invocation.proceed();
        try{
            if(CollectionUtils.isNotEmpty(results)){
                System.out.println("--------------------------------------------------查询的结果集为--------------------------------------------------");
                Class<?> cls = results.get(0).getClass();
                List<Field> fields = FieldUtils.getAllFields(cls);
                List<String> columnList = Lists.newArrayList();
                int sum = 0;
                for(Object result:results){
                    StringJoiner header = new StringJoiner(",  ", " Columns:  ", "");
                    StringJoiner row = new StringJoiner(",  ", " Row:  ", "");
                    for (Field field : fields) {
                        if(sum == 0 ){
                            TableField annotation = field.getAnnotation(TableField.class);
                            header.add(null != annotation ? annotation.value() : field.getName());
                        }
                        Object value = FieldUtils.getFieldValue(field,cls,result);
                        row.add(null == value ? null : value.toString());
                    }
                    if(sum == 0){
                        System.out.println("===> " + header.toString());
                    }
                    columnList.add(row.toString());
                    System.out.println("===> " + row.toString());
                    sum ++;
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally{
            return results;
        }
    }

    private void printSql(SqlLogDto sqlLogDto){
        if(null == sqlLogDto){
            return;
        }
        System.out.println("--------------------------------------------------查询的语句--------------------------------------------------");
        System.out.println("===>  sql id                " + sqlLogDto.getSqlId());
        System.out.println("===>  sql sentence          " + sqlLogDto.getSentence());
        System.out.println("===>  sql cost              " + sqlLogDto.getCost());
        if(null != sqlLogDto.getRows()){
            System.out.println("===>  sql rows              " + sqlLogDto.getRows());
        }
        WriteLogToFile.logToFile(SQL_LOG,sqlLogDto.toString());
    }

    private static String acronymToUpper(String str) {
        char[] chars = str.toCharArray();
        if (chars[0] >= 'a' && chars[0] <= 'z') {
            chars[0] = (char) (chars[0] - 32);
        }
        return new String(chars);
    }
}