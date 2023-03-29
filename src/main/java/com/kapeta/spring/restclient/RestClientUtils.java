package com.kapeta.spring.restclient;


import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

public class RestClientUtils {

    public static <T> Response<T> response(Call<T> call) {
        try {
            Response<T> out = call.execute();

            if (out.isSuccessful() || out.code() == 404) {
                return out;
            }

            throw new RestClientException(out);
        } catch (IOException e) {
            throw new RestClientException(500, e);
        }
    }

    public static <T> T silent(Call<T> call) {
        final Response<T> response = response(call);
        return response.body();
    }

    public static void silentVoid(Call call) {
        silent(call);
    }
}
