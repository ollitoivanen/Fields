package com.fields.curiumx.fields;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchFieldOnlyActivity extends Activity implements View.OnClickListener {

    String text;
    TextView textView;

    SearchView searchView;
    Button field_by_area_button;
    Button field_by_name_button;
    @BindView(R.id.fieldRecycler)
    EmptyRecyclerView ep;

    Boolean clicked_area;
    Boolean clicked_name;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirestoreRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_field_only);
        textView = findViewById(R.id.textViews_empty);
        searchView = findViewById(R.id.search1);
        field_by_area_button = findViewById(R.id.field_by_area1);
        field_by_area_button.setOnClickListener(this);
        field_by_name_button = findViewById(R.id.field_by_name1);
        field_by_name_button.setOnClickListener(this);
        ep = findViewById(R.id.fieldRecycler);
        field_by_name_button.callOnClick();
        ButterKnife.bind(this);
        ep.setEmptyView(textView);

        init();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                findField();
                adapter.startListening();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    public void init(){
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        ep.setLayoutManager(linearLayoutManager);
        db = FirebaseFirestore.getInstance();
    }

    public class fieldHolder extends EmptyRecyclerView.ViewHolder {
        @BindView(R.id.name)
        TextView textName;


        public fieldHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }



    public void findField(){
        text = searchView.getQuery().toString();
        Query query;
        if (clicked_name){
            query = db.collection("Fields").whereEqualTo("fieldName", text);
        }else {
            query = db.collection("Fields").whereEqualTo("fieldArea", text);
        }

        FirestoreRecyclerOptions<FieldMap> response = new FirestoreRecyclerOptions.Builder<FieldMap>()
                .setQuery(query, FieldMap.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<FieldMap, fieldHolder>(response) {
            @Override
            public void onBindViewHolder(fieldHolder holder, int position, final FieldMap model) {
                holder.textName.setText(model.getFieldName());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.putExtra("fieldName2", model.getFieldName());
                        intent.putExtra("fieldAddress", model.getFieldAddress());
                        intent.putExtra("fieldArea", model.getFieldArea());
                        intent.putExtra("fieldType", model.getFieldType());
                        intent.putExtra("fieldAccessType", model.getAccessType());
                        intent.putExtra("goalCount", model.getGoalCount());
                        intent.putExtra("creator", model.getCreator());
                        intent.putExtra("fieldID", model.getFieldID());
                        setResult(Activity.RESULT_OK, intent);
                        finish();

                    }
                });
            }

            @Override
            public fieldHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.list_item, group, false);
                return new fieldHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }
        };

        adapter.notifyDataSetChanged();
        ep.setAdapter(adapter);
    }



    @Override
    public void onClick(View v) {

        switch (v.getId()){


            case R.id.field_by_name1:
                clicked_name = true;
                clicked_area = false;
                field_by_name_button.setTextColor((getResources().getColor(R.color.colorPrimaryDark)));
                field_by_area_button.setTextColor((getResources().getColor(R.color.blacknot)));
                break;
            case R.id.field_by_area1:
                clicked_area = true;
                clicked_name = false;
                field_by_area_button.setTextColor((getResources().getColor(R.color.colorPrimaryDark)));
                field_by_name_button.setTextColor((getResources().getColor(R.color.blacknot)));


        }
    }
}
