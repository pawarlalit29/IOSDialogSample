package com.lalitp.iosplacepicker.Webapi;


import com.lalitp.iosplacepicker.Pojo.PlaceSearch.PlaceSearchData;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by atulsia on 19/2/16.
 */
public interface RestInterface {

    /*****************************
     * Location Api
     **********************************/
/*
    @GET("geocode/json")
    Call<LocationData> getCurrentLocation(@Query("latlng") String latlong, @Query("sensor") boolean sensor, @Query("key") String key);
*/


    @GET("directions/json")
    Call<String> getRouteDataCall(@QueryMap HashMap<String, String> hashMap);

    @GET("place/autocomplete/json")
    Call<PlaceSearchData> getPlacePredicationCall(@QueryMap HashMap<String, String> hashMap);


}
