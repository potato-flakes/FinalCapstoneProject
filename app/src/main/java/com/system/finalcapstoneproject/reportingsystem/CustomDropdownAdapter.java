package com.system.finalcapstoneproject.reportingsystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class CustomDropdownAdapter extends ArrayAdapter<String> {

    public CustomDropdownAdapter(Context context, List<String> options) {
        super(context, 0, options);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        String option = getItem(position);

        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(option);

        return convertView;
    }
}
