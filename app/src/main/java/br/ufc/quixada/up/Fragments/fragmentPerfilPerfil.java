package br.ufc.quixada.up.Fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import br.ufc.quixada.up.Activities.PerfilActivity;
import br.ufc.quixada.up.DAO.FirebaseConfig;
import br.ufc.quixada.up.Models.User;
import br.ufc.quixada.up.R;
import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static br.ufc.quixada.up.DAO.FirebaseConfig.auth;
import static br.ufc.quixada.up.R.drawable.image_test_1;

/**
 * Created by Brendon on 09/10/2017.
 */

public class fragmentPerfilPerfil extends Fragment {

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference profilePictureRef;
    TextView nome;
    public byte[] image;
    Bitmap bitmap;
    public boolean test;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        //cria a referencia para a imagem a ser baixada
        profilePictureRef = storage.getReference().child("UsersProfilePictures/"+PerfilActivity.id+"/"+PerfilActivity.fotoPerfil);
//        Toast.makeText(getActivity(),"caminho da imagem que vai ser baixada "+profilePictureRef.getPath(), Toast.LENGTH_LONG).show();

        test = profilePictureRef.getName().equals(PerfilActivity.fotoPerfil);

        if (test){
            downloadProfilePicture();
        }else{
            //nada
        }

        //chama o metodo de download
//        downloadProfilePicture();

        return inflater.inflate(R.layout.fragment_perfil_perfil, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nome = (TextView) getView().findViewById(R.id.user_profile_name);
        nome.setText(PerfilActivity.nome);

        //apos a view ser criada chama o metodo de download das imagens
//        downloadProfilePicture();

    }

    public void downloadProfilePicture(){
        //o download com o metodo getFile deve ser feito num try/catch
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
//                    Toast.makeText(getActivity(),profilePictureRef.getName(), Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                //monitora a falha do downlaod
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(),"Foto não encontrada", Toast.LENGTH_LONG).show();
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
        MultiTransformation multi = new MultiTransformation(
                new BlurTransformation(25));


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
                .apply(RequestOptions.bitmapTransform(multi))
                //insere a imagem no imageView
                .into((ImageView) getView().findViewById((R.id.header_cover_image)));

        Glide.with(this).load(bytes)
                .apply(requestOptions)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(200)))
                .into((ImageView) getView().findViewById((R.id.profile_image)));
    }

    @Override
    public void onResume() {
        super.onResume();


        test = profilePictureRef.getName().equals(PerfilActivity.fotoPerfil);

        profilePictureRef = storage.getReference().child("UsersProfilePictures/"+PerfilActivity.id+"/"+PerfilActivity.fotoPerfil);
        if (!test){
            downloadProfilePicture();
        }else{
            //nada
        }


    }
}
