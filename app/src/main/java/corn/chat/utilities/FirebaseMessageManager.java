package corn.chat.utilities;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.*;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import corn.chat.model.Message;
import corn.chat.ui.ChatActivity;

public class FirebaseMessageManager {

    private DatabaseReference databaseRoot;
    private static StorageReference imageRootRef;

    private FirebaseMessageEventListener chatListener;
    private Context context;
    private boolean isRetrievalDone = false;

    private ChildEventListener childEventListener;
    private ValueEventListener valueEventListener;

    private static String TAG = FirebaseMessageManager.class.getName();

    public FirebaseMessageManager(Context context, FirebaseMessageEventListener listener) {
        databaseRoot = FirebaseDatabase.getInstance().getReference();
        imageRootRef = FirebaseStorage.getInstance().getReference().child("images");

        this.context = context;
        this.chatListener = listener;
    }

    public static StorageReference getImageRef(String imageName){
        return imageRootRef.child(imageName);
    }

    // TODO: 12/12/18 can be changed to the transaction
    public void sendMessage(String receiver, final Message message) {
        Log.d(TAG, "sending message\n\n ---------------------------------------");

        DatabaseReference chatTable = databaseRoot.child(FirebaseConfig.CHAT_KEY);

        String sender = message.getSender();

        //set message for the sender
        DatabaseReference messageRef1 = chatTable.child(sender).child(receiver).push();
        messageRef1.setValue(message, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError == null) {
                    chatListener.sentMessage(message);
                }
            }
        });

        //set message for receiver
        DatabaseReference messageRef2 = chatTable.child(receiver).child(sender).push();
        messageRef2.setValue(message);
    }

    public void registerReceiveListener(final String sender, String receiver) {
        DatabaseReference chatTable = databaseRoot.child(FirebaseConfig.CHAT_KEY);

        DatabaseReference messageRef = chatTable.child(sender).child(receiver);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "data changed");
                isRetrievalDone = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };

        messageRef.addListenerForSingleValueEvent(valueEventListener);

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "Child added " + s);
                if (s != null) {
                    if(isRetrievalDone){
                        Message message = dataSnapshot.getValue(Message.class);
                        Log.d(TAG, message.toString());
                        if(message.getSender().equals(sender)){
                            message.setSent(false);
                            Log.d(TAG, "sender sent: " + message.toString() + s);
                            chatListener.receivedMessage(message);
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "Child changed " + s);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        messageRef.addChildEventListener(childEventListener);
    }

    public void sendImage(final String sender, final String receiver, final int sequence, final Uri imageUri) {
        StorageReference imageRef = imageRootRef.child(imageUri.getLastPathSegment());
        UploadTask uploadTask = imageRef.putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.w(TAG, exception);
                Toast.makeText(context, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "successfully uploaded");
                String imageName = taskSnapshot.getMetadata().getReference().getName();
                Message message = new Message(imageName, Message.IMAGE_TYPE, sender, sequence + 1, true);
                sendMessage(receiver, message);
            }
        });

    }
}













