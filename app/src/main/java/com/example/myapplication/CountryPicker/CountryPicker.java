package com.example.myapplication.CountryPicker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import androidx.fragment.app.DialogFragment;
import com.example.myapplication.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class CountryPicker extends DialogFragment {
    private CountryAdapter adapter;
    private Context context;
    private List<Country> countriesList = new ArrayList();
    private ListView countryListView;
    /* access modifiers changed from: private */
    public CountryPickerListener listener;
    private EditText searchEditText;
    /* access modifiers changed from: private */
    public List<Country> selectedCountriesList = new ArrayList();

    public static CountryPicker newInstance(String dialogTitle) {
        CountryPicker picker = new CountryPicker();
        Bundle bundle = new Bundle();
        bundle.putString("dialogTitle", dialogTitle);
        picker.setArguments(bundle);
        return picker;
    }

    public CountryPicker() {
        setCountriesList(Country.getAllCountries());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.country_picker, null);
        Bundle args = getArguments();
        if (args != null) {
            getDialog().setTitle(args.getString("dialogTitle"));
            getDialog().getWindow().setLayout(getResources().getDimensionPixelSize(R.dimen.cp_dialog_width), getResources().getDimensionPixelSize(R.dimen.cp_dialog_height));
        }
        this.searchEditText = (EditText) view.findViewById(R.id.country_code_picker_search);
        this.countryListView = (ListView) view.findViewById(R.id.country_code_picker_listview);
        this.selectedCountriesList = new ArrayList(this.countriesList.size());
        this.selectedCountriesList.addAll(this.countriesList);
        this.adapter = new CountryAdapter(getActivity(), this.selectedCountriesList);
        this.countryListView.setAdapter(this.adapter);
        this.countryListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (CountryPicker.this.listener != null) {
                    Country country = (Country) CountryPicker.this.selectedCountriesList.get(position);
                    CountryPicker.this.listener.onSelectCountry(country.getName(), country.getCode(), country.getDialCode(), country.getFlag());
                }
            }
        });
        this.searchEditText.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                CountryPicker.this.search(s.toString());
            }
        });
        return view;
    }

    public void setListener(CountryPickerListener listener2) {
        this.listener = listener2;
    }

    /* access modifiers changed from: private */
    @SuppressLint({"DefaultLocale"})
    public void search(String text) {
        this.selectedCountriesList.clear();
        for (Country country : this.countriesList) {
            if (country.getName().toLowerCase(Locale.ENGLISH).startsWith(text.toLowerCase())) {
                this.selectedCountriesList.add(country);
            }
        }
        this.adapter.notifyDataSetChanged();
    }

    public void setCountriesList(List<Country> newCountries) {
        this.countriesList.clear();
        this.countriesList.addAll(newCountries);
        Collections.sort(this.countriesList, new Comparator<Country>() {
            public int compare(Country country, Country t1) {
                return country.getName().compareTo(t1.getName());
            }
        });
    }
}
