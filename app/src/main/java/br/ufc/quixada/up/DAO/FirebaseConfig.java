package br.ufc.quixada.up.DAO;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Isaac Bruno on 20/10/2017.
 */

public class FirebaseConfig {

    public static DatabaseReference firebase;
    public static FirebaseAuth auth;

    public static DatabaseReference getFirebase(){
        if(firebase == null){
            firebase = FirebaseDatabase.getInstance().getReference();
        }

        return firebase;
    }

    public static FirebaseAuth getAuth(){
        if(auth == null){
            auth = FirebaseAuth.getInstance();
        }

        return auth;
    }

}
