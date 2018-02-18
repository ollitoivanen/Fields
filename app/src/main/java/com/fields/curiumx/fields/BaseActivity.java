package com.fields.curiumx.fields;




import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;
import android.view.View;


public class BaseActivity extends Activity {



        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_base);
        }

        public void onExploreClick(View view){
            Intent intent = new Intent(this, ExploreActivity.class);
            startActivity(intent);
        }



    }







