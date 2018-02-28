package com.fields.curiumx.fields;

import android.Manifest;
import android.app.Activity;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ExploreActivity extends Activity {

    FusedLocationProviderClient fusedLocationProviderClient;
    //Location provider client

    TextView emptyText;
    //Text displayed when adapter size is null, or no Fields are nearby

    // The RecyclerView and associated objects for displaying the nearby fields
    EmptyRecyclerView placePicker;
    //RecyclerView used to display Fields
    LinearLayoutManager placePickerManager;
    EmptyRecyclerView.Adapter placePickerAdapter;
    //Adapter used to give placePicker content to show

    String foursquareBaseURL = "https://api.foursquare.com/v2/";
    //The base URL for the Foursquare API

    private String clientID;
    private String clientSecret;
    //The client ID and client secret for authenticating with the Foursquare API

    String categoryId = "4cce455aebf7b749d5e191f5";
    int radius = 10000;
    int limit = 20;
    //Parameters used to modify Foursquare results

    int REQUEST_FINE_LOCATION;
    int REQUEST_CHECK_SETTINGS;

    LayoutInflater layoutInflater;
    PopupWindow popupWindow;
    LinearLayout exploreActivity;
    //Fields used to make location popup

    LocationRequest mLocationRequest;








    public void gettingLocation(){
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
                                     Toast.makeText(getApplicationContext(), "Check your internet connection", Toast.LENGTH_LONG).show();
                                     finish();

                                 }
                             });
                         } else {

                             Toast.makeText(getApplicationContext(), "Mr. Jitters can't determine your current location!", Toast.LENGTH_LONG).show();

                      }
                     }
                 });

     }

    public void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_FINE_LOCATION);
    }

    //This method is used to find out what user chose in the location prompt and execute according to it
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == REQUEST_FINE_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // permission was granted, yay! Do the
                // contacts-related task you need to do.
                gettingLocation();

            } else {

                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);


        placePicker = findViewById(R.id.fieldsList);

        emptyText = findViewById(R.id.emptyText);


        //Build the adapter, takes context and List of foursquareResults as parameters
        placePicker.setHasFixedSize(true);
        //View contents cannot affect Adapter size
        placePickerManager = new LinearLayoutManager(this);
        //context used to access resources. RecyclerViews must have layoutManager which controls the View's style
        placePicker.setLayoutManager(placePickerManager);
        //set the placePickerManager as placePicker's layoutManager
        placePicker.addItemDecoration(new DividerItemDecoration(placePicker.getContext(), placePickerManager.getOrientation()));
        //style for the RecyclerView
        placePicker.setEmptyView(emptyText);


        // Gets the stored Foursquare API client ID and client secret from XML
        clientID = getResources().getString(R.string.foursquare_api_client_id);
        clientSecret = getResources().getString(R.string.foursquare_client_secret);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        //Popup items
        layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        exploreActivity = findViewById(R.id.activity_explore);
        final ConstraintLayout popUp = findViewById(R.id.popup);
        final ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.location_prompt_popup, popUp);
        popupWindow = new PopupWindow(container, 1000, 600, false);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        popupWindow.setElevation(100);


        // Checks for location permissions at runtime (required for API >= 23)
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            gettingLocation();


        } else {

            exploreActivity.post(new Runnable() {
                @Override
                public void run() {
                    popupWindow.showAtLocation(exploreActivity, Gravity.CENTER_HORIZONTAL, 0, 0);
                    Button okButton = container.findViewById(R.id.okButton);
                    okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            requestPermissions();
                            popupWindow.dismiss();
                        }
                        //TODO show message to accept the location in settings
                    });

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






