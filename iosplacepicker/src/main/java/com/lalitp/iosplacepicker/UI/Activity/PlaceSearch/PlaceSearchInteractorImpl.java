package com.lalitp.iosplacepicker.UI.Activity.PlaceSearch;

import com.lalitp.iosplacepicker.Pojo.PlaceSearch.PlaceSearchData;
import com.lalitp.iosplacepicker.Webapi.RestClient;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by atulsia on 15/12/16.
 */

public class PlaceSearchInteractorImpl implements PlaceSearchInteractor {

    @Override
    public void getPlacePredicationData(HashMap<String, String> param, final LocationDetailChangeListener changeListener) {
        Call<PlaceSearchData> routeDataCall = RestClient.getMapClient().getPlacePredicationCall(param);
        routeDataCall.enqueue(new Callback<PlaceSearchData>() {
            @Override
            public void onResponse(Call<PlaceSearchData> call, Response<PlaceSearchData> response) {
                if (response.isSuccessful()) {
                    PlaceSearchData placePredictions = response.body();
                    changeListener.getPlacePredication(placePredictions.getPredictions());

                } else {
                    changeListener.onError("");
                }
            }

            @Override
            public void onFailure(Call<PlaceSearchData> call, Throwable t) {
                changeListener.onError(RestClient.parseErrorThrow(t));
            }
        });
    }


}
