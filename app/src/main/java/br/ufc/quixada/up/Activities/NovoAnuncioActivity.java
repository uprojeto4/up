package br.ufc.quixada.up.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;
import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;

import java.util.ArrayList;
import java.util.Locale;


import br.ufc.quixada.up.Adapters.NovoAnuncioRecyclerViewImageAdapter;
import br.ufc.quixada.up.R;
import br.ufc.quixada.up.Utils.InputMask;
import br.ufc.quixada.up.Utils.RecyclerViewPhotoSeparator;

public class NovoAnuncioActivity extends BaseActivity implements View.OnClickListener {

    ConstraintLayout mainConstraintLayout;
    RecyclerView recyclerView;
    LinearLayout linearLayoutNoImage;
    EditText tituloAnuncio;
    EditText descricaoAnuncio;
    EditText precoAnuncio;
    EditText qtdItensAnuncio;
    Spinner spinnerCategoriasAnuncio;
    String spinnerCategoriasItemSelecionado = null;
    Button buttonSalvarAnuncio;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.ItemDecoration itemDecoration;
    private NovoAnuncioRecyclerViewImageAdapter imageAdapter;
    private ArrayList<Image> images = new ArrayList<>();
    Locale locale = new Locale("pt", "BR");

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
        buttonSalvarAnuncio = (Button) findViewById(R.id.buttonSalvarAnuncio);
        mainConstraintLayout = findViewById(R.id.mainConstraintLayout);

        // imageAdapter e implementações de swipe para deletar
        imageAdapter = new NovoAnuncioRecyclerViewImageAdapter(this);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        itemDecoration = new RecyclerViewPhotoSeparator(4);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(imageAdapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.UP | ItemTouchHelper.DOWN) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                imageAdapter.removeImage(viewHolder.getAdapterPosition());
                manageItemDecoration();

                new CountDownTimer(500, 1000) {

                    public void onTick(long msUntilFinished) {

                    }

                    public void onFinish() {
                        if (imageAdapter.getItemCount() <= 1) {
                            linearLayoutNoImage.setVisibility(View.VISIBLE);
                        }
                    }
                }.start();
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

        //spinner de categorias (valor é setado em spinnerCategoriasItemSelecionado)
        spinnerCategoriasAnuncio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position > 0){
                    spinnerCategoriasItemSelecionado = (String) adapterView.getItemAtPosition(position);
//                    Toast.makeText(getApplicationContext(), "Selected: " + spinnerCategoriasItemSelecionado, Toast.LENGTH_SHORT).show();
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

                    boolean isPickerOpened = false;

                    @Override
                    public void onGranted() {

                        int maxSize = 5 - imageAdapter.getItemCount();
                        if (maxSize == 0) {
                            Snackbar.make(view, "Você só pode adicionar cinco imagens", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } else if (!isPickerOpened) {
                            ImagePicker.with(NovoAnuncioActivity.this)  //  Initialize ImagePicker with activity or fragment context
                                    .setToolbarColor("#5a185f")         //  Toolbar color
                                    .setStatusBarColor("#2e0035")       //  StatusBar color (works with SDK >= 21  )
                                    .setToolbarTextColor("#FFFFFF")     //  Toolbar text color (Title and Done button)
                                    .setToolbarIconColor("#FFFFFF")     //  Toolbar icon color (Back and Camera button)
                                    .setProgressBarColor("#4CAF50")     //  ProgressBar color
                                    .setBackgroundColor("#212121")      //  Background color
                                    .setCameraOnly(false)               //  Camera mode
                                    .setMultipleMode(true)              //  Select multiple images or single image
                                    .setFolderMode(true)                //  Folder mode
                                    .setShowCamera(true)                //  Show camera button
                                    .setFolderTitle("Álbuns")           //  Folder title (works with FolderMode = true)
                                    .setImageTitle("Galeria")         //  Image title (works with FolderMode = false)
                                    .setDoneTitle("Finalizar")               //  Done button title
                                    .setMaxSize(maxSize)                     //  Max images can be selected
                                    .setSavePath("Up - Compra e venda")         //  Image capture folder name
                                    .setSelectedImages(images)          //  Selected images
                                    .start();                           //  Start ImagePicker
                            this.isPickerOpened = true;
                        }
                    }

                    @Override
                    public void onDenied(String permission) {
                        Snackbar.make(view, "Desculpe, mas precisamos de permissão para acessar seus arquivos", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
            }
        });

        buttonSalvarAnuncio.setOnClickListener(this);
    }

    //gerencia o padding à direita de cada foto, para mantê-las separadas
    protected void manageItemDecoration() {
        recyclerView.removeItemDecoration(itemDecoration);
        recyclerView.addItemDecoration(itemDecoration);

    }

    //gerencia o click nos botões
    @Override
    public void onClick(View v) {
        Log.d("vID", "" + v.getId());
        switch (v.getId()) {
            case R.id.buttonCancelar: {
                if (imageAdapter.getItemCount() > 0 || tituloAnuncio.getText().length() != 0 || descricaoAnuncio.getText().length() != 0 ||
                        precoAnuncio.getText().length() != 0 || qtdItensAnuncio.getText().length() != 0 || spinnerCategoriasItemSelecionado != null) {
                    new AlertDialog.Builder(this)
                            .setMessage(NovoAnuncioActivity.this.getString(R.string.alert_sair_sem_salvar_message))
                            .setPositiveButton(NovoAnuncioActivity.this.getString(R.string.sim), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            }).setNegativeButton(NovoAnuncioActivity.this.getString(R.string.nao), null)
                            .show();
                } else {
                    finish();
                }
            }
            case R.id.buttonSalvarAnuncio: {
                //implementar ações de enviar dados e verificar se as informações estão corretas
            }
        }
    }

    //resultados da seleção de imagens
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Config.RC_PICK_IMAGES && resultCode == RESULT_OK && data != null) {

            ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            imageAdapter.addData(images);

            if (imageAdapter.getItemCount() > 1) {
                linearLayoutNoImage.setVisibility(View.GONE);
            } else {
                linearLayoutNoImage.setVisibility(View.VISIBLE);
                manageItemDecoration();
            }
            
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

//    impede que o tamanho do layout principal diminua quando o teclado for aberto
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        int mainConstraintLayoutHeight = mainConstraintLayout.getHeight();
        mainConstraintLayout.setMinHeight(mainConstraintLayoutHeight);
//        Log.d("Height", "" + mainConstraintLayoutHeight);
    }

    @Override
    public void onBackPressed() {
//        saveState();
        super.onBackPressed();
    }

}
