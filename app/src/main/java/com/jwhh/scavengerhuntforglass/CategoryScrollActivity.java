package com.jwhh.scavengerhuntforglass;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;


public class CategoryScrollActivity extends Activity implements GestureDetector.BaseListener {
    public static final String EXTRA_PHOTO_FILE_NAME = "photo file name";
    String mNewPhotoFileName;
    GestureDetector mDetector;
    CardScrollView mCardScrollView;
    AudioManager mAudioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        mDetector = new GestureDetector(this);
        mDetector.setBaseListener(this);

        setupCardScrollView();

        Intent startupIntent = getIntent();
        mNewPhotoFileName = startupIntent.getStringExtra(EXTRA_PHOTO_FILE_NAME);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCardScrollView.activate();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCardScrollView.deactivate();
    }

    private void setupCardScrollView() {
        mCardScrollView = new CardScrollView(this) {
            @Override
            protected boolean dispatchGenericFocusedEvent(MotionEvent event) {
                boolean handled = false;
                if(mDetector.onMotionEvent(event))
                    handled = true;
                else
                    handled = super.dispatchGenericFocusedEvent(event);
                return handled;
            }
        };

        mCardScrollView.setHorizontalScrollBarEnabled(true);

        CardScrollAdapter adapter = new CategoryScrollAdapter(this, CategoryManager.getInstance());
        mCardScrollView.setAdapter(adapter);

        setContentView(mCardScrollView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.category_scroll, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        return mDetector.onMotionEvent(event);
    }

    @Override
    public boolean onGesture(Gesture gesture) {
        boolean handled = false;
        switch (gesture) {
            case TAP:
                if(mNewPhotoFileName != null) {
                    int position = mCardScrollView.getSelectedItemPosition();
                    CategoryManager categoryManager = CategoryManager.getInstance();
                    Category category = categoryManager.getCategoryAt(position);
                    category.setPhotoFileName(mNewPhotoFileName);
                    categoryManager.setLastSelectedCategory(category);
                    mAudioManager.playSoundEffect(Sounds.TAP);
                    LiveCardService.refreshLiveCard(this);
                    handled = true;
                }
                break;
            case SWIPE_DOWN:
                mAudioManager.playSoundEffect(Sounds.DISMISSED);
                handled = true;
                break;
        }

        if(handled)
            finish();

        return handled;
    }
}
