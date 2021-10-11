package org.o7planning.mychatapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.o7planning.mychatapp.ADMIN.AdminFragment;
import org.o7planning.mychatapp.Fragments.ChatsFragment;
import org.o7planning.mychatapp.Fragments.ProfileFragment;
import org.o7planning.mychatapp.Fragments.UsersFragment;
import org.o7planning.mychatapp.Model.User;
import org.o7planning.mychatapp.Utils.Global;
import org.o7planning.mychatapp.Utils.NetworkUtil;
import org.o7planning.mychatapp.Utils.SnackbarUtil;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    CircleImageView mProfileImage;
    TextView mUserNameTextView;
    TabLayout mTabLayout;
    ViewPager mViewPager;
    Button mLogoutButton2;
    CoordinatorLayout mCoordinatorLayout;

    FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    String msg = " \n n \n n \n";
    String username = "";

    public void print(String s) {
        Log.i("MainActivity", msg + s + msg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProfileImage = findViewById(R.id.profile_image);
        mUserNameTextView = findViewById(R.id.username);
        mViewPager = findViewById(R.id.view_pager);
        mTabLayout = findViewById(R.id.tab_layout);
        mLogoutButton2 = findViewById(R.id.logout_button2);
        mCoordinatorLayout = findViewById(R.id.coordinator_main);

//        mLogoutButton2.setVisibility(View.GONE);

        mLogoutButton2.setOnClickListener(v -> {
            signout();
        });

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        final Handler handler = new Handler(Looper.getMainLooper());
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                //Do something after 100ms
//                // Set the User , Chat and Profile Fragment
//                SetupHomePageDetails();
//            }
//        }, 1000);

        NetworkUtil.setConnectivityTracking(getApplicationContext());
        if (!NetworkUtil.getConnectivityStatus(getApplicationContext())) {
            SnackbarUtil.makeLongSnack(mCoordinatorLayout, "Please Check Your Internet Connection");
        }

        SetupHomePageDetails();

        // get an instance of authorization
        mAuth = FirebaseAuth.getInstance();

        SharedPreferences prefs = getSharedPreferences("MyPreference", MODE_PRIVATE);
        String email = prefs.getString("email", "IDK"); //"Blank Name" the default value.
        String password = prefs.getString("password", "IDK");

        Global.getInstance().setPassword(password);
        Global.getInstance().setEmail(email);

//        ToastUtil.makeLongToast(getApplicationContext(),"Loaded data from sharedPreferences");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // I get data snapshot of the dataReference location
                // So i can access all the parameters of the current user since the current location here is that of current user
                if (!dataSnapshot.exists()) {
//                    ToastUtil.makeLongToast(getApplicationContext(), "DataSnapshot Doesn't Exist");
                    return;
                }
                User user = dataSnapshot.getValue(User.class);
                if (user == null) {
//                    ToastUtil.makeLongToast(getApplicationContext(), "user == null ");
                    return;
                }

                Log.i("MainActivity", msg + user.getEmail() + msg);
                String imageurl = user.getImageURL();
//                ToastUtil.makeLongToast(getApplicationContext(),"Username:" + user.getUsername());
//                ToastUtil.makeLongToast(getApplicationContext(),"Username:" + user.getId());
                mUserNameTextView.setText(user.getUsername().trim());

                ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
                viewPagerAdapter.addFragment(new ChatsFragment(), "chats");
                viewPagerAdapter.addFragment(new UsersFragment(), "Users");
                viewPagerAdapter.addFragment(new ProfileFragment(), "Profile");

                mViewPager.setAdapter(viewPagerAdapter);
                mTabLayout.setupWithViewPager(mViewPager);

//                createAlert(getApplicationContext(),"Working");
                username = user.getUsername();
                if (username.equals("admin")) {
                    viewPagerAdapter.addFragment(new AdminFragment(), "ADMIN");
                }
                viewPagerAdapter.notifyDataSetChanged();

                print(username);

                if (imageurl != null) {
                    if (imageurl.equals("default")) {
                        mProfileImage.setImageResource(R.drawable.ic_launcher_background);
                    } else {
                        Glide.with(getApplicationContext()).load(imageurl).into(mProfileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        NetworkUtil.getMutableLiveDataNetworkStatus().observe(this, (internet) -> {
            if (internet) {
                status("online");
            } else {
                status("offline");
            }
        });

    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        // to set the titles of the fragments we need the below function

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    private void signout() {

        Intent intent = new Intent(MainActivity.this, StartActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);   // FLAG_ACTIVITY_CLEAR_TOP too worked but what is written is better as it doesnt reproduce the main page again on back button click

        // signout called so that now in start activity the FirebaseUser is null ,
        // so automatic login doesnt take place
        FirebaseAuth.getInstance().signOut();
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.logout:
//                FirebaseAuth.getInstance().signOut();
//                startActivity(new Intent(MainActivity.this, StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//                finish();
                LogOutAlert();
                return true;
        }
        return false;
    }

    void LogOutAlert() {
        AlertDialog exitDialog = new AlertDialog.Builder(this).setCancelable(false).create();
        exitDialog.setMessage("LOGOUT");
        exitDialog.setButton(DialogInterface.BUTTON_POSITIVE, "YES", (dialog, which) -> {
            FirebaseAuth.getInstance().signOut();
            Intent logoutIntent = new Intent(MainActivity.this, StartActivity.class);
            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(logoutIntent);
            finish();
        });

        exitDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "CANCEL", (dialog, which) -> {
            exitDialog.dismiss();
        });

        exitDialog.show();
    }

    void SetupHomePageDetails() {

//        // get an instance of authorization
//        mAuth = FirebaseAuth.getInstance();
//
//        SharedPreferences prefs = getSharedPreferences("MyPreference", MODE_PRIVATE);
//        String email = prefs.getString("email", "IDK"); //"Blank Name" the default value.
//        String password = prefs.getString("password", "IDK");
//
//        Global.getInstance().setPassword(password);
//        Global.getInstance().setEmail(email);
//
////        ToastUtil.makeLongToast(getApplicationContext(),"Loaded data from sharedPreferences");
//
//        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid());
//
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                // I get data snapshot of the dataReference location
//                // So i can access all the parameters of the current user since the current location here is that of current user
//                if (!dataSnapshot.exists()) {
//                    ToastUtil.makeLongToast(getApplicationContext(), "DataSnapshot Doesn't Exist");
//                    return;
//                }
//                User user = dataSnapshot.getValue(User.class);
//                if (user == null) {
//                    ToastUtil.makeLongToast(getApplicationContext(), "user == null ");
//                    return;
//                }
//
//                Log.i("MainActivity", msg + user.getEmail() + msg);
//                username = user.getUsername();
//                String imageurl = user.getImageURL();
//                mUserName.setText(user.getUsername());
//
//                ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
//                viewPagerAdapter.addFragment(new ChatsFragment(), "chats");
//                viewPagerAdapter.addFragment(new UsersFragment(), "Users");
//                viewPagerAdapter.addFragment(new ProfileFragment(), "Profile");
//
////                createAlert(getApplicationContext(),"Working");
//
//                if (username.equals("admin")) {
//                    viewPagerAdapter.addFragment(new AdminFragment(), "ADMIN");
//                }
//
//                print(username);
//
//                mViewPager.setAdapter(viewPagerAdapter);
//                mTabLayout.setupWithViewPager(mViewPager);
//
//
//                if (imageurl != null) {
//                    if (imageurl.equals("default")) {
//                        mProfileImage.setImageResource(R.drawable.ic_launcher_background);
//                    } else {
//                        Glide.with(getApplicationContext()).load(imageurl).into(mProfileImage);
//                    }
//                }
//            }

//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

//        final Handler handler = new Handler(Looper.getMainLooper());
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                //Do something after 100ms
//                // Set the User , Chat and Profile Fragment
//
//            }
//        }, 1000);
//
//

    }

    private void status(String status) {
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                // Set the User , Chat and Profile Fragment
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("status", status);
                reference.updateChildren(hashMap);
            }
        }, 1500);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        status("offline");
    }

    @Override
    protected void onStart() {
        super.onStart();
        status("online");
    }

}