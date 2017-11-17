package br.ufc.quixada.up.Utils;

import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

import br.ufc.quixada.up.DAO.FirebaseConfig;
import br.ufc.quixada.up.Models.Message;

/**
 * Created by Macelo on 16/11/2017.
 */

public abstract class ChatControl {

    public static String startConversation(final String userId, final String remoteUserId, final String adId, final Message message) {

        final DatabaseReference dbReference = FirebaseConfig.getDatabase();
        String chatId = dbReference.child("messages").push().getKey();
        Map <String, Object> messageNode = new HashMap<>();
        messageNode.put("messagesId", chatId);
        messageNode.put("startDate", DateTimeControl.getCurrentDate());
        messageNode.put("lastMessage", message.getMessageText());

        dbReference.child("messages").child(chatId).push().setValue(message);
        dbReference.child("messages-metadata").child(userId).child(adId).push().setValue(messageNode);
        dbReference.child("messages-metadata").child(remoteUserId).child(adId).push().setValue(messageNode);

        return chatId;
    }

}
