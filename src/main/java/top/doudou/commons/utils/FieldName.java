package top.doudou.commons.utils;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

@FunctionalInterface
public interface FieldName extends Serializable {

    Object field();

    default String getName() {
        Method method = null;
        try {
            method = this.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(true);
            SerializedLambda serializedLambda = (SerializedLambda) method.invoke(this);
            String methodName =  serializedLambda.getImplMethodName();
            if (methodName.startsWith("get")){
                return StringUtil.toLowerCaseFirstOne(methodName.substring(3));
            }else if (methodName.startsWith("is")){
                return StringUtil.toLowerCaseFirstOne(methodName.substring(2));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
