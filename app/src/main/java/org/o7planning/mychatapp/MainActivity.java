package org.o7planning.mychatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import org.o7planning.mychatapp.Fragments.ChatsFragment;
import org.o7planning.mychatapp.Fragments.ProfileFragment;
import org.o7planning.mychatapp.Fragments.UsersFragment;
import org.o7planning.mychatapp.Model.User;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    Button mLogoutButton;
    CircleImageView mProfileImage;
    TextView mUserName;
    TabLayout mTabLayout;
    ViewPager mViewPager;

    FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    String msg = " \n n \n n \n";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProfileImage = findViewById(R.id.profile_image);
        mUserName = findViewById(R.id.username);
        mViewPager = findViewById(R.id.view_pager);
        mTabLayout = findViewById(R.id.tab_layout);
        mLogoutButton = findViewById(R.id.logout_button);

        mLogoutButton.setOnClickListener(v -> {
            signout();
        });

        // get an instance of authorization
        mAuth = FirebaseAuth.getInstance();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // I get data snapshot of the datareference location
                // So i can access all the parameters of the current user since the currenct location here is that of current user

                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    String imageurl = user.getImageURL();
                    mUserName.setText(user.getUsername());
                    if (imageurl != null) {
                        if (imageurl == "default") {
                            mProfileImage.setImageResource(R.drawable.ic_launcher_background);
                        } else {
                            Glide.with(getApplicationContext()).load(imageurl).into(mProfileImage);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Set the User , Chat and Profile Fragment

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new ChatsFragment(), "chats");
        viewPagerAdapter.addFragment(new UsersFragment(), "Users");
        viewPagerAdapter.addFragment(new ProfileFragment(), "Profile");
        mViewPager.setAdapter(viewPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        // Now inCase the data gets changed of some other user
        // Then it should reflect in the Parallel pages Chat and Userstoo
        // So another data Change Listener for these

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {                 //  ???
//
//        getMenuInflater().inflate(R.layout.menu, menu);
//        return true;
//
//    }

}
