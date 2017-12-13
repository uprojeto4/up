package br.ufc.quixada.up.Models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import br.ufc.quixada.up.Adapters.NegociacoesAdapter;
import br.ufc.quixada.up.Adapters.PostAdapter;
import br.ufc.quixada.up.DAO.FirebaseConfig;
import br.ufc.quixada.up.Utils.DateTimeControl;

/**
 * Created by Macelo on 14/11/2017.
 */

public class Negociacao {
// remoteUserId eh o usuário remoto, a outra parte com quem se negocia
// vendorId eh o vendedor
    private int unreadMessagesCounter;
    private String title;
    private String remoteUserId;
    private String vendorId;
    private String vendorName;
    private String adId;
    private String lastMessage;
    private String lastMessageSenderId;
    private long startDate;
    private long lastMessageTime;
    private String messagesId;
    private int status;
    private byte[] imageCover;
    private Negociacao negotiationTemp;
    private DatabaseReference reference = FirebaseConfig.getDatabase();

    public Negociacao() { }

    public Negociacao(String messagesId, String remoteUserId, String vendorId, String adId, String lastMessage, String lastMessageSenderId) {
        this.messagesId = messagesId;
        this.remoteUserId = remoteUserId;
        this.vendorId = vendorId;
        this.adId = adId;
        this.lastMessage = lastMessage;
        this.lastMessageSenderId = lastMessageSenderId;
        this.startDate = DateTimeControl.getCurrentDateTime();
        this.lastMessageTime = DateTimeControl.getCurrentDateTime();
        this.status = Constant.OPENED_NEGOTIATION;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getUnreadMessagesCounter() {
        return unreadMessagesCounter;
    }

    public void setUnreadMessagesCounter(int unreadMessagesCounter) {
        this.unreadMessagesCounter = unreadMessagesCounter;
    }

    public String getRemoteUserId() {
        return remoteUserId;
    }

    public void setRemoteUserId(String vendorId) {
        this.remoteUserId = vendorId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastMessageSenderId() {
        return lastMessageSenderId;
    }

    public void setLastMessageSenderId(String lastMessageSenderId) {
        this.lastMessageSenderId = lastMessageSenderId;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public String getMessagesId() {
        return messagesId;
    }

    public void setMessagesId(String chatId) {
        this.messagesId = chatId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public long getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(long lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public byte[] getImageCover() {
        return imageCover;
    }

    public void setImageCover(byte[] imageCover) {
        this.imageCover = imageCover;
    }

    public void downloadCover(final NegociacoesAdapter adapter, final Negociacao negociacao){

        final StorageReference storageReference = FirebaseConfig.getStorage();
        this.negotiationTemp = negociacao;

        Log.d("imageNameTAG", "" + negociacao);

        reference.child("posts").child(negociacao.getAdId()).child("pictures").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String fileName = dataSnapshot.child("0").getValue(String.class);
                final StorageReference imageRef = storageReference.child("PostsPictures/" + negociacao.getAdId() + "/thumb_" + fileName);
                try {
                    final File localFile = File.createTempFile("jpg", "image");
                    imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            negociacao.setImageCover(stream.toByteArray());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                            adapter.notifyDataSetChanged();
                        }
                    });
                } catch (IOException e){
                    e.printStackTrace();
                    Log.e("Main", "IOE exception");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
