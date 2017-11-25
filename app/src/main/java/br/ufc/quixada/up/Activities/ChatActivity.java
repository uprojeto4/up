package br.ufc.quixada.up.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.ufc.quixada.up.Adapters.ChatAdapter;
import br.ufc.quixada.up.DAO.FirebaseConfig;
import br.ufc.quixada.up.Models.Message;

import br.ufc.quixada.up.R;
import br.ufc.quixada.up.Utils.ChatControl;

public class ChatActivity extends BaseActivity {

    private DatabaseReference dbReference;
    private RecyclerView recyclerView;
    private EditText messageInput;
//    private List messages = new ArrayList<Message>();
    private ChatAdapter chatAdapter;
    private String chatId;
    private String userId;
    private String remoteUserId;
    private String adId;

//    public static void startActivity(Context context, String)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        adId = intent.getStringExtra("adId");
        remoteUserId = intent.getStringExtra("remoteUserId");

        messageInput = findViewById(R.id.editTextMessageInput);
        Button buttonSend = findViewById(R.id.buttonSend);
        dbReference = FirebaseConfig.getDatabase();
//        dbReference.keepSynced(true);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewConversation);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter(new ArrayList<Message>());
        recyclerView.setAdapter(chatAdapter);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        }

        buttonSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!messageInput.getText().toString().isEmpty()) {
                    Message message = new Message(messageInput.getText().toString(), userId);
                    if (chatId != null) {
                        dbReference.child("messages").child(chatId).push().setValue(message);
                    } else {
                        chatId = ChatControl.startConversation(userId, remoteUserId, adId, message);
                    }
                }
                messageInput.setText("");
                
            }
        });

//        reference.child("messages").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot messageDataSnapshot : dataSnapshot.getChildren()) {
//                    Message message = messageDataSnapshot.getValue(Message.class);
//                    chatAdapter.addMessage(message);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

//        dbReference.child("messages").child(chatId).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot messageDataSnapshot : dataSnapshot.getChildren()) {
//                    Message message = messageDataSnapshot.getValue(Message.class);
//                    chatAdapter.addMessage(message);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//        dbReference.child("messages").child(chatId).addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
//                    try {
//                        Message message = dataSnapshot.getValue(Message.class);
//                        chatAdapter.addMessage(message);
////                        recyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
//                    } catch (Exception ex) {
//                        Log.e("oops", ex.getMessage());
//                    }
//
//                }
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

    }

//    private class CreateMessageNode extends AsyncTask<String, String, String> {
//
////        negotiationId = ChatControl.startConversation(userId, remoteUserId, adId, message);
//
//        protected Long doInBackground() {
//            int count = urls.length;
//            long totalSize = 0;
//            for (int i = 0; i < count; i++) {
//                totalSize += Downloader.downloadFile(urls[i]);
//                publishProgress((int) ((i / (float) count) * 100));
//                // Escape early if cancel() is called
//                if (isCancelled()) break;
//            }
//            return totalSize;
//        }
//
//        protected void onProgressUpdate(Integer... progress) {
//            setProgressPercent(progress[0]);
//        }
//
//        protected void onPostExecute(Long result) {
//            showDialog("Downloaded " + result + " bytes");
//        }
//    }
}
