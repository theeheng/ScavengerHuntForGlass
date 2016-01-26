package com.jwhh.scavengerhuntforglass;

import android.util.Log;

import java.util.Random;

/**
 * Created by Jim on 4/27/14.
 */
public class CategoryManager {
  static final int CATEGORY_COUNT = 2;
  static final int NO_SELECTED_CATEGORY = -1;
  private static CategoryManager ourInstance = new CategoryManager();

  public static CategoryManager getInstance() {
    return ourInstance;
  }

  private CategoryManager() {
  }

  private Category[] mCategories;

  public int getCount() {
    return mCategories != null ? mCategories.length : 0;
  }

  public int getFoundCount() {
    int foundCount = 0;
    if (mCategories != null) {
      for (Category category : mCategories)
        foundCount += category.getPhotoFileName() != null ? 1 : 0;
    }
    return foundCount;
  }

  private int mIndexOfLastSelectedCategory = NO_SELECTED_CATEGORY;

  public Category getCategoryAt(int i) {
    return mCategories != null && i < getCount() ? mCategories[i] : null;
  }

  public Category getLastSelectedCategory() {
    return mIndexOfLastSelectedCategory == NO_SELECTED_CATEGORY ?
        null : mCategories[mIndexOfLastSelectedCategory];
  }

  public void setLastSelectedCategory(Category category) {
    for (int i = 0; i < mCategories.length; i++) {
      if (category == mCategories[i]) {
        mIndexOfLastSelectedCategory = i;
        break;
      }
    }
  }

  public String getStatusMessage() {
    String message;
    if (getCount() == 0)
      message = "Select Start Game to begin";
    else if (getFoundCount() == 0) {
      message = buildNewGameMessage();
    }
    else if (getFoundCount() == getCount())
      message = String.format("Congratulations!!\nYou have found all %d items", getCount());
    else
      message = String.format("You have found %d of %d items",
          getFoundCount(), getCount());
    return message;
  }

  public String getFooterMessage() {
    return getLastSelectedCategory() != null ?
        "Last find: " + getLastSelectedCategory().getName() : "";
  }

  private String buildNewGameMessage() {
    StringBuilder builder = new StringBuilder("Search for items in these categories:\n");
    for(int i = 0; i < getCount(); i++) {
      if(i != 0)
        builder.append(", ");
      builder.append(getCategoryAt(i).getName());
    }

    return builder.toString();
  }

  public void InitializeWithNewCategories() {
    Random r = new Random();
    int categoryNumber = r.nextInt(CATEGORY_COUNT);
    Log.d("ScavengerHunt", String.format("Random category index: %d", categoryNumber));

    String[] categoryNames = null;
    switch (categoryNumber) {
      case 0:
        categoryNames = new String[]{"Love", "Cat", "Robot"};
        break;
      case 1:
        categoryNames = new String[]{"Stretchy", "Bird", "Holiday"};
        break;
    }
    if (categoryNames != null) {
      mCategories = new Category[categoryNames.length];
      int index = -1;
      for (String categoryName : categoryNames)
        mCategories[++index] = new Category(categoryName);
    }

  }
}
