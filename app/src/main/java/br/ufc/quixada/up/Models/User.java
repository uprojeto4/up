package br.ufc.quixada.up.Models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

import br.ufc.quixada.up.DAO.FirebaseConfig;

/**
 * Created by Isaac Bruno on 20/10/2017.
 */

public class User {

    private String nome;
    private String email;
    private String senha;
    private String Id;

    public void save() {
        DatabaseReference databaseReference = FirebaseConfig.getFirebase();
        databaseReference.child("user").child(String.valueOf(getId())).setValue(this);
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> userHashMap = new HashMap<>();
        userHashMap.put("id", getId());
        userHashMap.put("name", getNome());
        userHashMap.put("email", getEmail());
        userHashMap.put("password", getSenha());

        return userHashMap;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
