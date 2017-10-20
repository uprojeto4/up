package br.ufc.quixada.up.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import br.ufc.quixada.up.Fragments.fragmentPerfilAnuncios;
import br.ufc.quixada.up.Fragments.fragmentPerfilPerfil;

/**
 * Created by Brendon on 09/10/2017.
 */

public class PerfilFragmentPagerAdapater extends FragmentPagerAdapter {

    private String[] perfilTabTitles;

    public PerfilFragmentPagerAdapater(FragmentManager fm, String[] perfilTabTitles) {
        super(fm);
        this.perfilTabTitles = perfilTabTitles;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                return new fragmentPerfilPerfil();
            case 1:
                return new fragmentPerfilAnuncios();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return this.perfilTabTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return this.perfilTabTitles[position];
    }
}
