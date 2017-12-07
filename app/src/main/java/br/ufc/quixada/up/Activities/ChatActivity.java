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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import br.ufc.quixada.up.DAO.FirebaseConfig;
import br.ufc.quixada.up.Models.Message;

import br.ufc.quixada.up.Models.Negociacao;
import br.ufc.quixada.up.Models.Post;
import br.ufc.quixada.up.Models.User;
import br.ufc.quixada.up.R;
import br.ufc.quixada.up.Utils.ChatControl;

public class ChatActivity extends BaseActivity implements RatingDialogListener {

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
    private LinearLayout noMessagesLayout;

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
        userId = localUser.getId();

        textViewTitleAnuncioChat = findViewById(R.id.titleAnuncioChat);
        textViewTitleAnuncioChat.setText(intent.getStringExtra("adTitle"));
        textViewVendedorAnuncioChat = findViewById(R.id.vendedorAnuncioChat);

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
                        unreadMessagesCounter += 1;
                        dbReference.child("negotiations").child(remoteUserId).child(adId).child("unreadMessagesCounter").setValue(unreadMessagesCounter);
                        dbReference.child("negotiations").child(remoteUserId).child(adId).child("lastMessage").setValue(messageInput.getText().toString());
                    } else {
                        System.out.println("userId " + userId);
                        chatId = ChatControl.startConversation(userId, remoteUserId, adId, message);
                    }
                    chatAdapter.addMessage(message);
                    messageInput.setText("");
                    linearLayoutManager.scrollToPosition(chatAdapter.getItemCount() - 1);
                }
            }
        });

        dbReference.child("negotiations").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(adId)) {
                    DataSnapshot negotiationSnapshot = dataSnapshot.child(adId);
//                    Log.d("maxxx", "ChatonDataChange: " + negotiationSnapshot);
                    Negociacao negociacao = negotiationSnapshot.getValue(Negociacao.class);
                    chatId = negociacao.getMessagesId();
                    unreadMessagesCounter = negociacao.getUnreadMessagesCounter();
                    dbReference.child("negotiations").child(userId).child(adId).child("unreadMessagesCounter").setValue(0);
                    getMessages();
                } else {
                    recyclerView.setVisibility(View.GONE);
                    noMessagesLayout.setVisibility(View.VISIBLE);
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
        dbReference.child("messages").child(chatId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot messageDataSnapshot : dataSnapshot.getChildren()) {
                    Message message = messageDataSnapshot.getValue(Message.class);
                    chatAdapter.addMessage(message);
                }
                linearLayoutManager.scrollToPosition(chatAdapter.getItemCount() - 1);
                if (chatAdapter.getItemCount() > 0) {
                    recyclerView.setVisibility(View.VISIBLE);
                    noMessagesLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void resolveRemoteUserName() {
        System.out.println(remoteUserId);
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
