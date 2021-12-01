package com.hmmrahul.ocrtranslator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.hmmrahul.ocrtranslator.databinding.ActivityMainBinding;
import com.hmmrahul.ocrtranslator.databinding.ActivityTranslatorBinding;

import io.ak1.OnBubbleClickListener;

public class TranslatorActivity extends AppCompatActivity {

    ActivityTranslatorBinding activityTranslatorBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityTranslatorBinding = ActivityTranslatorBinding.inflate(getLayoutInflater());
        setContentView(activityTranslatorBinding.getRoot());

        activityTranslatorBinding.bubbleTabBar.setSelectedWithId(R.id.translatorTab,false);
        activityTranslatorBinding.bubbleTabBar.addBubbleListener(new OnBubbleClickListener() {
            @Override
            public void onBubbleClick(int i) {
                String te = String.valueOf(i);
                Log.d("He",te);
                switch (i) {
                    case R.id.ocrTab:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);
                        break;
                    case R.id.translatorTab:

                        break;
                }
            }
        });

    }
}