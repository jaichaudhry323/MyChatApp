package org.o7planning.mychatapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.o7planning.mychatapp.MessageActivity;
import org.o7planning.mychatapp.Model.Chat;
import org.o7planning.mychatapp.Model.User;
import org.o7planning.mychatapp.R;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    Context mContext;         // ok
    List<User> mUsers;         // ok
    Boolean isChat;
    String mLastMessage;     // ok
    String msg = " \n n \n n \n";

    // we need to implement each user object as clickable plus which shows on itself last message
    // so we create a viewholder which will be put / binded in the recycler view and
    // we make it clickable and show the last message on it
    // basically we need to query the data from the cloud database for all the users in the user fragment nad pass the list here
    // this data is stored in the 'users' path
    // Pending -> img_on , img_off when online and offline thing in BindViewholder

    public UserAdapter(Context context,List<User>users,Boolean ischat)
    {
        mContext=context;
        mUsers=users;
        isChat=ischat;
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public ImageView profile_image;
        private ImageView img_on;
        private ImageView img_off;
        private TextView last_msg;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            last_msg = itemView.findViewById(R.id.last_msg);
        }
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {

        // 😁 😁 🙄 🙄   the main stuff

        User user = mUsers.get(position);

        holder.username.setText(user.getUsername());

        if (user.getImageURL().equals("default")) {
            holder.profile_image.setImageResource(R.drawable.ic_launcher_background);
        } else {
            Glide.with(mContext).load(user.getImageURL()).into(holder.profile_image);
        }

        if (isChat) {       // if there has been some chat between this user and the user currently getting appended
            // we call function to set last message
            lastmessage(user.getId(), holder.last_msg);
        } else {
            holder.last_msg.setVisibility(View.GONE);
        }

        if (isChat) {
            if (user.getStatus().equals("online")) {
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            } else {
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.VISIBLE);
            }

        } else {
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);
        }

        // How to set onClickListener on the holder itself??
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, MessageActivity.class);
            intent.putExtra("userid", user.getId());
            //NOTE : We always start activity from the context of original activity
            //startActivity()   <-- Wrong
            mContext.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    private void lastmessage(final String id,final TextView last_msg) {  // decalre as final so that we dont accidentally change it

        mLastMessage="default";

        // to display the last message we need to find what the last chat was and if none then we dont display anything
        // But then if the chat changes then we need to automatically update this last message
        // so we bring up the firebase things

        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        // Below code is wrong since we need chat DB and not the user details DB
        //  DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("chats");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // we got the data in datasnapshot , i.e all the chat
                // iterate over its subdirectory

                Log.i("UserAdapter Activity", msg + "Datasnapshot : "+dataSnapshot + msg);

                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    Chat chat=dataSnapshot1.getValue(Chat.class);


                    // see if the chat matches

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
