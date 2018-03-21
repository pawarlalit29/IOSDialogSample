package com.lalitp.iosplacepicker.Webapi.CacheLib;


import okhttp3.Request;
import retrofit2.Response;

public interface CachingSystem {
    <T> void addInCache(Response<T> response, byte[] rawResponse);
    <T> byte[] getFromCache(Request request);
}
