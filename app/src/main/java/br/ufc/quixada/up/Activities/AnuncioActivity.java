package br.ufc.quixada.up.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

import br.ufc.quixada.up.Models.Post;
import br.ufc.quixada.up.R;
import br.ufc.quixada.up.Utils.ChatControl;

public class AnuncioActivity extends AppCompatActivity {

    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncio);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabNegotiate);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ChatControl.startConversation("remoteUserId", "productId");
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if(intent != null){
            position = intent.getIntExtra("position", -1);
            start();
        }
    }

    public void start(){
        Post post = MainActivity.posts.get(position);
//        Toast.makeText(this, "opa é nois"+post.getTitle(), Toast.LENGTH_SHORT).show();

        TextView title = (TextView)findViewById(R.id.textView_title);
        TextView subtitle = (TextView)findViewById(R.id.textView_describ);
        TextView price = (TextView)findViewById(R.id.textView_price);
        Spinner qtd = (Spinner) findViewById(R.id.spinner);

        title.setText(post.getTitle());
        subtitle.setText(post.getSubtitle());
        price.setText("R$ "+post.getPrice());

        ArrayList<Integer> qtdList = new ArrayList<Integer>();
        for (int i = 0; i < post.getQtd(); i++){
            qtdList.add(i+1);
        }

        ArrayAdapter qtdAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, qtdList);
        qtd.setAdapter(qtdAdapter);

    }

}
