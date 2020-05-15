package com.example.demo.network;
import com.google.gson.internal.$Gson$Types;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Call;
/**
 * @ProjectName: Demo
 * @Package: com.example.demo.network
 * @ClassName: WSCallBack
 * @Description: java类作用描述
 * @Author: 作者名
 * @CreateDate: 2020/5/14 10:44 AM
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/5/14 10:44 AM
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public abstract class WSCallBack<T> {

    Type type;

    static Type getSuperclassTypeParameter(Class<?> subclass)
    {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class)
        {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
    }

    public WSCallBack()
    {
        type = getSuperclassTypeParameter(getClass());
    }

    public abstract void onFailure(Call call,  Exception e)  ;

    public abstract void onSuccess(T t);

}
