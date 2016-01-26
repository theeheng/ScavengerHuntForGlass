package com.jwhh.scavengerhuntforglass;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.glass.content.Intents;

public class MenuActivity extends Activity {

    private static final int PHOTO_REQUEST_CODE = 1;
    private boolean mAttachedToWindow;
    private boolean mOptionsMenuOpen;
    private boolean mTakingPhoto;
    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void openOptionsMenu() {
        if(!mOptionsMenuOpen && mAttachedToWindow) {
            mOptionsMenuOpen = true;
            super.openOptionsMenu();
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mAttachedToWindow = true;
        openOptionsMenu();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mAttachedToWindow = false;
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
        mOptionsMenuOpen = false;
        if(!mTakingPhoto) {
            LiveCardService.refreshLiveCard(this);
            finish();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        CategoryManager categoryManager = CategoryManager.getInstance();

        boolean gameNeedsStarting = categoryManager.getCount() == 0 ||
          categoryManager.getCount() == categoryManager.getFoundCount();
        boolean gameIsActive = categoryManager.getCount() > 0;

        setOptionsMenuState(menu, R.id.action_start_new_game, gameNeedsStarting);
        setOptionsMenuState(menu, R.id.action_take_a_picture, gameIsActive);
        setOptionsMenuState(menu, R.id.action_view_categories, gameIsActive);

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        boolean handled = true;
        int id = item.getItemId();
        switch (id) {
            case R.id.action_start_new_game:
                handleStartNewGame();
                break;
            case R.id.action_take_a_picture:
                handleTakeAPicture();
                break;
            case R.id.action_view_categories:
                handleViewCategories();
                break;
            case R.id.action_stop:
                handleStop();
                break;
            default:
                handled = super.onOptionsItemSelected(item);
        }
        return handled;
    }

    private void handleStartNewGame() {
        //Toast.makeText(this, "Start selected", Toast.LENGTH_LONG).show();
        CategoryManager.getInstance().InitializeWithNewCategories();
    }

    private void handleTakeAPicture() {
        //Toast.makeText(this, "Take a picture selected", Toast.LENGTH_SHORT).show();
        mTakingPhoto = true;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(photoIntent, PHOTO_REQUEST_CODE);
            }
        });
    }

    private void handleViewCategories() {
        //Toast.makeText(this, "View categories selected", Toast.LENGTH_SHORT).show();
        showCategoryScrollActivity(null);
    }

    private void handleStop() {
        Toast.makeText(this, "Bye bye", Toast.LENGTH_SHORT).show();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                stopService(new Intent(MenuActivity.this, LiveCardService.class));
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PHOTO_REQUEST_CODE && resultCode == RESULT_OK) {
            final String photoFileName = data.getStringExtra(Intents.EXTRA_THUMBNAIL_FILE_PATH);
            //Toast.makeText(this, "Photo:" + photoFileName, Toast.LENGTH_LONG).show();
            showCategoryScrollActivity(photoFileName);
        }

        finish();
    }

    void showCategoryScrollActivity(final String photoFileName) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MenuActivity.this, CategoryScrollActivity.class);
                if(photoFileName != null)
                    intent.putExtra(CategoryScrollActivity.EXTRA_PHOTO_FILE_NAME, photoFileName);
                startActivity(intent);
            }
        });

    }

    private static void setOptionsMenuState(Menu menu, int menuItemId, boolean enabled) {
        MenuItem menuItem = menu.findItem(menuItemId);
        menuItem.setVisible(enabled);
        menuItem.setEnabled(enabled);
    }
}
