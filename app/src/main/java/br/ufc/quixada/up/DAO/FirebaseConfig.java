package br.ufc.quixada.up.DAO;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

import br.ufc.quixada.up.Models.User;

/**
 * Created by Isaac Bruno on 20/10/2017.
 */

public class FirebaseConfig {

    public static DatabaseReference database;
    public static FirebaseAuth auth;
    public static StorageReference storage;
//    public static FirebaseStorage storage;

    public static DatabaseReference getDatabase(){
        if(database == null){
            database = FirebaseDatabase.getInstance().getReference();
        }

        return database;
    }

    public static FirebaseAuth getAuth(){
        if(auth == null){
            auth = FirebaseAuth.getInstance();
        }

        return auth;
    }

    public static StorageReference getStorage(){
        if (storage == null){
            storage = FirebaseStorage.getInstance().getReference();
        }
        return storage;
    }

//    public static void downloadImages(String path){
//        StorageReference storageReference = FirebaseConfig.getStorage();
//        StorageReference imageRef;
//
//        //recupera apenas a primeira imagem
//        imageRef = storageReference.child(path);
//
//        try{
//            //cria o arquivo temporário local onde a imagem será armazenada
//            final File localFile = File.createTempFile("jpg", "image");
//             imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                @Override
//                //monitora o sucesso do download
//                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                    //transforma a imagem baixada em um bitmap
//                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
//                    Log.d("TAG", ""+localFile);
//                    //transforma o bitmap em stream
//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                    //transforma o stream em um array de bytes
//                    byte[] picture = stream.toByteArray();
//                    //método que aplica a imagem nos lugares desejsdos
////                    applyImage(pictureCover);
//                    Log.d("TAG","Imagem baixada com sucesso!");
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                //monitora a falha do downlaod
//                public void onFailure(@NonNull Exception e) {
////                    Toast.makeText(context,"Imagem não foi baixada", Toast.LENGTH_SHORT).show();
//                    Log.d("TAG","Imagem não foi baixada! "+e);
//                }
//            }).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
//                    Log.d("TAG", " "+task.getResult().getTotalByteCount());
//                }
//            });
//        } catch (IOException e){
//            e.printStackTrace();
//            //manipular exceções
//            Log.e("Main", "IOE exception");
//        }
//    }

//    public static void uploadImages(ArrayList<Image> images, String path){
//        StorageReference storageReference = getStorage();
//        StorageReference imageRef;
//        for (Image image : images){
//            Uri file = Uri.fromFile(new File(image.getPath()));
//
//            //se o caminho não existir ele é criado, se já existir as imagens são enviadas para ele, portanto enviar duas imagens com o mesmo nome resulta na sobrescrita da anterior
//            imageRef = storageReference.child(path + file.getLastPathSegment());
//
//            //cria os metadados
//            StorageMetadata metadata = new StorageMetadata.Builder().setContentType("image/jpg").build();
//
//            //faz upload do arquivo junto com os metadados
//            final UploadTask uploadTask = imageRef.putFile(file, metadata);
//            //monitora o andamento do upload
//            uploadTask
//                    //monitora caso de falha
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
////                            Toast.makeText(context, "Erro ao enviar a imagem", Toast.LENGTH_LONG).show();
//                            Log.d("TAG", "Erro ao enviar a imagem");
//                        }
//                    })
//                    //monitora caso de sucesso
//                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
//                            Log.d("TAG", "Imagem enviada com sucesso "+downloadUrl);
//
//                        }
//                    }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
////                    if(i == images.size()){
////                        teste();
////                        Log.d("TAG", "Anuncio Inserido com Sucesso!");
////
////                    }else{
////                        i++;
////                    }
//                }
//            });

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

//        }
//    }
}
