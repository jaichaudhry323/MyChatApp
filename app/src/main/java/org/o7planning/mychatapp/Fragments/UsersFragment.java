package org.o7planning.mychatapp.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.o7planning.mychatapp.Adapters.UserAdapter;
import org.o7planning.mychatapp.Model.User;
import org.o7planning.mychatapp.R;

import java.util.ArrayList;

public class UsersFragment extends Fragment {

    RecyclerView mRecyclerView;
    UserAdapter mUserAdapter;
    ArrayList<User> mUsers;
    EditText mSearchUsers;

    String msg = " \n n \n n \n";

    // this fragment contains list of users in a Recycler View

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // on making attach to root 3 parameter to true app crashes due to increased stack size
        View fragment = inflater.inflate(R.layout.fragment_users, container, false);

        mRecyclerView = fragment.findViewById(R.id.recycler_view);
        mSearchUsers = fragment.findViewById(R.id.search_users);

        // Stack size exceeds problem comes with this below line thus crashing the app
//        mRecyclerView.setNestedScrollingEnabled(true);

        //  A RecyclerView needs to have a layout manager and an adapter to be instantiated.
        //  A layout manager positions item views inside a RecyclerView and determines when
        //  to reuse item views that are no longer visible to the user
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUsers = new ArrayList<User>();
        readUsers();

        mSearchUsers.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().equals(""))
                {
                    readUsers();
                }
                else{
                    Search_Users(mSearchUsers.getText().toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return fragment;
    }

    private void Search_Users(String s) {
        // here the user list will be changed according to the text typed in search user text bar

        // NOW WHAT DO WE NEED HERE ??
        // All users ok , and that should do right

        // NOTE :
        // .startAt()  .endAt() functions return something from the database of type Query
        // So we cannot use DatabaseReference to create below variable

        // apply startAt on search parameter of the values of DB
        s=s.toLowerCase();

        Query query = FirebaseDatabase.getInstance().getReference("users").orderByChild("search").startAt(s).endAt(s + "\uf8ff");

        Log.i("UserFragment", msg + "Query : " + query.toString() + msg);
        Log.i("UserFragment", msg + "Query : " + "\uf8ff" + msg);

        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
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

                mUserAdapter = new UserAdapter(getContext(), mUsers, false);
                mRecyclerView.setAdapter(mUserAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void readUsers() {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();

                Log.i("UserFragment", msg + "Datasnapshot : " + dataSnapshot + msg);

                if (mSearchUsers.getText().toString().equals("")) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);

                        if (user != null && user.getId() != null && firebaseUser != null && !user.getId().equals(firebaseUser.getUid())) {
//                            Log.i("UserFragment", msg + "UserId : "+user.getId() + "\n FirebaseUserId :"+firebaseUser.getUid());
                            mUsers.add(user);
                        }

                    }
                    mUserAdapter = new UserAdapter(getContext(), mUsers, false);
                    mRecyclerView.setAdapter(mUserAdapter);
                } else {
                    mUserAdapter = new UserAdapter(getContext(), mUsers, false);
                    mRecyclerView.setAdapter(mUserAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
