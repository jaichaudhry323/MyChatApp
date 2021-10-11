package org.o7planning.mychatapp.ADMIN;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import org.o7planning.mychatapp.ADMIN.MaintainChats.SelectChatsActivity;
import org.o7planning.mychatapp.R;

public class MaintainChatsActivity extends AppCompatActivity {

    Button mViewChatsButton;

    public void onCreate(Bundle B) {
        super.onCreate(B);
        setContentView(R.layout.activity_maintain_chats);
        mViewChatsButton = findViewById(R.id.view_chats_button);

        mViewChatsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, SelectChatsActivity.class);
            startActivity(intent);
        });

    }

}

