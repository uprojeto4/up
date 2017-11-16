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

import br.ufc.quixada.up.Activities.LoginActivity;
import br.ufc.quixada.up.DAO.FirebaseConfig;
import br.ufc.quixada.up.Models.Post;
import br.ufc.quixada.up.Models.User;

/**
 * Created by Isaac Bruno on 20/10/2017.
 */

public class FirebasePreferences {

    DatabaseReference databaseReference;
    protected int countPost;
    boolean ready = false;
    private Context context;
    private SharedPreferences preferences;
    private String NOME_ARQUIVO = "br.ufc.quixada.up.FirebasePreferences";
    private int MODE = 0;
    private SharedPreferences.Editor editor;

    private final String CHAVE_ID = "userLoggedId";
    private final String CHAVE_NOME = "userLoggedName";
    private final String CHAVE_EMAIL = "userLoggedEmail";

    public FirebasePreferences(Context context){
        this.context = context;
        preferences = context.getSharedPreferences(NOME_ARQUIVO, MODE);

        editor = preferences.edit();
    }

    public void SaveUserPreferences(String userId, String userName, String userEmail){
        editor.putString(CHAVE_ID, userId);
        editor.putString(CHAVE_NOME, userName);
        editor.putString(CHAVE_EMAIL, userEmail);
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

    public int getCountPost() {
        return countPost;
    }
    public void setCountPost(int countPost) {
        this.countPost = countPost;
    }


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
//    }
}
