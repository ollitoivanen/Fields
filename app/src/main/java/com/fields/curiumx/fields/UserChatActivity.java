package com.fields.curiumx.fields;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserChatActivity extends AppCompatActivity {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirestoreRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    @BindView(R.id.chat_recycler)
    EmptyRecyclerView chatRecycler;
    String userID;
    Query query;
    ConstraintLayout messageConstraint;
    TextView messageView;
    Boolean thisUser;
    String UserMessages = "UserMessages";
    String TheseMessages = "TheseMessages";

    EditText messageTextEdit;
    Button sendMessageButton;

    public void init() {
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        chatRecycler.setLayoutManager(linearLayoutManager);
        db = FirebaseFirestore.getInstance();
    }

    public class chatHolder extends EmptyRecyclerView.ViewHolder {
        @BindView(R.id.messageView)
        TextView messageView;

        @BindView(R.id.mesgCont)
        LinearLayout msgCont;

        public chatHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_chat);
        chatRecycler = findViewById(R.id.chat_recycler);
        init();
        messageConstraint = findViewById(R.id.message_constraint);

        messageTextEdit = findViewById(R.id.message_box);
        sendMessageButton = findViewById(R.id.send_button);
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
                if (ds.get("Started")==null) {
                    Bundle info = getIntent().getExtras();
                    userID = info.getString("userID");
                    //
                    db.collection("Users").document(userID).collection(UserMessages).document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        //
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot ds1 = task.getResult();
                            if (ds1.get("Started")==null) {


                                Map<String, Object> map = new HashMap<>();
                                map.put("Started", "Yes");
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
        ChatMap chatMap = new ChatMap(messageText, user.getDisplayName(), user.getUid());
        DocumentReference dr;
        if (thisUser){
             dr = db.collection("Users").document(uid).collection(UserMessages)
                    .document(userID).collection(TheseMessages).document(Long.toString(System.currentTimeMillis()));
        }else {
             dr = db.collection("Users").document(userID).collection(UserMessages)
                    .document(uid).collection(TheseMessages).document(Long.toString(System.currentTimeMillis()));
        }
        dr.set(chatMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                messageTextEdit.setText("");
            }
        });


    }

    public void setRecycler(){
if (thisUser) {
    query = db.collection("Users").document(uid).collection(UserMessages).document(userID).collection(TheseMessages);
}else {
    query = db.collection("Users").document(userID).collection(UserMessages).document(uid).collection(TheseMessages);

}
    FirestoreRecyclerOptions<ChatMap> response = new FirestoreRecyclerOptions.Builder<ChatMap>()
            .setQuery(query, ChatMap.class)
            .build();

    adapter = new FirestoreRecyclerAdapter<ChatMap, chatHolder>(response) {

        @Override
        public chatHolder onCreateViewHolder (ViewGroup group,int i){
            View view = LayoutInflater.from(group.getContext())
                    .inflate(R.layout.chat_list, group, false);
            messageConstraint = view.findViewById(R.id.message_constraint);
            messageView = view.findViewById(R.id.messageView);
            return new chatHolder(view);
        }

        @Override
        public void onBindViewHolder (chatHolder holder,int position, final ChatMap model){
        holder.messageView.setText(model.getText());
        if (model.getText().equals("init")){

        }
        if (model.getSender().equals(uid)) {
            holder.messageView.setBackground(getResources().getDrawable(R.drawable.rounded_view_green));
            holder.msgCont.setGravity(Gravity.END);

        }
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
}
