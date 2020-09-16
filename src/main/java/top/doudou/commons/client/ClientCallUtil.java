//package top.doudou.commons.client;
//
//import lombok.extern.slf4j.Slf4j;
//
//import java.util.concurrent.Callable;
//
///**
// * @Author: 傻男人
// * @Date: 2020/6/24 15:13
// * @Version: 1.0
// * @Description:
// */
//@Slf4j
//public class ClientCallUtil {
//
//
//    public static <T extends JsonResponse> T callBackExceptionNull(Callable<T> fun) {
//        T result = null;
//        try {
//            result = fun.call();
//            return result;
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            return result;
//        }
//    }
//
//    public static <T extends JsonResponse> T callBackException(Callable<T> fun) {
//        T result = null;
//        try {
//            result = fun.call();
//            return result;
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new BizException(e.getMessage());
//        }
//    }
//
//    /**
//     * 获取client中data的数据
//     * @param fun
//     * @param target  JsonResponse中data的类型
//     * @return
//     */
//    public static <E,T extends JsonResponse> E getClientResult(Callable<T> fun, Class<E> target){
//        T t = callBackException(fun);
//        return (E)getResult(t);
//    }
//
//    /**
//     * 获取client中data的数据,如果为空，则抛异常
//     * @param fun
//     * @param target  JsonResponse中data的类型
//     * @return
//     */
//    public static <E,T extends JsonResponse> E getClientResultNullException(Callable<T> fun, Class<E> target){
//        T t = callBackException(fun);
//        E result = (E) getResult(t);
//        if(null == result){
//            throw new BizException(SuggestiveLanguage.CLIENT_CALL_RETURN_NULL);
//        }
//        return result;
//    }
//
//    public static <T> T getResult(JsonResponse<T> response) {
//        if ( null == response) {
//            throw new BizException(SuggestiveLanguage.CLIENT_CALL_RETURN_NULL);
//        }
//        if (response.getCode() != 0) {
//            throw new BizException(SuggestiveLanguage.clientCall(response.getMessage()));
//        }
//        return response.getData();
//    }
//}
