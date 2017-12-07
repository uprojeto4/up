package br.ufc.quixada.up.Models;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nguyenhoanglam.imagepicker.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import br.ufc.quixada.up.Activities.MainActivity;
import br.ufc.quixada.up.Adapters.PostAdapter;
import br.ufc.quixada.up.DAO.FirebaseConfig;
import br.ufc.quixada.up.R;
import br.ufc.quixada.up.Utils.DateTimeControl;
import br.ufc.quixada.up.TesteActivity;

import static br.ufc.quixada.up.R.layout.post;

/**
 * Created by Isaac Bruno on 09/10/2017.
 */

    public class Post {

    private String title;
    private String subtitle;
    private Double price;
    private int qtd;
    private String categoria;
    private int ups;
    private String userId;
    private ArrayList<String> pictures;
    private String id;
    private ArrayList<String> upsList = new ArrayList<String>();
    private byte[] imageCover;
    private MainActivity activity;

    Post postTemp;
    private long dataCadastro;

//    private List<String> upsList;

    int i;



    public void save(){
        DatabaseReference databaseReference = FirebaseConfig.getDatabase();
        databaseReference.child("posts").child(getId()).setValue(this);
    }

    public ArrayList<String> getUpsList() {
        return upsList;
    }

    public void setUpsList(ArrayList<String> upsList) {
        this.upsList = upsList;
    }

    public int getUps() {
        return ups;
    }

    private void setUps() {
        this.ups = upsList.size();
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public int getQtd() {
        return qtd;
    }

    public void setQtd(int qtd) {
        this.qtd = qtd;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ArrayList<String> getPictures() {
        if(pictures != null){
            return pictures;
        }
        return null;
    }

    public void addPictureName(String imageName) {
        this.pictures.add(imageName);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setImageCover(byte[] image) {
        this.imageCover = image;
    }

    public byte[] getImageCover(){
        return this.imageCover;
    }

    public long getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(long dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public int getDefaultImage(){
        return R.drawable.default_img;
    }

    public void up(final String uid, final String id){

//        final Activity main = mainActivity;

        DatabaseReference postRef = FirebaseConfig.getDatabase().child("posts").child(id);
        postRef.runTransaction(new Transaction.Handler() {

            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {

                Post post = mutableData.getValue(Post.class);
//                System.out.println("test " + post);
                if (post == null) {
                    return Transaction.success(mutableData);
                }

                ArrayList<String> aux = post.getUpsList();
                if (post.getUpsList().contains(uid)) {
                // Unstar the post and remove self from stars
                    aux.remove(uid);
                    System.out.println("posts: "+post.getUpsList());
                    MainActivity.getInstance().up(false);

                } else {
                // Star the post and add self to stars
                    aux.add(uid);
                    System.out.println("posts3 enois: "+post.getUpsList());
                    MainActivity.getInstance().up(true);

                }
                post.setUpsList(aux);
                post.setUps();

                // Set value and report transaction success
                mutableData.setValue(post);
//                mutableData.child("upsList").setValue(post.getUpsList());
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d("TAG", "postTransaction:onComplete:" + databaseError);
                Log.d("TAG", "postTransaction:onComplete:" + dataSnapshot.child("ups").getValue());
            }
        });
    }

    public int[] scaleImage(double width, double height){

        Double h;
        Double w;
        Double scale;
        int[] dimensoes = {0,0};

        if (width > height){
            scale=new Double(68000/width);
            w =new Double(680);
            h = new Double(height*(scale/100));
            Log.d("Widht_Height", ""+h);
            Log.d("Widht_Height", ""+w);
            dimensoes[0] = w.intValue();
            dimensoes[1] = h.intValue();
            return dimensoes;
//            file = Bitmap.createScaledBitmap(file, w.intValue(), h.intValue(), false);
        }else if (height > width){
            scale=new Double(68000/height);
            h =new Double(680);
            w = new Double(width*(scale/100));
            Log.d("Widht_Height", ""+h);
            Log.d("Widht_Height", ""+w);
            dimensoes[0] = w.intValue();
            dimensoes[1] = h.intValue();
            return dimensoes;
//            file = Bitmap.createScaledBitmap(file, w.intValue(), h.intValue(), false);
        }else{
            dimensoes[0] = 680;
            dimensoes[1] = 680;
            return dimensoes;
//            file = Bitmap.createScaledBitmap(file, 680, 680, false);
        }
    }

    public void setActivity (MainActivity activity){
        this.activity = activity;
    }

    public void upload(final ArrayList<Image> images) {
        //pega a referencia
        DatabaseReference databaseReference = FirebaseConfig.getDatabase();
        StorageReference storageReference = FirebaseConfig.getStorage();
        //cria a variavel para receber a referencia
        StorageReference imageRef;
        pictures = new ArrayList<String>();

        //cria nó no banco e retorna id
        setId(databaseReference.child("posts").push().getKey());

        i = 1;
        //Faz upload das novas imagens para o servidor.
        //pega o caminho do arquivo a ser enviado
            for (Image image : images){
                byte[] imageData=null;
                //pega o caminho da imagem no disco
//            Uri file = Uri.fromFile(new File(image.getPath()));
                Bitmap file = BitmapFactory.decodeFile(image.getPath());

//                int width = file.getWidth();
//                int height = file.getHeight();
//
//                Double h;
//                Double w;
//                Double scale;

                int[] dimensoes = scaleImage(file.getWidth(),file.getHeight());

                file = Bitmap.createScaledBitmap(file, dimensoes[0], dimensoes[1], false);
//                if (width > height){
//                    scale=new Double(68000/width);
//                    w =new Double(680);
//                    h = new Double(height*(scale/100));
//                    Log.d("Widht_Height", ""+h);
//                    Log.d("Widht_Height", ""+w);
//                    file = Bitmap.createScaledBitmap(file, w.intValue(), h.intValue(), false);
//                }else if (height > width){
//                    scale=new Double(68000/height);
//                    h =new Double(680);
//                    w = new Double(width*(scale/100));
//                    Log.d("Widht_Height", ""+h);
//                    Log.d("Widht_Height", ""+w);
//                    file = Bitmap.createScaledBitmap(file, w.intValue(), h.intValue(), false);
//                }else{
//                }

//            .fromFile(new File(image.getPath()));
                //cria a referencia para o arquivo no caminho a ser enviado, pasta UsersProfilePictures > [ID_do_usuário_logado] > [nome_do_arquivo]
                //se o caminho não existir ele é criado, se já existir as imagens são enviadas para ele, portanto enviar duas imagens com o mesmo nome resulta na sobrescrita da anterior
                imageRef = storageReference.child("PostsPictures/" + getId() + "/" + image.getName());
                final String imageName = imageRef.getName();
                //cria os metadados
            StorageMetadata metadata = new StorageMetadata.Builder().setContentType("image/jpg").build();

            ByteArrayOutputStream fileStream = new ByteArrayOutputStream();
            file.compress(Bitmap.CompressFormat.JPEG, 50, fileStream);
            imageData = fileStream.toByteArray();
            //faz upload do arquivo junto com os metadados
//            final UploadTask uploadTask = imageRef.putFile(file, metadata);
            final UploadTask uploadTask = imageRef.putBytes(imageData, metadata);
            //monitora o andamento do upload
            uploadTask
                    //monitora caso de falha
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(context, "Erro ao enviar a imagem", Toast.LENGTH_LONG).show();
                            Log.d("TAG", "Erro ao enviar a imagem");
                        }
                    })
                    //monitora caso de sucesso
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            Log.d("TAG2", "Imagem enviada com sucesso "+downloadUrl);
//                    Toast.makeText(context, "Imagem enviada com sucesso", Toast.LENGTH_LONG).show();
                            addPictureName(imageName);

                        }
                    }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(i == images.size()){
//                        downloadImages("PostsPictures/" + getId() + "/" +getPictures().get(0));
                        Log.d("TAG", "Anuncio Inserido com Sucesso!");
                        save();

                    }else{
                        i++;
                    }
                }
            });

            //         #monitora o progresso
            //        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            //            @Override
            //            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
            //                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            //                if (progress < 100.0){
            //                    //deixa o loading visivel
            //                    loading.setVisibility(View.VISIBLE);
            //                }else{
            //                    //deixa o loading invisível
            //                    loading.setVisibility(View.GONE);
            //                }
            //            }
            //        });

        }
    }

    public void downloadImages(String path, final PostAdapter postAdapter, final Post post){
        StorageReference storageReference = FirebaseConfig.getStorage();
        StorageReference imageRef;

        this.postTemp = post;

        //recupera apenas a primeira imagem
        imageRef = storageReference.child("PostsPictures/" + getId() + "/" + path);

        try{
            //cria o arquivo temporário local onde a imagem será armazenada
            final File localFile = File.createTempFile("jpg", "image");
             imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                //monitora o sucesso do download
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    //transforma a imagem baixada em um bitmap
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    Log.d("TAG", ""+localFile);
                    //transforma o bitmap em stream
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    //transforma o stream em um array de bytes
//                    byte[] picture = stream.toByteArray();
                    postTemp.setImageCover(stream.toByteArray());
                    //método que aplica a imagem nos lugares desejsdos
//                    applyImage(pictureCover);
                    Log.d("TAG","Imagem baixada com sucesso! ");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                //monitora a falha do downlaod
                public void onFailure(@NonNull Exception e) {
//                    Toast.makeText(context,"Imagem não foi baixada", Toast.LENGTH_SHORT).show();
                    Log.d("TAG","Imagem não foi baixada! "+e);
                }
            }).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                    Log.d("Entrou2", "entrou2");
                    postAdapter.notifyDataSetChanged();
//                    Log.d("TAG", " "+task.getResult().getTotalByteCount());
//                    MainActivity.getInstance().mudarImage(postTemp.getPictures().get(0));
                }
            });
        } catch (IOException e){
            e.printStackTrace();
            //manipular exceções
            Log.e("Main", "IOE exception");
        }
    }

    @Override
    public String toString() {
        return "Post{" +
                "title='" + title + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", price=" + price +
                ", qtd=" + qtd +
                ", categoria='" + categoria + '\'' +
                ", ups=" + ups +
                ", upsList=" + upsList +
                '}';
    }
}
