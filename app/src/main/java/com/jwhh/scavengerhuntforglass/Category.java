package com.jwhh.scavengerhuntforglass;

/**
 * Created by Jim on 4/27/14.
 */
public class Category {
  public Category(String name) {
    mName = name;
  }

  public Category(String name, String photoFileName) {
    this(name);
    mPhotoFileName = photoFileName;
  }

  public String getName() {
    return mName;
  }

  private String mName;

  public String getPhotoFileName() {
    return mPhotoFileName;
  }

  public void setPhotoFileName(String photoFileName) {
    mPhotoFileName = photoFileName;
  }

  private String mPhotoFileName;
}
