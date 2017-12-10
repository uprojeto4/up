package br.ufc.quixada.up.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.ufc.quixada.up.Activities.LoginActivity;
import br.ufc.quixada.up.DAO.FirebaseConfig;
import br.ufc.quixada.up.Models.Address;
import br.ufc.quixada.up.Models.Post;
import br.ufc.quixada.up.Models.User;

/**
 * Created by Isaac Bruno on 20/10/2017.
 */

public class FirebasePreferences {

    DatabaseReference databaseReference;
    private Context context;
    private SharedPreferences preferences;
    private String NOME_ARQUIVO = "br.ufc.quixada.up.FirebasePreferences";
    private int MODE = 0;
    private SharedPreferences.Editor editor;

    private final String CHAVE_ID = "userLoggedId";
    private final String CHAVE_NOME = "userLoggedName";
    private final String CHAVE_EMAIL = "userLoggedEmail";
    private final String CHAVE_FOTO_PERFIL = "userLoggedProfilePicture";
    private final String CHAVE_ENDERECO = "userAdress";
    private final String CHAVE_NUM_VENDAS = "userNumVendas";
    private final String CHAVE_AV_VENDEDOR = "userAvVendedor";
    private final String CHAVE_NUM_COMPRAS = "userNumCompras";
    private final String CHAVE_AV_COMPRADOR = "userAvComprador";


    public FirebasePreferences(Context context){
        this.context = context;
        preferences = context.getSharedPreferences(NOME_ARQUIVO, MODE);

        editor = preferences.edit();
    }

    public void SaveUserPreferences(String userId, String userName, String userEmail, String profilePicture, Address adress,
                                    int numVendas, float avVendedor, int numCompras, float avComprador){
        editor.putString(CHAVE_ID, userId);
        editor.putString(CHAVE_NOME, userName);
        editor.putString(CHAVE_EMAIL, userEmail);
        editor.putString(CHAVE_FOTO_PERFIL, profilePicture);
        editor.putString(CHAVE_ENDERECO, adress.toString());
        editor.putInt(CHAVE_NUM_VENDAS, numVendas);
        editor.putFloat(CHAVE_AV_VENDEDOR, avVendedor);
        editor.putInt(CHAVE_NUM_COMPRAS, numCompras);
        editor.putFloat(CHAVE_AV_COMPRADOR, avComprador);
        editor.commit();
    }

    public void clearUserPreferences(){
        editor.clear();
        editor.commit();
    }

    public String getId () {
        return preferences.getString(CHAVE_ID, null);
    }
    public String getUserName(){
        return preferences.getString(CHAVE_NOME, null);
    }
    public String getUserEmail(){
        return preferences.getString(CHAVE_EMAIL, null);
    }
    public String getProfilePicture(){
        return preferences.getString(CHAVE_FOTO_PERFIL, null);


    //uso de transections
//    public int setCountPosts(){
//        databaseReference = FirebaseConfig.getDatabase();
//        DatabaseReference countPostRef = databaseReference.child("countPost");
//
//        countPostRef.runTransaction(new Transaction.Handler() {
//            @Override
//            public Transaction.Result doTransaction(MutableData mutableData) {
//
//                if (mutableData.getValue() == null) {
//                    mutableData.setValue(1);
//                    return Transaction.success(mutableData);
//                }else{
//                    countPost = mutableData.getValue(Integer.class);
//                    countPost += 1;
//                    mutableData.setValue(countPost);
//                }
//                // Set value and report transaction success
//                return Transaction.success(mutableData);
//            }
//
//            @Override
//            public void onComplete(DatabaseError databaseError, boolean b,
//                                   DataSnapshot dataSnapshot) {
//                // Transaction completed
//                Log.d("TAG", "postTransaction:onComplete:" + databaseError);
//            }
//        }, true);
//
//        return countPost;
//        DatabaseReference countPostRef = databaseReference.child("countPost");
//        countPostRef.addValueEventListener(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
//                    countPost = dataSnapshot.getValue(Integer.class);
//                    Log.d("Print",""+countPost);
//                    countPost = countPost+1;
//                    Log.d("Print",""+countPost);
//                    Log.d("Print",""+countPost);
//
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
////                Log.e(TAG, "onCancelled", databaseError.toException());
//            }
//        });
//
//        databaseReference.child("countPost").setValue(countPost);
    }
    public String getAdress(){
        return preferences.getString(CHAVE_ENDERECO, null);
    }
    public int getNumVendas(){
        return preferences.getInt(CHAVE_NUM_VENDAS, 0);
    }
    public float getAvVendas(){
        return preferences.getFloat(CHAVE_AV_VENDEDOR, 0);
    }
    public int getNumCompras(){
        return preferences.getInt(CHAVE_NUM_COMPRAS, 0);
    }
    public float getAvCompras(){
        return preferences.getFloat(CHAVE_AV_COMPRADOR, 0);
    }

}
