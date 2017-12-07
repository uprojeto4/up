package br.ufc.quixada.up.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import br.ufc.quixada.up.Activities.MainActivity;
import br.ufc.quixada.up.Activities.PerfilActivity;
import br.ufc.quixada.up.Activities.PerfilPublicoActivity;
import br.ufc.quixada.up.DAO.FirebaseConfig;
import br.ufc.quixada.up.MapsActivityPerfil;
import br.ufc.quixada.up.Models.User;
import br.ufc.quixada.up.R;
import br.ufc.quixada.up.Utils.HttpDataHandler;
import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * Created by Brendon on 09/10/2017.
 */

public class fragmentPerfilPerfil extends Fragment {

    StorageReference storage = FirebaseConfig.getStorage();
    DatabaseReference databaseReference = FirebaseConfig.getDatabase();
    StorageReference profilePictureRef;
    TextView nome;
    TextView enderecoAnunciante;
    TextView tvNumVendas;
    TextView tvAvVendas;
    TextView tvNumCompras;
    TextView tvAvCompras;
    public byte[] image;
    Bitmap bitmap;
    public boolean test;
    public File localFile;

    public String anuncianteFoto;
    public String anuncianteNome;

//    public String logradouro;
//    public String numero;
//    public String complemento;
//    public String bairro;
//    public String cidade;
//    public String estado;

    public String address;
    public String addressMap;



//    Geocoder geocoder = new Geocoder(getActivity());



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        //cria a referencia para a imagem a ser baixada
        profilePictureRef = storage.child("UsersProfilePictures/"+PerfilActivity.id+"/"+PerfilActivity.fotoPerfil);
//        Toast.makeText(getActivity(),"caminho da imagem que vai ser baixada "+profilePictureRef.getPath(), Toast.LENGTH_LONG).show();

//        test = profilePictureRef.getName().equals(PerfilActivity.testFoto);

//        Log.d("getName", profilePictureRef.getName());
//        // 1509810338632_1.jpg
//        Log.d("perfilActivity", PerfilActivity.testFoto);
//        //1509810338632_1.jpg
//
//        if (test){
//            Log.d("getName do if", profilePictureRef.getName());
//            // 1509810338632_1.jpg
//            Log.d("perfilActivity do if", PerfilActivity.testFoto);
//            //1509810338632_1.jpg
//
////            Log.d("Caminho", localFile.getName());
//
//            try{
//                localFile = File.createTempFile("jpg", "image");
//                profilePictureRef.getFile(localFile);
//                bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.PNG, 10, stream);
//                //transforma o stream em um array de bytes
//                image = stream.toByteArray();
//                //método que aplica a imagem nos lugares desejsdos
//                applyImage(image);
//
//            }catch (IOException e){
//                e.printStackTrace();
//            }
//
//
//        }else{
//            //nada
//        }


        return inflater.inflate(R.layout.fragment_perfil_perfil, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(MainActivity.localUserId.equals(PerfilPublicoActivity.anuncianteId) || PerfilPublicoActivity.anuncianteId == null ){
            profilePictureRef = storage.child("UsersProfilePictures/"+PerfilActivity.id+"/"+PerfilActivity.fotoPerfil);

            downloadProfilePicture();

            nome = (TextView) getView().findViewById(R.id.user_profile_name);
            nome.setText(PerfilActivity.nome);

            TextView enderecoLocalUser;

            enderecoLocalUser = (TextView) getView().findViewById(R.id.user_profile_adress);

            tvNumVendas = (TextView) getView().findViewById(R.id.numVendas);
            tvAvVendas = (TextView) getView().findViewById(R.id.avVendas);

            tvNumCompras = (TextView) getView().findViewById(R.id.numCompras);
            tvAvCompras = (TextView) getView().findViewById(R.id.avCompras);


            tvNumVendas.setText(""+PerfilActivity.numVendas);

            DecimalFormat numberFormat = new DecimalFormat("#.0");
            tvAvVendas.setText(""+numberFormat.format(PerfilActivity.avVendedor));

            tvNumCompras.setText(""+PerfilActivity.numCompras);
            tvAvCompras.setText(""+PerfilActivity.avComprador);

            address = PerfilActivity.endereco.getLogradouro() + ", " + PerfilActivity.endereco.getNumero() + ", " + PerfilActivity.endereco.getComplemento() + ", " + PerfilActivity.endereco.getBairro() + ", " + PerfilActivity.endereco.getCidade() + " - " + PerfilActivity.endereco.getEstado();

            if (PerfilActivity.endereco.getLogradouro().equals("") || PerfilActivity.endereco.getNumero().equals("") ||
                    PerfilActivity.endereco.getBairro().equals("") || PerfilActivity.endereco.getCidade().equals("")){
                enderecoLocalUser.setText("Usuário não forneceu endereço");
            }else if(PerfilActivity.endereco.getComplemento().equals("")){
                enderecoLocalUser.setText(PerfilActivity.endereco.getLogradouro() + ", " + PerfilActivity.endereco.getNumero() + ", " + PerfilActivity.endereco.getBairro() + ", " + PerfilActivity.endereco.getCidade() + " - " + PerfilActivity.endereco.getEstado());
            }else{
                enderecoLocalUser.setText(address);
            }
            addressMap = PerfilActivity.endereco.getLogradouro() + ", " + PerfilActivity.endereco.getNumero() + ", " + PerfilActivity.endereco.getBairro() + ", " + PerfilActivity.endereco.getCidade() + " - " + PerfilActivity.endereco.getEstado();

            Log.d("enderecoAnunciante map", addressMap);



            enderecoLocalUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new GetCoordinate().execute(addressMap.replaceAll("\\s+","+"));
                }
            });
        } else if(MainActivity.localUserId == null || MainActivity.localUserId != PerfilPublicoActivity.anuncianteId ){


            Log.d("Entrou aqui", "enttrou mesmo");

            nome = (TextView) getView().findViewById(R.id.user_profile_name);

//            TextView enderecoAnunciante;

            enderecoAnunciante = (TextView) getView().findViewById(R.id.user_profile_adress);

            tvNumVendas = (TextView) getView().findViewById(R.id.numVendas);
            tvAvVendas = (TextView) getView().findViewById(R.id.avVendas);

            tvNumCompras = (TextView) getView().findViewById(R.id.numCompras);
            tvAvCompras = (TextView) getView().findViewById(R.id.avCompras);

//
//            tvNumVendas.setText(""+PerfilActivity.numVendas);
//            tvAvVendas.setText(""+PerfilActivity.avVendedor);
//
//            tvNumCompras.setText(""+PerfilActivity.numCompras);
//            tvAvCompras.setText(""+PerfilActivity.avComprador);

            Query getUserData = databaseReference.child("users").orderByChild("id").equalTo(PerfilPublicoActivity.anuncianteId);
            getUserData.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                        User usuarioAnunciante = singleSnapshot.getValue(User.class);
                        Log.d("nome_anunciane", ""+usuarioAnunciante.getNome());

                        anuncianteFoto = usuarioAnunciante.getFotoPerfil();

                        profilePictureRef = storage.child("UsersProfilePictures/"+usuarioAnunciante.getId()+"/"+usuarioAnunciante.getFotoPerfil());

                        downloadProfilePicture();

                        nome.setText(usuarioAnunciante.getNome());
                        anuncianteNome = usuarioAnunciante.getNome();

                        tvNumVendas.setText(""+usuarioAnunciante.getNumVendas());
//                        tvAvVendas.setText(""+usuarioAnunciante.getAvVendedor());
                        DecimalFormat numberFormat = new DecimalFormat("#.0");
                        tvAvVendas.setText(""+numberFormat.format(usuarioAnunciante.getAvVendedor()));

                        tvNumCompras.setText(""+usuarioAnunciante.getNumCompras());
                        tvAvCompras.setText(""+usuarioAnunciante.getAvComprador());

                        address = usuarioAnunciante.getAddress().getLogradouro() + ", " + usuarioAnunciante.getAddress().getNumero() + ", " + usuarioAnunciante.getAddress().getComplemento() + ", " + usuarioAnunciante.getAddress().getBairro() + ", " + usuarioAnunciante.getAddress().getCidade() + " - " + usuarioAnunciante.getAddress().getEstado();

                        if (usuarioAnunciante.getAddress().getLogradouro().equals("") || usuarioAnunciante.getAddress().getNumero().equals("") ||
                                usuarioAnunciante.getAddress().getBairro().equals("") || usuarioAnunciante.getAddress().getCidade().equals("")){
                            enderecoAnunciante.setText("Usuário não forneceu endereço");
                        }else if(usuarioAnunciante.getAddress().getComplemento().equals("")){
                            enderecoAnunciante.setText(usuarioAnunciante.getAddress().getLogradouro() + ", " + usuarioAnunciante.getAddress().getNumero() + ", " + usuarioAnunciante.getAddress().getBairro() + ", " + usuarioAnunciante.getAddress().getCidade() + " - " + usuarioAnunciante.getAddress().getEstado());
                        }else{
                            enderecoAnunciante.setText(address);
                        }
                        addressMap = usuarioAnunciante.getAddress().getLogradouro() + ", " + usuarioAnunciante.getAddress().getNumero() + ", " + usuarioAnunciante.getAddress().getBairro() + ", " + usuarioAnunciante.getAddress().getCidade() + " - " + usuarioAnunciante.getAddress().getEstado();

                        Log.d("enderecoAnunciante map", addressMap);



                        enderecoAnunciante.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new GetCoordinate().execute(addressMap.replaceAll("\\s+","+"));
                            }
                        });


//                        idAnunciante = usuarioAnunciante.getId();
//                        anuncianteNome.setText(usuarioAnunciante.getNome());
//                        avaliacaoVendedor.setText(""+usuarioAnunciante.getAvVendedor());

                        if (usuarioAnunciante.getNumVendas() == 0){
//                            tituloUsuario.setText("Novato");
                        } else if (usuarioAnunciante.getNumVendas() <= 10){
//                            tituloUsuario.setText("Iniciante");
                        } else if (usuarioAnunciante.getNumVendas() > 10){
//                            tituloUsuario.setText("Sênior");
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


//            Log.d("IdAnunciante", "entrou aqui");
//            Log.d("IdAnunciante", MainActivity.localUserId);
        }


    }

    private class GetCoordinate extends AsyncTask<String, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String response;
            try{
                String address = strings[0];
                HttpDataHandler http = new HttpDataHandler();
                String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=AIzaSyBEJfX5N3IUByF8qDUXK-ddmsPcGLlaOOo", address);
                response = http.getHttpData(url);
                return response;
            } catch (Exception ex){
                Log.d("LATITUDE_LONGITUDE", "caiu aqui2");
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try{
                JSONObject jsonObject = new JSONObject(s);

//                String lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").get("lat").toString();
                Double lat = Double.parseDouble(((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").get("lat").toString());
//                String lng = ((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").get("lng").toString();
                Double lng = Double.parseDouble(((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").get("lng").toString());

                Intent intent = new Intent(getActivity(), MapsActivityPerfil.class);
                intent.putExtra("Lat", lat);
                intent.putExtra("Long", lng);
                if (PerfilPublicoActivity.anuncianteId == null){
                    Log.d("EntrouAquiOtario", "entru no if");
                    intent.putExtra("nomeUsuario", PerfilActivity.nome);
                } else if(PerfilPublicoActivity.anuncianteId != MainActivity.localUserId){
                    Log.d("EntrouAquiOtario", "entru no else");
                    intent.putExtra("nomeUsuario", anuncianteNome);
                } else if (PerfilActivity.anuncianteId == null){
                    Log.d("EntrouAquiOtario", "entru no else if 1");
                    intent.putExtra("nomeUsuario", PerfilActivity.nome);
                } else if(PerfilActivity.anuncianteId == MainActivity.localUserId){
                    Log.d("EntrouAquiOtario", "entru no else if 2");
                    intent.putExtra("nomeUsuario", PerfilActivity.nome);
                } else {
                    Log.d("EntrouAquiOtario", "entru no else");
                    intent.putExtra("nomeUsuario", anuncianteNome);
                }
                startActivity(intent);

                Log.d("LATITUDE_LONGITUDE", " "+ lat);
                Log.d("LATITUDE_LONGITUDE", " "+ lng);

            }catch (JSONException e){
                Log.d("LATITUDE_LONGITUDE", s);
                e.printStackTrace();
            }
        }
    }



    public void downloadProfilePicture(){
        FirebaseStorage storage=FirebaseStorage.getInstance();
//        o download com o metodo getFile deve ser feito num try/catch
        try{
            //cria o arquivo temporário local onde a imagem será armazenada
            localFile = File.createTempFile("jpg", "image");
            profilePictureRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                //monitora o sucesso do download
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    //transforma a imagem baixada em um bitmap
                    bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
//                    Toast.makeText(getActivity(),localFile.getAbsolutePath(), Toast.LENGTH_LONG).show();

                    Log.d("Caminho", localFile.getPath());

                    //transforma o bitmap em stream
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    //transforma o stream em um array de bytes
                    image = stream.toByteArray();
                    //método que aplica a imagem nos lugares desejsdos
                    applyImage(image);
//                    Toast.makeText(getActivity(),profilePictureRef.getName(), Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                //monitora a falha do downlaod
                public void onFailure(@NonNull Exception e) {
//                    Toast.makeText(getActivity(),"Foto não encontrada", Toast.LENGTH_LONG).show();
                }
            });
        } catch (IOException e){
            e.printStackTrace();
            //manipular exceções
            Log.e("Main", "IOE exception");
        }



        // Create a storage reference from our app

//        final StorageReference storageRef = storage.getReferenceFromUrl("gs://up-compra-venda.appspot.com/UsersProfilePictures/"+PerfilActivity.id);
//
////        gs://up-compra-venda.appspot.com/UsersProfilePictures/YnJlbmRvbmdpcmFvQGdtYWlsLmNvbQ==/1509810338632_1.jpg
//
//        storageRef.child(PerfilActivity.fotoPerfil).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//
//            @Override
//            public void onSuccess(byte[] bytes) {
//                // Use the bytes to display the image
//                String path= "/data/data/br.ufc.quixada.up/cache/"+storageRef.child(PerfilActivity.fotoPerfil).getName();
//                try {
//                    FileOutputStream fos = new FileOutputStream(path);
//                    fos.write(bytes);
//                    Log.d("path ", path);
//
//                    bitmap = BitmapFactory.decodeFile(path);
////                    Toast.makeText(getActivity(),localFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
//
////                    Log.d("Caminho", localFile.getPath());
//
//                    //transforma o bitmap em stream
//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 10, stream);
//                    //transforma o stream em um array de bytes
//                    image = stream.toByteArray();
//                    applyImage(image);
//
//                    fos.close();
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                    new AlertDialog.Builder(getActivity())
//                            .setTitle(R.string.no_address_dialog_title)
//                            .setMessage(getActivity().getString(R.string.insert_address_message))
//                            .setPositiveButton(getActivity().getString(R.string.sim), new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
////                            finish();
//                                    Intent intent = new Intent(getActivity(), EditPerfilActivity.class);
//                                    startActivity(intent);
//                                }
//                            }).setNegativeButton(getActivity().getString(R.string.nao), null)
//                            .show();
////                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
//
//                } catch (IOException e) {
//                    e.printStackTrace();
////                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
//                }
//
////                pd.dismiss();
//
//            }
//
//        }).addOnFailureListener(new OnFailureListener() {
//
//            @Override
//
//            public void onFailure(@NonNull Exception exception) {
//
//                // Handle any errors
//
////                pd.dismiss();
//
////                Toast.makeText(getActivity(), exception.toString()+"!!!", Toast.LENGTH_SHORT).show();
//                new AlertDialog.Builder(getActivity())
//                        .setTitle(R.string.no_address_dialog_title)
//                        .setMessage(getActivity().getString(R.string.insert_profile_picture_message))
//                        .setPositiveButton(getActivity().getString(R.string.sim), new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
////                            finish();
//                                Intent intent = new Intent(getActivity(), EditPerfilActivity.class);
//                                startActivity(intent);
//                            }
//                        }).setNegativeButton(getActivity().getString(R.string.nao), null)
//                        .show();
//
//            }
//
//        });


    }

    public void applyImage(byte[] bytes){
        //efeito de blur para a imagem
        MultiTransformation multi = new MultiTransformation(
                new BlurTransformation(25));


        //options para o glide
        RequestOptions requestOptions = new RequestOptions();
        //não salava a imagem em cache, para que ela possa ser alterada caso outra pessoa se logue
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        requestOptions.skipMemoryCache(true);

        //carrega a imagem
        Glide.with(this).load(bytes)
                //aplica as options de cache
                .apply(requestOptions)
                //aplica as options de transformação
                .apply(RequestOptions.bitmapTransform(multi))
                //insere a imagem no imageView
                .into((ImageView) getView().findViewById((R.id.header_cover_image)));

        Glide.with(this).load(bytes)
                .apply(requestOptions)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(200)))
                .into((ImageView) getView().findViewById((R.id.profile_image)));
    }

    @Override
    public void onResume() {
        super.onResume();


        if (PerfilPublicoActivity.anuncianteId == null){

            Log.d("EntrouAquiOtario", "esta null");

//            if (PerfilActivity.fotoPerfil != anuncianteFoto){
//            if (PerfilPublicoActivity.anuncianteId == MainActivity.localUserId){
                Log.d("EntrouAquiOtario", "entrou aqui tb");
                test = profilePictureRef.getName().equals(PerfilActivity.fotoPerfil);

                profilePictureRef = storage.child("UsersProfilePictures/"+PerfilActivity.id+"/"+PerfilActivity.fotoPerfil);
                if (!test){
                    downloadProfilePicture();
                }else{
                    //nada
                }
//            } else {
//                Log.d("EntrouAquiOtario", "não é igual");
//            }

        } else{
            Log.d("EntrouAquiOtario", "não esta null");

            if (PerfilActivity.anuncianteId == MainActivity.localUserId){
                Log.d("EntrouAquiOtario", "entrou aqui tb");
                test = profilePictureRef.getName().equals(PerfilActivity.fotoPerfil);

                profilePictureRef = storage.child("UsersProfilePictures/"+PerfilActivity.id+"/"+PerfilActivity.fotoPerfil);
                if (!test){
                    downloadProfilePicture();
                }else{
                    //nada
                }
            } else {
                Log.d("EntrouAquiOtario", "não é igual");
                profilePictureRef = storage.child("UsersProfilePictures/"+PerfilPublicoActivity.anuncianteId+"/"+anuncianteFoto);
                downloadProfilePicture();
            }


        }




    }
}
