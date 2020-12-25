package com.ascend.www.abkms.utils;

import android.graphics.drawable.Drawable;

public class Holder {
  private static Drawable sDrawable;

  private Holder() {
    throw new AssertionError();
  }

  public static synchronized void set(Drawable drawable) {
    sDrawable = drawable;
  }

  public static synchronized Drawable get() {
    return sDrawable;
  }
}
