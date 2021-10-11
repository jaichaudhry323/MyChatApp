package org.o7planning.mychatapp.ADMIN.MaintainUsers;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.o7planning.mychatapp.Model.User;
import org.o7planning.mychatapp.R;
import org.o7planning.mychatapp.Utils.Global;
import org.o7planning.mychatapp.Utils.NetworkUtil;
import org.o7planning.mychatapp.Utils.ToastUtil;

import java.util.HashMap;

public class AddUserActivity extends AppCompatActivity {

    TextView mEmailIdTextView;
    TextView mUserName;
    Button mAddUserButton;

//    Arra

    private FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    public void onCreate(Bundle B) {
        super.onCreate(B);
        setContentView(R.layout.activity_1_add_user);

        mEmailIdTextView = findViewById(R.id.email);
        mUserName = findViewById(R.id.username);
        mAddUserButton = findViewById(R.id.add_user_button);

        NetworkUtil.setConnectivityTracking(getBaseContext());

        mAddUserButton.setOnClickListener(v -> {

            String email = mEmailIdTextView.getText().toString();
//            String username = mUserName.getText().toString();
            String usernameFromGmail = email.split("@gmail.com")[0];

            if (usernameFromGmail.equals("")) {
                ToastUtil.makeLongToast(getApplicationContext(), "Invalid Gmail");
            }
            if (!verifyEmail(email)) {
                return;
            }
            if (!NetworkUtil.getConnectivityStatus(getBaseContext())) {
                ToastUtil.makeLongToast(getApplicationContext(), "Check Your Network Connection");
                return;
            }

            addUser(email, "Abcd@dtu1", usernameFromGmail);
        });

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                // Set the User , Chat and Profile Fragment

            }
        }, 1000);

    }


    public void addUser(String email, String password, String username) {

        mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

//                            ADD THE USER TO DATABASE
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();   // user has email , password and an id
                            assert firebaseUser != null;
                            String userId = firebaseUser.getUid();

                            databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", userId);
                            hashMap.put("username", username);
                            hashMap.put("imageurl", "default");
                            hashMap.put("status", "offline");
                            hashMap.put("search", username.toLowerCase());
                            hashMap.put("email", email.toLowerCase());


                            FirebaseAuth.getInstance().signOut();
                            signin();

                            databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        ToastUtil.makeLongToast(getApplicationContext(), "Successfully added to Database");

                                        String finalEmail = email;
                                        Global.getInstance().removeEmail(finalEmail);
                                    } else {
                                        ToastUtil.makeLongToast(getApplicationContext(), "Database insertion failure");
                                    }
                                }
                            });


                        } else {
                            ToastUtil.makeLongToast(getApplicationContext(), "Success, Please use Reset Password Now");
                        }
                    }
                });
    }

    // Not usefull since i don't know the email and password of a user with already registered email
    void signinsignout(String email, String password, String username) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = mAuth.getCurrentUser();
                            String userId = user.getUid();

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
                                        ToastUtil.makeLongToast(getApplicationContext(), "Successfully added to Database");
                                    } else {
                                        ToastUtil.makeLongToast(getApplicationContext(), "Database insertion failure");
                                    }
                                }

                            });

                            FirebaseAuth.getInstance().signOut();
                            signin();
                            ToastUtil.makeLongToast(getApplicationContext(), "Email is already registered, Please use Reset Password");


                        } else {
                            // If sign in fails, display a message to the user.
                            ToastUtil.makeLongToast(getApplicationContext(), "Please check your email or password");
                        }
                    }
                });
    }

    void signin() {
        String email = Global.getInstance().getEmail();
        String password = Global.getInstance().getPassword();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
//                            ToastUtil.makeLongToast(getApplicationContext(), "Relogin SUccess");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
//                            ToastUtil.makeLongToast(getApplicationContext(), "Relogin Failed");
                        }
                    }
                });
    }

    Boolean verifyEmail(String email) {
        int ok = 1;

        if (!email.contains("@gmail.com")) {
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


//    void check_and_insert(String email)
//    {
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("status", status);
//        reference.updateChildren(hashMap);
//    }
//
//
//    private void readUsers() {
//
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
//
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

//                mUsers.clear();
//                readusers = Boolean.TRUE;
//
//                Log.i("UserFragment", msg + "Datasnapshot : " + dataSnapshot + msg);
//
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    User user = snapshot.getValue(User.class);
//
//                    if (user != null && user.getId() != null ) {
//                        mUsers.add(user);
//                    }
//                }

//                ToastUtil.makeLongToast(getApplicationContext(),"1");
//                ToastUtil.makeLongToast(getApplicationContext(),"Reading Users");
//                ToastUtil.makeLongToast(getApplicationContext(), "Num of Users: "+Integer.toString(mUsers.size()));
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//    }


}

