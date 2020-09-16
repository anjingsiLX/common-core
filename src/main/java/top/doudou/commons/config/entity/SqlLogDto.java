package top.doudou.commons.config.entity;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * @Author: 傻男人
 * @Date: 2020/8/25 14:47
 * @Version: 1.0
 * @Description:
 */
@Data
public class SqlLogDto implements Serializable {
    /**
     * 耗时
     */
    private Long cost;

    /**
     * sql id
     */
    private String sqlId;

    /**
     * sql语句
     */
    private String sentence;

    /**
     * 请求的随机id
     */
    private String requestId;

    /**
     * 返回的行数
     */
    private Integer rows;


    /**
     * 返回的结果
     */
    private Object result;

    public SqlLogDto(){}

    public SqlLogDto(String sqlId,String sentence){
        this.sentence = sentence;
        this.sqlId = sqlId;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String requestId = this.getRequestId();
        if(StringUtils.isNotEmpty(requestId)){
            result.append(result.length() == 0 ? requestId : "    "+requestId);
        }
        Long cost = this.getCost();
        if(null != cost){
            result.append(result.length() == 0 ? cost : "    "+cost);
        }
        String sqlId = this.getSqlId();
        if(StringUtils.isNotEmpty(sqlId)){
            result.append(result.length() == 0 ? sqlId : "    "+sqlId);
        }
        String sentence = this.getSentence();
        if(StringUtils.isNotEmpty(sentence)){
            result.append(result.length() == 0 ? sentence : "    "+sentence);
        }
        Integer rows = this.getRows();
        if(null != rows){
            result.append(result.length() == 0 ? rows : "    "+rows);
        }
        Object rtn = this.getResult();
        if(null != rtn){
            result.append(result.length() == 0 ? rtn : "    "+rtn);
        }
        return result.toString();
    }
}
