package org.o7planning.mychatapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.google.firebase.database.ValueEventListener;

import org.o7planning.mychatapp.Adapters.UserAdapter;
import org.o7planning.mychatapp.Model.Chat;
import org.o7planning.mychatapp.Model.User;
import org.o7planning.mychatapp.R;
import org.o7planning.mychatapp.Utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {

    // we need to display the users with which current user has had chat
    // we need a list , a recyclerview object
    // then we need an adapter instance, then firebase user and Database reference for on valuechangeListener

    private RecyclerView mRecyclerView;
    UserAdapter mUserAdapter;

    private List<String> userIdList;
    private List<String> userIdList2;
    private List<User> mChatList;
    private List<User> mPreviousChatList;

    private FirebaseUser firebaseUser;
    private DatabaseReference chatDatabaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View fragment = inflater.inflate(R.layout.fragment_chats, container, false);

        mRecyclerView = fragment.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userIdList = new ArrayList<>();
        mChatList = new ArrayList<>();
        userIdList2 = new ArrayList<>();
        mPreviousChatList = new ArrayList<>();

        mUserAdapter = new UserAdapter(getContext(), mChatList, true, 1);

        // we display users on basis of chat so we have chat DB here
        chatDatabaseReference = FirebaseDatabase.getInstance().getReference("chats");

        chatDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userIdList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    User user =snapshot.getValue(User.class);

                    Chat chat = snapshot.getValue(Chat.class);

                    // NOW WHAT TO DO ??

                    // If the current user is a sender or a receiver of any chat then add the other user

                    if (chat != null && firebaseUser!=null  && chat.getReceiver().toLowerCase().equals(firebaseUser.getUid().toLowerCase())) {
                        userIdList.add(chat.getSender());
                    } else if (chat != null && firebaseUser!=null && chat.getSender().toLowerCase().equals(firebaseUser.getUid().toLowerCase())) {
                        userIdList.add(chat.getReceiver());
                    }
                }
                if (userIdList.size() > 0) {
                    if (getContext() == null) {
//                        ToastUtil.makeLongToast(getContext(), "Calling ReadUsersFromChat");
                        return;
                    }
                    readUsersFromChat();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return fragment;
    }

    private void readUsersFromChat() {

        DatabaseReference userDatabaseReference = FirebaseDatabase.getInstance().getReference("users");
        userDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mChatList.clear();
//                assert dataSnapshot != null;
                if (!dataSnapshot.exists()) {
                    return;
                }

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    for (String id : userIdList) {
                        assert user != null;
                        assert user.getId() != null;
                        assert id != null;
                        if (user.getId() == null) {
//                            assert getContext()!=null;
//                            ToastUtil.makeLongToast(getContext(),"Null User : "+user.getUsername());
                            continue;
                        }
//                        if (id == null) {
//                            return;
//                        }
                        if (user.getId().equals(id)) {
                            mChatList.add(user);
                            break;
                        }
                    }
                }

                if (mChatList.size() > 0 && mPreviousChatList != mChatList) {

//                    if (getContext() != null)
//                        ToastUtil.makeLongToast(getContext(), "Read Users From Chat");

//                    mUserAdapter = new UserAdapter(getContext(), mChatList, true, 1);
                    mUserAdapter.notifyDataSetChanged();
                    mRecyclerView.setAdapter(mUserAdapter);
                    mPreviousChatList.addAll(mChatList);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
