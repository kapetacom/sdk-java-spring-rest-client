package com.blockware.spring.restclient;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class SimpleCallAdapterFactory extends CallAdapter.Factory {
    @Override
    public CallAdapter<?, ?> get(final Type returnType, Annotation[] annotations, Retrofit retrofit) {
        if (getRawType(returnType) == Call.class) {
            return null;
        }

        return new CallAdapter<Object, Object>() {
            @Override
            public Type responseType() {
                return returnType;
            }

            @Override
            public Object adapt(Call<Object> call) {
                return RestClientUtils.silent(call);
            }

        };
    }
}
