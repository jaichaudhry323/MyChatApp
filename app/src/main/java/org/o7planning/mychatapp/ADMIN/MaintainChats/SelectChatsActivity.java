package org.o7planning.mychatapp.ADMIN.MaintainChats;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
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
import org.o7planning.mychatapp.Utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class SelectChatsActivity extends AppCompatActivity {


    String msg = "\n n \n n \n n";

    List<Chat> mChat;
    List<User> mUsers;
    FirebaseUser firebaseUser;
    DatabaseReference UserDatabaseReference;
    private FirebaseAuth mAuth;

    RecyclerView mRecyclerView;
    UserAdapter mUserAdapter;
    EditText mSearchUsers;

    TextView mEmail1TextView;
    TextView mEmail2TextView;
    Button mViewChatsButton;

    String userid1;
    String userid2;

    public void onCreate(Bundle B) {
        super.onCreate(B);
        setContentView(R.layout.activity_2_select_chats);

        mChat = new ArrayList<>();
        mUsers = new ArrayList<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        UserDatabaseReference = FirebaseDatabase.getInstance().getReference("users").child("");

        mViewChatsButton = findViewById(R.id.view_chats_button);
        mEmail1TextView = findViewById(R.id.email1);
        mEmail2TextView = findViewById(R.id.email2);

        mRecyclerView = findViewById(R.id.recycler_view);
        mSearchUsers = findViewById(R.id.search_users);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        mUsers = new ArrayList<User>();
        readUsers();

        mSearchUsers.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().equals("")) {
                    readUsers();
                } else {
                    Search_Users(mSearchUsers.getText().toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        /////////////////////////////////////////////////////
        mViewChatsButton.setOnClickListener(v -> {

            Bundle args = new Bundle();
            Intent intent = new Intent(this, ViewChatsActivity.class);

            String email1 = mEmail1TextView.getText().toString();
            String email2 = mEmail2TextView.getText().toString();


            if (!verifyEmail(email1)) {
                return;
            }
            if (!verifyEmail(email2)) {
                return;
            }

            if(email1.equals(email2))
            {
                ToastUtil.makeLongToast(getApplicationContext(),"Please Enter Different Email id's");
                return;
            }

            int user1 = 0;
            int user2 = 0;
            for (User user : mUsers) {
                if (user.getEmail().equals(email1))
                {
                    user1 += 1;
                }
                if(user.getEmail().equals(email2))
                {
                    user2 += 1;
                }
            }

          if(user1==0)
          {
              ToastUtil.makeLongToast(getApplicationContext(),"NO User with Email-1");
              return;
          }
          if(user2==0)
          {
              ToastUtil.makeLongToast(getApplicationContext(),"No User with Email-2");
              return ;
          }

            args.putString("email1", mEmail1TextView.getText().toString());
            args.putString("email2", mEmail2TextView.getText().toString());

            for (User user : mUsers) {
                if (user.getEmail().equals(email1)) {
                    userid1 = user.getId();
                }
                if (user.getEmail().equals(email2)) {
                    userid2 = user.getId();
                }
            }

            args.putString("userid1", userid1);
            args.putString("userid2", userid2);
//            args.putSerializable("users", mUsers);
            intent.putExtras(args);
            startActivity(intent);
        });

    }

    private void readUsers() {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();

                Log.i("UserFragment", msg + "Datasnapshot : " + dataSnapshot + msg);

                if (mSearchUsers.getText().toString().equals("")) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);

                        if (user != null && user.getId() != null ) {
                            mUsers.add(user);
                        }
                    }

                    mUserAdapter = new UserAdapter(getApplicationContext(), mUsers, false, 0);
                    mRecyclerView.setAdapter(mUserAdapter);
                } else {

                    mUserAdapter = new UserAdapter(getApplicationContext(), mUsers, false, 0);
                    mRecyclerView.setAdapter(mUserAdapter);
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


    private void Search_Users(String s) {

        s = s.toLowerCase();

        Query query = FirebaseDatabase.getInstance().getReference("users").orderByChild("search").startAt(s).endAt(s + "\uf8ff");

        Log.i("UserFragment", msg + "Query : " + query.toString() + msg);
        Log.i("UserFragment", msg + "Query : " + "\uf8ff" + msg);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);

                    assert firebaseUser != null;
                    if (user != null && !firebaseUser.getUid().toLowerCase().equals(user.getId().toLowerCase())) {
                        mUsers.add(user);
                    }
                }

                mUserAdapter = new UserAdapter(getApplicationContext(), mUsers, false, 0);
                mRecyclerView.setAdapter(mUserAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    Boolean verifyEmail(String email) {

        int ok = 1;

        if (!email.contains("@gmail.com")) {
            ok = 0;
        }

        if (email.contains((" "))) {
            ok = 0;
        }

        String[] sarr = email.split("@gmail.com");

        if (sarr.length != 1) {
            ok = 0;
        }

        if (ok == 0) {
            ToastUtil.makeLongToast(getApplicationContext(), "Please Enter a Valid Email");
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

}
