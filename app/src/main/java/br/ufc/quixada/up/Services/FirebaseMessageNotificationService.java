package br.ufc.quixada.up.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import br.ufc.quixada.up.R;
import br.ufc.quixada.up.Utils.DateTimeControl;

/**
 * Created by Macelo on 07/12/2017.
 */

public class FirebaseMessageNotificationService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String notificationTitle = remoteMessage.getNotification().getTitle();
        String notificationMessage = remoteMessage.getNotification().getBody();
        String clickAction = remoteMessage.getNotification().getClickAction();

        String adId = remoteMessage.getData().get("adId");
        String remoteUserId = remoteMessage.getData().get("remoteUserId");
        int negotiationType = Integer.parseInt(remoteMessage.getData().get("negotiationType"));
        String adTitle = remoteMessage.getData().get("adTitle");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                                            .setSmallIcon(R.drawable.ic_notification)
                                            .setContentTitle(notificationTitle)
                                            .setContentText(notificationMessage)
                                            .setDefaults(Notification.DEFAULT_ALL)
                                            .setPriority(Notification.PRIORITY_HIGH)
                                            .setAutoCancel(true);

        Intent resultIntent = new Intent(clickAction);
        resultIntent.putExtra("adId", adId);
        resultIntent.putExtra("adTitle", adTitle);
        resultIntent.putExtra("remoteUserId", remoteUserId);
        resultIntent.putExtra("negotiationType", negotiationType);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);

        int notificationId = (int) DateTimeControl.getCurrentDateTime();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, builder.build());
    }

}
