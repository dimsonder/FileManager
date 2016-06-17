package com.example.cherubim.FileManager;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cherubim on 2016/6/16.
 */
public class IconifiedTextListAdapter extends BaseAdapter {

    private Context mContext = null;

    public IconifiedTextListAdapter(Context context) {
        mContext = context;

    }


    //文件列表集合
    private List<IconifiedText> items = new ArrayList<IconifiedText>();

    //添加一个条目
    public void addItem(IconifiedText it) {
        items.add(it);
    }

    // 设置文件列表
    public void setListItems(List<IconifiedText> list) {
        items = list;
    }


    @Override
    public int getCount() {
        //  return 0;
        return items.size();
    }

    @Override
    public Object getItem(int position) {
//        return null;
        return items.get(position);
    }

    // 能否全部选中
    public boolean areAllItemsSelectable() {
        return false;
    }

    // 判断指定文件是否被选中
    public boolean isSelectable(int position) {
        return items.get(position).isSelectable();
    }


    // 得到一个文件的 ID
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        IconifiedTextView view;
        if (convertView == null) {
            view = new IconifiedTextView(mContext, items.get(position));
        } else {
            view = (IconifiedTextView) convertView;
            view.setText(items.get(position).getText());
            view.setIcon(items.get(position).getIcon());
        }
    return view;

    }
    }
