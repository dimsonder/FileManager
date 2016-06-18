package com.example.cherubim.FileManager;

import android.graphics.drawable.Drawable;
import android.os.Environment;

import java.util.Comparator;

/**
 * Created by cherubim on 2016/6/16.
 */
public class IconifiedText implements Comparable<IconifiedText> {


    /* 文件名 */
    private String mText = "";
    /* 文件的图标 ICNO */
    private Drawable mIcon = null;

    /* 能否选中 */
    private boolean mSelectable = true;

    public IconifiedText(String text, Drawable icon) {
mText=text;mIcon=icon;

    }
    //是否可以选中

    public boolean isSelectable() {   return mSelectable;  }

    // 设置是否可用选中
    public void setSelectable(boolean selectable) {   mSelectable = selectable;  }

    // 得到文件名
     public String getText() {   return mText;  }

    // 设置文件名
     public void setText(String text) {   mText = text;  }

    // 设置图标
     public void setIcon(Drawable icon){
         mIcon=icon;
     }
    public Drawable getIcon(){
       return  mIcon;
    }
    //比较

    @Override
    public int compareTo(IconifiedText another) {
        if (mText!=null){

            return this.mText.compareTo(another.getText()); }
            else
            throw new IllegalArgumentException();


    }
}
