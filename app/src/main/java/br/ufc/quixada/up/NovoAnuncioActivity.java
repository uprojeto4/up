package br.ufc.quixada.up;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;
import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;

import java.util.ArrayList;

import br.ufc.quixada.up.adapters.RecyclerViewImageAdapter;

public class NovoAnuncioActivity extends BaseActivity{

    RecyclerView recyclerView;
    ImageView imgViewIcone;
    TextView txtViewAddImagens;
    private RecyclerViewImageAdapter imageAdapter;
    private ArrayList<Image> images = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_anuncio);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recyclerView);
        imgViewIcone = findViewById(R.id.imageViewNovoAnuncioPlaceholder);
        txtViewAddImagens = findViewById((R.id.textViewAddImagens));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Spinner spinnerCategorias = (Spinner) findViewById(R.id.spinnerCategorias);

        spinnerCategorias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedItemText = (String) adapterView.getItemAtPosition(position);
                if (position > 0){
                    Toast.makeText(getApplicationContext(), "Selected: " + selectedItemText, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        FloatingActionButton fabAddFotos = (FloatingActionButton) findViewById(R.id.fabAddFotos);

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

        imageAdapter = new RecyclerViewImageAdapter(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(imageAdapter);
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.UP | ItemTouchHelper.DOWN) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                imageAdapter.removeImage(viewHolder.getAdapterPosition());
                new CountDownTimer(500, 1000) {

                    public void onTick(long msUntilFinished) {

                    }

                    public void onFinish() {
                        if (imageAdapter.getItemCount() <= 1) {
                            imgViewIcone.setVisibility(View.VISIBLE);
                            txtViewAddImagens.setVisibility(View.VISIBLE);

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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Config.RC_PICK_IMAGES && resultCode == RESULT_OK && data != null) {

            ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            imageAdapter.addData(images);

            if (images.size() > 1) {
                imgViewIcone.setVisibility(View.GONE);
                txtViewAddImagens.setVisibility(View.GONE);
            } else {
                imgViewIcone.setVisibility(View.VISIBLE);
                txtViewAddImagens.setVisibility(View.VISIBLE);
            }
            
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
