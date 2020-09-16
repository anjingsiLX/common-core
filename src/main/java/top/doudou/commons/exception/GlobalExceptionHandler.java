//package top.doudou.commons.exception;
//
//import com.fasterxml.jackson.databind.exc.MismatchedInputException;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.TypeMismatchException;
//import org.springframework.dao.DataAccessException;
//import org.springframework.dao.DuplicateKeyException;
//import org.springframework.http.converter.HttpMessageConversionException;
//import org.springframework.http.converter.HttpMessageNotReadableException;
//import org.springframework.validation.FieldError;
//import org.springframework.web.HttpRequestMethodNotSupportedException;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.MissingServletRequestParameterException;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.multipart.MultipartException;
//import org.springframework.web.multipart.support.MissingServletRequestPartException;
//
//import javax.validation.UnexpectedTypeException;
//import java.sql.SQLException;
//import java.sql.SQLSyntaxErrorException;
//import java.util.List;
//
///**
// * @Author: 傻男人
// * @Date: 2020/06/18 14:20
// * @Version: 1.0
// * @Description:
// */
//@ControllerAdvice
//@Slf4j
//public class GlobalExceptionHandler {
//
//    @ExceptionHandler(Exception.class)
//    @ResponseBody
//    public JsonResponse exceptionHandler(Exception e) {
//        e.printStackTrace();
//        return JsonResponse.error("内部服务出错了",e.getMessage());
//    }
//
//    @ExceptionHandler(BizException.class)
//    @ResponseBody
//    public JsonResponse bizExceptionHandler(BizException e) {
//        e.printStackTrace();
//        return JsonResponse.fail(e.getMessage());
//    }
//
//    @ExceptionHandler(RuntimeException.class)
//    @ResponseBody
//    public JsonResponse exceptionHandler(RuntimeException e) {
//        e.printStackTrace();
//        return JsonResponse.error(e.getMessage(),e.getLocalizedMessage());
//    }
//
//    @ExceptionHandler(SQLSyntaxErrorException.class)
//    @ResponseBody
//    public JsonResponse sQLSyntaxErrorExceptionHandler(SQLSyntaxErrorException e) {
//        e.printStackTrace();
//        return JsonResponse.error(1000,"数据库出错了",e);
//    }
//
//
//    @ExceptionHandler(MultipartException.class)
//    @ResponseBody
//    public JsonResponse multipartExceptionHandler(MultipartException e) {
//        e.printStackTrace();
//        return JsonResponse.error(1000,"请求中缺少Multipart",e);
//    }
//
//    @ExceptionHandler(MissingServletRequestPartException.class)
//    @ResponseBody
//    public JsonResponse missingServletRequestPartExceptionHandler(MissingServletRequestPartException e) {
//        e.printStackTrace();
//        return JsonResponse.error(2000,"缺少"+e.getRequestPartName()+"参数",e);
//    }
//
//    @ExceptionHandler(MissingServletRequestParameterException.class)
//    @ResponseBody
//    public JsonResponse missingServletRequestParameterExceptionHandler(MissingServletRequestParameterException e) {
//        e.printStackTrace();
//        return JsonResponse.error(2000,"缺少"+e.getParameterName()+"参数",e);
//    }
//
//    /**
//     * 参数验证异常处理
//     * @param e
//     * @return
//     */
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    @ResponseBody
//    public JsonResponse methodArgumentNotValidExceptionHandle(MethodArgumentNotValidException e){
//        StringBuilder sb = new StringBuilder(100);
//        List<FieldError> list = e.getBindingResult().getFieldErrors();
//        for (FieldError fieldError : list) {
//            sb.append(fieldError.getField()).append(" ").append(fieldError.getDefaultMessage()).append(";");
//        }
//
//        return JsonResponse.error(400,"参数错误: "+sb.toString());
//    }
//
//    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
//    @ResponseBody
//    public JsonResponse httpRequestMethodNotSupportedExceptionHandler(HttpRequestMethodNotSupportedException e) {
//        e.printStackTrace();
//        return JsonResponse.error(505,"不支持当前请求"+e.getMethod(),e);
//    }
//
//    @ExceptionHandler(IllegalArgumentException.class)
//    @ResponseBody
//    public JsonResponse illegalArgumentExceptionHandler(IllegalArgumentException e) {
//        e.printStackTrace();
//        return JsonResponse.error(606,"参数非法,请检查参数格式",e);
//    }
//
//    @ExceptionHandler(UnexpectedTypeException.class)
//    @ResponseBody
//    public JsonResponse unexpectedTypeExceptionHandler(UnexpectedTypeException e) {
//        e.printStackTrace();
//        return JsonResponse.error(607,"注解与需要校验的数据类型不匹配",e);
//    }
//
//    @ExceptionHandler(ClassCastException.class)
//    @ResponseBody
//    public JsonResponse classCastExceptionHandler(Exception e) {
//        e.printStackTrace();
//        return JsonResponse.error(601,"引用类型转换异常",e);
//    }
//
//    @ExceptionHandler(HttpMessageNotReadableException.class)
//    @ResponseBody
//    public JsonResponse httpMessageNotReadableExceptionHandler(HttpMessageNotReadableException e) {
//        e.printStackTrace();
//        return JsonResponse.error(601,"json序列化错误",e);
//    }
//
//    @ExceptionHandler(MismatchedInputException.class)
//    @ResponseBody
//    public JsonResponse mismatchedInputExceptionHandler(MismatchedInputException e) {
//        e.printStackTrace();
//        return JsonResponse.error(601,"json转换为实体类出错",e);
//    }
//
//    @ExceptionHandler(HttpMessageConversionException.class)
//    @ResponseBody
//    public JsonResponse httpMessageConversionExceptionHandler(HttpMessageConversionException e) {
//        e.printStackTrace();
//        return JsonResponse.error(602,"参数类型错误",e);
//    }
//
//    @ExceptionHandler(NullPointerException.class)
//    @ResponseBody
//    public JsonResponse nullPointerExceptionHandler(NullPointerException e) {
//        e.printStackTrace();
//        StackTraceElement element = e.getStackTrace()[0];
//        return JsonResponse.error("空指针异常",element.getFileName()+"文件中的方法名为："+element.getMethodName()+"的方法，该方法中行号： "+element.getLineNumber()+" 出现空指针");
//    }
//
//    @ExceptionHandler(DataAccessException.class)
//    @ResponseBody
//    public JsonResponse dataAccessExceptionHandler(DataAccessException e) {
//        e.printStackTrace();
//        return JsonResponse.error(701,"数据库访问异常",e);
//    }
//
//    @ExceptionHandler(SQLException.class)
//    @ResponseBody
//    public JsonResponse sqlExceptionHandler(SQLException e) {
//        e.printStackTrace();
//        return JsonResponse.error(702,"sql异常",e);
//    }
//
//    @ExceptionHandler(TypeMismatchException.class)
//    @ResponseBody
//    public JsonResponse typeMismatchExceptionHandler(TypeMismatchException e) {
//        e.printStackTrace();
//        return JsonResponse.error(603,"类型不匹配",e);
//    }
//
//    @ExceptionHandler(DuplicateKeyException.class)
//    @ResponseBody
//    public JsonResponse duplicateKeyExceptionHandler(DuplicateKeyException e) {
//        e.printStackTrace();
//        return JsonResponse.error(604,"数据库主键重复",e);
//    }
//
//
//
//
//
//
//
//
//
//
//}
//
