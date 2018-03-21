package com.lalitp.iosplacepicker.UI.Activity.PlaceSearch;


import com.lalitp.iosplacepicker.Pojo.PlaceSearch.Prediction;

import java.util.HashMap;
import java.util.List;

/**
 * Created by atulsia on 15/12/16.
 */

public class PlaceSearchPresenterImpl implements PlaceSearchPresenter, PlaceSearchInteractor.LocationDetailChangeListener {

    private PlaceSearchView placeSearchView;
    private PlaceSearchInteractor placeSearchInteractor;

    public PlaceSearchPresenterImpl(PlaceSearchView locationDetailView) {
        this.placeSearchView = locationDetailView;
        this.placeSearchInteractor = new PlaceSearchInteractorImpl();
    }

    @Override
    public void getPlacePredication(HashMap<String, String> hashMap) {
        if (placeSearchView!=null)
            placeSearchView.showProgress();

        placeSearchInteractor.getPlacePredicationData(hashMap,this);
    }

    @Override
    public void getPlacePredication(List<Prediction> predictionList) {
        if (placeSearchView != null)
            placeSearchView.placePredication(predictionList);
    }


    @Override
    public void onError(String strMsg) {
        if (placeSearchView != null)
            placeSearchView.showError(strMsg);
    }
}
