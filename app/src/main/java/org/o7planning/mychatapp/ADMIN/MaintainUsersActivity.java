package org.o7planning.mychatapp.ADMIN;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import org.o7planning.mychatapp.ADMIN.MaintainUsers.AddUserActivity;
import org.o7planning.mychatapp.ADMIN.MaintainUsers.DeleteUserActivity;
import org.o7planning.mychatapp.ADMIN.MaintainUsers.UpdateUserActivity;
import org.o7planning.mychatapp.R;

public class MaintainUsersActivity extends AppCompatActivity {
    Button mloginButton;
    TextView mEmailTextView;
    TextView mPasswordTextView;
    TextView mForgotPassword;
    private FirebaseAuth mAuth;

    Button mViewUserButton;
    Button mAddUserButton;
    Button mDeleteUserButton;
    Button mUpdateUserButton;

    public void onCreate(Bundle B) {
        super.onCreate(B);
        setContentView(R.layout.activity_maintain_users);

        mAddUserButton = findViewById(R.id.add_user_button);
        mDeleteUserButton = findViewById(R.id.delete_user_button2);
        mUpdateUserButton = findViewById(R.id.update_user_button);

        mAddUserButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddUserActivity.class);
            startActivity(intent);
        });

        mUpdateUserButton.setVisibility(View.GONE);

        mUpdateUserButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, UpdateUserActivity.class);
            startActivity(intent);
        });
        mDeleteUserButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, DeleteUserActivity.class);
            startActivity(intent);
        });

    }


}
