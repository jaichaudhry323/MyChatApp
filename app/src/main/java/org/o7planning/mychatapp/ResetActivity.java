package org.o7planning.mychatapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetActivity extends AppCompatActivity {

    TextView mEmailTextView;
    Button mResetButton;
    FirebaseAuth mAuth;

    public void onCreate(Bundle B) {
        super.onCreate(B);
        setContentView(R.layout.activity_reset);

        mResetButton = findViewById(R.id.reset_button);
        mEmailTextView = findViewById(R.id.send_email);

        mAuth = FirebaseAuth.getInstance();

        mResetButton.setOnClickListener(v -> {

            String email = mEmailTextView.getText().toString();

            if (email == "") {
                Toast.makeText(getApplicationContext(), "Field cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ResetActivity.this, "Please check your email", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ResetActivity.this, LoginActivity.class));
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(ResetActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        });

    }
}
