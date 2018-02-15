package com.fields.curiumx.fields;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ExploreActivity extends Activity implements       GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    // The client object for connecting to the Google API
    private FusedLocationProviderClient mFusedLocationClient;


    List<FoursquareResults> frs = new ArrayList<>();


    // The TextView for displaying the current location
    private TextView snapToPlace;

    // The RecyclerView and associated objects for displaying the nearby coffee spots
    private RecyclerView placePicker;
    LinearLayoutManager placePickerManager;
    private RecyclerView.Adapter placePickerAdapter;

    // The base URL for the Foursquare API
    String foursquareBaseURL = "https://api.foursquare.com/v2/";

    // The client ID and client secret for authenticating with the Foursquare API
    private String clientID;
    private String clientSecret;

    String categoryId = "4cce455aebf7b749d5e191f5";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);


        // The visible TextView and RecyclerView objects
        snapToPlace = findViewById(R.id.snapToPlace);
        placePicker = findViewById(R.id.fieldsList);


        placePickerAdapter = new PlacePickerAdapter(getApplicationContext(), frs);
        placePicker.setHasFixedSize(true);
        placePickerManager = new LinearLayoutManager(this);
        placePicker.setLayoutManager(placePickerManager);
        placePicker.addItemDecoration(new DividerItemDecoration(placePicker.getContext(), placePickerManager.getOrientation()));
        placePicker.setAdapter(placePickerAdapter);


        // Creates a connection to the Google API for location services
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        // Gets the stored Foursquare API client ID and client secret from XML
        clientID = getResources().getString(R.string.foursquare_api_client_id);
        clientSecret = getResources().getString(R.string.foursquare_client_secret);


    }

    @Override
    public void onConnected(Bundle connectionHint) {

        // Checks for location permissions at runtime (required for API >= 23)
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // Makes a Google API request for the user's last known location
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object

                                // The user's current latitude, longitude, and location accuracy
                                String ll = location.getLatitude() + "," + location.getLongitude();
                                double llAcc = location.getAccuracy();

                                // Builds Retrofit and FoursquareService objects for calling the Foursquare API and parsing with GSON
                                Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl(foursquareBaseURL)
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();
                                FoursquareService foursquare = retrofit.create(FoursquareService.class);

                                // Calls the Foursquare API to snap the user's location to a Foursquare venue
                                Call<FoursquareJSON> stpCall = foursquare.snapToPlace(
                                        clientID,
                                        clientSecret,
                                        ll,
                                        llAcc,
                                        categoryId);

                                stpCall.enqueue(new Callback<FoursquareJSON>() {
                                    @Override
                                    public void onResponse(Call<FoursquareJSON> call, Response<FoursquareJSON> response) {

                                        // Gets the venue object from the JSON response
                                        FoursquareJSON fjson = response.body();
                                        FoursquareResponse fr = fjson.response;
                                        List<FoursquareVenue> frs = fr.venues;
                                        FoursquareVenue fv = frs.get(0);

                                        // Notifies the user of their current location
                                        snapToPlace.setText("You're at " + fv.name + ". Here's some fields nearby.");
                                    }

                                    @Override
                                    public void onFailure(Call<FoursquareJSON> call, Throwable t) {
                                        Toast.makeText(getApplicationContext(), "Can't connect to Foursquare's servers!", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                });

                                // Calls the Foursquare API to explore nearby fields
                                Call<FoursquareJSON> fieldsCall = foursquare.searchCoffee(
                                        clientID,
                                        clientSecret,
                                        ll,
                                        llAcc,
                                        categoryId);


                                fieldsCall.enqueue(new Callback<FoursquareJSON>() {
                                    @Override
                                    public void onResponse(Call<FoursquareJSON> call, Response<FoursquareJSON> response) {

                                        // Gets the venue object from the JSON response
                                        FoursquareJSON fjson = response.body();
                                        FoursquareResponse fr = fjson.response;
                                        FoursquareGroup fg = fr.group;
                                        List<FoursquareResults> frs = fg.results;


                                        placePickerAdapter.notifyDataSetChanged();

                                    }

                                    @Override
                                    public void onFailure(Call<FoursquareJSON> call, Throwable t) {
                                        Toast.makeText(getApplicationContext(), "Can't connect to Foursquare's servers!", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                });
                            } else {
                                Toast.makeText(getApplicationContext(), "Can't determine your current location!", Toast.LENGTH_LONG).show();
                                finish();

                            }
                        }
                    });
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        mFusedLocationClient.getLastLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFusedLocationClient.flushLocations();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), "Mr. Jitters can't connect to Google's servers!", Toast.LENGTH_LONG).show();
        finish();
    }
}