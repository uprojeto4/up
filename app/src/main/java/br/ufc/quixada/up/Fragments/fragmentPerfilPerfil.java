package br.ufc.quixada.up.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.request.RequestOptions;

import br.ufc.quixada.up.R;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static br.ufc.quixada.up.R.drawable.image_test_1;

/**
 * Created by Brendon on 09/10/2017.
 */

public class fragmentPerfilPerfil extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_perfil_perfil, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MultiTransformation multi = new MultiTransformation(
                new BlurTransformation(25));

        Glide.with(this).load(image_test_1)
                .apply(RequestOptions.bitmapTransform(multi))
                .into((ImageView) getView().findViewById((R.id.header_cover_image)));
    }
}
