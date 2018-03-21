package com.lalitp.iosplacepicker.UI.Activity.PlaceSearch;



import com.lalitp.iosplacepicker.Pojo.PlaceSearch.Prediction;

import java.util.HashMap;
import java.util.List;

/**
 * Created by atulsia on 15/12/16.
 */

public interface PlaceSearchInteractor {
    interface LocationDetailChangeListener{
        void getPlacePredication(List<Prediction> predictionList);
        void onError(String strMsg);
    }

    void getPlacePredicationData(HashMap<String, String> param, LocationDetailChangeListener changeListener);
}
