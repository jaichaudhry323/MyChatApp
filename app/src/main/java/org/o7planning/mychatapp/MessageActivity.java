package org.o7planning.mychatapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.o7planning.mychatapp.Adapters.MessageAdapter;
import org.o7planning.mychatapp.Model.Chat;
import org.o7planning.mychatapp.Model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

// THE MAIN THING

public class MessageActivity extends AppCompatActivity {

    // WE NEED DB FIREBASEUSER , RECYCLERVIEW , ONDATACHAGE LISTENERS , etc etc

    CircleImageView mProfileImage;
    TextView mUserName;                     // other persons username
    ImageButton mSendButton;
    EditText mSendText;
    RecyclerView mRecyclerView;
    List<Chat> mChat;

    String mOtherUser_Id;

    FirebaseUser firebaseUser;
    DatabaseReference UserDatabaseReference;

    String msg = " \n n \n n \n";
//                Log.i("Main Activity", msg + "Database data changed " + msg);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivty_message);

        mProfileImage = findViewById(R.id.profile_image);
        mRecyclerView = findViewById(R.id.recycler_view);
        mSendButton = findViewById(R.id.btn_send);
        mSendText = findViewById(R.id.text_send);
        mUserName = findViewById(R.id.username);

        Intent intent = getIntent();
        mOtherUser_Id = intent.getStringExtra("userid");
        mChat = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MessageActivity.this);

        // To enable the chat to show up on from bottom instead of top
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        mSendButton.setOnClickListener(v -> {

            String sendtext = mSendText.getText().toString();
            if (!sendtext.equals("")) {
                SendMessage(firebaseUser.getUid(), mOtherUser_Id, sendtext);
            } else {
                Toast.makeText(MessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
            }
            mSendText.setText("");
        });

        UserDatabaseReference = FirebaseDatabase.getInstance().getReference("users").child(mOtherUser_Id);
        UserDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Here we update the profile photo of user in case he/she changes it
                // then the messsages that the other user might have sent
                User OtherUser = dataSnapshot.getValue(User.class);

                if (OtherUser != null) {

                    mUserName.setText(OtherUser.getUsername());

                    if (OtherUser.getImageURL() == "default") {
                        mProfileImage.setImageResource(R.drawable.ic_launcher_background);
                    } else {
                        Glide.with(getApplicationContext()).load(OtherUser.getImageURL()).into(mProfileImage);
                    }
                    // NOW ITS TIME TO READ LATEST MESSAGES BCOZ OF WHICH THIS FUNCTION MAY HAVE BEEN CALLED
                    ReadMessages(firebaseUser.getUid(), mOtherUser_Id, OtherUser.getImageURL());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void ReadMessages(String userId, String OtherUser_Id, String imageUrl) {
        mChat.clear();

        DatabaseReference ChatDatabaseReference = FirebaseDatabase.getInstance().getReference("chats");

        ChatDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);

                    // SEE IF THIS CHAT BELONGS TO ME AND THE OTHER USER I HAVE OPENED CHAT WITH
                    if (chat != null && chat.getReceiver().equals(OtherUser_Id) && chat.getSender().equals(userId)) {
                        mChat.add(chat);
                    }
                    if (chat != null && chat.getSender().equals(OtherUser_Id) && chat.getReceiver().equals(userId)) {
                        mChat.add(chat);
                    }
                    MessageAdapter messageAdapter = new MessageAdapter(getApplicationContext(), mChat, imageUrl);
                    mRecyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SendMessage(String sender, String receiver, String sendtext) {

        UserDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // We put chat data inside chats DB , simple
                HashMap<String, Object> hashMap = new HashMap();
                hashMap.put("sender", sender);
                hashMap.put("receiver", receiver);
                hashMap.put("message", sendtext);
                hashMap.put("isseen", false);

                // Writing this may replace all chat data with this chat data hashMap
//                FirebaseDatabase.getInstance().getReference().child("chats").setValue(hashMap);

                // So we push to create a new child of chats and then set its value to hashMap fields
                FirebaseDatabase.getInstance().getReference().child("chats").push().setValue(hashMap);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
