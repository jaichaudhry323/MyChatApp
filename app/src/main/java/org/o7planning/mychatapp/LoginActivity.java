package org.o7planning.mychatapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    Button mloginButton;
    TextView mEmailTextView;
    TextView mPasswordTextView;
    TextView mForgotPassword;
    private FirebaseAuth mAuth;

    public void onCreate(Bundle B)
    {
        super.onCreate(B);
        setContentView(R.layout.activity_login);

        mloginButton=findViewById(R.id.login_button);
        mEmailTextView=findViewById(R.id.email);
        mPasswordTextView=findViewById(R.id.password);
        mForgotPassword=findViewById(R.id.forgot_password);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        mForgotPassword.setOnClickListener(v->{
            Intent intent =new Intent(LoginActivity.this,ResetActivity.class);
            startActivity(intent);
        });

        mloginButton.setOnClickListener(v->{

            String email=mEmailTextView.getText().toString();
            String password=mPasswordTextView.getText().toString();

            ///////////////////////////////////////

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("LognActivity", "signInWithEmail:success");

                                Toast.makeText(getApplicationContext(),"SUCCESS",
                                        Toast.LENGTH_SHORT).show();

                                Intent intent =new Intent(LoginActivity.this,MainActivity.class);

                                // clears the stack trace , task means stack (of activities obviously)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);   // FLAG_ACTIVITY_CLEAR_TOP too worked but what is written is better as it doesnt reproduce the main page again on back button click
                                startActivity(intent);

                                FirebaseUser user = mAuth.getCurrentUser();

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("LoginActivity", "signInWithEmail:failure", task.getException());

                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
//                                updateUI(null);
                            }

                            // ...
                        }
                    });

            ////////////////////////////////////////


        });
    }
}
