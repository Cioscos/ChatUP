package com.example.chatup.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.print.PrintAttributes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.chatup.R;
import com.example.chatup.model.Messaggio;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder> {
    private Activity mActivity;
    private DatabaseReference mDataBaseReference;
    private String mDisplayName;
    private ArrayList<DataSnapshot> mDataSnapshot;

    private ChildEventListener mListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            mDataSnapshot.add(dataSnapshot);
            notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    public ChatListAdapter(Activity activity, DatabaseReference ref, String name) {

        mActivity = activity;
        mDataBaseReference = ref.child("messaggi");
        mDisplayName = name;

        mDataSnapshot = new ArrayList<>();
        mDataBaseReference.addChildEventListener(mListener);
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView autore, messaggio;
        LinearLayout.LayoutParams params;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            autore = itemView.findViewById(R.id.tv_autore);
            messaggio = itemView.findViewById(R.id.tv_messaggio);
            params = (LinearLayout.LayoutParams) autore.getLayoutParams();

            messaggio.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    String [] opzioni = new String[2];
                    opzioni[0] = "Elimina messaggio";

                    new AlertDialog.Builder(v.getContext())
                            .setItems(opzioni, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //TODO: Completa le azioni da svolgere
                                }
                            });
                    return false;
                }
            });
        }
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        LayoutInflater inflater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.chat_msg_row, viewGroup, false);
        ChatViewHolder vh = new ChatViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder chatViewHolder, int i) {

        DataSnapshot snapshot = mDataSnapshot.get(i);
        Messaggio msg = snapshot.getValue(Messaggio.class);

        chatViewHolder.autore.setText(msg.getAutore());
        chatViewHolder.messaggio.setText(msg.getMessaggio());

        Boolean mineMsg = msg.getAutore().equals(mDisplayName);
        setChatItemStyle(mineMsg, chatViewHolder);
    }

    private void setChatItemStyle(Boolean mineMsg, ChatViewHolder holder) {

        if (mineMsg) {
            holder.params.gravity = Gravity.END;
            holder.autore.setTextColor(Color.BLUE);
            holder.messaggio.setBackgroundResource(R.drawable.in_msg_bg);
            holder.params.setMarginEnd(10);
        } else {
            holder.params.gravity = Gravity.START;
            holder.params.setMarginStart(10);
            holder.autore.setTextColor(Color.MAGENTA);
            holder.messaggio.setBackgroundResource(R.drawable.out_msg_bg);
        }
        holder.autore.setLayoutParams(holder.params);
        holder.messaggio.setLayoutParams(holder.params);
    }

    @Override
    public int getItemCount() {
        return mDataSnapshot.size();
    }

    public void clean() {
        mDataBaseReference.removeEventListener(mListener);
    }
}
