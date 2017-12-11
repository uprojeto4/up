package br.ufc.quixada.up.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;

import br.ufc.quixada.up.DAO.FirebaseConfig;
import br.ufc.quixada.up.Models.Constant;
import br.ufc.quixada.up.Models.Post;
import br.ufc.quixada.up.Models.User;
import br.ufc.quixada.up.R;
import br.ufc.quixada.up.Utils.ChatControl;
import br.ufc.quixada.up.Utils.FirebasePreferences;

public class AnuncioActivity extends BaseActivity {

    private int position;
    private DatabaseReference databaseReference;
    TextView title;
    TextView subtitle;
    TextView price;
    Spinner qtd;
    TextView anuncianteNome;
    TextView avaliacaoVendedor;
    TextView tituloUsuario;
    String idAnunciante;
    String nomeAnunciante;
    Post post;
    int callerId;
    String postId;
    FloatingActionButton fab;

//    User usuarioAnunciante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncio);
        databaseReference = FirebaseConfig.getDatabase();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        title = (TextView)findViewById(R.id.textView_title);
        subtitle = (TextView)findViewById(R.id.textView_describ);
        price = (TextView)findViewById(R.id.textView_price);
        anuncianteNome = (TextView) findViewById(R.id.anuncianteNome);
        avaliacaoVendedor = (TextView) findViewById(R.id.avVendedor);
        tituloUsuario = (TextView) findViewById(R.id.tituloUsuario);
        qtd = (Spinner) findViewById(R.id.spinner);
        fab = (FloatingActionButton) findViewById(R.id.fabNegotiate);

        if (intent != null){
            position = intent.getIntExtra("position", -1);
            callerId = intent.getIntExtra("callerId", -1);
            postId = intent.getStringExtra("postId");
            start();
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), ChatActivity.class);
                intent.putExtra("remoteUserId", post.getUserId());
                intent.putExtra("adId", post.getId());
                intent.putExtra("adTitle", post.getTitle());
                intent.putExtra("negotiationType", 0);
                intent.putExtra("submitDate", post.getDataCadastro());
                intent.putExtra("callerId", Constant.CHAT_CALLER_NEGOTIATION_ADAPTER);
                startActivity(intent);
            }
        });

        anuncianteNome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (idAnunciante.equals(localUser.getId())){
                    Log.d("EntrouAquiOtario", "entrou neste intent");
                    Intent intent = new Intent(AnuncioActivity.this, PerfilActivity.class);
                    intent.putExtra("idAnunciante", idAnunciante);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(AnuncioActivity.this, PerfilPublicoActivity.class);
                    intent.putExtra("idAnunciante", idAnunciante);
                    intent.putExtra("nomeAnunciante", nomeAnunciante);
                    startActivity(intent);
                }
            }
        });
//        getActionBar().setTitle("Anúncio");
    }

    public void start(){
//        Toast.makeText(this, "opa é nois"+post.getTitle(), Toast.LENGTH_SHORT).show();
        if (callerId == Constant.POST_CALLER_MAIN_ACTIVITY) {
            post = MainActivity.posts.get(position);
            title.setText(post.getTitle());
            subtitle.setText(post.getSubtitle());
            price.setText("R$ "+post.getPrice());
            if (localUserId.equals(post.getUserId())) {
                fab.setVisibility(View.GONE);
            }
            getData();
        } else if (callerId == Constant.POST_CALLER_CHAT_ACTIVITY){
            databaseReference.child("posts").child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    post = dataSnapshot.getValue(Post.class);
                    title.setText(post.getTitle());
                    subtitle.setText(post.getSubtitle());
                    price.setText("R$ "+post.getPrice());
                    if (localUserId.equals(post.getUserId())) {
                        fab.setVisibility(View.GONE);
                    }
                    getData();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void getData() {
        Query getUserData = databaseReference.child("users").orderByChild("id").equalTo(post.getUserId());
        getUserData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    User usuarioAnunciante = singleSnapshot.getValue(User.class);
                    Log.d("nome_anunciane", ""+usuarioAnunciante.getNome());
                    idAnunciante = usuarioAnunciante.getId();
                    nomeAnunciante = usuarioAnunciante.getNome();
                    anuncianteNome.setText(usuarioAnunciante.getNome());
                    DecimalFormat numberFormat = new DecimalFormat("#.0");

                    avaliacaoVendedor.setText(""+numberFormat.format(usuarioAnunciante.getAvVendedor()));

                    if (usuarioAnunciante.getNumVendas() == 0){
                        tituloUsuario.setText("Novato");
                    } else if (usuarioAnunciante.getNumVendas() <= 10){
                        tituloUsuario.setText("Iniciante");
                    } else if (usuarioAnunciante.getNumVendas() > 10){
                        tituloUsuario.setText("Sênior");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Log.d("id_usuario", post.getUserId());

        ArrayList<Integer> qtdList = new ArrayList<Integer>();
        for (int i = 0; i < post.getQtd(); i++){
            qtdList.add(i+1);
        }

        ArrayAdapter qtdAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, qtdList);
        qtd.setAdapter(qtdAdapter);
    }

}
