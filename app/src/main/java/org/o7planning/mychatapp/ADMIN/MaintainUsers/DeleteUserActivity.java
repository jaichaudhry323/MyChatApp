package org.o7planning.mychatapp.ADMIN.MaintainUsers;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.o7planning.mychatapp.Adapters.UserAdapter;
import org.o7planning.mychatapp.MainActivity;
import org.o7planning.mychatapp.Model.User;
import org.o7planning.mychatapp.R;
import org.o7planning.mychatapp.StartActivity;
import org.o7planning.mychatapp.Utils.Global;
import org.o7planning.mychatapp.Utils.NetworkUtil;
import org.o7planning.mychatapp.Utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class DeleteUserActivity extends AppCompatActivity {

    TextView mEmailIdTextView;
    Button mDeleteUserButton;

    ArrayList<User> mUsers;

    String msg = "\n n \n n \n n";

    private FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    public void onCreate(Bundle B) {
        super.onCreate(B);
        setContentView(R.layout.activity_1_delete_user);

        mEmailIdTextView = findViewById(R.id.email);

        mDeleteUserButton = findViewById(R.id.delete_user_button);
        mUsers = new ArrayList<>();
        NetworkUtil.setConnectivityTracking(getBaseContext());

        mDeleteUserButton.setOnClickListener(v -> {
            String email = mEmailIdTextView.getText().toString();
            if (!verifyEmail(email)) {
                return;
            }
            deleteuser(email, "abc@dtu_1");
        });
    }

    public void deleteuser(String email, String password) {

        mAuth = FirebaseAuth.getInstance();
        email = email.toLowerCase();

        if (email.equals(Global.getInstance().getEmail())) {
            ToastUtil.makeLongToast(getApplicationContext(), "You Cannot Delete YourSelf");
            return;
        }

        if (!NetworkUtil.getConnectivityStatus(getBaseContext())) {
            ToastUtil.makeLongToast(getApplicationContext(), "Check Your Network Connection");
            return;
        }
        search_and_delete(email);
    }

    private void search_and_delete(String email) {

        email = email.toLowerCase();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Query query = FirebaseDatabase.getInstance().getReference("users").orderByChild("email").equalTo(email);

        Log.i("UserFragment", msg + "Query : " + query.toString() + msg);
        Log.i("UserFragment", msg + "Query : " + "\uf8ff" + msg);

        String finalEmail = email;
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    ToastUtil.makeShortToast(getApplicationContext(), "No Such User");
                }
                for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                    AlertDialog exitDialog = new AlertDialog.Builder(DeleteUserActivity.this).setCancelable(false).create();
                    exitDialog.setMessage("Please Confirm Deletion");
                    exitDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Delete", (dialog, which) -> {
                        appleSnapshot.getRef().removeValue();
                        ToastUtil.makeShortToast(getApplicationContext(), "Removed Successfully");
                        Global.getInstance().addEmail(finalEmail);
                    });

                    exitDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "CANCEL", (dialog, which) -> {
                        exitDialog.dismiss();
                    });
                    exitDialog.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                ToastUtil.makeShortToast(getApplicationContext(), "No Such User");
                Log.e("DeleteUserActivity", "onCancelled", databaseError.toException());
            }
        });

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);

                    if (user != null && !firebaseUser.getUid().toLowerCase().equals(user.getId().toLowerCase())) {
                        mUsers.add(user);
                    }
                }

                for (int i = 0; i < mUsers.size(); i++) {
                    Log.i("UserFragment", msg + "Query : " + mUsers.get(i).getEmail() + msg);
                }
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
