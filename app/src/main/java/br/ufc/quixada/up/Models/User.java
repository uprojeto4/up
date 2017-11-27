package br.ufc.quixada.up.Models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.ufc.quixada.up.DAO.FirebaseConfig;

/**
 * Created by Isaac Bruno on 20/10/2017.
 */

public class User {

    private String nome;
    private String email;
    private String Id;
    private String fotoPerfil;

    private Map endereco = new HashMap();
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;

    private static User instance;

    private User() {
    }

    public void save() {
        DatabaseReference databaseReference = FirebaseConfig.getDatabase();
        databaseReference.child("users").child(String.valueOf(getId())).setValue(this);
    }

//    @Exclude
//    public Map<String, Object> toMap() {
//        HashMap<String, Object> userHashMap = new HashMap<>();
//        userHashMap.put("id", getId());
//        userHashMap.put("name", getNome());
//        userHashMap.put("email", getEmail());
//        userHashMap.put("fotoPerfil", getFotoPerfil());
//        userHashMap.put("endereco", getEndereco());
//
//        return userHashMap;
//    }

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

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

//    public void adressToMap(String logradouro, String numero, String complemento, String bairro, String cidade, String estado){
//        this.endereco.put("logradouro", logradouro);
//        this.endereco.put("numero", numero);
//        this.endereco.put("complemento", complemento);
//        this.endereco.put("bairro", bairro);
//        this.endereco.put("cidade", cidade);
//        this.endereco.put("estado", estado);
//        setEndereco(endereco);
//    }

    public Map getEndereco() {
        return endereco;
    }

    public void setEndereco(Map endereco) {
        this.endereco = endereco;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public static User getInstance(){
        if(instance == null){
            synchronized (User.class){
                if( instance == null ) {
                    instance = new User();
                }
            }
        }
        return instance;
    }

    @Override
    public String toString() {
        return "User{" +
                "nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", Id='" + Id + '\'' +
                ", fotoPerfil='" + fotoPerfil + '\'' +
                ", endereco='" + endereco + '\'' +
                '}';
    }
}
