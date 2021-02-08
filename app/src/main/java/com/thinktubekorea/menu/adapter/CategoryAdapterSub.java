package com.thinktubekorea.menu.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.thinktubekorea.menu.R;
import com.thinktubekorea.menu.model.Category;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoryAdapterSub extends ArrayAdapter<Category> {
    private Context mContext;
    private ArrayList<Category> mItems;

    public CategoryAdapterSub(Context context, int resource, ArrayList<Category> items) {
        super(context, resource, items);
        this.mContext = context;
        this.mItems = items;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(R.layout.category_sub_row, parent, false);

            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        Category item = mItems.get(position);

        holder.menuCategory2.setText(item.getCategoryName());
        return view;
    }


    static
    class ViewHolder {
        @BindView(R.id.menu_category2)
        TextView menuCategory2;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
