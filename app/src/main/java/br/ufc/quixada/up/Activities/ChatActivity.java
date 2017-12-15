package br.ufc.quixada.up.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.util.Arrays;

import br.ufc.quixada.up.Adapters.ChatAdapter;
import br.ufc.quixada.up.Models.Constant;
import br.ufc.quixada.up.DAO.FirebaseConfig;
import br.ufc.quixada.up.Models.Message;

import br.ufc.quixada.up.Models.Negociacao;
import br.ufc.quixada.up.Models.User;
import br.ufc.quixada.up.R;
import br.ufc.quixada.up.Utils.ChatControl;
import br.ufc.quixada.up.Utils.DateTimeControl;

public class ChatActivity extends BaseActivity implements RatingDialogListener {

    private DatabaseReference dbReference;
    private RecyclerView recyclerViewChat;
    public LinearLayoutManager linearLayoutManager;
    private EditText messageInput;
    private Button buttonSend;
    private ChatAdapter chatAdapter;
    private String messagesId;
    private String userId;
    private String remoteUserId;
    private String adId;
    private int unreadMessagesCounter = 0;
    private TextView tituloAnuncio;
    private TextView vendedorAnuncio;
    private TextView dataCadastroAnuncio;
    private TextView negociante;
    private TextView negociacaoFinalizadaLabel;
    private LinearLayout noMessagesLayout;
    private int negotiationType;
    private String negotiationKey;
    private int callerId;
    private boolean isShowingMessages = false;
    private String nomeNegociante;
    private String notificationType;

    User remoteUser;
    Negociacao negociacao;

    MenuItem finalizar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        noMessagesLayout = findViewById(R.id.noMessagesLayout);
        messageInput = findViewById(R.id.editTextMessageInput);
        buttonSend = findViewById(R.id.buttonSend);
        negociacaoFinalizadaLabel = findViewById(R.id.negociacaoFinalizadaLabel);
        dbReference = FirebaseConfig.getDatabase();
        userId = localUser.getId();

        recyclerViewChat = findViewById(R.id.recyclerViewConversation);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewChat.setLayoutManager(linearLayoutManager);
        chatAdapter = new ChatAdapter(userId);
        recyclerViewChat.setAdapter(chatAdapter);

        Intent intent = getIntent();
        adId = intent.getStringExtra("adId");
        remoteUserId = intent.getStringExtra("remoteUserId");
        negotiationType = intent.getIntExtra("negotiationType", 0);
        callerId = intent.getIntExtra("callerId", -1);
        negotiationKey = intent.getStringExtra("negotiationKey");
        notificationType = intent.getStringExtra("notificationType");




//        if (intent.hasExtra("notificationType")){
//            Log.d("Avaliacao", intent.getStringExtra("notificationType"));
//            if (intent.getStringExtra("notificationType").equals("avaliacao")){
//                Log.d("Avaliacao", "intent de avalicacao");
//            } else {
//                Log.d("Mensagem", "intent de mensagem");
//
//            }
//        }

        if (intent.hasExtra("negotiationStatus")) {
            if (intent.getIntExtra("negotiationStatus", -1) == Constant.CLOSED_NEGOTIATION) {
                buttonSend.setVisibility(View.GONE);
                messageInput.setVisibility(View.GONE);
                negociacaoFinalizadaLabel.setVisibility(View.VISIBLE);
            }
        }

        resolveRemoteUserName();

        if (callerId == Constant.CHAT_CALLER_NEGOTIATION_ADAPTER) {
            NegociacoesActivity.isChatActivityOpened = true;
            NegociacoesActivity.currentOpenedChatNegotiationKey = negotiationKey;
        }

        tituloAnuncio = findViewById(R.id.titleAnuncioChat);
        negociante = findViewById(R.id.textViewNegocianteChat);
        vendedorAnuncio = findViewById(R.id.vendedorAnuncioChat);
        dataCadastroAnuncio = findViewById(R.id.dataCadastroAnuncio);

        tituloAnuncio.setText(intent.getStringExtra("adTitle"));
        if (negotiationType == Constant.NEGOTIATION_TYPE_BUY) {
            negociante.setText(R.string.vendedor);
        } else {
            negociante.setText(R.string.comprador);
        }
        dataCadastroAnuncio.setText(DateTimeControl.formatMillisToDate(intent.getLongExtra("submitDate", 0)));

//<<<<<<< final-brendon
  //      noMessagesLayout = findViewById(R.id.noMessagesLayout);
    //    messageInput = findViewById(R.id.editTextMessageInput);
      //  Button buttonSend = findViewById(R.id.buttonSend);
       // dbReference = FirebaseConfig.getDatabase();
        //resolveRemoteUserName();
//        testActivityOpen();

        recyclerViewChat = findViewById(R.id.recyclerViewConversation);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewChat.setLayoutManager(linearLayoutManager);
        chatAdapter = new ChatAdapter(userId);
        recyclerViewChat.setAdapter(chatAdapter);

//=======
//>>>>>>> sprint-final
        tituloAnuncio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), AnuncioActivity.class);
                intent.putExtra("postId", adId);
                startActivity(intent);
            }
        });

        vendedorAnuncio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), PerfilPublicoActivity.class);
                intent.putExtra("idAnunciante", remoteUserId);
                intent.putExtra("nomeAnunciante", nomeNegociante);
                startActivity(intent);
            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!messageInput.getText().toString().isEmpty()) {
                    Message message = new Message(messageInput.getText().toString(), userId);
                    Log.d("CHAT", "Sending message from " + userId + " to " + remoteUserId);
                    if (messagesId != null) {
                        unreadMessagesCounter = 0;
                        dbReference.child("messages").child(messagesId).push().setValue(message);
                        dbReference.child("negotiations").child(userId).child(negotiationKey).child("lastMessage").setValue(messageInput.getText().toString());
                        dbReference.child("negotiations").child(userId).child(negotiationKey).child("lastMessageSenderId").setValue(userId);
                        dbReference.child("negotiations").child(userId).child(negotiationKey).child("lastMessageTime").setValue(DateTimeControl.getCurrentDateTime());
                        dbReference.child("negotiations").child(userId).child(negotiationKey).child("unreadMessagesCounter").setValue(0);
                        dbReference.child("negotiations").child(remoteUserId).child(negotiationKey).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getChildrenCount() > 0) {
                                    unreadMessagesCounter = dataSnapshot.child("unreadMessagesCounter").getValue(Integer.class);
                                    dbReference.child("negotiations").child(remoteUserId).child(negotiationKey).child("unreadMessagesCounter").setValue(unreadMessagesCounter + 1);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        dbReference.child("negotiations").child(remoteUserId).child(negotiationKey).child("lastMessage").setValue(messageInput.getText().toString());
                        dbReference.child("negotiations").child(remoteUserId).child(negotiationKey).child("lastMessageSenderId").setValue(userId);
                        dbReference.child("negotiations").child(remoteUserId).child(negotiationKey).child("lastMessageTime").setValue(DateTimeControl.getCurrentDateTime());
                    } else {
                        messagesId = ChatControl.startConversation(userId, remoteUserId, adId, message);
                        negotiationKey = ChatControl.negotiationKey;
                        System.out.println("Negotiation key: " + negotiationKey);
                    }
                    if (!isShowingMessages) {
                        getMessages();
                    }
                    messageInput.setText("");
                    linearLayoutManager.scrollToPosition(chatAdapter.getItemCount() - 1);
                }
            }
        });

        dbReference.child("negotiations").child(userId).orderByChild("adId").equalTo(adId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot negotiationSnapshot : dataSnapshot.getChildren()) {
                        Negociacao negociacao = negotiationSnapshot.getValue(Negociacao.class);
                        if (negociacao.getStatus() == Constant.NEGOTIATION_TYPE_BUY) {
                            buttonSend.setVisibility(View.GONE);
                            messageInput.setVisibility(View.GONE);
                            negociacaoFinalizadaLabel.setVisibility(View.VISIBLE);
                        }
                        if (negociacao.getRemoteUserId().equals(remoteUserId)) {
                            messagesId = negotiationSnapshot.child("messagesId").getValue(String.class);
                            negotiationKey = negotiationSnapshot.getKey();
                            getMessages();
                        }
                    }
                } else {
                    showNoMessageInfo();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        finalizar = (MenuItem) findViewById(R.id.action_finalizar_negociacao);
//
//        finalizar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem menuItem) {
//                openRate();
//                return false;
//            }
//        });

    }

    @Override
    protected void onStart() {
        super.onStart();
//        testActivityOpen();
    }

    public void testActivityOpen(){
        Intent intent2 = getIntent();
        if (intent2.hasExtra("notificationType")){
            Log.d("Avaliacao", intent2.getStringExtra("notificationType"));
            if (intent2.getStringExtra("notificationType").equals("avaliacao")){
                Log.d("Avaliacao", "intent de avalicacao");
                new AppRatingDialog.Builder()
                        .setPositiveButtonText("Avaliar")
                        .setNegativeButtonText("Cancelar")
                        .setNoteDescriptions(Arrays.asList("Muito ruim", "Ruim", "Razoável", "Bom", "Muito bom"))
                        .setDefaultRating(3)
                        .setTitle("Avalie o negociante")
                        .setStarColor(R.color.colorPrimary)
                        .setNoteDescriptionTextColor(R.color.colorPrimary)
                        .setTitleTextColor(R.color.colorPrimary)
                        .setDescriptionTextColor(R.color.colorPrimary)
//                            .setHint("Please write your comment here ...")
                        .setHintTextColor(R.color.primaryTextColor)
                        .setCommentTextColor(R.color.primaryTextColor)
                        .setHint("Deixe um comentario sobre o vendedor")
                        .setHintTextColor(R.color.primaryTextColor)
                        .setCommentBackgroundColor(R.color.colorPrimaryDark)
                        .setWindowAnimation(R.style.MyDialogFadeAnimation)
                        .create(ChatActivity.this)
                        .show();
//                dbReference.child("users").child(localUser.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        User remoteUser = dataSnapshot.getValue(User.class);
//                        System.out.println("usuarioRemoto"+remoteUser);
//                        if (remoteUser.getId().equals(remoteUserId)){
////                    Log.d("Você vai avaliar", "vendedor - " + remoteUser.getNome());
//
//                            new AppRatingDialog.Builder()
//                                    .setPositiveButtonText("Avaliar")
//                                    .setNegativeButtonText("Cancelar")
//                                    .setNoteDescriptions(Arrays.asList("Muito ruim", "Ruim", "Razoável", "Bom", "Muito bom"))
//                                    .setDefaultRating(3)
//                                    .setTitle("Avalie "+remoteUser.getNome())
//                                    .setStarColor(R.color.colorPrimary)
//                                    .setNoteDescriptionTextColor(R.color.colorPrimary)
//                                    .setTitleTextColor(R.color.colorPrimary)
//                                    .setDescriptionTextColor(R.color.colorPrimary)
////                            .setHint("Please write your comment here ...")
//                                    .setHintTextColor(R.color.primaryTextColor)
//                                    .setCommentTextColor(R.color.primaryTextColor)
//                                    .setHint("Deixe um comentario sobre o vendedor")
//                                    .setHintTextColor(R.color.primaryTextColor)
//                                    .setCommentBackgroundColor(R.color.colorPrimaryDark)
//                                    .setWindowAnimation(R.style.MyDialogFadeAnimation)
//                                    .create(ChatActivity.this)
//                                    .show();
//
//
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });

            } else {
                Log.d("Mensagem", "intent de mensagem");

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        testActivityOpen();
    }

    @Override
    public void onPositiveButtonClicked(final int rate, String comment) {
        // interpret results, send it to analytics etc...
        Log.d("Rate", ""+rate);
        Log.d("Rate", comment);


        dbReference.child("negotiations").child(remoteUserId).child(negotiationKey).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                negociacao = dataSnapshot.getValue(Negociacao.class);
                Log.d("erro1", remoteUserId+"");
                Log.d("erro1", adId+"");

                dbReference.child("users").child(remoteUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        remoteUser = dataSnapshot.getValue(User.class);
                        Log.d("erro1", remoteUser+"");
//                        System.out.println("usuarioRemoto"+remoteUser);
                        if (negociacao.getVendorId().equals(remoteUserId)){
//                if (negotiationType == Constant.NEGOTIATION_TYPE_BUY){
                            Log.d("Você vai avaliar if", "vendedor - " + remoteUser.getNome());
                            dataSnapshot.getRef().child("somaAvVendedor").setValue(remoteUser.getSomaAvVendedor()+rate);
                            double soma = remoteUser.getSomaAvVendedor()+rate;
                            dataSnapshot.getRef().child("qtdAvVendedor").setValue(remoteUser.getQtdAvVendedor()+1);
                            double qtd = remoteUser.getQtdAvVendedor()+1;
                            dataSnapshot.getRef().child("avVendedor").setValue(soma / qtd);
                            dataSnapshot.getRef().child("numVendas").setValue(remoteUser.getNumVendas() + 1);
                            dataSnapshot.getRef().child("ultimaAvaliacao").setValue(negociacao.getAdId());
                            dataSnapshot.getRef().child("ultimoAvaliador").setValue(negociacao.getRemoteUserId());
                        }else {
                            Log.d("Você vai avaliar else", "comprador - " + remoteUser.getNome());
//                    Log.d("Você vai avaliar", "vendedor - " + remoteUser.getNome());
                            dataSnapshot.getRef().child("somaAvComprador").setValue(remoteUser.getSomaAvComprador()+rate);
                            double soma = remoteUser.getSomaAvComprador()+rate;
                            dataSnapshot.getRef().child("qtdAvComprador").setValue(remoteUser.getQtdAvComprador()+1);
                            double qtd = remoteUser.getQtdAvComprador()+1;
                            dataSnapshot.getRef().child("avComprador").setValue(soma / qtd);
                            dataSnapshot.getRef().child("numCompras").setValue(remoteUser.getNumCompras() + 1);
                            dataSnapshot.getRef().child("ultimaAvaliacao").setValue(negociacao.getAdId());
                            dataSnapshot.getRef().child("ultimoAvaliador").setValue(negociacao.getRemoteUserId());
                        }
//                        Log.d("ultimo", remoteUser.getUltimoAvaliador());
//                        Log.d("ultimo", localUser.getId());
                        if (remoteUser.getUltimoAvaliador().equals(localUser.getId())){
                            Log.d("ultimo1", localUser.getUltimaAvaliacao()+"");
                            dbReference.child("users").child(localUser.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    User u = dataSnapshot.getValue(User.class);
                                    if (u.getUltimaAvaliacao() != null){
                                        dbReference.child("negotiations").child(u.getId()).child(negotiationKey).child("status").setValue(Constant.CLOSED_NEGOTIATION);
                                        dbReference.child("negotiations").child(remoteUser.getId()).child(negotiationKey).child("status").setValue(Constant.CLOSED_NEGOTIATION);
                                        finish();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onNeutralButtonClicked() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_finalizar_negociacao:
                openRate();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    };

    public void openRate(){
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Avaliar")
                .setNegativeButtonText("Cancelar")
                .setNoteDescriptions(Arrays.asList("Muito ruim", "Ruim", "Razoável", "Bom", "Muito bom"))
                .setDefaultRating(3)
                .setTitle("Avalie o negociante")
                .setStarColor(R.color.colorPrimary)
                .setNoteDescriptionTextColor(R.color.colorPrimary)
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
//                            .setHint("Please write your comment here ...")
                .setHintTextColor(R.color.primaryTextColor)
                .setCommentTextColor(R.color.primaryTextColor)
                .setHint("Deixe um comentario sobre o negociante")
                .setHintTextColor(R.color.primaryTextColor)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
                .setWindowAnimation(R.style.MyDialogFadeAnimation)
                .create(ChatActivity.this)
                .show();
    }

    private void getMessages() {
        recyclerViewChat.setVisibility(View.VISIBLE);
        noMessagesLayout.setVisibility(View.GONE);
        this.isShowingMessages = true;

        if (callerId == Constant.CHAT_CALLER_NEGOTIATION_ADAPTER) {
            dbReference.child("negotiations").child(userId).child(negotiationKey).child("unreadMessagesCounter").setValue(0);
        }

        dbReference.child("messages").child(messagesId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);
                chatAdapter.addMessage(message);
                linearLayoutManager.scrollToPosition(chatAdapter.getItemCount() - 1);
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
                User user = dataSnapshot.getValue(User.class);
                nomeNegociante = user.getNome();
                vendedorAnuncio.setText(user.getNome());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showNoMessageInfo(){
        Log.d("CHAT", "we'here here");
        recyclerViewChat.setVisibility(View.GONE);
        noMessagesLayout.setVisibility(View.VISIBLE);
        this.isShowingMessages = false;
    }


    @Override
    public void onStop() {
//        if (isShowingMessages) {
//            unreadMessagesCounter = 0;
//        }
        if (callerId != Constant.CHAT_CALLER_POST_ADAPTER) {
            if (negotiationKey.length() > 0) {
                dbReference.child("negotiations").child(userId).child(negotiationKey).child("unreadMessagesCounter").setValue(0);
            }
        }
        if (callerId == Constant.CHAT_CALLER_NEGOTIATION_ADAPTER) {
            NegociacoesActivity.isChatActivityOpened = false;
            NegociacoesActivity.currentOpenedChatNegotiationKey = "";
        }
        super.onStop();
    }
}
