package com.example.chatup.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.chatup.R;
import com.example.chatup.model.Messaggio;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder> {
    private Activity mActivity;
    private DatabaseReference mDataBaseReference;
    private String mDisplayName;
    private ArrayList<DataSnapshot> mDataSnapshot;

    public ChatListAdapter(Activity activity, DatabaseReference ref, String name) {

        mActivity = activity;
        mDataBaseReference = ref.child("messaggi");
        mDisplayName = name;

        mDataSnapshot = new ArrayList<>();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {

        TextView autore, messaggio;
        LinearLayout.LayoutParams params;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            autore = (TextView)itemView.findViewById(R.id.tv_autore);
            messaggio = (TextView)itemView.findViewById(R.id.tv_messaggio);
            params = (LinearLayout.LayoutParams) autore.getLayoutParams();
        }
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.chat_msg_row, null, false);

        ChatViewHolder vh = new ChatViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder chatViewHolder, int i) {

        DataSnapshot snapshot = mDataSnapshot.get(i);

        Messaggio msg = snapshot.getValue(Messaggio.class);

        chatViewHolder.autore.setText(msg.getAutore());
        chatViewHolder.messaggio.setText(msg.getMessaggio());

    }

    @Override
    public int getItemCount() {
        return mDataSnapshot.size();
    }
}
