package com.fields.curiumx.fields;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends Activity implements View.OnClickListener{
    TextView textView;
    SearchView search;
    String text;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirestoreRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    Button usersButton;
    Button teamsButton;
    Button fieldsButton;
    CardView addNewField;
    @BindView(R.id.teamRecycler)
    EmptyRecyclerView teamRecycler;
    FusedLocationProviderClient fusedLocationProviderClient;
    int REQUEST_FINE_LOCATION;

    LayoutInflater layoutInflater;
    PopupWindow popupWindow;
    ConstraintLayout searchActivity;

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
        setContentView(R.layout.activity_search);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        init();

        addNewField = findViewById(R.id.add_new_field);
        addNewField.setOnClickListener(this);
        usersButton = findViewById(R.id.users_button);
        usersButton.setOnClickListener(this);
        teamsButton = findViewById(R.id.teams_button);
        teamsButton.setOnClickListener(this);
        fieldsButton = findViewById(R.id.fields_button);
        fieldsButton.setOnClickListener(this);
        search = findViewById(R.id.search);
        textView = findViewById(R.id.textViews);
        fieldsButton.callOnClick();
        teamRecycler.setEmptyView(textView);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //Popup items
        layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        searchActivity = findViewById(R.id.search_activity);
        final ConstraintLayout popUp = findViewById(R.id.popup);
        final ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.location_prompt_popup, popUp);
        popupWindow = new PopupWindow(container, 1000, 600, false);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        popupWindow.setElevation(100);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            gettingLocation();


        } else {

            searchActivity.post(new Runnable() {
                @Override
                public void run() {
                    popupWindow.showAtLocation(searchActivity, Gravity.CENTER_HORIZONTAL, 0, 0);
                    Button okButton = container.findViewById(R.id.okButton);
                    okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            requestPermissions();
                            popupWindow.dismiss();
                        }
                    });

                }
            });


        }

    }

    public void init(){
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        teamRecycler.setLayoutManager(linearLayoutManager);
        db = FirebaseFirestore.getInstance();
    }

    public class teamHolder extends EmptyRecyclerView.ViewHolder {
        @BindView(R.id.name)
        TextView textName;
        @BindView(R.id.country)
        TextView textCountry;

        public teamHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    public void findTeam(){
        text = search.getQuery().toString();


        Query query = db.collection("Teams").whereEqualTo("teamNameText", text);

        FirestoreRecyclerOptions<TeamMap> response = new FirestoreRecyclerOptions.Builder<TeamMap>()
                .setQuery(query, TeamMap.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<TeamMap, teamHolder>(response) {
            @Override
            public void onBindViewHolder(teamHolder holder, int position, final TeamMap model) {
                holder.textName.setText(model.getTeamNameText());
                holder.textCountry.setText(model.getTeamCountryText());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), DetailTeamActivity.class);
                        intent.putExtra("name", model.getTeamNameText());
                        intent.putExtra("country", model.getTeamCountryText());
                        startActivity(intent);
                    }
                });
            }

            @Override
            public teamHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.list_item, group, false);
                return new teamHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }
        };

        adapter.notifyDataSetChanged();
        teamRecycler.setAdapter(adapter);
    }


    public void findUser(){
        text = search.getQuery().toString();


        Query query = db.collection("Users").whereEqualTo("username", text);

        FirestoreRecyclerOptions<UserMap> response = new FirestoreRecyclerOptions.Builder<UserMap>()
                .setQuery(query, UserMap.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<UserMap, teamHolder>(response) {
            @Override
            public void onBindViewHolder(teamHolder holder, int position, final UserMap model) {
                holder.textName.setText(model.getUsername());


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), DetailUserActivity.class);
                        intent.putExtra("displayName", model.getDisplayName());
                        intent.putExtra("username", model.getUsername());
                        intent.putExtra("userID", model.getUserID());
                        intent.putExtra("currentFieldName", model.getCurrentFieldName());
                        startActivity(intent);
                    }
                });
            }

            @Override
            public teamHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.list_item, group, false);
                return new teamHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }
        };

        adapter.notifyDataSetChanged();
        teamRecycler.setAdapter(adapter);
    }


    public void findField(){

    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.users_button:
                addNewField.setVisibility(View.GONE);
                usersButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                teamsButton.setBackgroundColor(getResources().getColor(R.color.cardview_light_background));
                fieldsButton.setBackgroundColor(getResources().getColor(R.color.cardview_light_background));

                search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        findUser();
                        adapter.startListening();
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }
                });
                break;

            case R.id.teams_button:
                addNewField.setVisibility(View.GONE);
                teamsButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                usersButton.setBackgroundColor(getResources().getColor(R.color.cardview_light_background));
                fieldsButton.setBackgroundColor(getResources().getColor(R.color.cardview_light_background));

                search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        findTeam();
                        adapter.startListening();
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }
                });
                break;

            case R.id.fields_button:
                fieldsButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                usersButton.setBackgroundColor(getResources().getColor(R.color.cardview_light_background));
                teamsButton.setBackgroundColor(getResources().getColor(R.color.cardview_light_background));
                findField();
                break;

            case R.id.add_new_field:
                startActivity(new Intent(SearchActivity.this, CreateNewFieldActivity.class));
        }

    }








}
