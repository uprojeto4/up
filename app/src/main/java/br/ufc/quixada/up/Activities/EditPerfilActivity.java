package br.ufc.quixada.up.Activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static br.ufc.quixada.up.R.drawable.image_test_1;

import br.ufc.quixada.up.R;

public class EditPerfilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_perfil);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                String[] permissionsToRequest = new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
                PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(EditPerfilActivity.this, permissionsToRequest, new PermissionsResultAction() {


                    @Override
                    public void onGranted() {

//                        int maxSize = 5 - imageAdapter.getItemCount();

                            ImagePicker.with(EditPerfilActivity.this)  //  Initialize ImagePicker with activity or fragment context
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
                                    .setMaxSize(1)                     //  Max images can be selected
                                    .setSavePath("Up - Compra e venda")         //  Image capture folder name
//                                    .setSelectedImages(images)          //  Selected images
                                    .start();                           //  Start ImagePicker
                        }

                    @Override
                    public void onDenied(String permission) {
                        Snackbar.make(view, "Desculpe, mas precisamos de permissão para acessar seus arquivos", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });

            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Config.RC_PICK_IMAGES && resultCode == RESULT_OK && data != null) {

            ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);

            Log.d("printa ", "arr "+images.get(0).getPath());

//            MultiTransformation multi = new MultiTransformation(
//                    new BlurTransformation(25));

//            RequestOptions options = new RequestOptions().centerCrop();


            ImageView imageView = (ImageView) findViewById((R.id.imageViewIconeFoto));
            Glide.with(imageView.getContext()).load(images.get(0).getPath())
                    .into(imageView);

//            imageAdapter.addData(images);

//            if (imageAdapter.getItemCount() > 1) {
//                linearLayoutNoImage.setVisibility(View.GONE);
//            } else {
//                linearLayoutNoImage.setVisibility(View.VISIBLE);
//                manageItemDecoration();
//            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
