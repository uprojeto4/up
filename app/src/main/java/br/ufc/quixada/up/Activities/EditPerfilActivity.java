package br.ufc.quixada.up.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.ufc.quixada.up.DAO.FirebaseConfig;
import br.ufc.quixada.up.Utils.FirebasePreferences;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static br.ufc.quixada.up.R.drawable.image_test_1;

import br.ufc.quixada.up.R;

//TIVE QUE FAZER A ACTIVITY HERDAR DE PERFILACTIVITY POIS PRECISAVA DOS DADOS DE USUÁRIO QUE SÃO ATUALIZADOS LA, JÁ QUE ELA HERDA DE BASE
public class EditPerfilActivity extends PerfilActivity {

    Button saveEdit;

    FirebaseStorage storage = FirebaseStorage.getInstance();

    StorageReference storageRef;

    StorageReference profilePictureRef;

    ArrayList<Image> images;

    LinearLayout loading;

    EditText nome;
    EditText logradouroEt;
    EditText numeroEt;
    EditText complementoEt;
    EditText bairroEt;
    EditText cidadeEt;
    EditText estadoEt;
    EditText telefone;
    EditText email;
    EditText senha;
    EditText senha2;
    ImageView foto;

    Bitmap bitmap;
    public byte[] image;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_perfil);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                String[] permissionsToRequest = new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
                PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(EditPerfilActivity.this, permissionsToRequest, new PermissionsResultAction() {


                    @Override
                    public void onGranted() {

//                        int maxSize = 5 - imageAdapter.getItemCount();

                            ImagePicker.with(EditPerfilActivity.this)  //  Initialize ImagePicker with activity or fragment context
                                    .setToolbarColor("#5a185f")         //  Toolbar color
                                    .setStatusBarColor("#2e0035")       //  StatusBar color (works with SDK >= 21  )
                                    .setToolbarTextColor("#FFFFFF")     //  Toolbar text color (Title and Done button)
                                    .setToolbarIconColor("#FFFFFF")     //  Toolbar icon color (Back and Camera button)
                                    .setProgressBarColor("#4CAF50")     //  ProgressBar color
                                    .setBackgroundColor("#212121")      //  Background color
                                    .setCameraOnly(false)               //  Camera mode
                                    .setMultipleMode(true)              //  Select multiple images or single image
                                    .setFolderMode(true)                //  Folder mode
                                    .setShowCamera(true)                //  Show camera button
                                    .setFolderTitle("Álbuns")           //  Folder title (works with FolderMode = true)
                                    .setImageTitle("Galeria")         //  Image title (works with FolderMode = false)
                                    .setDoneTitle("Finalizar")               //  Done button title
                                    .setMaxSize(1)                     //  Max images can be selected
                                    .setSavePath("Up - Compra e venda")         //  Image capture folder name
//                                    .setSelectedImages(images)          //  Selected images
                                    .start();                           //  Start ImagePicker
                        }

                    @Override
                    public void onDenied(String permission) {
                        Snackbar.make(view, "Desculpe, mas precisamos de permissão para acessar seus arquivos", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });

            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        saveEdit = (Button) findViewById(R.id.buttonSalvarEdicao);
        //quando o botão salvar for clicado chamar metodo de upload
        saveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadProfilePicture();
            }
        });

        //cria a referencia ao storage
        storageRef = storage.getReference();

        loading = (LinearLayout) findViewById(R.id.loading);

//        Toast.makeText(this, firebasePreferences.getId()+" - "+firebasePreferences.getUserName()+" - "+firebasePreferences.getUserEmail(), Toast.LENGTH_LONG).show();


        nome = (EditText) findViewById(R.id.edit_nome);

        logradouroEt = (EditText) findViewById(R.id.edit_logradouro);
        numeroEt = (EditText) findViewById(R.id.edit_numero);
        complementoEt = (EditText) findViewById(R.id.edit_complemento);
        bairroEt = (EditText) findViewById(R.id.edit_bairro);
        cidadeEt = (EditText) findViewById(R.id.edit_cidade);
        estadoEt = (EditText) findViewById(R.id.edit_estado);

//        endereco = (EditText) findViewById(R.id.edit_endereco);
        telefone = (EditText) findViewById(R.id.edit_telefone);
        email = (EditText) findViewById(R.id.edit_email);
        senha = (EditText) findViewById(R.id.edit_senha);
        senha2 = (EditText) findViewById(R.id.edit_senha_2);
//        foto = (ImageView) findViewById(R.id.imageViewIconeFoto);



        nome.setText(PerfilActivity.nome);
        email.setText(PerfilActivity.email);

        logradouroEt.setText(localUser.getLogradouro());
        numeroEt.setText(localUser.getNumero());
        complementoEt.setText(localUser.getComplemento());
        bairroEt.setText(localUser.getBairro());
        cidadeEt.setText(localUser.getCidade());
        estadoEt.setText(localUser.getEstado());

        profilePictureRef = storage.getReference().child("UsersProfilePictures/"+PerfilActivity.id+"/"+localUser.getFotoPerfil());
//        Toast.makeText(getBaseContext(),profilePictureRef.getPath(), Toast.LENGTH_LONG).show();



        try{
            //cria o arquivo temporário local onde a imagem será armazenada
            final File localFile = File.createTempFile("jpg", "image");
            profilePictureRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
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
                    applyImage(image);
//                    Toast.makeText(getBaseContext(),profilePictureRef.getName(), Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                //monitora a falha do downlaod
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getBaseContext(),"Imagem não foi baixada", Toast.LENGTH_LONG).show();
                }
            });
        } catch (IOException e){
            e.printStackTrace();
            //manipular exceções
            Log.e("Main", "IOE exception");
        }

    }

    public void applyImage(byte[] bytes){
        //efeito de blur para a imagem
//        MultiTransformation multi = new MultiTransformation(
//                new BlurTransformation(25));


        //options para o glide
        RequestOptions requestOptions = new RequestOptions();
        //não salava a imagem em cache, para que ela possa ser alterada caso outra pessoa se logue
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        requestOptions.skipMemoryCache(true);

        //carrega a imagem
        Glide.with(this).load(bytes)
                //aplica as options de cache
                .apply(requestOptions)
                //aplica as options de transformação
//                .apply(RequestOptions.bitmapTransform(multi))
                //insere a imagem no imageView
                .into((ImageView) findViewById((R.id.imageViewIconeFoto)));

    }

    @Override
    public void onResume() {
        super.onResume();
        fotoPerfil = localUser.getFotoPerfil();
        localUser.setFotoPerfil(fotoPerfil);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Config.RC_PICK_IMAGES && resultCode == RESULT_OK && data != null) {

            images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);

            Log.d("printa ", "arr "+images.get(0).getPath());

            ImageView imageView = (ImageView) findViewById((R.id.imageViewIconeFoto));
            Glide.with(imageView.getContext()).load(images.get(0).getPath()).apply(RequestOptions.bitmapTransform(new CenterCrop()))
                    .into(imageView);


        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void uploadProfilePicture(){

        //pega o caminho do arquivo a ser enviado
        Uri file = Uri.fromFile(new File(images.get(0).getPath()));

        //cria a referencia para o arquivo no caminho a ser enviado, pasta UsersProfilePictures > [ID_do_usuário_logado] > [nome_do_arquivo]
        //se o caminho não existir ele é criado, se já existir as imagens são enviadas para ele, portanto enviar duas imagens com o mesmo nome resulta na sobrescrita da anterior
        final StorageReference imageProfileRef = storageRef.child("UsersProfilePictures/"+firebasePreferences.getId()+"/"+file.getLastPathSegment());

        //cria os metadados
        StorageMetadata metadata = new StorageMetadata.Builder().setContentType("image/jpg").build();

        //faz upload do arquivo junto com os metadados
        UploadTask uploadTask = imageProfileRef.putFile(file, metadata);

        //monitora o andamento do upload
        uploadTask
                //monitora caso de falha
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getBaseContext(),"Erro ao enviar a imagem", Toast.LENGTH_LONG).show();
            }
        })
                //monitora caso de sucesso
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Toast.makeText(getBaseContext(),"Imagem enviada com sucesso", Toast.LENGTH_LONG).show();
                profilePictureName = imageProfileRef.getName();

                //atualiza a foto do usuario local
                localUser.setFotoPerfil(imageProfileRef.getName());
//                Toast.makeText(getBaseContext(),"foto do user local " + localUser.getFotoPerfil(), Toast.LENGTH_LONG).show();

                firebasePreferences = new FirebasePreferences(EditPerfilActivity.this);
                firebasePreferences.SaveUserPreferences(localUser.getId(), localUser.getNome(), localUser.getEmail(), localUser.getFotoPerfil(), localUser.getEndereco());

                //atualiza a referencia a foto no banco
                databaseReference.child("users").child(PerfilActivity.id).child("fotoPerfil").setValue(localUser.getFotoPerfil());
                PerfilActivity.fotoPerfil = localUser.getFotoPerfil();
            }
        })
                //monitora o progresso
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
             @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                 if (progress < 100.0){
                     //deixa o loading visivel
                    loading.setVisibility(View.VISIBLE);
                 }else{
                     //deixa o loading invisível
                     loading.setVisibility(View.GONE);
                 }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_perfil, menu);
        return true;
    }

}
