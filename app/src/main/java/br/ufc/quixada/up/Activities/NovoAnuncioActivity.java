package br.ufc.quixada.up.Activities;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;
import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;


import br.ufc.quixada.up.Adapters.NovoAnuncioRecyclerViewImageAdapter;
import br.ufc.quixada.up.R;
import br.ufc.quixada.up.Utils.InputMask;
import br.ufc.quixada.up.Utils.RecyclerViewPhotoSeparator;

public class NovoAnuncioActivity extends BaseActivity {

    ConstraintLayout mainConstraintLayout;
    RecyclerView recyclerView;
    LinearLayout linearLayoutNoImage;
    EditText tituloAnuncio;
    EditText descricaoAnuncio;
    EditText precoAnuncio;
    EditText qtdItensAnuncio;
    ArrayList<String> categorias = new ArrayList<>();
    Spinner spinnerCategoriasAnuncio;
    String spinnerCategoriasItemSelecionado = null;
    Button buttonSalvarAnuncio;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.ItemDecoration itemDecoration;
    private NovoAnuncioRecyclerViewImageAdapter imageAdapter;
    private ArrayList<Image> images = new ArrayList<>();
    Locale locale = new Locale("pt", "BR");

//    static final String SAVED_STATE_AD_TITLE = "";
//    static final String SAVED_STATE_AD_DESCRIPTION = "";
//    static final String SAVE_STATE_AD_CATEGORY = "";
//    static final int SAVED_STATE_PRICE

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_novo_anuncio);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        recyclerView = findViewById(R.id.recyclerView);
        linearLayoutNoImage = findViewById((R.id.layoutNoImage));
        tituloAnuncio = findViewById(R.id.input_novo_anuncio_titulo);
        descricaoAnuncio = findViewById(R.id.input_novo_anuncio_descricao);
        precoAnuncio = findViewById(R.id.input_novo_anuncio_preco);
        qtdItensAnuncio = findViewById(R.id.input_novo_anuncio_qtd);
        spinnerCategoriasAnuncio = findViewById(R.id.spinnerCategorias);
        buttonSalvarAnuncio = findViewById(R.id.buttonSalvarAnuncio);
        mainConstraintLayout = findViewById(R.id.mainConstraintLayout);

        // imageAdapter e implementações de swipe para deletar
        imageAdapter = new NovoAnuncioRecyclerViewImageAdapter(this);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        itemDecoration = new RecyclerViewPhotoSeparator(4);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        recyclerView.setAdapter(imageAdapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.UP | ItemTouchHelper.DOWN) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                imageAdapter.removeImage(viewHolder.getAdapterPosition());
                imagePreviewController();
            }

            @Override
            public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
                super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // máscara de preço
        TextWatcher quantidadeMask = InputMask.monetario(precoAnuncio);
        precoAnuncio.addTextChangedListener(quantidadeMask);

        categorias.add("Selecione...");
        categorias.addAll(Arrays.asList(getResources().getStringArray(R.array.categorias)));
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categorias) {

            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinnerCategoriasAnuncio.setAdapter(spinnerArrayAdapter);

        //spinner de categorias (valor é setado em spinnerCategoriasItemSelecionado)
        spinnerCategoriasAnuncio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position > 0) {
                    spinnerCategoriasItemSelecionado = (String) adapterView.getItemAtPosition(position);
                    Toast.makeText(getApplicationContext(), "Selecionado: " + spinnerCategoriasItemSelecionado, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        FloatingActionButton fabAddFotos = findViewById(R.id.fabAddFotos);

        fabAddFotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                String[] permissionsToRequest = new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
                PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(NovoAnuncioActivity.this, permissionsToRequest, new PermissionsResultAction() {

                    @Override
                    public void onGranted() {
                        getPictures(view);
                    }

                    @Override
                    public void onDenied(String permission) {
                        Snackbar.make(view, "Desculpe, mas precisamos de permissão para acessar seus arquivos", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
            }
        });
    }

    //configura e obtem imagens usando a library imagePicker
    protected void getPictures(View view){
        int maxSize = 10 - imageAdapter.getItemCount();
        if (maxSize == 0) {
            Snackbar.make(view, "Você só pode adicionar dez imagens", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else {
            ImagePicker.with(NovoAnuncioActivity.this)         //  Initialize ImagePicker with activity or fragment context
                       .setToolbarColor("#5a185f")             //  Toolbar color
                       .setStatusBarColor("#2e0035")           //  StatusBar color (works with SDK >= 21  )
                       .setToolbarTextColor("#FFFFFF")         //  Toolbar text color (Title and Done button)
                       .setToolbarIconColor("#FFFFFF")         //  Toolbar icon color (Back and Camera button)
                       .setProgressBarColor("#4CAF50")         //  ProgressBar color
                       .setBackgroundColor("#212121")          //  Background color
                       .setCameraOnly(false)                   //  Camera mode
                       .setMultipleMode(true)                  //  Select multiple images or single image
                       .setFolderMode(true)                    //  Folder mode
                       .setShowCamera(true)                    //  Show camera button
                       .setFolderTitle("Álbuns")               //  Folder title (works with FolderMode = true)
                       .setImageTitle("Galeria")               //  Image title (works with FolderMode = false)
                       .setDoneTitle("Finalizar")              //  Done button title
                       .setMaxSize(maxSize)                    //  Max images can be selected
                       .setSavePath("Up - Compra e venda")     //  Image capture folder name
                       .setSelectedImages(images)              //  Selected images
                       .start();                               //  Start ImagePicker
        }
    }

    //tratamento do resultado da seleção de imagens
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Config.RC_PICK_IMAGES && resultCode == RESULT_OK && data != null) {

            ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            imageAdapter.addData(images);
            imagePreviewController();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void imagePreviewController() {
        //atualização do padding entre as imagens
        recyclerView.removeItemDecoration(itemDecoration);
        recyclerView.addItemDecoration(itemDecoration);
        
        //verificação para decidir exibir ou não a informação de que não há imagens
        if (imageAdapter.getItemCount() == 0) {
            recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        } else if (imageAdapter.getItemCount() == 1) {
            new CountDownTimer(500, 1000) {
                public void onTick(long msUntilFinished) {

                }

                public void onFinish() {
                    linearLayoutNoImage.setVisibility(View.VISIBLE);
                    recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
                }
            }.start();
        } else if (imageAdapter.getItemCount() > 1) {
            recyclerView.setMinimumHeight(200);
            linearLayoutNoImage.setVisibility(View.INVISIBLE);
            recyclerView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        }
    }

    //impede que o tamanho do layout principal diminua quando o teclado for aberto
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        int mainConstraintLayoutHeight = mainConstraintLayout.getHeight();
        mainConstraintLayout.setMinHeight(mainConstraintLayoutHeight);
    }

    @Override
    public void onBackPressed() {
        Log.d("sismac", "back pressed");
        super.onBackPressed();
    }

//    @Override
//    public void onSaveInstanceState(Bundle saveInstanceState) {
//        Log.i("sismac", "called");
//        saveInstanceState.putString(SAVED_STATE_AD_TITLE, tituloAnuncio.getText().toString());
//        saveInstanceState.putString(SAVED_STATE_AD_DESCRIPTION, descricaoAnuncio.getText().toString());
//
//        super.onSaveInstanceState(saveInstanceState);
//    }
//
//    public void onRestoreInstanceState(Bundle savedInstanceState) {
//        if (savedInstanceState != null) {
//            tituloAnuncio.setText(savedInstanceState.getString(SAVED_STATE_AD_TITLE));
//            descricaoAnuncio.setText(savedInstanceState.getString(SAVED_STATE_AD_DESCRIPTION));
//            Log.d("sismac", savedInstanceState.getString(SAVED_STATE_AD_TITLE));
//        }
//        super.onRestoreInstanceState(savedInstanceState);
//    }

    @Override
    public void onPause(){
        Log.d("sismac", "pausing");
        super.onPause();
    }

    @Override
    public void onStop(){
        Log.d("sismac", "stopping");
        super.onStop();
    }

}
