package com.fields.curiumx.fields;

import android.content.Intent;
import android.os.Bundle;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class TeamChatActivity extends AppCompatActivity {
    Boolean teamFieldsPlus;
    String teamID;
    FirestoreRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    EmptyRecyclerView teamChatRecycler;
    ConstraintLayout messageConstraint;

    EditText messageTextEdit;
    Button sendMessageButton;
    String time;



    Query query;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    ActionBar actionBar;

    public class chatHolder extends EmptyRecyclerView.ViewHolder {
        TextView messageView;
        TextView messageSender;
        TextView time;


        public chatHolder(View view) {
            super(view);
            messageView = view.findViewById(R.id.messageView_team);
            messageSender = view.findViewById(R.id.message_sender_team);
            time = view.findViewById(R.id.time_message);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_chat);
        Bundle info = getIntent().getExtras();
        teamFieldsPlus = info.getBoolean("teamFieldsPlus");
        teamID = info.getString("teamID");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        teamChatRecycler = findViewById(R.id.team_chat_recycler);
        messageConstraint = findViewById(R.id.message_constraint_team);
        messageTextEdit = findViewById(R.id.message_box_team);
        sendMessageButton = findViewById(R.id.send_button_team);


        if (teamFieldsPlus){
            teamChatFieldsPlus();
        }else {
            teamChatNoFieldsPlus();
        }
    }
    public void init() {
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        teamChatRecycler.setLayoutManager(linearLayoutManager);

    }

    public void setRecycler(){
        init();
        query =  db.collection("Teams").document(teamID).collection("teamMessages")
                .orderBy("time", Query.Direction.DESCENDING).limit(50);


        FirestoreRecyclerOptions<ChatMap> response = new FirestoreRecyclerOptions.Builder<ChatMap>()
                .setQuery(query, ChatMap.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<ChatMap, chatHolder>(response) {

            @Override
            public chatHolder onCreateViewHolder (ViewGroup group, int i){
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.team_chat_list, group, false);

                return new chatHolder(view);
            }

            @Override
            public void onBindViewHolder (chatHolder holder, int position, final ChatMap model){

                    if (!DateFormat.is24HourFormat(getApplicationContext())){
                    if (model.getTime().getTime()-System.currentTimeMillis()<-86400000) {
                        SimpleDateFormat dateFormatPmOverDay = new SimpleDateFormat("hh:mm a,  dd MMM", Locale.getDefault());
                        time = dateFormatPmOverDay.format(model.getTime());
                    }else {
                        SimpleDateFormat dateFormatPm = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                        time = dateFormatPm.format(model.getTime());
                    }

                }else {
                    if (model.getTime().getTime() - System.currentTimeMillis()<-86400000) {
                        SimpleDateFormat simpleDateFormatOverDay = new SimpleDateFormat("HH:mm,  dd MMM", Locale.getDefault());
                        time = simpleDateFormatOverDay.format(model.getTime());
                    }else {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                        time = simpleDateFormat.format(model.getTime());
                    }
                }

                holder.messageView.setText(model.getText());
                holder.messageSender.setText(model.getSender());
                holder.time.setText(time);

        }

            @Override
            public void onError (FirebaseFirestoreException e){
                Log.e("error", e.getMessage());
            }
        };


teamChatRecycler.smoothScrollToPosition(50);
        adapter.notifyDataSetChanged();
        teamChatRecycler.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (teamFieldsPlus){
            adapter.stopListening();
            teamChatRecycler.setAdapter(null);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (teamFieldsPlus){
            adapter.stopListening();
            teamChatRecycler.setAdapter(null);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (teamFieldsPlus){
            teamChatRecycler.setAdapter(adapter);
            adapter.startListening();
        }
    }

    public void teamChatFieldsPlus(){
        teamChatRecycler.setVisibility(View.VISIBLE);
        messageTextEdit.setVisibility(View.VISIBLE);
        sendMessageButton.setVisibility(View.VISIBLE);

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        setTitle(getString(R.string.chat));

        setRecycler();

    }

    public void teamChatNoFieldsPlus(){
        ConstraintLayout noFieldsConst = findViewById(R.id.noTeamFieldsPlusScreen);
        noFieldsConst.setVisibility(View.VISIBLE);
       actionBar = getSupportActionBar();
        actionBar.setTitle("");





        TextView levelUp2 = findViewById(R.id.level_up_text2);
        String text2 =

                "<font COLOR=\'#FFFFFF\'><b>" + getResources().getString(R.string.open_team_chat) + "</b></font>" + " " +
                        "<font COLOR=\'#3FACFF\'><b>" + getResources().getString(R.string.plus_comma) + "</b></font>" + " " +

                        "<font COLOR=\'#FFFFFF\'><b>" + getResources().getString(R.string.level_up2) + "</b></font>" + " " +
                "<font COLOR=\'#3FACFF\'><b>" + getResources().getString(R.string.plus) + "</b></font>" + " " +
                "<font COLOR=\'#FFFFFF\'><b>" + getResources().getString(R.string.level_up3) + "</b></font>" + " " +
                "<font COLOR=\'#3bd774\'><b>" + getResources().getString(R.string.team_chat_feature) + "</b></font>";
        levelUp2.setText(Html.fromHtml(text2));



        Button discoverButton = findViewById(R.id.discover);
        discoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeamChatActivity.this, FieldsPlusStartActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);

            }
        });


    }

    public void sendMessage(){
        String messageText = messageTextEdit.getText().toString().trim();
        if (!messageText.equals("")) {
            Calendar c = Calendar.getInstance();
            c.getTime();
            ChatMap chatMap = new ChatMap(messageText, user.getDisplayName(), uid, c.getTime(), null);//change
            db.collection("Teams").document(teamID).collection("teamMessages")
                    .document(Long.toString(System.currentTimeMillis())).set(chatMap);
            messageTextEdit.setText("");
            messageTextEdit.onEditorAction(EditorInfo.IME_ACTION_DONE);
            teamChatRecycler.smoothScrollToPosition(0);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            onBackPressed();
            finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
    }

}
