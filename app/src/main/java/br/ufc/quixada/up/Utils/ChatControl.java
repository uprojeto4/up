package br.ufc.quixada.up.Utils;

import com.google.firebase.database.DatabaseReference;

import br.ufc.quixada.up.DAO.FirebaseConfig;
import br.ufc.quixada.up.Models.Message;
import br.ufc.quixada.up.Models.Negociacao;

/**
 * Created by Macelo on 16/11/2017.
 */

public abstract class ChatControl {

    public static String negotiationKey;

    public static String startConversation(final String userId, final String remoteUserId, final String adId, final Message message) {

        final DatabaseReference dbReference = FirebaseConfig.getDatabase();
        String messagesId = dbReference.child("messages").push().getKey();
        Negociacao negociacao = new Negociacao(messagesId, remoteUserId, remoteUserId, adId, message.getText(), userId);
        dbReference.child("messages").child(messagesId).push().setValue(message);
        negociacao.setUnreadMessagesCounter(0);
        negotiationKey = dbReference.child("negotiations").child(userId).push().getKey();
        dbReference.child("negotiations").child(userId).child(negotiationKey).setValue(negociacao);
        negociacao.setRemoteUserId(userId);
        negociacao.setUnreadMessagesCounter(1);
        dbReference.child("negotiations").child(remoteUserId).child(negotiationKey).setValue(negociacao);

        //se nao der certo, trocar em new negociacao userId por remoteUserId

        return messagesId;
    }

}