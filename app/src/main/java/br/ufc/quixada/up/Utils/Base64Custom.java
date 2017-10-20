package br.ufc.quixada.up.Utils;

import android.util.Base64;

/**
 * Created by Isaac Bruno on 20/10/2017.
 */

public class Base64Custom {

    public static String encodeBase64(String text){
        return Base64.encodeToString(text.getBytes(), Base64.DEFAULT).replaceAll("(\\n|\\r)", "");
    }

    public static String decodeBase64(String codedText) {
        return new String(Base64.decode(codedText, Base64.DEFAULT));
    }
}
