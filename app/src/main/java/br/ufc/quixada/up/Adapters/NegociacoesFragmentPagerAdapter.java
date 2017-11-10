package br.ufc.quixada.up.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import br.ufc.quixada.up.Fragments.ComprasFragment;
import br.ufc.quixada.up.Fragments.VendasFragment;

/**
 * Created by Isaac Bruno on 31/10/2017.
 */

public class NegociacoesFragmentPagerAdapter extends FragmentPagerAdapter{

    private String[] tabs;

    public NegociacoesFragmentPagerAdapter(FragmentManager fm, String[] tabs) {
        super(fm);
        this.tabs = tabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new ComprasFragment();
            case 1:
                return new VendasFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabs.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return this.tabs[position];
    }
}
