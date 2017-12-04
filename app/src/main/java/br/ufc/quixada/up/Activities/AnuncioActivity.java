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

import java.util.ArrayList;

import br.ufc.quixada.up.DAO.FirebaseConfig;
import br.ufc.quixada.up.Models.Post;
import br.ufc.quixada.up.Models.User;
import br.ufc.quixada.up.R;
import br.ufc.quixada.up.Utils.ChatControl;
import br.ufc.quixada.up.Utils.FirebasePreferences;

public class AnuncioActivity extends AppCompatActivity {

    private int position;

    private DatabaseReference databaseReference;

    TextView anuncianteNome;

    TextView avaliacaoVendedor;

    TextView tituloUsuario;

//    User usuarioAnunciante;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncio);
        databaseReference = FirebaseConfig.getDatabase();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabNegotiate);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ChatControl.startConversation("remoteUserId", "productId");
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if(intent != null){
            position = intent.getIntExtra("position", -1);
            start();
        }
//        getActionBar().setTitle("Anúncio");

    }

    public void start(){
        Post post = MainActivity.posts.get(position);
//        Toast.makeText(this, "opa é nois"+post.getTitle(), Toast.LENGTH_SHORT).show();

        TextView title = (TextView)findViewById(R.id.textView_title);
        TextView subtitle = (TextView)findViewById(R.id.textView_describ);
        TextView price = (TextView)findViewById(R.id.textView_price);
        Spinner qtd = (Spinner) findViewById(R.id.spinner);

        anuncianteNome = (TextView) findViewById(R.id.anuncianteNome);
        avaliacaoVendedor = (TextView) findViewById(R.id.avVendedor);
        tituloUsuario = (TextView) findViewById(R.id.tituloUsuario);

        title.setText(post.getTitle());
        subtitle.setText(post.getSubtitle());
        price.setText("R$ "+post.getPrice());


        Query getUserData = databaseReference.child("users").orderByChild("id").equalTo(post.getUserId());
        getUserData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    User usuarioAnunciante = singleSnapshot.getValue(User.class);
                    Log.d("nome_anunciane", ""+usuarioAnunciante.getNome());
                    anuncianteNome.setText(usuarioAnunciante.getNome());
                    avaliacaoVendedor.setText(""+usuarioAnunciante.getAvVendedor());

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
