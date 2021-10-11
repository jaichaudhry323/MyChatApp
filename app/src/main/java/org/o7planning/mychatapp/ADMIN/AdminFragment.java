package org.o7planning.mychatapp.ADMIN;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import org.o7planning.mychatapp.ADMIN.MaintainChats.SelectChatsActivity;
import org.o7planning.mychatapp.Model.User;
import org.o7planning.mychatapp.R;

import java.util.List;

public class AdminFragment extends Fragment {

    // we need to display the users with which current user has had chat
    // we need a list , a recyclerview object
    // then we need an adapter instance, then firebase user and Database reference for on valuechangeListener

    RecyclerView mRecyclerView;
    List<String> userlist;
    List<User> mChatList;

    FirebaseUser firebaseUser;
    DatabaseReference chatDatabaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View fragment = inflater.inflate(R.layout.fragment_admin, container, false);
        Button mMaintainUsersButton = fragment.findViewById(R.id.maintain_users_button);
        Button mMaintainChatsButton = fragment.findViewById(R.id.maintain_chats_button);

        mMaintainChatsButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SelectChatsActivity.class);
            startActivity(intent);
        });

        mMaintainUsersButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MaintainUsersActivity.class);
            startActivity(intent);
        });


        return fragment;
    }
}