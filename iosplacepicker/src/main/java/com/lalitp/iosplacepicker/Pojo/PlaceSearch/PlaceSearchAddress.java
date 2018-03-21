package com.lalitp.iosplacepicker.Pojo.PlaceSearch;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by atulsia on 30/1/18.
 */

public class PlaceSearchAddress implements Parcelable {
    private LatLng latLng;
    private String name;
    private String address;

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.latLng, flags);
        dest.writeString(this.name);
        dest.writeString(this.address);
    }

    public PlaceSearchAddress() {
    }

    protected PlaceSearchAddress(Parcel in) {
        this.latLng = in.readParcelable(LatLng.class.getClassLoader());
        this.name = in.readString();
        this.address = in.readString();
    }

    public static final Creator<PlaceSearchAddress> CREATOR = new Creator<PlaceSearchAddress>() {
        @Override
        public PlaceSearchAddress createFromParcel(Parcel source) {
            return new PlaceSearchAddress(source);
        }

        @Override
        public PlaceSearchAddress[] newArray(int size) {
            return new PlaceSearchAddress[size];
        }
    };
}
