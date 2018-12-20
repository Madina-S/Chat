package corn.chat.utilities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.firebase.storage.StorageReference;
import corn.chat.R;
import corn.chat.model.Message;

import java.io.IOException;
import java.util.ArrayList;

public class ChatRecycViewAdapter extends RecyclerView.Adapter {

    private ArrayList<Message> conversation;
    private Context context;

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final int VIEW_TYPE_IMAGE_MESSAGE_SENT = 3;
    private static final int VIEW_TYPE_IMAGE_MESSAGE_RECEIVED = 4;

    private static final String TAG = ChatRecycViewAdapter.class.getName();

    public ChatRecycViewAdapter(Context context, ArrayList<Message> conversation) {
        this.context = context;
        this.conversation = conversation;
    }

    public void addMessage(Message message) {
        Log.d(TAG, "Added new message\n" + message.getMessage());
        conversation.add(message);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = conversation.get(position);

        if (message.isSent()) {
            if (message.getMessage_type() == Message.TEXT_TYPE)
                return VIEW_TYPE_MESSAGE_SENT;

            return VIEW_TYPE_IMAGE_MESSAGE_SENT;
        }

        if (message.getMessage_type() == Message.TEXT_TYPE)
            return VIEW_TYPE_MESSAGE_RECEIVED;

        return VIEW_TYPE_IMAGE_MESSAGE_RECEIVED;
    }

    // TODO: 17/12/18 apply polymorphism
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        Log.d(TAG, "view type: " + viewType);

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_sent, parent, false);
            return new MessageSentViewHolder(view);
        }

        if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_received, parent, false);
            return new MessageReceivedViewHolder(view);
        }

        if (viewType == VIEW_TYPE_IMAGE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.image_message_sent, parent, false);
            return new ImageMessageSentViewHolder(view);
        }

        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_message_received, parent, false);
        return new ImageMessageReceivedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = conversation.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((MessageSentViewHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((MessageReceivedViewHolder) holder).bind(message);
                break;
            case VIEW_TYPE_IMAGE_MESSAGE_SENT:
                ((ImageMessageSentViewHolder) holder).bind(message);
                break;
            case VIEW_TYPE_IMAGE_MESSAGE_RECEIVED:
                ((ImageMessageReceivedViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        if (conversation == null)
            return 0;

        return conversation.size();
    }

    class MessageSentViewHolder extends RecyclerView.ViewHolder {
        private TextView messageTextView;

        public MessageSentViewHolder(View itemView) {
            super(itemView);

            messageTextView = itemView.findViewById(R.id.message_body);
        }

        public void bind(Message message) {
            messageTextView.setText(message.getMessage());
        }
    }

    class ImageMessageSentViewHolder extends RecyclerView.ViewHolder {
        private ImageView messageImageView;

        public ImageMessageSentViewHolder(View itemView) {
            super(itemView);

            messageImageView = itemView.findViewById(R.id.image);
        }

        public void bind(Message message) {
            StorageReference reference = FirebaseMessageManager.getImageRef(message.getMessage());

            GlideApp.with(ChatRecycViewAdapter.this.context)
                    .load(reference)
                    .into(messageImageView);
        }
    }

    class MessageReceivedViewHolder extends RecyclerView.ViewHolder {
        private TextView messageTextView;
        private TextView initialTextView;
        private TextView nameTextView;

        public MessageReceivedViewHolder(View itemView) {
            super(itemView);

            messageTextView = itemView.findViewById(R.id.message_body);
            initialTextView = itemView.findViewById(R.id.initial_text_view);
            nameTextView = itemView.findViewById(R.id.name);
        }

        public void bind(Message message) {
            Log.d(TAG, message.getMessage());
            messageTextView.setText(message.getMessage());
            initialTextView.setText(message.getSenderInitial());
            nameTextView.setText(message.getSender());
        }
    }

    class ImageMessageReceivedViewHolder extends RecyclerView.ViewHolder {
        private ImageView messageImageView;
        private TextView initialTextView;
        private TextView nameTextView;

        public ImageMessageReceivedViewHolder(View itemView) {
            super(itemView);

            messageImageView = itemView.findViewById(R.id.image);
            initialTextView = itemView.findViewById(R.id.initial_text_view);
            nameTextView = itemView.findViewById(R.id.name);
        }

        public void bind(Message message) {
            StorageReference reference = FirebaseMessageManager.getImageRef(message.getMessage());

            GlideApp.with(ChatRecycViewAdapter.this.context)
                    .load(reference)
                    .into(messageImageView);

            initialTextView.setText(message.getSenderInitial());
            nameTextView.setText(message.getSender());
        }
    }
}
