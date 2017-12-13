package br.ufc.quixada.up.Models;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;

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

    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;

    private Address address = new Address();

    private int qtdAvVendedor = 0;
    private int somaAvVendedor = 0;

    private int qtdAvComprador = 0;
    private int somaAvComprador = 0;


    private float avVendedor = 0;
    private int numVendas = 0;

    private float avComprador = 0;
    private int numCompras = 0;

    private String ultimaAvaliacao;
    private String ultimoAvaliador;

    private ArrayList<String> listaDesejos = new ArrayList<String>();


//    public String addressString;

//    Map endereco = new HashMap<String, String>();

    private static User instance;

    public User() {
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


    public String getUltimaAvaliacao() {
        return ultimaAvaliacao;
    }

    public void setUltimaAvaliacao(String ultimaAvaliacao) {
        this.ultimaAvaliacao = ultimaAvaliacao;
    }

    public String getUltimoAvaliador() {
        return ultimoAvaliador;
    }

    public void setUltimoAvaliador(String ultimoAvaliador) {
        this.ultimoAvaliador = ultimoAvaliador;
    }

    public ArrayList<String> getListaDesejos() {
        return listaDesejos;
    }

    public void setListaDesejos(ArrayList<String> listaDesejos) {
        this.listaDesejos = listaDesejos;
    }

    public int getQtdAvVendedor() {
        return qtdAvVendedor;
    }

    public void setQtdAvVendedor(int qtdAvVendedor) {
        this.qtdAvVendedor = qtdAvVendedor;
    }

    public int getSomaAvVendedor() {
        return somaAvVendedor;
    }

    public void setSomaAvVendedor(int somaAvVendedor) {
        this.somaAvVendedor = somaAvVendedor;
    }

    public int getQtdAvComprador() {
        return qtdAvComprador;
    }

    public void setQtdAvComprador(int qtdAvComprador) {
        this.qtdAvComprador = qtdAvComprador;
    }

    public int getSomaAvComprador() {
        return somaAvComprador;
    }

    public void setSomaAvComprador(int somaAvComprador) {
        this.somaAvComprador = somaAvComprador;
    }

    public float getAvComprador() {
        return avComprador;
    }

    public void setAvComprador(float avComprador) {
        this.avComprador = avComprador;
    }

    public int getNumCompras() {
        return numCompras;
    }

    public void setNumCompras(int numCompras) {
        this.numCompras = numCompras;
    }

    public float getAvVendedor() {
        return avVendedor;
    }

    public void setAvVendedor(float avVendedor) {
        this.avVendedor = avVendedor;
    }

    public int getNumVendas() {
        return numVendas;
    }

    public void setNumVendas(int numVendas) {
        this.numVendas = numVendas;
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

    public Address getAddress(){
//        address = new Address()
        return address;
    };

    public void setAddress(Address address){
        this.address = address;

//        Log.d("testeeee", this.address.getLogradouro());
//        setLogradouro(address.getLogradouro());
//        setNumero(address.getNumero());
//        setComplemento(address.getComplemento());
//        setComplemento(address.getBairro());
//        setCidade(address.getCidade());
//        setEstado(address.getEstado());
    }

    public void setAddressString(String address){
        addressStringToObject(address);
//        this.addressString = address;
    }

//    public String getAddressString(){
////        return this.addressString;
//    }

    public void addressStringToObject(String addressString){

        if (addressString != null){

            String onlyMidlle = addressString.substring(8, addressString.length()-1);
            String[] pairs = onlyMidlle.split(",");
            for (int i=0; i<pairs.length; i++) {
                String pair = pairs[i];
                String[] keyValue = pair.split("=");
    //            Log.d("chave_valor", " " + keyValue[0]);
    //            addressMap.put(keyValue[0], (keyValue[1]));

                keyValue[0].replaceAll(" ","");

                if (keyValue[0].equals("logradouro")){
                    if (keyValue[1].equals(null)){
                        address.setLogradouro(" ");
                    }else {
                        address.setLogradouro(keyValue[1].substring(1, keyValue[1].length()-1));
                    }
                } else if(keyValue[0].equals(" numero")){
                    Log.d("chave_valor2", " " + keyValue[1]);
                    if (keyValue[1].equals(null)){
                        address.setNumero(" ");
                    }else {
                        address.setNumero(keyValue[1].substring(1, keyValue[1].length()-1));
                    }
                } else if(keyValue[0].equals(" complemento")){
                    if (keyValue[1].equals(null)){
                        address.setComplemento(" ");
                    }else {
                        address.setComplemento(keyValue[1].substring(1, keyValue[1].length()-1));
                    }
                } else if(keyValue[0].equals(" bairro")){
                    if (keyValue[1].equals(null)){
                        address.setBairro(" ");
                    }else {
                        address.setBairro(keyValue[1].substring(1, keyValue[1].length()-1));
                    }
                } else if(keyValue[0].equals(" cidade")){
                    if (keyValue[1].equals(null)){
                        address.setCidade(" ");
                    }else {
                        address.setCidade(keyValue[1].substring(1, keyValue[1].length()-1));
                    }
                } else if(keyValue[0].equals(" estado")){
                    if (keyValue[1].equals(null)){
                        address.setEstado(" ");
                    }else {
                        address.setEstado(keyValue[1].substring(1, keyValue[1].length()-1));
                    }
                }

            }
            Log.d("endereco_teste", address.getLogradouro() + ", " + address.getNumero() + ", " + address.getComplemento() + ", " + address.getBairro() + ", " +
                    address.getCidade() + " - " + address.getEstado());

        }


    }

//    public void adressToObject(String logradouro, String numero, String complemento, String bairro, String cidade, String estado){
//        this.endereco.put("logradouro", logradouro);
//        this.endereco.put("numero", numero);
//        this.endereco.put("complemento", complemento);
//        this.endereco.put("bairro", bairro);
//        this.endereco.put("cidade", cidade);
//        this.endereco.put("estado", estado);
//        setEndereco(endereco);
//    }

//    public Map getEndereco() {
//        return endereco;
//    }
//
//    public void setEndereco(Map endereco) {
//        this.endereco = endereco;
//    }
//
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
                ", endereco='" + address + '\'' +
                ", numeroVendas='" + numVendas + '\'' +
                ", avaliacaoVendedor='" + avVendedor + '\'' +
                ", numeroCompras='" + numCompras+ '\'' +
                ", avaliacaoComprador='" + avComprador + '\'' +
                '}';
    }
}
