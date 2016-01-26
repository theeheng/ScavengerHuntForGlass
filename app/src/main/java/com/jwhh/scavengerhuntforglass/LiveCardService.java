package com.jwhh.scavengerhuntforglass;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.google.android.glass.timeline.LiveCard;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LiveCardService extends Service {
    private static final String LIVE_CARD_TAG = "Scavenger Hunt For Glass Status";
    private static final String ACTION_STOP = "Stop";
    private static final String ACTION_REFRESH = "Refresh";

    RemoteViews mLiveCardViews;
    LiveCard mLiveCard;

    public static void refreshLiveCard(Context context) {
        Intent intent = new Intent(context, LiveCardService.class);
        intent.setAction(ACTION_REFRESH);
        context.startService(intent);
    }
    public LiveCardService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        if(mLiveCard != null)
            mLiveCard.unpublish();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(mLiveCard == null) {
            CategoryManager.getInstance().InitializeWithNewCategories();
            mLiveCardViews = new RemoteViews(getPackageName(),
              R.layout.status_live_card_layout);
            mLiveCard = new LiveCard(this, LIVE_CARD_TAG);

            mLiveCard.setViews(mLiveCardViews);
            Intent cardActionIntent = new Intent(this, MenuActivity.class);
            mLiveCard.setAction(PendingIntent.getActivity(this, 0, cardActionIntent, 0));

            mLiveCard.publish(LiveCard.PublishMode.REVEAL);
        }
        else {
//            if(ACTION_STOP == intent.getAction())
//                stopSelf();
//            else
            if(ACTION_REFRESH != intent.getAction())
                mLiveCard.navigate();
        }

        displayLiveCardContent();
        return START_STICKY;

    }

    void displayLiveCardContent() {
        CategoryManager categoryManager = CategoryManager.getInstance();
        mLiveCardViews.setTextViewText(R.id.message, categoryManager.getStatusMessage());
        String footerMessage = categoryManager.getFooterMessage();

        if(!footerMessage.isEmpty()) {
            mLiveCardViews.setTextViewText(R.id.footer, footerMessage);
            mLiveCardViews.setTextViewText(R.id.timestamp,
              new SimpleDateFormat("hh:mm").format(new Date()));
        }
        else {
            mLiveCardViews.setTextViewText(R.id.footer, "");
            mLiveCardViews.setTextViewText(R.id.timestamp, "");
        }

        mLiveCard.setViews(mLiveCardViews);
    }
}
