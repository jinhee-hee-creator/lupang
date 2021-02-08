package com.thinktubekorea.menu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class ItemViewer extends LinearLayout {

    TextView textView;
    TextView textView2;
    ImageView imageView;
    public ItemViewer(Context context) {
        super(context);


        init(context);
    }

    public ItemViewer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public void init(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.item_sub_row,this,true);

        textView = (TextView)findViewById(R.id.textYear);
        textView2 = (TextView)findViewById(R.id.textPrice);
        imageView = (ImageView) findViewById(R.id.bottle);
    }

    public void setItem(ItemSub itemSub){
        textView.setText(itemSub.getName());
        textView2.setText(itemSub.getTel());
        imageView.setImageResource(itemSub.getImage());
    }
}