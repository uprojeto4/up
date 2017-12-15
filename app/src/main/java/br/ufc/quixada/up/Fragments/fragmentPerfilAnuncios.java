package br.ufc.quixada.up.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.ufc.quixada.up.Activities.PerfilActivity;
import br.ufc.quixada.up.Activities.PerfilPublicoActivity;
import br.ufc.quixada.up.Adapters.PostAdapter;
import br.ufc.quixada.up.DAO.FirebaseConfig;
import br.ufc.quixada.up.Models.Post;
import br.ufc.quixada.up.R;

/**
 * Created by Brendon on 09/10/2017.
 */

public class fragmentPerfilAnuncios extends Fragment{

    private RecyclerView recyclerViewMeusAnuncios;
    private LinearLayoutManager linearLayoutManager;
    private PostAdapter meusAnunciosAdapter;
    private ArrayList<Post> posts = new ArrayList<Post>();
    private Post post;
    private DatabaseReference postsReference = FirebaseConfig.getDatabase().child("posts");
    private String userId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_perfil_anuncios, container, false);

        recyclerViewMeusAnuncios = rootView.findViewById(R.id.recyclerViewMeusAnuncios);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewMeusAnuncios.setLayoutManager(linearLayoutManager);

        System.out.println("fragment " + PerfilPublicoActivity.anuncianteId);

        if (PerfilPublicoActivity.anuncianteId != null) {
            postsReference.orderByChild("userId").equalTo(PerfilPublicoActivity.anuncianteId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                        post = singleSnapshot.getValue(Post.class);

                    if(!posts.contains(post)){
                        post.downloadImageCover(post.getPictures().get(0), meusAnunciosAdapter, post);
                      }
                        meusAnunciosAdapter.addTopListItem(post);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("oops", databaseError.getMessage());
                }
            });
        } else {
            postsReference.orderByChild("userId").equalTo(PerfilActivity.anuncianteId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                        post = singleSnapshot.getValue(Post.class);


                        if(!posts.contains(post)){
                            post.downloadImages(post.getPictures().get(0), meusAnunciosAdapter, post);
                        }
                        meusAnunciosAdapter.addTopListItem(post);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("oops", databaseError.getMessage());
                }
            });
        }

        meusAnunciosAdapter = new PostAdapter(rootView.getContext(), posts);
        recyclerViewMeusAnuncios.setAdapter(meusAnunciosAdapter);

        return rootView;
    }

}
