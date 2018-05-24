package com.fields.curiumx.fields;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UserChatActivity extends AppCompatActivity {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirestoreRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    EmptyRecyclerView chatRecycler;
    String userID;
    Query query;
    TextView messageView;
    Boolean thisUser;
    String UserMessages = "UserMessages";
    String TheseMessages = "TheseMessages";
    String time;
    EditText messageTextEdit;
    Button sendMessageButton;

    public void init() {
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        chatRecycler.setLayoutManager(linearLayoutManager);
        db = FirebaseFirestore.getInstance();
    }

    public class chatHolder extends EmptyRecyclerView.ViewHolder {
        TextView messageView;
        TextView messageSender;
        TextView time;

        public chatHolder(View view) {
            super(view);
            messageView = view.findViewById(R.id.messageView_team);
            messageSender = view.findViewById(R.id.message_sender_team);
            time = view.findViewById(R.id.time_message);        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_chat);
        chatRecycler = findViewById(R.id.user_chat_recycler);
        init();
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getResources().getString(R.string.chat));
        messageTextEdit = findViewById(R.id.message_box_user);
        sendMessageButton = findViewById(R.id.send_button_user);
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        Bundle info = getIntent().getExtras();
        userID = info.getString("userID");

        db.collection("Users").document(uid).collection(UserMessages).document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot ds = task.getResult();
                if (ds.get("3")==null) {
                    Bundle info = getIntent().getExtras();
                    userID = info.getString("userID");

                    db.collection("Users").document(userID).collection(UserMessages).document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        //
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot ds1 = task.getResult();
                            if (ds1.get("3")==null) {


                                Map<String, Object> map = new HashMap<>();
                                map.put("3", "1");
                                db.collection("Users").document(uid)
                                        .collection(UserMessages).document(userID)
                                        .set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        thisUser = true;
                                        setRecycler();
                                    }
                                });
                            } else {
                                thisUser = false;
                                setRecycler();
                            }
                        }
                    });

                    }else {
                    thisUser = true;
                    setRecycler();
                }
            }
        });
    }

    public void sendMessage() {
        String messageText = messageTextEdit.getText().toString().trim();
        DocumentReference dr;
        if (!messageText.equals("")) {
            Calendar c = Calendar.getInstance();
            c.getTime();
            ChatMap chatMap = new ChatMap(messageText, user.getDisplayName(), uid, c.getTime());

            if (thisUser) {
                dr = db.collection("Users").document(uid).collection(UserMessages)
                        .document(userID).collection(TheseMessages).document(Long.toString(System.currentTimeMillis()));
            } else {
                dr = db.collection("Users").document(userID).collection(UserMessages)
                        .document(uid).collection(TheseMessages).document(Long.toString(System.currentTimeMillis()));
            }
            dr.set(chatMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    messageTextEdit.setText("");
                    messageTextEdit.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    chatRecycler.smoothScrollToPosition(0);
                }
            });
        }
    }

    public void setRecycler(){
        if (thisUser) {
            query = db.collection("Users").document(uid).collection(UserMessages).document(userID).collection(TheseMessages)
                    .orderBy("time", Query.Direction.DESCENDING).limit(50);
        }else {
            query = db.collection("Users").document(userID).collection(UserMessages).document(uid).collection(TheseMessages)
                    .orderBy("time", Query.Direction.DESCENDING).limit(50);
        }
    FirestoreRecyclerOptions<ChatMap> response = new FirestoreRecyclerOptions.Builder<ChatMap>()
            .setQuery(query, ChatMap.class)
            .build();

    adapter = new FirestoreRecyclerAdapter<ChatMap, chatHolder>(response) {

        @Override
        public chatHolder onCreateViewHolder (ViewGroup group,int i){
            View view = LayoutInflater.from(group.getContext())
                    .inflate(R.layout.team_chat_list, group, false);
            return new chatHolder(view);
        }

        @Override
        public void onBindViewHolder (chatHolder holder,int position, final ChatMap model){
            if (!DateFormat.is24HourFormat(getApplicationContext())){
                SimpleDateFormat dateFormatPm = new SimpleDateFormat("hh:mm a");
                time = dateFormatPm.format(model.getTime());

            }else {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                time = simpleDateFormat.format(model.getTime());
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

    adapter.notifyDataSetChanged();
    chatRecycler.setAdapter(adapter);
    adapter.startListening();


    }

    @Override
    protected void onStart() {
        super.onStart();
chatRecycler.smoothScrollToPosition(20);    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

            adapter.stopListening();
            chatRecycler.setAdapter(null);

    }

    @Override
    protected void onStop() {
        super.onStop();
            adapter.stopListening();
            chatRecycler.setAdapter(null);
        }

    @Override
    protected void onRestart() {
        super.onRestart();
        chatRecycler.setAdapter(adapter);
        adapter.startListening();
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
