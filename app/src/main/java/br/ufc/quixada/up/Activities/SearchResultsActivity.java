package br.ufc.quixada.up.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

import br.ufc.quixada.up.Adapters.SearchResultsAdapter;
import br.ufc.quixada.up.Interfaces.RecyclerViewOnClickListener;
import br.ufc.quixada.up.Models.Post;
import br.ufc.quixada.up.R;

public class SearchResultsActivity extends BaseActivity implements RecyclerViewOnClickListener {

    Post post;

    SearchResultsAdapter searchResultsAdapter;

    RecyclerView recyclerView;

    LinearLayout notFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        notFound = (LinearLayout) findViewById(R.id.notfound);

        recyclerView = (RecyclerView) findViewById(R.id.searchResults);
        recyclerView.hasFixedSize();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        searchResultsAdapter = new SearchResultsAdapter(this, MainActivity.searchPosts);
        searchResultsAdapter.setRecyclerViewOnClickListener(this);
        recyclerView.setAdapter(searchResultsAdapter);


        if (MainActivity.searchPosts.size() == 0){
            notFound.setVisibility(View.VISIBLE);
        } else{
            for (Post post : MainActivity.searchPosts){
                post.downloadImagesForSearchResult(post.getPictures().get(0), searchResultsAdapter, post);
            }
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_perfil, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();

        getSupportActionBar().setTitle(intent.getStringExtra("searchTerm"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainActivity.searchPosts = new ArrayList<Post>();
    }

    @Override
    public void onClickListener(View view, int position) {
        Intent intent = new Intent(getBaseContext(), AnuncioActivity.class);
        intent.putExtra("positionSearch", position);
        Log.d("posicao", position+"");
        startActivity(intent);
    }
}
