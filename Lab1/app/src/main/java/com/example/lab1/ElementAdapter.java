package com.example.lab1;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ElementAdapter extends ArrayAdapter<Element> {

    private LayoutInflater inflater;
    private int layout;
    private List<Element> elements;

    public ElementAdapter(Context context, int resource, List<Element> elements) {
        super(context, resource, elements);
        this.elements = elements;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if(convertView==null){
            convertView = inflater.inflate(this.layout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Element element = elements.get(position);
        if(position %2 == 1)
        {
            // Set a background color for ListView regular row/item
            convertView.setBackgroundColor(Color.parseColor("#CCCCCC"));
        }
        else
        {
            // Set the background color for alternate row/item
            convertView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        viewHolder.imageView.setImageResource(R.drawable.lotus123);
        viewHolder.nameView.setText(element.getStr());

        return convertView;
    }
    private class ViewHolder {
        final ImageView imageView;
        final TextView nameView;
        ViewHolder(View view){
            imageView = (ImageView)view.findViewById(R.id.pict);
            nameView = (TextView) view.findViewById(R.id.intstr);
        }
    }
}
