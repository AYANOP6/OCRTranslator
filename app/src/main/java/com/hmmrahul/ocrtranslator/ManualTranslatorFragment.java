package com.hmmrahul.ocrtranslator;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;
import com.hmmrahul.ocrtranslator.databinding.FragmentManualTranslatorBinding;
import com.theartofdev.edmodo.cropper.CropImage;


public class ManualTranslatorFragment extends Fragment{

    FragmentManualTranslatorBinding fragmentManualTranslatorBinding;
    int fromlanguageCode = -1;
    int tolanguageCode = -1;
    String inputText;

    public ManualTranslatorFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentManualTranslatorBinding = FragmentManualTranslatorBinding.inflate(inflater,container,false);

        setUpSpinner();


        fragmentManualTranslatorBinding.spinner1.setOnItemSelectedListener(new fromSpinnerClass());
        fragmentManualTranslatorBinding.spinner2.setOnItemSelectedListener(new toSpinnerClass());


        fragmentManualTranslatorBinding.scanNowFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputText = fragmentManualTranslatorBinding.typedText.getText().toString();
               translateText(inputText);
            }
        });
        return fragmentManualTranslatorBinding.getRoot();
    }

    private void setUpSpinner() {
        ArrayAdapter adapter = ArrayAdapter.createFromResource(getContext(), R.array.languages, R.layout.spinner_dropdown_text);
        ArrayAdapter adapter1 = ArrayAdapter.createFromResource(getContext(), R.array.languages, R.layout.spinner_dropdown_text);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_text);
        adapter1.setDropDownViewResource(R.layout.spinner_dropdown_text);
        fragmentManualTranslatorBinding.spinner1.setAdapter(adapter);
        fragmentManualTranslatorBinding.spinner2.setAdapter(adapter1);
    }

    class fromSpinnerClass implements AdapterView.OnItemSelectedListener
    {
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id)
        {
            String text = parent.getItemAtPosition(position).toString();
            fromlanguageCode = getLanguageCode(text);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    class toSpinnerClass implements AdapterView.OnItemSelectedListener
    {
       public void onItemSelected(AdapterView<?> parent, View v, int position, long id)
        {
            String text = parent.getItemAtPosition(position).toString();
            tolanguageCode = getLanguageCode(text);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    public int getLanguageCode(String language) {
        int languageCode = 0;
        switch (language) {
            case "Afrikaans":
                languageCode = FirebaseTranslateLanguage.AF;
                break;
            case "Arabic":
                languageCode = FirebaseTranslateLanguage.AR;
                break;
            case "Bengali":
                languageCode = FirebaseTranslateLanguage.BN;
                break;
            case "German":
                languageCode = FirebaseTranslateLanguage.DE;
                break;
            case "English":
                languageCode = FirebaseTranslateLanguage.EN;
                break;
            case "Spanish":
                languageCode = FirebaseTranslateLanguage.ES;
                break;
            case "French":
                languageCode = FirebaseTranslateLanguage.FR;
                break;
            case "Gujarati":
                languageCode = FirebaseTranslateLanguage.GU;
                break;
            case "Hindi":
                languageCode = FirebaseTranslateLanguage.HI;
                break;
            case "Italian":
                languageCode = FirebaseTranslateLanguage.IT;
                break;
            case "Japanese":
                languageCode = FirebaseTranslateLanguage.JA;
                break;
            case "Kannada":
                languageCode = FirebaseTranslateLanguage.KN;
                break;
            case "Korean":
                languageCode = FirebaseTranslateLanguage.KO;
                break;
            case "Marathi":
                languageCode = FirebaseTranslateLanguage.MR;
                break;
            case "Malay":
                languageCode = FirebaseTranslateLanguage.MS;
                break;
            case "Russian":
                languageCode = FirebaseTranslateLanguage.RU;
                break;
            case "Tamil":
                languageCode = FirebaseTranslateLanguage.TA;
                break;
            case "Telugu":
                languageCode = FirebaseTranslateLanguage.TE;
                break;
            case "Urdu":
                languageCode = FirebaseTranslateLanguage.UR;
                break;
            default:
                languageCode = 0;
        }
        return languageCode;
    }

    private void translateText(String input) {
        FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(fromlanguageCode)
                .setTargetLanguage(tolanguageCode)
                .build();

        FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().build();

        fragmentManualTranslatorBinding.scanNowFrame.setVisibility(View.GONE);
        fragmentManualTranslatorBinding.translatedTextLable.setVisibility(View.GONE);
        fragmentManualTranslatorBinding.translatedText.setVisibility(View.GONE);
        fragmentManualTranslatorBinding.scannedTextLable.setVisibility(View.GONE);
        fragmentManualTranslatorBinding.typedText.setVisibility(View.GONE);
        fragmentManualTranslatorBinding.bottomLinearLayout.setVisibility(View.GONE);
        fragmentManualTranslatorBinding.scannedTextProgressBar.setVisibility(View.VISIBLE);
        fragmentManualTranslatorBinding.translatingTextLable.setVisibility(View.VISIBLE);
        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                translator.translate(input)
                        .addOnSuccessListener(new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                fragmentManualTranslatorBinding.scannedTextProgressBar.setVisibility(View.GONE);
                                fragmentManualTranslatorBinding.translatingTextLable.setVisibility(View.GONE);
                                fragmentManualTranslatorBinding.translatedText.setText(s);
                                fragmentManualTranslatorBinding.translatedTextLable.setVisibility(View.VISIBLE);
                                fragmentManualTranslatorBinding.translatedText.setVisibility(View.VISIBLE);
                                fragmentManualTranslatorBinding.typedText.setText(input);
                                fragmentManualTranslatorBinding.bottomLinearLayout.setVisibility(View.VISIBLE);
                                fragmentManualTranslatorBinding.scanNowFrame.setVisibility(View.VISIBLE);
                                fragmentManualTranslatorBinding.scanNowText.setText("Retake");
                                fragmentManualTranslatorBinding.copyTextFrame.setVisibility(View.VISIBLE);
                                fragmentManualTranslatorBinding.scannedTextLable.setVisibility(View.VISIBLE);
                                fragmentManualTranslatorBinding.typedText.setVisibility(View.VISIBLE);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        fragmentManualTranslatorBinding.scannedTextProgressBar.setVisibility(View.GONE);
                        fragmentManualTranslatorBinding.translatingTextLable.setVisibility(View.GONE);
                        fragmentManualTranslatorBinding.bottomLinearLayout.setVisibility(View.VISIBLE);
                        fragmentManualTranslatorBinding.scanNowFrame.setVisibility(View.VISIBLE);
                        fragmentManualTranslatorBinding.scanNowText.setText("Scan Now");
                        fragmentManualTranslatorBinding.copyTextFrame.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Fail to Translate" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                fragmentManualTranslatorBinding.scannedTextProgressBar.setVisibility(View.GONE);
                fragmentManualTranslatorBinding.translatingTextLable.setVisibility(View.GONE);
                fragmentManualTranslatorBinding.bottomLinearLayout.setVisibility(View.VISIBLE);
                fragmentManualTranslatorBinding.scanNowFrame.setVisibility(View.VISIBLE);
                fragmentManualTranslatorBinding.scanNowText.setText("Scan Now");
                fragmentManualTranslatorBinding.copyTextFrame.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Fail to Download Language Model" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}