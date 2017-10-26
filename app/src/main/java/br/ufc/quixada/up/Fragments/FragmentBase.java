package br.ufc.quixada.up.Fragments;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import br.ufc.quixada.up.Models.User;

/**
 * Created by Brendon on 25/10/2017.
 */

public class FragmentBase extends Fragment {

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference databaseReference;

    User localUser;

    public void updateLocalUser(){

        Query email = databaseReference.child("users").orderByChild("email").equalTo(user.getEmail());
        email.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    localUser = singleSnapshot.getValue(User.class);
//                    Toast.makeText(getBaseContext(), "Olá: "+ localUser, Toast.LENGTH_SHORT).show();
//                    textViewName.setText(localUser.getNome());
//                    textViewEmail.setText(localUser.getEmail());
                    updateProfile();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
//                Log.e(TAG, "onCancelled", databaseError.toException());
                Toast.makeText(getActivity(), "Usuário não autorizado!", Toast.LENGTH_SHORT).show();
            }
        });

//        ValueEventListener userListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // Get Post object and use the values to update the UI
////                String s = dataSnapshot.child("users").child("01").getValue(String.class);
//                User user = dataSnapshot.child("user").child("aXNhYWMtcGpAaG90bWFpbC5jb20=").getValue(User.class);
//                Toast.makeText(getBaseContext(), "Opa: " + user, Toast.LENGTH_LONG).show();
//                // ...
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Getting Post failed, log a message
//                Toast.makeText(getBaseContext(), "Opa, deu merda!", Toast.LENGTH_LONG).show();
//                // ...
//            }
//        };
        //Executa sempre que os dados mudarem
//        databaseReference.addValueEventListener(userListener);

        //Executa apenas uma vez
//        databaseReference.addListenerForSingleValueEvent(userListener);

    }


    public void updateProfile(){
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(localUser.getNome())
                .build();

        user = auth.getCurrentUser();

        if(user != null){
            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getActivity(), "Olá "+ user.getDisplayName() +"! :)", Toast.LENGTH_LONG).show();
                }
            });
        }
    }


}
