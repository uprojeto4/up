package br.ufc.quixada.up.Models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
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
import java.util.ArrayList;

import br.ufc.quixada.up.DAO.FirebaseConfig;
import br.ufc.quixada.up.R;

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

    int i = 1;



    public void save(){
        DatabaseReference databaseReference = FirebaseConfig.getDatabase();
        databaseReference.child("posts").child(getId()).setValue(this);
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

//    public void setImage(byte[] image) {
//        this.image = image;
//    }

    public int getDefaultImage(){
        return R.drawable.default_img;
    }

    public void up(final String uid, final String id){
        DatabaseReference postRef = FirebaseConfig.getDatabase().child("posts").child(id);
        postRef.runTransaction(new Transaction.Handler() {

            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {

                Post post = mutableData.getValue(Post.class);
                if (post == null) {
                    return Transaction.success(mutableData);
                }

                System.out.println("posts: "+post.upsList);

                if (post.upsList.contains(uid)) {
                // Unstar the post and remove self from stars
                    post.upsList.remove(uid);
                    System.out.println("posts: "+post.upsList);
                } else {
                // Star the post and add self to stars
                    post.upsList.add(uid);
                    System.out.println("posts2 enois: "+post.upsList);
                }
                post.setUps();

                // Set value and report transaction success
                mutableData.setValue(post);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d("TAG", "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    public void upload(final ArrayList<Image> images) {
        DatabaseReference databaseReference = FirebaseConfig.getDatabase();
        StorageReference storageReference = FirebaseConfig.getStorage();
        StorageReference imageRef;
        pictures = new ArrayList<String>();

        setId(databaseReference.child("posts").push().getKey());

        //Faz upload das novas imagens para o servidor.
        //pega o caminho do arquivo a ser enviado
        for (Image image : images){
            Uri file = Uri.fromFile(new File(image.getPath()));
            //cria a referencia para o arquivo no caminho a ser enviado, pasta UsersProfilePictures > [ID_do_usuário_logado] > [nome_do_arquivo]
            //se o caminho não existir ele é criado, se já existir as imagens são enviadas para ele, portanto enviar duas imagens com o mesmo nome resulta na sobrescrita da anterior
            imageRef = storageReference.child("PostsPictures/" + getId() + "/" + file.getLastPathSegment());
            final String imageName = imageRef.getName();
            //cria os metadados
            StorageMetadata metadata = new StorageMetadata.Builder().setContentType("image/jpg").build();
            //faz upload do arquivo junto com os metadados
            final UploadTask uploadTask = imageRef.putFile(file, metadata);
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
//                    Toast.makeText(context, "Imagem enviada com sucesso", Toast.LENGTH_LONG).show();
                            addPictureName(imageName);
                            Log.d("TAG", "Imagem enviada com sucesso "+downloadUrl);

                        }
                    }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(i == images.size()){
                        downloadImages("PostsPictures/" + getId() + "/" +getPictures().get(0));
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

    public static void downloadImages(String path){
        StorageReference storageReference = FirebaseConfig.getStorage();
        StorageReference imageRef;

        //recupera apenas a primeira imagem
        imageRef = storageReference.child(path);

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
                    byte[] picture = stream.toByteArray();
                    //método que aplica a imagem nos lugares desejsdos
//                    applyImage(pictureCover);
                    Log.d("TAG","Imagem baixada com sucesso! "+picture);
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
                    Log.d("TAG", " "+task.getResult().getTotalByteCount());
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
                '}';
    }
}
