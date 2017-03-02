package id.co.ppu.collectionfast2.fcm;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import id.co.ppu.collectionfast2.util.ConstChat;
import id.co.ppu.collectionfast2.util.NotificationUtils;

/**
 * Created by Eric on 13-Dec-16.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    public static void broadcastMessage(Context ctx, String collFrom, @Nullable RemoteMessage.Notification notification
                                     , Map<String, String> data
    ) {
        //Displaying data in log
        //It is optional
        Log.e(TAG, "From: " + collFrom); //719224898380

        try {
            Intent pushNotification = new Intent(ConstChat.PUSH_NOTIFICATION);
            pushNotification.putExtra("from", collFrom);

            if (notification != null) {
                Log.e(TAG, "Notification Message Body: " + notification.getBody());  //Hello Test notification

                pushNotification.putExtra("body", notification.getBody());
            }

            Log.e(TAG, "Data Size: " + data.size());
            if (data.size() > 0) {
                String key_from = data.get(ConstChat.KEY_FROM);
                String key_uid = data.get(ConstChat.KEY_UID);
                String key_msg = data.get(ConstChat.KEY_MESSAGE);
                String key_status = data.get(ConstChat.KEY_STATUS);
//                String key_seqno = remoteMessage.getData().get(ConstChat.KEY_SEQNO);
                String key_timestamp = data.get(ConstChat.KEY_TIMESTAMP);

                pushNotification.putExtra(ConstChat.KEY_FROM, key_from);

                // replace previous chat_msg with real data
                pushNotification.putExtra(ConstChat.KEY_UID, key_uid);

                pushNotification.putExtra(ConstChat.KEY_MESSAGE, key_msg);
                pushNotification.putExtra(ConstChat.KEY_STATUS, key_status);
//                pushNotification.putExtra(ConstChat.KEY_SEQNO, key_seqno);
                pushNotification.putExtra(ConstChat.KEY_TIMESTAMP, key_timestamp);
            }

            LocalBroadcastManager.getInstance(ctx).sendBroadcast(pushNotification);

            // play notification sound
//        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            NotificationUtils.playNotificationSound(ctx);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage == null)
            return;

        broadcastMessage(this, remoteMessage.getFrom(), remoteMessage.getNotification(), remoteMessage.getData());
    }
}
