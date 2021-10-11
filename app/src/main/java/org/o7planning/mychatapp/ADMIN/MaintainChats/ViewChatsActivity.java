package org.o7planning.mychatapp.ADMIN.MaintainChats;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.o7planning.mychatapp.Adapters.MessageAdapter;
import org.o7planning.mychatapp.Adapters.UserAdapter;
import org.o7planning.mychatapp.Model.Chat;
import org.o7planning.mychatapp.Model.User;
import org.o7planning.mychatapp.R;
import org.o7planning.mychatapp.Utils.NetworkUtil;
import org.o7planning.mychatapp.Utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ViewChatsActivity extends AppCompatActivity {


    String msg = "\n n \n n \n n";

    List<Chat> mChat;
    List<User> mUsers;
    FirebaseUser firebaseUser;
    DatabaseReference UserDatabaseReference;

    RecyclerView mRecyclerView;

    String email1;
    String email2;

    TextView mUserName1;
    TextView mUserName2;

    String leftuserid;
    String rightuserid;

    public void onCreate(Bundle B) {
        super.onCreate(B);
        setContentView(R.layout.activity_2_view_chats);

        mChat = new ArrayList<>();
        mUsers = new ArrayList<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        UserDatabaseReference = FirebaseDatabase.getInstance().getReference("users").child("");

        mUserName1 = findViewById(R.id.username1);
        mUserName2 = findViewById(R.id.username2);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (!Objects.equals(bundle.getString("email1"), "")) {
                email1 = bundle.getString("email1");
            }
            if (!Objects.equals(bundle.getString("email2"), "")) {
                email2 = bundle.getString("email2");
            }
            if (!Objects.equals(bundle.getString("userid1"), "")) {
                leftuserid = bundle.getString("userid1");
            }
            if (!Objects.equals(bundle.getString("userid2"), "")) {
                rightuserid= bundle.getString("userid2");
            }
        }

        mUserName1.setText(email1);
        mUserName2.setText(email2);

        readUsers();

//        for (User user : mUsers) {
//            if (user.getEmail().equals(email1)) {
//                userId1 = user.getId();
//            }
//            if (user.getEmail().equals(email2)) {
//                userId2 = user.getId();
//            }
//        }

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                // Set the User , Chat and Profile Fragment
            }
        }, 1500);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        NetworkUtil.setConnectivityTracking(getBaseContext());
        if (!NetworkUtil.getConnectivityStatus(getBaseContext())) {
            ToastUtil.makeLongToast(getApplicationContext(), "Check Your Network Connection");
            return;
        }
        ReadMessages(leftuserid, rightuserid, "default");
    }

    private void ReadMessages(String leftuserid, String rightuserid, String imageUrl) {
        mChat.clear();

        DatabaseReference ChatDatabaseReference = FirebaseDatabase.getInstance().getReference("chats");
        ChatDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);

                    // SEE IF THIS CHAT BELONGS TO ME AND THE OTHER USER I HAVE OPENED CHAT WITH
                    if (chat != null && chat.getReceiver().equals(leftuserid) && chat.getSender().equals(rightuserid)) {
                        mChat.add(chat);
                    }
                    if (chat != null && chat.getSender().equals(leftuserid) && chat.getReceiver().equals(rightuserid)) {
                        mChat.add(chat);
                    }

                    MessageAdapter messageAdapter = new MessageAdapter(getApplicationContext(), mChat, imageUrl,leftuserid,rightuserid);
                    mRecyclerView.setAdapter(messageAdapter);

//                    if(mChat.size()==0)
//                    {
//                        ToastUtil.makeLongToast(getApplicationContext(),"No Chats");
//                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void readUsers() {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();

                Log.i("ViewChatsActivity", msg + "Datasnapshot : " + dataSnapshot + msg);

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);

                    if (user != null && user.getId() != null && firebaseUser != null && !user.getId().equals(firebaseUser.getUid())) {
                        mUsers.add(user);
                    }

//                    mUserAdapter = new UserAdapter(getApplicationContext(), mUsers, false,0);
//                    mRecyclerView.setAdapter(mUserAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}

