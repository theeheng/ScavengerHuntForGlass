package com.jwhh.scavengerhuntforglass;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.glass.widget.CardScrollAdapter;

/**
 * Created by Jim on 5/6/2014.
 */
public class CategoryScrollAdapter extends CardScrollAdapter {
    Context mContext;
    CategoryManager mCategoryManager;

    public CategoryScrollAdapter(Context context, CategoryManager categoryManager) {
        mContext = context;
        mCategoryManager = categoryManager;
    }

    @Override
    public int getCount() {
        return mCategoryManager.getCount();
    }

    @Override
    public Object getItem(int i) {
        return mCategoryManager.getCategoryAt(i);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(R.layout.category_card, viewGroup);
        }

        TextView textName = (TextView) view.findViewById(R.id.name);
        ImageView imagePhoto = (ImageView) view.findViewById(R.id.photo);

        Category category = (Category) getItem(i);

        textName.setText(category.getName());
        String photoFileName = category.getPhotoFileName();
        if(photoFileName != null) {
            Bitmap photoBitmap = BitmapFactory.decodeFile(photoFileName);
            imagePhoto.setImageBitmap(photoBitmap);
        }

        return view;
    }

    @Override
    public int getPosition(Object o) {
        int position = AdapterView.INVALID_POSITION;

        if(o instanceof  Category) {
            for(int i = 0; i < getCount(); i++) {
                if(getItem(i) == o) {
                    position = i;
                    break;
                }
            }
        }

        return position;
    }
}
