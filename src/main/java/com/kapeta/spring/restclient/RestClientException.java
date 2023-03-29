package com.kapeta.spring.restclient;


import retrofit2.Response;

public class RestClientException extends RuntimeException {

    private int code;

    private Response response;

    public RestClientException(int code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public RestClientException(Response response) {
        this.code = response.code();
        this.response = response;
    }

    public int getCode() {
        return code;
    }

    public Response getResponse() {
        return response;
    }
}
