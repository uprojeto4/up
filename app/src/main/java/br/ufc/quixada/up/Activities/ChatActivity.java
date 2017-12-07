package br.ufc.quixada.up.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.util.ArrayList;
import java.util.Arrays;

import br.ufc.quixada.up.Adapters.ChatAdapter;
import br.ufc.quixada.up.Constant;
import br.ufc.quixada.up.DAO.FirebaseConfig;
import br.ufc.quixada.up.Models.Message;

import br.ufc.quixada.up.Models.Negociacao;
import br.ufc.quixada.up.Models.Post;
import br.ufc.quixada.up.Models.User;
import br.ufc.quixada.up.R;
import br.ufc.quixada.up.Utils.ChatControl;
import br.ufc.quixada.up.Utils.DateTimeControl;

public class ChatActivity extends BaseActivity implements RatingDialogListener {

    private DatabaseReference dbReference;
    private RecyclerView recyclerViewChat;
    public LinearLayoutManager linearLayoutManager;
    private EditText messageInput;
    private ChatAdapter chatAdapter;
    private String chatId;
    private String userId;
    private String remoteUserId;
    private String adId;
    private int unreadMessagesCounter = 0;
    private TextView tituloAnuncio;
    private TextView vendedorAnuncio;
    private TextView dataCadastroAnuncio;
    private TextView negociante;
    private LinearLayout noMessagesLayout;
    private int negotiationType;
    private int callerId;
    private boolean isShowingMessages = false;

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

        Intent intent = getIntent();
        adId = intent.getStringExtra("adId");
        remoteUserId = intent.getStringExtra("remoteUserId");
        negotiationType = intent.getIntExtra("negotiationType", 0);
        callerId = intent.getIntExtra("callerId", 0);

        if (callerId == Constant.CHAT_CALLER_POST_ADAPTER) {

        } else if (callerId == Constant.CHAT_CALLER_NEGOTIATION_ACTIVITY) {
            NegociacoesActivity.isChatActivityOpened = true;
            NegociacoesActivity.currentOpenedChatNegotiationKey = intent.getStringExtra("negotiationKey");
        }

        userId = localUser.getId();

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

        noMessagesLayout = findViewById(R.id.noMessagesLayout);
        messageInput = findViewById(R.id.editTextMessageInput);
        Button buttonSend = findViewById(R.id.buttonSend);
        dbReference = FirebaseConfig.getDatabase();
        resolveRemoteUserName();

        recyclerViewChat = findViewById(R.id.recyclerViewConversation);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewChat.setLayoutManager(linearLayoutManager);
        chatAdapter = new ChatAdapter(userId);
        recyclerViewChat.setAdapter(chatAdapter);

//        tituloAnuncio.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getBaseContext(), AnuncioActivity.class);
//                intent.putExtra("position", position);
//                startActivity(intent);
//            }
//        });

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
                        dbReference.child("negotiations").child(remoteUserId).child(adId).child("unreadMessagesCounter").setValue(unreadMessagesCounter);
                        dbReference.child("negotiations").child(remoteUserId).child(adId).child("lastMessage").setValue(messageInput.getText().toString());
                        dbReference.child("negotiations").child(remoteUserId).child(adId).child("lastMessageSenderId").setValue(userId);
                    } else {
                        unreadMessagesCounter = 1;
                        chatId = ChatControl.startConversation(userId, remoteUserId, adId, message);
                    }
                    if (!isShowingMessages) {
                        getMessages();
                    }
                    messageInput.setText("");
                    linearLayoutManager.scrollToPosition(chatAdapter.getItemCount() - 1);
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
                    getMessages();
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
    public void onPositiveButtonClicked(final int rate, String comment) {
        // interpret results, send it to analytics etc...
        Log.d("Rate", ""+rate);
        Log.d("Rate", comment);

//        dbReference.child("posts").child(adId).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                Post post = dataSnapshot.getValue(Post.class);
//
//                dbReference.child("negotiations").child(remoteUserId).child(adId).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        Negociacao negociacao = dataSnapshot.getValue(Negociacao.class);
//                        if (negociacao.getRemoteUserId().equals(remoteUserId)){
//                            dbReference.child("users").child(remoteUserId).addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    User remoteUser = dataSnapshot.getValue(User.class);
//                                    System.out.println("usuarioRemoto"+remoteUser);
////                            if (remoteUser.getId().equals(remoteUserId)){
//                                    Log.d("Você vai avaliar", "vendedor - " + remoteUser.getNome());
//                                    dataSnapshot.getRef().child("somaAvVendedor").setValue(remoteUser.getSomaAvVendedor()+rate);
//                                    double soma = remoteUser.getSomaAvVendedor()+rate;
//                                    dataSnapshot.getRef().child("qtdAvVendedor").setValue(remoteUser.getQtdAvVendedor()+1);
//                                    double qtd = remoteUser.getQtdAvVendedor()+1;
//                                    dataSnapshot.getRef().child("avVendedor").setValue(soma / qtd);
//                                    dataSnapshot.getRef().child("numVendas").setValue(remoteUser.getNumVendas() + 1);
////                            } else {
////                                Log.d("Você vai avaliar", "comprador - " + remoteUser.getNome());
////                                Log.d("Você vai avaliar", "vendedor - " + remoteUser.getNome());
////                                dataSnapshot.getRef().child("somaAvComprador").setValue(remoteUser.getSomaAvComprador()+rate);
////                                double soma = remoteUser.getSomaAvComprador()+rate;
////                                dataSnapshot.getRef().child("qtdAvComprador").setValue(remoteUser.getQtdAvComprador()+1);
////                                double qtd = remoteUser.getQtdAvComprador()+1;
////                                dataSnapshot.getRef().child("avComprador").setValue(soma / qtd);
////                                dataSnapshot.getRef().child("numCompras").setValue(remoteUser.getNumCompras() + 1);
////                            }
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//
//                                }
//                            });
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//
//                if (post.getUserId().equals(remoteUserId)){
//                    dbReference.child("users").child(remoteUserId).addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            User remoteUser = dataSnapshot.getValue(User.class);
//                            System.out.println("usuarioRemoto"+remoteUser);
////                            if (remoteUser.getId().equals(remoteUserId)){
//                            Log.d("Você vai avaliar", "vendedor - " + remoteUser.getNome());
//                            dataSnapshot.getRef().child("somaAvVendedor").setValue(remoteUser.getSomaAvVendedor()+rate);
//                            double soma = remoteUser.getSomaAvVendedor()+rate;
//                            dataSnapshot.getRef().child("qtdAvVendedor").setValue(remoteUser.getQtdAvVendedor()+1);
//                            double qtd = remoteUser.getQtdAvVendedor()+1;
//                            dataSnapshot.getRef().child("avVendedor").setValue(soma / qtd);
//                            dataSnapshot.getRef().child("numVendas").setValue(remoteUser.getNumVendas() + 1);
////                            } else {
////                                Log.d("Você vai avaliar", "comprador - " + remoteUser.getNome());
////                                Log.d("Você vai avaliar", "vendedor - " + remoteUser.getNome());
////                                dataSnapshot.getRef().child("somaAvComprador").setValue(remoteUser.getSomaAvComprador()+rate);
////                                double soma = remoteUser.getSomaAvComprador()+rate;
////                                dataSnapshot.getRef().child("qtdAvComprador").setValue(remoteUser.getQtdAvComprador()+1);
////                                double qtd = remoteUser.getQtdAvComprador()+1;
////                                dataSnapshot.getRef().child("avComprador").setValue(soma / qtd);
////                                dataSnapshot.getRef().child("numCompras").setValue(remoteUser.getNumCompras() + 1);
////                            }
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//                } else {
//                    dbReference.child("users").child(remoteUserId).addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            User remoteUser = dataSnapshot.getValue(User.class);
//                            System.out.println("usuarioRemoto"+remoteUser);
////                            if (remoteUser.getId().equals(remoteUserId)){
////                            Log.d("Você vai avaliar", "vendedor - " + remoteUser.getNome());
////                            dataSnapshot.getRef().child("somaAvVendedor").setValue(remoteUser.getSomaAvVendedor()+rate);
////                            double soma = remoteUser.getSomaAvVendedor()+rate;
////                            dataSnapshot.getRef().child("qtdAvVendedor").setValue(remoteUser.getQtdAvVendedor()+1);
////                            double qtd = remoteUser.getQtdAvVendedor()+1;
////                            dataSnapshot.getRef().child("avVendedor").setValue(soma / qtd);
////                            dataSnapshot.getRef().child("numVendas").setValue(remoteUser.getNumVendas() + 1);
////                            } else {
//                            Log.d("Você vai avaliar", "comprador - " + remoteUser.getNome());
//                            Log.d("Você vai avaliar", "vendedor - " + remoteUser.getNome());
//                            dataSnapshot.getRef().child("somaAvComprador").setValue(remoteUser.getSomaAvComprador()+rate);
//                            double soma = remoteUser.getSomaAvComprador()+rate;
//                            dataSnapshot.getRef().child("qtdAvComprador").setValue(remoteUser.getQtdAvComprador()+1);
//                            double qtd = remoteUser.getQtdAvComprador()+1;
//                            dataSnapshot.getRef().child("avComprador").setValue(soma / qtd);
//                            dataSnapshot.getRef().child("numCompras").setValue(remoteUser.getNumCompras() + 1);
////                            }
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//
//                }
//
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

//        dbReference.child("users").child(remoteUserId).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                User remoteUser = dataSnapshot.getValue(User.class);
//                System.out.println("usuarioRemoto"+remoteUser);
//                if (remoteUser.getId().equals(remoteUserId)){
//                    Log.d("Você vai avaliar", "vendedor - " + remoteUser.getNome());
//                    dataSnapshot.getRef().child("somaAvVendedor").setValue(remoteUser.getSomaAvVendedor()+rate);
//                    double soma = remoteUser.getSomaAvVendedor()+rate;
//                    dataSnapshot.getRef().child("qtdAvVendedor").setValue(remoteUser.getQtdAvVendedor()+1);
//                    double qtd = remoteUser.getQtdAvVendedor()+1;
//                    dataSnapshot.getRef().child("avVendedor").setValue(soma / qtd);
//                    dataSnapshot.getRef().child("numVendas").setValue(remoteUser.getNumVendas() + 1);
//                } else {
//                    Log.d("Você vai avaliar", "comprador - " + remoteUser.getNome());
//                    Log.d("Você vai avaliar", "vendedor - " + remoteUser.getNome());
//                    dataSnapshot.getRef().child("somaAvComprador").setValue(remoteUser.getSomaAvComprador()+rate);
//                    double soma = remoteUser.getSomaAvComprador()+rate;
//                    dataSnapshot.getRef().child("qtdAvComprador").setValue(remoteUser.getQtdAvComprador()+1);
//                    double qtd = remoteUser.getQtdAvComprador()+1;
//                    dataSnapshot.getRef().child("avComprador").setValue(soma / qtd);
//                    dataSnapshot.getRef().child("numCompras").setValue(remoteUser.getNumCompras() + 1);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

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
        dbReference.child("users").child(remoteUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User remoteUser = dataSnapshot.getValue(User.class);
                System.out.println("usuarioRemoto"+remoteUser);
                if (remoteUser.getId().equals(remoteUserId)){
                    Log.d("Você vai avaliar", "vendedor - " + remoteUser.getNome());

                    new AppRatingDialog.Builder()
                            .setPositiveButtonText("Avaliar")
                            .setNegativeButtonText("Cancelar")
                            .setNoteDescriptions(Arrays.asList("Muito ruim", "Ruim", "Razoável", "Bom", "Muito bom"))
                            .setDefaultRating(3)
                            .setTitle("Avalie "+remoteUser.getNome())
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


                } else {
                    Log.d("Você vai avaliar", "comprador - " + remoteUser.getNome());
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
                System.out.println("incoming message: " + dataSnapshot);
                Message message = dataSnapshot.getValue(Message.class);
                chatAdapter.addMessage(message);
                if (!isShowingMessages) {
                    showMessages();
                }
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
                vendedorAnuncio.setText(user.getNome());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showMessages(){
        recyclerViewChat.setVisibility(View.VISIBLE);
        noMessagesLayout.setVisibility(View.GONE);
        this.isShowingMessages = true;
    }

    private void showNoMessageInfo(){
        recyclerViewChat.setVisibility(View.GONE);
        noMessagesLayout.setVisibility(View.VISIBLE);
        this.isShowingMessages = false;
    }

    private void emptyMessagesCounter() {
        if (isShowingMessages) {
            unreadMessagesCounter = 0;
            dbReference.child("negotiations").child(userId).child(adId).child("unreadMessagesCounter").setValue(0);
        }
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
        if (callerId == Constant.CHAT_CALLER_NEGOTIATION_ACTIVITY) {
            NegociacoesActivity.isChatActivityOpened = false;
            NegociacoesActivity.currentOpenedChatNegotiationKey = "";
        }
    }
}
