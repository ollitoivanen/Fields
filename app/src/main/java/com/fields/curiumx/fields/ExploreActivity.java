package com.fields.curiumx.fields;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ExploreActivity extends Activity {

    FusedLocationProviderClient fusedLocationProviderClient;


   TextView emptyText2;

    // The RecyclerView and associated objects for displaying the nearby fields
    RecyclerView placePicker;
    LinearLayoutManager placePickerManager;
    RecyclerView.Adapter placePickerAdapter;

    // The base URL for the Foursquare API
    String foursquareBaseURL = "https://api.foursquare.com/v2/";

    // The client ID and client secret for authenticating with the Foursquare API
    private String clientID;
    private String clientSecret;

    String categoryId = "4cce455aebf7b749d5e191f5";
    int radius = 100000;
    int limit = 20;
    int REQUEST_FINE_LOCATION;

    private LayoutInflater layoutInflater;
    private PopupWindow popupWindow;
    private LinearLayout exploreActivity;


    public void requestPermissions(){
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_FINE_LOCATION);
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        placePicker = findViewById(R.id.fieldsList);

        emptyText2 = findViewById(R.id.emptyText2);



        //Build the adapter, takes context and List of foursquareResults as parameters
        placePicker.setHasFixedSize(true);
        //View contents cannot affect Adapter size
        placePickerManager = new LinearLayoutManager(this);
        //context used to access resources. RecyclerViews must have layoutManager which controls the View's style
        placePicker.setLayoutManager(placePickerManager);
        //set the placePickerManager as placePicker's layoutManager
        placePicker.addItemDecoration(new DividerItemDecoration(placePicker.getContext(), placePickerManager.getOrientation()));
        //style for the RecyclerView


        // Gets the stored Foursquare API client ID and client secret from XML
        clientID = getResources().getString(R.string.foursquare_api_client_id);
        clientSecret = getResources().getString(R.string.foursquare_client_secret);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

            if (placePickerAdapter == null){
                placePicker.setVisibility(View.GONE);
                emptyText2.setVisibility(View.VISIBLE);
            }else{
                placePicker.setVisibility(View.VISIBLE);
                emptyText2.setVisibility(View.GONE);
                //TODO remove text when location is not allowed and popup is showed
            }


        layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        exploreActivity = findViewById(R.id.activity_explore);
        ConstraintLayout popUp = findViewById(R.id.popup);
        ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.location_prompt_popup, popUp );
        popupWindow = new PopupWindow(container, 400, 400, true);

        // Checks for location permissions at runtime (required for API >= 23)
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            exploreActivity.post(new Runnable() {
                @Override
                public void run() {
                    popupWindow.showAtLocation(exploreActivity, Gravity.CENTER, 500, 500);
                }
            });
        }else{

            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location lastLocation) {
                            // Got last known location. In some rare situations this can be null.


                            if (lastLocation != null) {
                                // Logic to handle location object

                                // The user's current latitude, longitude, and location accuracy
                                String ll = lastLocation.getLatitude() + "," + lastLocation.getLongitude();
                                double llAcc = lastLocation.getAccuracy();


                                // Builds Retrofit and FoursquareService objects for calling the Foursquare API and parsing with GSON
                                Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl(foursquareBaseURL)
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();
                                FoursquareService foursquare = retrofit.create(FoursquareService.class);


                                // Calls the Foursquare API to explore nearby fields
                                Call<FoursquareJSON> fieldsCall = foursquare.searchFields(
                                        clientID,
                                        clientSecret,
                                        ll,
                                        llAcc,
                                        categoryId,
                                        radius,
                                        limit);

                                fieldsCall.enqueue(new Callback<FoursquareJSON>() {
                                    @Override
                                    public void onResponse(Call<FoursquareJSON> call, Response<FoursquareJSON> response) {

                                        // Gets the venue object from the JSON response
                                        FoursquareJSON fjson = response.body();

                                        FoursquareResponse fr = fjson.response;
                                        FoursquareGroup fg = fr.group;
                                        List<FoursquareResults> frs = fg.results;

                                        // Displays the results in the RecyclerView
                                        placePickerAdapter = new PlacePickerAdapter(getApplicationContext(), frs);
                                        placePicker.setAdapter(placePickerAdapter);

                                    }

                                    @Override
                                    public void onFailure(Call<FoursquareJSON> call, Throwable t) {
                                        Toast.makeText(getApplicationContext(), "Can't connect to Foursquare's servers!", Toast.LENGTH_LONG).show();
                                        finish();

                                    }
                                });
                            } else {

                            }
                        }
                    });
            }
    }




    @Override
    protected void onResume() {
        super.onResume();

    }
    @Override
    protected void onPause() {
        super.onPause();

    }


}