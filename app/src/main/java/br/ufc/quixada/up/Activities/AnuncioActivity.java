package br.ufc.quixada.up.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import br.ufc.quixada.up.DAO.FirebaseConfig;
import br.ufc.quixada.up.Models.Constant;
import br.ufc.quixada.up.Models.Post;
import br.ufc.quixada.up.Models.User;
import br.ufc.quixada.up.R;
import br.ufc.quixada.up.Utils.ChatControl;
import br.ufc.quixada.up.Utils.FirebasePreferences;
import de.hdodenhof.circleimageview.CircleImageView;

public class AnuncioActivity extends BaseActivity {

    private int position;
    private int positionSearch;

    private DatabaseReference databaseReference;
    TextView title;
    TextView subtitle;
    TextView price;
    Spinner qtd;
    TextView anuncianteNome;
    TextView avaliacaoVendedor;
    TextView tituloUsuario;

    CircleImageView anuncianteFoto;

    String fotoPerfilAnunciante;
    StorageReference anuncianteFotoRef;

    String idAnunciante;
    String nomeAnunciante;
    Post post;
    int callerId;
    String postId;
    FloatingActionButton fab;

    public static boolean isActivityOpen;
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

        if(intent != null){
            if (intent.hasExtra("position")){
                position = intent.getIntExtra("position", -1);
                start();
            } else if (intent.hasExtra("positionSearch")){
                positionSearch = intent.getIntExtra("positionSearch", -1);
                startFromSearch();
            }
        }

        setListeners();

        anuncianteNome = (TextView) findViewById(R.id.anuncianteNome);
        anuncianteFoto = (CircleImageView) findViewById(R.id.profile_image);

        isActivityOpen = true;

    }

    @Override
    protected void onPause() {
        super.onPause();
        isActivityOpen = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isActivityOpen = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        isActivityOpen = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActivityOpen = true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_perfil, menu);
        return true;
    }

    public void startFromSearch() {
        Post post = new Post();
        if (MainActivity.searchPosts != null){
            Log.d("hjhjh",MainActivity.searchPosts+"");
            post = MainActivity.searchPosts.get(positionSearch);
        } else if(ListaDesejosActivity.posts != null ){
            post = ListaDesejosActivity.posts.get(positionSearch);
        }


        TextView title = (TextView) findViewById(R.id.textView_title);
        TextView subtitle = (TextView) findViewById(R.id.textView_describ);
        TextView price = (TextView) findViewById(R.id.textView_price);
        Spinner qtd = (Spinner) findViewById(R.id.spinner);

        anuncianteNome = (TextView) findViewById(R.id.anuncianteNome);
        avaliacaoVendedor = (TextView) findViewById(R.id.avVendedor);
        tituloUsuario = (TextView) findViewById(R.id.tituloUsuario);

        title.setText(post.getTitle());
        subtitle.setText(post.getSubtitle());
        price.setText("R$ " + post.getPrice());


        Query getUserData = databaseReference.child("users").orderByChild("id").equalTo(post.getUserId());
        getUserData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    User usuarioAnunciante = singleSnapshot.getValue(User.class);
                    Log.d("nome_anunciane", "" + usuarioAnunciante.getNome());
                    idAnunciante = usuarioAnunciante.getId();
                    nomeAnunciante = usuarioAnunciante.getNome();
                    fotoPerfilAnunciante = usuarioAnunciante.getFotoPerfil();
                    anuncianteFotoRef = storage.getReference().child("UsersProfilePictures/" + idAnunciante + "/" + fotoPerfilAnunciante);
                    downloadAnuncianteFoto();
                    anuncianteNome.setText(usuarioAnunciante.getNome());
                    DecimalFormat numberFormat = new DecimalFormat("#.0");

                    if (usuarioAnunciante.getAvVendedor() == 0) {
                        avaliacaoVendedor.setText("" + usuarioAnunciante.getAvVendedor());
                    } else {
                        avaliacaoVendedor.setText("" + numberFormat.format(usuarioAnunciante.getAvVendedor()));
                    }
                    anuncianteNome.setText(usuarioAnunciante.getNome());
                    avaliacaoVendedor.setText("" + usuarioAnunciante.getAvVendedor());

                    if (usuarioAnunciante.getNumVendas() == 0) {
                        tituloUsuario.setText("Novato");
                    } else if (usuarioAnunciante.getNumVendas() <= 10) {
                        tituloUsuario.setText("Iniciante");
                    } else if (usuarioAnunciante.getNumVendas() > 10) {
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
        for (int i = 0; i < post.getQtd(); i++) {
            qtdList.add(i + 1);
        }

        ArrayAdapter qtdAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, qtdList);
        qtd.setAdapter(qtdAdapter);
    }

    public void setListeners(){
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
                if (idAnunciante.equals(localUser.getId())) {
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
    }

    public void start(){

        if (callerId == Constant.POST_CALLER_MAIN_ACTIVITY) {
            post = BaseActivity.posts.get(position);
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
                    fotoPerfilAnunciante = usuarioAnunciante.getFotoPerfil();
                    anuncianteFotoRef = storage.getReference().child("UsersProfilePictures/"+idAnunciante+"/"+fotoPerfilAnunciante);
                    downloadAnuncianteFoto();
                    anuncianteNome.setText(usuarioAnunciante.getNome());
                    DecimalFormat numberFormat = new DecimalFormat("#.0");

                    if(usuarioAnunciante.getAvVendedor() == 0){
                        avaliacaoVendedor.setText(""+usuarioAnunciante.getAvVendedor());
                    }else{
                        avaliacaoVendedor.setText(""+numberFormat.format(usuarioAnunciante.getAvVendedor()));
                    }
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

    public void downloadAnuncianteFoto(){
        try{
            //cria o arquivo temporário local onde a imagem será armazenada
            final File localFile = File.createTempFile("jpg", "image");
            anuncianteFotoRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                //monitora o sucesso do download
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    //transforma a imagem baixada em um bitmap
                    bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    //transforma o bitmap em stream
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    //transforma o stream em um array de bytes
                    image = stream.toByteArray();
                    //método que aplica a imagem nos lugares desejsdos
                    applyImageAnunciante(image);
//                    Toast.makeText(getActivity(),profilePictureRef.getName(), Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                //monitora a falha do downlaod
                public void onFailure(@NonNull Exception e) {
//                    Toast.makeText(getBaseContext(),"Foto não encontrada", Toast.LENGTH_LONG).show();
                }
            });
        } catch (IOException e){
            e.printStackTrace();
            //manipular exceções
            Log.e("Main", "IOE exception");
        }
    }

    public void applyImageAnunciante(byte[] imagem){
        RequestOptions requestOptions = new RequestOptions();
        //não salava a imagem em cache, para que ela possa ser alterada caso outra pessoa se logue
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        requestOptions.skipMemoryCache(true);
        if (isActivityOpen){
            Glide.with(this).load(imagem)
                    .apply(requestOptions)
                    .into(anuncianteFoto);

        }
    }

}
