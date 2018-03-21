package com.lalitp.iosplacepicker.UI.Activity.PlaceSearch;




import com.lalitp.iosplacepicker.Pojo.PlaceSearch.Prediction;

import java.util.List;

/**
 * Created by atulsia on 15/12/16.
 */

public interface PlaceSearchView {
    void showProgress();
    void placePredication(List<Prediction> predictionList);
    void showError(String msg);
}
