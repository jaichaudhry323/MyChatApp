package org.o7planning.mychatapp;

import android.content.Intent;
import android.content.SharedPreferences;
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
import org.o7planning.mychatapp.Utils.Global;
import org.o7planning.mychatapp.Utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    Button mloginButton;
    TextView mEmailTextView;
    TextView mPasswordTextView;
    TextView mForgotPassword;
    private FirebaseAuth mAuth;

    String msg = " \n n \n n \n";
    ArrayList<User> mUsers;
    Boolean readusers;

    public void onCreate(Bundle B) {
        super.onCreate(B);
        setContentView(R.layout.activity_login);

        mloginButton = findViewById(R.id.login_button);
        mEmailTextView = findViewById(R.id.email);
        mPasswordTextView = findViewById(R.id.password);
        mForgotPassword = findViewById(R.id.forgot_password);

        mUsers = new ArrayList<>();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        mForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ResetActivity.class);
            startActivity(intent);
        });

        readUsers();

        mloginButton.setOnClickListener(v -> {
            String email = mEmailTextView.getText().toString();
            String password = mPasswordTextView.getText().toString();

            Log.i("LoginActivity", msg + email + "\n" + password + msg);

            ///////////////////////////////////////
            int invalid = 0;
            for(String e:Global.getInstance().getInvalidEmails())
            {
                if(email.equals(e))
                {
                    invalid=1;
                }
            }

            if(invalid==1)
            {
                ToastUtil.makeLongToast(getApplicationContext(),"You Are Not Registered");
                return;
            }

            ///////////////////////////////////////

            if (!verifyEmail(email)) {
                return;
            }
            if (!verifyPassword(password)) {
                return;
            }

            if (!readusers) {
                ToastUtil.makeLongToast(getApplicationContext(), "Please Try Again");
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                ToastUtil.makeLongToast(getApplicationContext(), "Login Success");

                                Boolean present = Boolean.FALSE;
                                for (User user : mUsers) {
                                    if (email.equals(user.getEmail())) {
                                        present = Boolean.TRUE;
                                    }
                                }

                                if (!present) {
//                                    ToastUtil.makeLongToast(getApplicationContext(), "User wasnt present in DB");
                                    enterIntoDB();
                                } else {
//                                    ToastUtil.makeLongToast(getApplicationContext(), "User Present in DB");
                                }

                                // Sign in success, update UI with the signed-in user's information
                                Log.d("LoginActivity", "signInWithEmail:success");

//                                ToastUtil.makeShortToast(getApplicationContext(), "SUCCESS");
                                final Handler handler = new Handler(Looper.getMainLooper());
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        //Do something after 100ms
                                        // Set the User , Chat and Profile Fragment
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        // clears the stack trace , task means stack (of activities obviously)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);   // FLAG_ACTIVITY_CLEAR_TOP too worked but what is written is better as it doesnt reproduce the main page again on back button click
                                        startActivity(intent);
                                    }
                                }, 2000);

                                FirebaseUser user = mAuth.getCurrentUser();

                                Global.getInstance().setEmail(email);
                                Global.getInstance().setPassword(password);
                                SharedPreferences.Editor editor = getSharedPreferences("MyPreference", MODE_PRIVATE).edit();
                                editor.putString("email", email);
                                editor.putString("password", password);
                                editor.apply();

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("LoginActivity", "signInWithEmail:failure", task.getException());

                                ToastUtil.makeLongToast(getApplicationContext(), "Please check your email or password");
//                                updateUI(null);
                            }
                            // ...
                        }
                    });
        });
    }

    void enterIntoDB() {

//        if (mUsers.size() == 0) {
//            ToastUtil.makeLongToast(getApplicationContext(),"Returning from enterintoDB");
//            return;
//        }

//        ToastUtil.makeLongToast(getApplicationContext(), " enterintoDB Function Running");

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        assert firebaseUser != null;
        String userId = firebaseUser.getUid();
        String email = firebaseUser.getEmail();
        assert email != null;
        String username = email.split("@gmail.com")[0];

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("id", userId);
        hashMap.put("username", username);
        hashMap.put("imageurl", "default");
        hashMap.put("status", "offline");
        hashMap.put("search", username.toLowerCase());
        hashMap.put("email", email);

        databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
//                    ToastUtil.makeLongToast(getApplicationContext(), "Successfully added to Database.");

                } else {
//                    ToastUtil.makeLongToast(getApplicationContext(), "Database insertion failure");
                }
            }
        });
    }

    private void readUsers() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

        databaseReference.addValueEventListener(new ValueEventListener() {@Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mUsers.clear();
                readusers = Boolean.TRUE;

                Log.i("UserFragment", msg + "Datasnapshot : " + dataSnapshot + msg);

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);

                    if (user != null && user.getId() != null ) {
                        mUsers.add(user);
                    }
                }

//                ToastUtil.makeLongToast(getApplicationContext(),"1");
//                ToastUtil.makeLongToast(getApplicationContext(),"Reading Users");
//                ToastUtil.makeLongToast(getApplicationContext(), "Num of Users: "+Integer.toString(mUsers.size()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    Boolean verifyPassword(String password) {

        int ok = 1;
        int a = 0, A = 0, num = 0;

        if (password.contains(" ")) {
            ToastUtil.makeLongToast(getApplicationContext(), "Error password contains WhiteSpace");
            ok = 0;
            return Boolean.FALSE;
        }

        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);

            if (c >= 48 && c <= 57) {
                num++;
            } else if (c >= 65 && c <= 90) {
                A++;
            } else if (c >= 97 && c <= 122) {
                a++;
            }
        }

        if (a == 0) {
            ToastUtil.makeLongToast(getApplicationContext(), "Password should contain atleast 1 small case character");
            ok = 0;
            return Boolean.FALSE;
        }
        if (A == 0) {
            ToastUtil.makeLongToast(getApplicationContext(), "Password should contain atleast 1 upper case character");
            ok = 0;
            return Boolean.FALSE;
        }
        if (num == 0) {
            ToastUtil.makeLongToast(getApplicationContext(), "Password should contain atleast 1 number");
            ok = 0;
            return Boolean.FALSE;
        }

        if (a + A + num == password.length()) {
            ToastUtil.makeLongToast(getApplicationContext(), "Password should contain atleast 1 special character");
            ok = 0;
            return Boolean.FALSE;
        }


        if (ok == 0) {
            return Boolean.FALSE;
        }

        if (password.length() > 15 || password.length() < 8) {
            ToastUtil.makeLongToast(getApplicationContext(), "Password Length must be between 8-15");
            return Boolean.FALSE;
        }


        return Boolean.TRUE;
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
