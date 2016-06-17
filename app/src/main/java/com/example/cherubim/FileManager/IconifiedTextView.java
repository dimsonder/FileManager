package com.example.cherubim.FileManager;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by cherubim on 2016/6/16.
 * 自定义的布局，继承线性布局
 */
public class IconifiedTextView extends LinearLayout {

    //文件名
    private TextView fileName;
    //前面的图标
    private ImageView fileIcon;


    public IconifiedTextView(Context context, IconifiedText iconifiedText) {
        super(context);
        //水平 显示
        this.setOrientation(HORIZONTAL);
//图标添加
        fileIcon = new ImageView(context);
        fileIcon.setImageDrawable(iconifiedText.getIcon());

        fileIcon.setPadding(5, 4, 4, 6);

      /*  添加到布局
                参数1：要添加的内容
                参数2：布局填充方式,填充父窗口*/
        addView(fileIcon, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        //设置文件名、填充方式、字体大小
        fileName = new TextView(context);
        fileName.setText(iconifiedText.getText());
        fileName.setPadding(5, 4, 4, 6);
        fileName.setTextSize(20);
        // 将文件名添加到布局中
        addView(fileName, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));


    }

    // 设置文件名
    public void setText(String words) {
        fileName.setText(words);
    }

    // 设置图标
    public void setIcon(Drawable icon) {
        fileIcon.setImageDrawable(icon);
    }


}
