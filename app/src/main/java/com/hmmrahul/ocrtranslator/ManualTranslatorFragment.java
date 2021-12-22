package com.hmmrahul.ocrtranslator;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hmmrahul.ocrtranslator.databinding.FragmentManualTranslatorBinding;


public class ManualTranslatorFragment extends Fragment {

    FragmentManualTranslatorBinding fragmentManualTranslatorBinding;

    public ManualTranslatorFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentManualTranslatorBinding = FragmentManualTranslatorBinding.inflate(inflater,container,false);
        return fragmentManualTranslatorBinding.getRoot();
    }
}