package br.ufc.quixada.up.DAO;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import br.ufc.quixada.up.Models.User;

/**
 * Created by Isaac Bruno on 20/10/2017.
 */

public class FirebaseConfig {

    public static DatabaseReference database;
    public static FirebaseAuth auth;
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

//    public static FirebaseStorage getStorage(){
//        if (storage == null){
//            storage = FirebaseStorage.getInstance();
//        }
//        return storage;
//    }
}
