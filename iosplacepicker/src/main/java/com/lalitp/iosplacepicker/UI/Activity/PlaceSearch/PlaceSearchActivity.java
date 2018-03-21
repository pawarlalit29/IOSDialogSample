package com.lalitp.iosplacepicker.UI.Activity.PlaceSearch;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.lalitp.iosplacepicker.Pojo.PlaceSearch.PlaceSearchAddress;
import com.lalitp.iosplacepicker.Pojo.PlaceSearch.Prediction;
import com.lalitp.iosplacepicker.R;
import com.lalitp.iosplacepicker.R2;
import com.lalitp.iosplacepicker.UI.Adaptor.PlaceAutoCompleteAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlaceSearchActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, PlaceSearchView, PlaceAutoCompleteAdapter.ItemClickListner {


    @BindView(R2.id.et_search_place)
    AppCompatEditText etSearchPlace;
    @BindView(R2.id.btn_cancel)
    Button btnCancel;
    @BindView(R2.id.btn_cancel_searchBar)
    ImageView btnCancelSearchBar;
    @BindView(R2.id.rv_result)
    RecyclerView rvResult;
    @BindView(R2.id.progressBar)
    ProgressBar progressBar;

    private Context context;
    private static String TAG = PlaceSearchActivity.class.getSimpleName();
    public static String GOOGLE_API_KEY = "";
    public static String SEARCH_ADDRESS = "" ;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    public static int CUSTOM_AUTOCOMPLETE_REQUEST_CODE = 20;
    private static final int MY_PERMISSIONS_REQUEST_LOC = 30;
    double latitude;
    double longitude;
    private PlaceSearchPresenter placeSearchPresenter;
    private List<Prediction> placeAutoCompletes;
    private Timer timer = new Timer();
    private final long DELAY = 500; // milliseconds
    private PlaceAutoCompleteAdapter placeAutoCompleteAdapter;
    private String apiKey;
    private boolean sentToSettings = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_search);
        ButterKnife.bind(this);
        context = this;
        init(getIntent().getExtras());
    }

    private void init(Bundle bundle) {

        if (bundle.containsKey(GOOGLE_API_KEY)) {
            apiKey = bundle.getString(GOOGLE_API_KEY);
        }


        placeAutoCompletes = new ArrayList<>();
        placeSearchPresenter = new PlaceSearchPresenterImpl(this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvResult.setLayoutManager(layoutManager);
        rvResult.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        rvResult.setItemAnimator(new DefaultItemAnimator());

        placeAutoCompleteAdapter = new PlaceAutoCompleteAdapter(this, placeAutoCompletes);
        rvResult.setAdapter(placeAutoCompleteAdapter);
        placeAutoCompleteAdapter.setOnItemClickListener(this);

        //get permission for Android M
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            fetchLocation();
        } else {

            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOC);

            } else {
                fetchLocation();
            }
        }

        etSearchPlace.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etSearchPlace.getText().toString().equalsIgnoreCase("")) {
                    btnCancelSearchBar.setVisibility(View.GONE);
                } else {
                    btnCancelSearchBar.setVisibility(View.VISIBLE);
                }

                if (s.length() > 2) {
                    waitForActionDone(s.toString());
                } else
                    clearResult();
                //countryCodeAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onItemclicked(int pos) {
        Prediction placeAutoComplete = placeAutoCompletes.get(pos);
        String placeId = placeAutoComplete.getPlaceId();

        Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId)
                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (places.getStatus().isSuccess()) {
                            final Place myPlace = places.get(0);
                            LatLng queriedLocation = myPlace.getLatLng();
                            Log.v("Latitude is", "" + queriedLocation.latitude);
                            Log.v("Longitude is", "" + queriedLocation.longitude);

                            PlaceSearchAddress placeSearchAddress = new PlaceSearchAddress();
                            placeSearchAddress.setName(myPlace.getName().toString());
                            placeSearchAddress.setAddress(myPlace.getAddress().toString());
                            placeSearchAddress.setLatLng(myPlace.getLatLng());

                            Intent intent = new Intent();
                            intent.putExtra(SEARCH_ADDRESS, placeSearchAddress);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                        places.release();

                    }
                });
    }

    @OnClick(R2.id.btn_cancel)
    public void onCancelBtnClick() {
        finish();
    }

    @OnClick(R2.id.btn_cancel_searchBar)
    public void onCancelBtnSearchbarClick() {
        etSearchPlace.setText(null);
        clearResult();
    }

    @OnClick(R2.id.et_search_place)
    public void onEtSearchClick() {
        btnCancel.setVisibility(View.VISIBLE);
    }

    public void fetchLocation() {
        //Build google API client to use fused location
        buildGoogleApiClient();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();


    }

    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);

            if (mLastLocation != null) {
                latitude = mLastLocation.getLatitude();
                longitude = mLastLocation.getLongitude();
            }

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void placePredication(List<Prediction> predictionList) {
        progressBar.setVisibility(View.INVISIBLE);
        if (placeAutoCompletes != null && !placeAutoCompletes.isEmpty())
            placeAutoCompletes.clear();

        placeAutoCompletes.addAll(predictionList);
        placeAutoCompleteAdapter.notifyDataSetChanged();

    }

    @Override
    public void showError(String msg) {
        progressBar.setVisibility(View.INVISIBLE);
    }

    private HashMap<String, String> getPredicationParam(String query) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("input", query);
        hashMap.put("location", latitude + "," + longitude);
        hashMap.put("radius", "500");
        hashMap.put("language", "en");
        hashMap.put("key", apiKey);

        return hashMap;
    }

    private void waitForActionDone(final String query) {
        System.out.println("waitForAction waiting");
        timer.cancel();
        timer = new Timer();
        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        // TODO: do what you need here (refresh list)
                        // you will probably need to use runOnUiThread(Runnable action) for some specific actions
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                placeAutoCompleteAdapter.setSearchQuery(query);
                                placeSearchPresenter.getPlacePredication(getPredicationParam(query));
                            }
                        });
                    }
                },
                DELAY
        );
    }

    private void clearResult() {
        if (placeAutoCompletes != null && !placeAutoCompletes.isEmpty())
            placeAutoCompletes.clear();

        placeAutoCompleteAdapter.notifyDataSetChanged();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_PERMISSIONS_REQUEST_LOC) {
            if (ActivityCompat.checkSelfPermission(PlaceSearchActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                fetchLocation();
            }
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(PlaceSearchActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                fetchLocation();
            }
        }
    }
}
