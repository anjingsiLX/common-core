package top.doudou.commons.config;

import org.hibernate.proxy.map.MapProxyFactory;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnClass(MapProxyFactory.class)
public class JpaInterceptor implements StatementInspector {

    @Override
    public String inspect(String sql) {
        System.out.println("==>  sql sentence          " + replace(sql));
        return sql;
    }

    private String replace(String sql){
        sql = sql.replaceAll("as [\\w]+", "");
        sql = sql.replaceAll("[\\w]+\\d_","");
        sql = sql.replaceAll(", ", ",").replaceAll(" ,", ",");
        return sql.replaceAll("\\.","");
    }
}
