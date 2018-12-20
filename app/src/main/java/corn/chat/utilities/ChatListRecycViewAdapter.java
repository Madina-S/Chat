package corn.chat.utilities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import corn.chat.R;
import corn.chat.model.ChatItem;
import corn.chat.model.User;

import java.util.ArrayList;

public class ChatListRecycViewAdapter extends RecyclerView.Adapter<ChatListRecycViewAdapter.ChatViewHolder> {

    private Context context;
    private ArrayList<ChatItem> chats;
    private OnItemClickListener listener;

    public ChatListRecycViewAdapter(Context context, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void update(ArrayList<ChatItem> chats){
        this.chats = chats;
        notifyDataSetChanged();
    }

    // TODO: 11/12/18 update the entry if the new message is got
    public void update(User user){
        for (int i = 0; i < chats.size(); i++){
            ChatItem chat = chats.get(i);
            String user2Login = chat.getChatMember();
            chat.setLastMessage(user.getLastMessage(user2Login));
        }

        notifyDataSetChanged();
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_item, parent, false);

        return new ChatViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        ChatItem chat = chats.get(position);

        holder.initialTextView.setText(chat.getChatMemberInitial());
        holder.userNameTextView.setText(chat.getChatMember());
        holder.lastMessageTextView.setText(chat.getLastMessage());
    }

    @Override
    public int getItemCount() {
        if(chats == null)
            return 0;

        return chats.size();
    }

    class ChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView initialTextView;
        private TextView userNameTextView;
        private TextView lastMessageTextView;

        public ChatViewHolder(View itemView) {
            super(itemView);
            
            this.initialTextView = itemView.findViewById(R.id.initial_text_view);
            this.userNameTextView = itemView.findViewById(R.id.chat_user_name);
            this.lastMessageTextView = itemView.findViewById(R.id.chat_last_message);

            itemView.setOnClickListener(this);
        }

        // TODO: 12/12/18 change the color on the click
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            ChatItem chat = chats.get(position);
            listener.onClick(chat);
        }
    }
}
