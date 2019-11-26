package com.aahlad.thismessagingservice.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.aahlad.thismessagingservice.MessageActivity;
import com.aahlad.thismessagingservice.Model.Conversation;
import com.aahlad.thismessagingservice.R;

import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {
    private Context mContext;
    private List<Conversation> mConversations;

    public ConversationAdapter(Context mContext, List<Conversation> mConversations) {
        this.mConversations = mConversations;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ConversationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.conversation_item, parent, false);
        return new ConversationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationAdapter.ViewHolder holder, int position) {
        final Conversation conversation = mConversations.get(position);
        // Set text
        holder.title.setText(conversation.getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                System.out.println(conversation.getId());
                intent.putExtra("conversationID", conversation.getId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mConversations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title;

        public ViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.conversation_title);
        }
    }
}
