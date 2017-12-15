package br.ufc.quixada.up.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import br.ufc.quixada.up.DAO.FirebaseConfig;
import br.ufc.quixada.up.Models.Constant;
import br.ufc.quixada.up.Models.Post;
import br.ufc.quixada.up.Models.User;
import br.ufc.quixada.up.PhotoActivity;
import br.ufc.quixada.up.R;
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
    ImageView imageView;

    CircleImageView anuncianteFoto;

    String fotoPerfilAnunciante;
    StorageReference anuncianteFotoRef;

    String idAnunciante;
    String nomeAnunciante;
    Post post;
    int callerId;
    String postId;
    FloatingActionButton fab;
    public static ArrayList<byte[]> pictures;
    ArrayList<ImageView> images;
    GridLayout gridLayout;
    int index;

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
        imageView = (ImageView) findViewById(R.id.imageView4);

        if(intent != null){
            if (intent.hasExtra("position")){
                position = intent.getIntExtra("position", -1);
                callerId = Constant.POST_CALLER_MAIN_ACTIVITY;
                start();
            } else if (intent.hasExtra("positionSearch")){
                positionSearch = intent.getIntExtra("positionSearch", -1);
                startFromSearch();
            } else if (intent.hasExtra("postId")){
                postId = intent.getStringExtra("postId");
                callerId = Constant.POST_CALLER_CHAT_ACTIVITY;
                Log.d("INTENT ANUNCIO", postId);
                start();
            }
        }

        downloadPictures(post);
        index = 0;
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
//<<<<<<< isaac-final
        post = MainActivity.searchPosts.get(positionSearch);
//=======
 //       Post post = new Post();
        if (MainActivity.searchPosts != null){
            Log.d("hjhjh",MainActivity.searchPosts+"");
            post = MainActivity.searchPosts.get(positionSearch);
        } else if(ListaDesejosActivity.posts != null ){
            post = ListaDesejosActivity.posts.get(positionSearch);
        }

//>>>>>>> sprint-final

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
        System.out.println("callerID " + callerId);
        System.out.println("postID " + postId);
        if (callerId == Constant.POST_CALLER_MAIN_ACTIVITY) {
            post = BaseActivity.posts.get(position);
            title.setText(post.getTitle());
            subtitle.setText(post.getSubtitle());
            price.setText("R$ "+post.getPrice());
//            this.applyImage(post.getImageCover(), imageView);
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
                    applyImage(image, anuncianteFoto);
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

    public Task<byte[]> downloadImage(String path){
        StorageReference storageReference = FirebaseConfig.getStorage()
                .child("PostsPictures/" + post.getId() + "/" + path);

        final long ONE_MEGABYTE = 1024 * 1024;
        Task<byte[]> task = storageReference.getBytes(ONE_MEGABYTE);

        return task;
    }

    public void downloadPictures(Post post){
        pictures = new ArrayList<byte[]>();
        images = new ArrayList<ImageView>();
        gridLayout = (GridLayout) findViewById(R.id.miniaturas);

        images.add((ImageView) findViewById(R.id.image0));
        images.add((ImageView) findViewById(R.id.image1));
        images.add((ImageView) findViewById(R.id.image2));
        images.add((ImageView) findViewById(R.id.image3));
        images.add((ImageView) findViewById(R.id.image4));
        images.add((ImageView) findViewById(R.id.image5));
        images.add((ImageView) findViewById(R.id.image6));
        images.add((ImageView) findViewById(R.id.image7));
        images.add((ImageView) findViewById(R.id.image8));
        images.add((ImageView) findViewById(R.id.image9));

        for(int i = 0; i < post.getPictures().size(); i++){
            StorageReference storageReference = FirebaseConfig.getStorage()
                    .child("PostsPictures/" + post.getId() + "/" + post.getPictures().get(i));

            final long ONE_MEGABYTE = 1024 * 1024;
            storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>(){
                @Override
                //monitora o sucesso do download
                public void onSuccess(byte[] bytes) {
                    Log.d("TAG","Imagem baixada com sucesso! ");
                    pictures.add(bytes);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                //monitora a falha do downlaod
                public void onFailure(@NonNull Exception e) {
                    Log.d("TAG","Imagem não foi baixada! "+e);

                }
            }).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                @Override
                public void onComplete(@NonNull Task<byte[]> task){
//                Log.d("TAG","Imagem terminou de ser baixada! "+task.getResult());
                    Log.d("TAG","Imagem terminou de ser baixada! "+task.getResult());
                    ImageView imageView = (ImageView)findViewById(R.id.imageView4);
                    applyImage(pictures.get(0),(ImageView)findViewById(R.id.imageView4));
                    for(int i = 0; i < pictures.size(); i++){
                        applyImage(pictures.get(i), images.get(i));
                    }
                }
            });

//            GridLayout.Spec row = GridLayout.spec(0);
//            GridLayout.Spec column = GridLayout.spec(i);
//            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams(row, column);
////            DrawerLayout.LayoutParams imageParams = new DrawerLayout.LayoutParams();
////            ImageView imageView = new ImageView(this);
//            ImageView imageView = (ImageView)findViewById(R.id.image0);
////            imageView.setImageResource(R.drawable.default_img);
//            gridLayout.addView(imageView, layoutParams);

            images.get(i).setVisibility(View.VISIBLE);

        }
    }

    public void fullImage(View view){
        Intent intent = new Intent(this, PhotoActivity.class);
        intent.putExtra("index", index);
        startActivity(intent);
    }

    public void setImage(View view){
        ImageView thumb = (ImageView)view;
        view.setBackgroundResource(R.drawable.border);
        view.setSelected(true);
        ImageView imageView = (ImageView)findViewById(R.id.imageView4);
        imageView.setImageDrawable(thumb.getDrawable());
        index = images.indexOf(thumb);
    }

    public void share(View view){
        TextView textView_title = (TextView)findViewById(R.id.textView_title);
        TextView textView_describ = (TextView)findViewById(R.id.textView_describ);
        TextView textView_price = (TextView)findViewById(R.id.textView_price);

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, textView_title.getText() +
                " - " + textView_describ.getText() +
                " - " + textView_price.getText());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    public void up(View view){

    }

    public void applyImage(byte[] imagem, ImageView  image){
        RequestOptions requestOptions = new RequestOptions();
        //não salava a imagem em cache, para que ela possa ser alterada caso outra pessoa se logue
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        requestOptions.skipMemoryCache(true);
//<<<<<<< isaac-final
 //       Glide.with(this).load(imagem)
   //             .apply(requestOptions)
     //           .into(image);
//=======
        if (isActivityOpen){
            Glide.with(this).load(imagem)
                    .apply(requestOptions)
                    .into(image);

        }
//>>>>>>> sprint-final
    }
}
