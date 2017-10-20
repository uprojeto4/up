package br.ufc.quixada.up.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Isaac Bruno on 20/10/2017.
 */

public class FirebasePreferences {

    private Context context;
    private SharedPreferences preferences;
    private String NOME_ARQUIVO = "br.ufc.quixada.up.FirebasePreferences";
    private int MODE = 0;
    private SharedPreferences.Editor editor;

    private final String CHAVE_ID = "userLoggedId";
    private final String CHAVE_NOME = "userLoggedName";

    public FirebasePreferences(Context context){
        this.context = context;
        preferences = context.getSharedPreferences(NOME_ARQUIVO, MODE);

        editor = preferences.edit();
    }

    public void SaveUserPreferences(String userId, String userName){
        editor.putString(CHAVE_ID, userId);
        editor.putString(CHAVE_NOME, userName);
        editor.commit();
    }

    public String getId () {
        return preferences.getString(CHAVE_ID, null);
    }

    public String getUserName(){
        return preferences.getString(CHAVE_NOME, null);
    }



}
