package br.ufc.quixada.up.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import br.ufc.quixada.up.Models.Negociacao;
import br.ufc.quixada.up.Models.User;
import br.ufc.quixada.up.R;
import br.ufc.quixada.up.Utils.ChatControl;
import br.ufc.quixada.up.Utils.DateTimeControl;

public class ChatActivity extends BaseActivity {

    private DatabaseReference dbReference;
    private RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    private EditText messageInput;
    private ChatAdapter chatAdapter;
    private String chatId;
    private String userId;
    private String remoteUserId;
    private String adId;
    private int unreadMessagesCounter;
    private TextView textViewTitleAnuncioChat;
    private TextView textViewVendedorAnuncioChat;
    private TextView textViewDataCadastroAnuncioChat;
    private LinearLayout noMessagesLayout;
    private boolean isShowingMessages = false;

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
        NegociacoesActivity.isChatActivityOpened = true;
        NegociacoesActivity.currentOpenedChatNegotiationKey = intent.getStringExtra("negotiationKey");

        userId = localUser.getId();

        textViewTitleAnuncioChat = findViewById(R.id.titleAnuncioChat);
        textViewVendedorAnuncioChat = findViewById(R.id.vendedorAnuncioChat);
        textViewDataCadastroAnuncioChat = findViewById(R.id.dataCadastroAnuncio);

        textViewTitleAnuncioChat.setText(intent.getStringExtra("adTitle"));
        textViewDataCadastroAnuncioChat.setText(DateTimeControl.formatMillisToDate(intent.getLongExtra("submitDate", 0)));

        noMessagesLayout = findViewById(R.id.noMessagesLayout);
        messageInput = findViewById(R.id.editTextMessageInput);
        Button buttonSend = findViewById(R.id.buttonSend);
        dbReference = FirebaseConfig.getDatabase();
        resolveRemoteUserName();
//        dbReference.keepSynced(true);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewConversation);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        linearLayoutManager = new LinearLayoutManager(this);
//        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        chatAdapter = new ChatAdapter(userId);
        recyclerView.setAdapter(chatAdapter);

        buttonSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!messageInput.getText().toString().isEmpty()) {
                    Message message = new Message(messageInput.getText().toString(), userId);
                    if (chatId != null) {
                        dbReference.child("messages").child(chatId).push().setValue(message);
                        dbReference.child("negotiations").child(userId).child(adId).child("lastMessage").setValue(messageInput.getText().toString());
                        dbReference.child("negotiations").child(userId).child(adId).child("lastMessageSenderId").setValue(userId);
                        unreadMessagesCounter ++;
//                        Toast.makeText(getBaseContext(), "" + unreadMessagesCounter, Toast.LENGTH_SHORT).show();
                        dbReference.child("negotiations").child(remoteUserId).child(adId).child("unreadMessagesCounter").setValue(unreadMessagesCounter);
                        dbReference.child("negotiations").child(remoteUserId).child(adId).child("lastMessage").setValue(messageInput.getText().toString());
                        dbReference.child("negotiations").child(remoteUserId).child(adId).child("lastMessageSenderId").setValue(userId);
                    } else {
                        System.out.println("userId " + userId);
                        chatId = ChatControl.startConversation(userId, remoteUserId, adId, message);
                    }
//                    chatAdapter.addMessage(message);
                    messageInput.setText("");
                    linearLayoutManager.scrollToPosition(chatAdapter.getItemCount() - 1);
                    if (!isShowingMessages) {
                        showMessages();
                    }
                }
            }
        });

        dbReference.child("negotiations").child(remoteUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(adId)) {
                    DataSnapshot negotiationSnapshot = dataSnapshot.child(adId);
                    chatId = negotiationSnapshot.child("messagesId").getValue(String.class);
                    unreadMessagesCounter = negotiationSnapshot.child("unreadMessagesCounter").getValue(Integer.class);
//                    dbReference.child("negotiations").child(userId).child(adId).child("unreadMessagesCounter").setValue(0);
                    getMessages();
                } else {
                    showNoMessageInfo();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getMessages() {
        dbReference.child("messages").child(chatId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                System.out.println(dataSnapshot);
                Message message = dataSnapshot.getValue(Message.class);
                chatAdapter.addMessage(message);
                linearLayoutManager.scrollToPosition(chatAdapter.getItemCount() - 1);
                if (chatAdapter.getItemCount() > 0) {
                    showMessages();
                } else {

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

    private void resolveRemoteUserName() {
        dbReference.child("users").child(remoteUserId).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot);
                User user = dataSnapshot.getValue(User.class);
                textViewVendedorAnuncioChat.setText(user.getNome());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showMessages(){
        recyclerView.setVisibility(View.VISIBLE);
        noMessagesLayout.setVisibility(View.GONE);
        this.isShowingMessages = true;
    }

    private void showNoMessageInfo(){
        recyclerView.setVisibility(View.GONE);
        noMessagesLayout.setVisibility(View.VISIBLE);
        this.isShowingMessages = false;
    }

    private void emptyMessagesCounter() {
        unreadMessagesCounter = 0;
        dbReference.child("negotiations").child(userId).child(adId).child("unreadMessagesCounter").setValue(0);
    }

    @Override
    public void onStart() {
        super.onStart();
        emptyMessagesCounter();
    }

    @Override
    public void onStop() {
        super.onStop();
        emptyMessagesCounter();
        NegociacoesActivity.isChatActivityOpened = false;
        NegociacoesActivity.currentOpenedChatNegotiationKey = "";
    }
}
