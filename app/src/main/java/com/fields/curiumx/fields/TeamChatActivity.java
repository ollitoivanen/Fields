package com.fields.curiumx.fields;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TeamChatActivity extends AppCompatActivity {
    Boolean teamFieldsPlus;
    String teamID;
    FirestoreRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @BindView(R.id.team_chat_recycler)
    EmptyRecyclerView teamChatRecycler;
    ConstraintLayout messageConstraint;

    EditText messageTextEdit;
    Button sendMessageButton;


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
            ButterKnife.bind(this, itemView);
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
        teamChatRecycler.setLayoutManager(linearLayoutManager);

    }

    public void setRecycler(){
        init();
        query =  db.collection("Teams").document(teamID).collection("teamMessages").limit(50);


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

                holder.messageView.setText(model.getText());
                holder.messageSender.setText(model.getSender());

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


        TextView levelUp = findViewById(R.id.level_up_text);
        String text1 =
                "<font COLOR=\'#FFFFFF\'><b>" + getResources().getString(R.string.level_up1) + "</b></font>" + " " +
                "<font COLOR=\'#3FACFF\'><b>" + getResources().getString(R.string.plus_comma) + "</b></font>";


        TextView levelUp2 = findViewById(R.id.level_up_text2);
        String text2 = "<font COLOR=\'#FFFFFF\'><b>" + getResources().getString(R.string.level_up2) + "</b></font>" + " " +
                "<font COLOR=\'#3FACFF\'><b>" + getResources().getString(R.string.plus) + "</b></font>" + " " +
                "<font COLOR=\'#FFFFFF\'><b>" + getResources().getString(R.string.level_up3) + "</b></font>" + " " +
                "<font COLOR=\'#3bd774\'><b>" + getResources().getString(R.string.team_chat_feature) + "</b></font>";
        levelUp2.setText(Html.fromHtml(text2));



        levelUp.setText(Html.fromHtml(text1));
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
            ChatMap chatMap = new ChatMap(messageText, user.getDisplayName(), uid);
            db.collection("Teams").document(teamID).collection("teamMessages")
                    .document(Long.toString(System.currentTimeMillis())).set(chatMap);
            messageTextEdit.setText("");
            messageTextEdit.onEditorAction(EditorInfo.IME_ACTION_DONE);
            teamChatRecycler.smoothScrollToPosition(50);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
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
