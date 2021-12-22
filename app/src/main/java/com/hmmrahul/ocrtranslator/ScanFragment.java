package com.hmmrahul.ocrtranslator;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;
import com.hmmrahul.ocrtranslator.databinding.FragmentScanBinding;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;


public class ScanFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    FragmentScanBinding fragmentScanBinding;
    Bitmap bitmap;
    int languageCode = FirebaseTranslateLanguage.HI;

    public ScanFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        fragmentScanBinding = FragmentScanBinding.inflate(inflater, container, false);

        setUpSpinner();
        fragmentScanBinding.scannedText.setMovementMethod(new ScrollingMovementMethod());
        fragmentScanBinding.translatedText.setMovementMethod(new ScrollingMovementMethod());

        fragmentScanBinding.scanNowFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .start(getContext(), ScanFragment.this);
            }
        });

        fragmentScanBinding.spinner.setOnItemSelectedListener(this);

        return fragmentScanBinding.getRoot();
    }

    private void setUpSpinner() {
        ArrayAdapter adapter = ArrayAdapter.createFromResource(getContext(), R.array.languages, R.layout.spinner_dropdown_text);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_text);
        fragmentScanBinding.spinner.setAdapter(adapter);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), resultUri);
                    getTextFromImage(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void getTextFromImage(Bitmap bitmap) {
        TextRecognizer recognizer = new TextRecognizer.Builder(getContext()).build();
        if (!recognizer.isOperational()) {
            Toast.makeText(getContext(), "Error Occurred", Toast.LENGTH_SHORT).show();
        } else {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> textBlockSparseArray = recognizer.detect(frame);
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < textBlockSparseArray.size(); i++) {
                TextBlock textBlock = textBlockSparseArray.valueAt(i);
                stringBuilder.append(textBlock.getValue());
                stringBuilder.append("\n");
            }
            translateText(stringBuilder.toString());

        }
    }

    private void translateText(String input) {
        FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(FirebaseTranslateLanguage.EN)
                .setTargetLanguage(languageCode)
                .build();

        FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().build();
        fragmentScanBinding.animationView.setVisibility(View.GONE);
        fragmentScanBinding.scanNowFrame.setVisibility(View.GONE);
        fragmentScanBinding.tipsText.setVisibility(View.GONE);
        fragmentScanBinding.scannedTextProgressBar.setVisibility(View.VISIBLE);
        fragmentScanBinding.translatingTextLable.setVisibility(View.VISIBLE);
        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                translator.translate(input)
                        .addOnSuccessListener(new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                fragmentScanBinding.scannedTextProgressBar.setVisibility(View.GONE);
                                fragmentScanBinding.tipsText.setVisibility(View.GONE);
                                fragmentScanBinding.translatingTextLable.setVisibility(View.GONE);
                                fragmentScanBinding.translatedText.setText(s);
                                fragmentScanBinding.translatedTextLable.setVisibility(View.VISIBLE);
                                fragmentScanBinding.translatedText.setVisibility(View.VISIBLE);
                                fragmentScanBinding.scannedText.setText(input);
                                fragmentScanBinding.scanNowFrame.setVisibility(View.VISIBLE);
                                fragmentScanBinding.scanNowText.setText("Retake");
                                fragmentScanBinding.copyTextFrame.setVisibility(View.VISIBLE);
                                fragmentScanBinding.scannedTextLable.setVisibility(View.VISIBLE);
                                fragmentScanBinding.scannedText.setVisibility(View.VISIBLE);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        fragmentScanBinding.scannedTextProgressBar.setVisibility(View.GONE);
                        fragmentScanBinding.animationView.setVisibility(View.VISIBLE);
                        fragmentScanBinding.tipsText.setVisibility(View.VISIBLE);
                        fragmentScanBinding.translatingTextLable.setVisibility(View.GONE);
                        fragmentScanBinding.scanNowFrame.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), "Fail to Translate" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Fail to Download Language Model" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public int getLanguageCode(String language) {
        int languageCode = 0;
        switch (language) {
            case "Bengali":
                languageCode = FirebaseTranslateLanguage.BN;
                break;
            case "Hindi":
                languageCode = FirebaseTranslateLanguage.HI;
                break;
            case "Gujarati":
                languageCode = FirebaseTranslateLanguage.GU;
                break;
            case "Spanish":
                languageCode = FirebaseTranslateLanguage.ES;
                break;
            default:
                languageCode = 0;
        }
        return languageCode;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
        String text = adapterView.getItemAtPosition(pos).toString();
//        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
        fragmentScanBinding.scannedTextLable.setVisibility(View.GONE);
        fragmentScanBinding.scannedText.setVisibility(View.GONE);
        fragmentScanBinding.translatedTextLable.setVisibility(View.GONE);
        fragmentScanBinding.translatedText.setVisibility(View.GONE);
        fragmentScanBinding.copyTextFrame.setVisibility(View.GONE);

        fragmentScanBinding.animationView.setVisibility(View.VISIBLE);
        fragmentScanBinding.tipsText.setVisibility(View.VISIBLE);
        fragmentScanBinding.scanNowText.setText("Scan Now");
        languageCode = getLanguageCode(text);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}