package org.o7planning.mychatapp.Adapters;

import android.content.Context;
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

import org.o7planning.mychatapp.Model.Chat;
import org.o7planning.mychatapp.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    public final int MSG_TYPE_LEFT=0;
    public final int MSG_TYPE_RIGHT=1;

    Context mContext;
    List<Chat> mChat;
    String mImageUrl;
    FirebaseUser firebaseUser;

    // get the imgurl of the other person
    public MessageAdapter(Context context,List<Chat>chat,String ImgUrl)
    {
        mChat=chat;
        mImageUrl=ImgUrl;
        mContext=context;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView show_message;
        public ImageView profile_image;
        public TextView txt_seen;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
//            profile_image=itemView.findViewById(R.id.profile_image);
            show_message=itemView.findViewById(R.id.show_message);
            txt_seen=itemView.findViewById(R.id.txt_seen);
        }
    }

    @NonNull
    @Override
    // WE GET VIEW TYPE FROM A FUNCTION THAT IS OVERRIDEN BELOW THIS BELOW CODE
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new MessageViewHolder(view);
        }else{
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new MessageViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        // Each chat text has a sender and a receiver string
        // if the current chat's receiver == current user then left align the chat text
        // in other words if the chat's sender ==current user then we right align the chat
       FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        if(mChat.get(position).getSender().equals(firebaseUser.getUid()))
        {
            return MSG_TYPE_RIGHT;
        }
        return MSG_TYPE_LEFT;        // or else return this
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {

        Chat chat = mChat.get(position);

        // set message of this holder
        holder.show_message.setText(chat.getMessage());

        // load profile image
        if(mImageUrl.equals("default")) {
//            holder.profile_image.setImageResource(R.drawable.ic_launcher_foreground);
        }
        else{
//            Glide.with(mContext).load(mImageUrl).into(holder.profile_image);
        }

        // Now for the last message ,
        if(position==mChat.size()-1) {
            if(chat.isIsseen())
            {
                holder.txt_seen.setText("seen");
            }
            else{
                holder.txt_seen.setText("Delivered");
            }
        }
        else{
            holder.txt_seen.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }
}
