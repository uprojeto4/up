package br.ufc.quixada.up.Activities;

import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.ufc.quixada.up.Adapters.ChatAdapter;
import br.ufc.quixada.up.DAO.FirebaseConfig;
import br.ufc.quixada.up.Models.Message;
import br.ufc.quixada.up.R;

public class ChatActivity extends AppCompatActivity {

    private DatabaseReference reference;
    private RecyclerView recyclerView;
    private EditText messageInput;
    private Button buttonSend;
    private List messages = new ArrayList<Message>();
    private ChatAdapter adapter;
    private String mId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        messageInput = findViewById(R.id.editTextMessageInput);
        buttonSend = findViewById(R.id.buttonSend);
        reference = FirebaseConfig.getDatabase();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewConversation);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatAdapter(messages);
        recyclerView.setAdapter(adapter);

        buttonSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                reference.child("messages").push().setValue(new Message(messageInput.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getDisplayName()));
                messageInput.setText("");
            }
        });

        reference.child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot messageDataSnapshot : dataSnapshot.getChildren()) {
                    Message message = messageDataSnapshot.getValue(Message.class);
                    messages.add(message);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        reference.child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    try {
                        Message message = dataSnapshot.getValue(Message.class);
                        messages.add(message);
                        recyclerView.scrollToPosition(messages.size() - 1);
                        adapter.notifyItemInserted(messages.size() - 1);
                    } catch (Exception ex) {
                        Log.e("pff", ex.getMessage());
                    }

                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
