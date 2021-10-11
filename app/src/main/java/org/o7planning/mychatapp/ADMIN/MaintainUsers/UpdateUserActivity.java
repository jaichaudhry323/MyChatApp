package org.o7planning.mychatapp.ADMIN.MaintainUsers;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.o7planning.mychatapp.Model.User;
import org.o7planning.mychatapp.R;
import org.o7planning.mychatapp.Utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class UpdateUserActivity extends AppCompatActivity {

    Button mUpdateUserButton;
    TextView mNewEmailTextView;
    TextView mNOldmailTextView;
    private FirebaseAuth mAuth;

    public void onCreate(Bundle B) {
        super.onCreate(B);
        setContentView(R.layout.activity_1_update_user);

        mUpdateUserButton = findViewById(R.id.update_user_button);
        mNewEmailTextView = findViewById(R.id.new_email_textview);
        mNOldmailTextView = findViewById(R.id.old_email_textview);

        mUpdateUserButton.setOnClickListener(v -> {
            String oldEmail = mNOldmailTextView.getText().toString();
            String newEmail = mNewEmailTextView.getText().toString();

            if (validate(newEmail) && validate(oldEmail)) {
                updateEmail(oldEmail, newEmail);
            }

        });
    }

    void updateEmail(String oldemail, String newemail) {

        Query query = FirebaseDatabase.getInstance().getReference("users");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<User> users = new ArrayList<>();

                int ok = 0;
                User olduser = new User();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    assert user != null;
                    if (user.getEmail().equals(oldemail)) {
                        ok = 1;
                        olduser = user;
                        break;
                    }
                }

                if (ok == 1) {
                    String userId = olduser.getId();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);
                    String username = olduser.getUsername();
                    String imageurl = olduser.getImageURL();
                    String search = olduser.getSearch();

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("id", userId);
                    hashMap.put("username", username);
                    hashMap.put("imageurl", imageurl);
                    hashMap.put("status", "offline");
                    hashMap.put("search", username.toLowerCase());
                    hashMap.put("email", search.toLowerCase());

                    databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                ToastUtil.makeLongToast(getApplicationContext(), "Successfully added to Database");

                            } else {
                                ToastUtil.makeLongToast(getApplicationContext(), "Database insertion failure");
                            }

                        }
                    });
                } else {
                    ToastUtil.makeLongToast(getApplicationContext(), "No Such User");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    Boolean validate(String email) {
        return Boolean.TRUE;
    }

    void adduser() {

    }

}




/*
Query query = FirebaseDatabase.getInstance().getReference("users");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<User> users = new ArrayList<>();

                int ok = 1;

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    assert user != null;
                    if(user.getEmail().equals(email))
                    {
                        ok = 0;
                        break;
                    }
                }

                if(ok == 1)
                {
                    String userId = UUID.randomUUID().toString();
                    databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("id", userId);
                    hashMap.put("username", username);
                    hashMap.put("imageurl", "default");
                    hashMap.put("status", "offline");
                    hashMap.put("search", username.toLowerCase());
                    hashMap.put("email", email.toLowerCase());

                    databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                       ToastUtil.makeLongToast(getApplicationContext(),"Successfully added to Database");

                            } else {
                                        ToastUtil.makeLongToast(getApplicationContext(),"Database insertion failure");
                            }
                        }
                    });
                }
                else
                {
                    ToastUtil.makeLongToast(getApplicationContext(),"Email Already Exists");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

* */