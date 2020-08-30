package com.example.myapplication.CountryPicker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.myapplication.R;

import java.util.List;

public class CountryAdapter extends BaseAdapter {
    List<Country> countries;
    LayoutInflater inflater;
    private Context mContext;

    static class Cell {
        public ImageView imageView;
        public TextView textView;

        Cell() {
        }

        static Cell from(View view) {
            if (view == null) {
                return null;
            }
            if (view.getTag() != null) {
                return (Cell) view.getTag();
            }
            Cell cell = new Cell();
            cell.textView = (TextView) view.findViewById(R.id.row_title);
            cell.imageView = (ImageView) view.findViewById(R.id.row_icon);
            view.setTag(cell);
            return cell;
        }
    }

    public CountryAdapter(Context context, List<Country> countries2) {
        this.mContext = context;
        this.countries = countries2;
        this.inflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return this.countries.size();
    }

    public Object getItem(int arg0) {
        return null;
    }

    public long getItemId(int arg0) {
        return 0;
    }

    public View getView(int position, View view, ViewGroup parent) {
        Country country = (Country) this.countries.get(position);
        if (view == null)
        {
            view = this.inflater.inflate(R.layout.row, null);
        }
        Cell cell = Cell.from(view);
        cell.textView.setText(country.getName());
        country.loadFlagByCode(this.mContext);
        if (country.getFlag() != -1) {
            cell.imageView.setImageResource(country.getFlag());
        }
        return view;
    }
}
