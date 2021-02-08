package com.thinktubekorea.menu.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.thinktubekorea.menu.R;
import com.thinktubekorea.menu.model.Item;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemAdapter extends ArrayAdapter<Item> {
    private Context mContext;
    private ArrayList<Item> mItems;
    private int lastPosition = -1;

    public ItemAdapter(Context context, int resource, ArrayList<Item> items) {
        super(context, resource, items);
        this.mContext = context;
        this.mItems = items;
    }

    public void setArrayListData(ArrayList<Item> items) {
        this.mItems = items;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        //ViewHolder holder;
        Item item = mItems.get(position);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
//        if(view != null) {
//            holder = (ViewHolder) view.getTag();
//        } else {
        if (!item.isLargeSize()) {
            view = inflater.inflate(R.layout.item_row, parent, false);
        } else {
            view = inflater.inflate(R.layout.large_item_row, parent, false);
        }

//            holder = new ViewHolder(view);
//            view.setTag(holder);
//        }
        TextView itemName = (TextView)view.findViewById(R.id.item_name);
        TextView itemExpression = (TextView)view.findViewById(R.id.item_expression);
        TextView itemGlassAmount = (TextView)view.findViewById(R.id.item_glassAmount);
        TextView itemBottleAmount = (TextView)view.findViewById(R.id.item_bottleAmount);

        itemName.setText(item.getItemName());


        itemExpression.setText(item.getExpression());
        if (!StringUtils.isEmpty(item.getGlassAmount())) {
            itemGlassAmount.setText("₩ " + moneyFormatToWon(item.getGlassAmount()));
        } else {
            itemGlassAmount.setText(" ");
        }
        if (!StringUtils.isEmpty(item.getBottleAmount())) {
            itemBottleAmount.setText("₩ " + moneyFormatToWon(item.getBottleAmount()));
        } else {
            itemBottleAmount.setText(" ");
        }
        if (!item.isAnimation()) {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.down_from_top);
            animation.setStartOffset(50 * position);
            view.startAnimation(animation);
        }
        lastPosition = position;
        item.setAnimation(true);
        return view;
    }



    public String moneyFormatToWon(String inputMoney) {
        if (StringUtils.isEmpty(inputMoney)) {
            return null;
        }
        DecimalFormat myFormatter = new DecimalFormat("###,###");
        Long data = Long.parseLong(inputMoney);
        return myFormatter.format(data);
    }



    class ViewHolder {
        @BindView(R.id.item_name)
        TextView itemName;
        @BindView(R.id.item_expression)
        TextView itemExpression;
        @BindView(R.id.item_glassAmount)
        TextView itemGlassAmount;
        @BindView(R.id.item_bottleAmount)
        TextView itemBottleAmount;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
