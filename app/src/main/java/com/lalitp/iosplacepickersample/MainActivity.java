package com.lalitp.iosplacepickersample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.lalitp.iosplacepicker.Pojo.PlaceSearch.PlaceSearchAddress;
import com.lalitp.iosplacepicker.UI.Activity.PlaceSearch.PlaceSearchActivity;
import com.lalitp.iosplacepicker.Webapi.RestClient;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn_location)
    Button btnLocation;
    @BindView(R.id.txt_location)
    TextView txtLocation;

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_location)
    public void onViewClicked() {
        openPlaceSearchActivity();
    }

    private void openPlaceSearchActivity() {

        // The autocomplete activity requires Google Play Services to be available. The intent
        // builder checks this and throws an exception if it is not the case.
        Intent intent = new Intent(MainActivity.this, PlaceSearchActivity.class);
        intent.putExtra(PlaceSearchActivity.GOOGLE_API_KEY, "AIzaSyCVonFmmMi-WH4cvLPevCK3PIsvx-tbPC0");
        startActivityForResult(intent, PlaceSearchActivity.CUSTOM_AUTOCOMPLETE_REQUEST_CODE);
    }

    /**
     * Called after the autocomplete activity has finished to return its result.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that the result was from the autocomplete widget.
        if (requestCode == PlaceSearchActivity.CUSTOM_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Get the user's selected place from the Intent.
                PlaceSearchAddress place = (PlaceSearchAddress) data.getParcelableExtra(PlaceSearchActivity.SEARCH_ADDRESS);

                // Format the place's details and display them in the TextView.
                txtLocation.setText(place.getAddress());

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(MainActivity.this, data);
                Log.e(TAG, "Error: Status = " + status.toString());
            } else if (resultCode == RESULT_CANCELED) {
                // Indicates that the activity closed before a selection was made. For example if
                // the user pressed the back button.
            }
        }
    }

}
