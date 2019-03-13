package com.example.keval.wikisnaps.Fragments;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.keval.wikisnaps.Activities.Search;
import com.example.keval.wikisnaps.R;
import com.example.keval.wikisnaps.databinding.FragTranslationBinding;
import com.mannan.translateapi.Language;
import com.mannan.translateapi.TranslateAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A Translator Fragment.
 */
public class TranslationFragment extends Fragment {

    private FragTranslationBinding translationBinding;
    private List<String> languages = new ArrayList<>();
    private TextToSpeech t1;
    private String lang;

    public TranslationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        translationBinding = DataBindingUtil.inflate(inflater, R.layout.frag_translation, container, false);
        View view = translationBinding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*--Toolbar--*/
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Translator");
            ((AppCompatActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.colorPrimaryDark));
            ((Search) getActivity()).updateStatusBarColor("#415dad");
        }
        catch (Exception e) {
            Log.d("myd", "onViewCreated: "+e.getMessage());
        }

        translationBinding.edtTranslateText.requestFocus();
        translationBinding.edtTranslateText.setSelection(translationBinding.edtTranslateText.getText().length());

        getLanguages(languages);
        final ArrayAdapter<String> langAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, languages);
        langAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        translationBinding.spTranslate.setAdapter(langAdapter);

        translationBinding.spTranslate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String langSelected = parent.getItemAtPosition(position).toString();
                    lang = langSelected.substring(0, 2);


                    /*--TTS Set Language--*/
                    t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if (status != TextToSpeech.ERROR) {
                                t1.setLanguage(Locale.forLanguageTag(lang));

                                Log.i("langTTs", "onInit: " + lang);
                            }
                        }
                    });
                    Log.i("check", "onItemSelected: " + lang);

                    /*--Translate Text As per the Selected Language--*/
                    TranslateAPI translateAPI = new TranslateAPI(
                            Language.AUTO_DETECT,   //Source Language
                            lang,         //Target Language
                            translationBinding.edtTranslateText.getText().toString()); //Query Text

                    translateAPI.setTranslateListener(new TranslateAPI.TranslateListener() {
                        @Override
                        public void onSuccess(String translatedText) {
                            Log.d("Translate", "onSuccess: " + translatedText);
                            translationBinding.txtTranslatedText.setText(translatedText);
                        }

                        @Override
                        public void onFailure(String ErrorText) {
                            //TastyToast.makeText(getContext(), "Sorry! Counld'nt Translate The Text", TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
                            Log.d("Translate", "onFailure: " + ErrorText);
                        }
                    });

                } catch (Exception e) {
                    Log.d("myd", "onItemSelected: " + e.getMessage());
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        translationBinding.ibSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Toast.makeText(getActivity(), "Speaking", Toast.LENGTH_SHORT).show();
                    t1.speak(translationBinding.txtTranslatedText.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                } catch (Exception e) {
                    Log.e("mye", "On Speak: " + e.getMessage());
                }
            }
        });
        translationBinding.edtTranslateText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (translationBinding.edtTranslateText.getRight() -
                            translationBinding.edtTranslateText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        translationBinding.edtTranslateText.setText("");
                        return true;
                    }
                }
                return false;
            }
        });

        translationBinding.ibMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

        Timer timer = new Timer();
        MyTimer mt = new MyTimer();
        timer.schedule(mt, 1000, 1000);

    }

    /*--Add Languages in list*/
    private void getLanguages(List<String> languages) {

        languages.add(Language.ENGLISH + " - English");
        languages.add(Language.HINDI + " - Hindi");
        languages.add(Language.GUJARATI + " - Gujarati");
        languages.add(Language.BENGALI + " - Bengali");
        languages.add(Language.KANNADA + " - Kannada");
        languages.add(Language.TAMIL + " - Tamil");
        languages.add(Language.TELUGU + " - Telugu");
        languages.add(Language.GERMAN + " -German");
        languages.add(Language.ROMANIAN + " - Romanian");
        languages.add(Language.CHINESE + " - Chinese");
        languages.add(Language.JAPANESE + " - Japanese");
        languages.add(Language.ARABIC + " - Arabic");
        languages.add(Language.DUTCH + " - Dutch");
        languages.add(Language.FRENCH + " - French");

    }

    /*--Speech To Text--*/
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, 100);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getContext(),
                    "Speech Not Supported",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    translationBinding.edtTranslateText.setText(result.get(0));
                }
                break;
            }
        }
    }

    class MyTimer extends TimerTask {

        public void run() {
            //This runs in a background thread.
            try {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {

                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        public void run() {
                            Random rand = new Random();
                            //The random generator creates values between [0,256) for use as RGB values used below to create a random color
                            // accountSettingsBinding.view.setBackgroundColor(Color.argb(255, rand.nextInt(100), rand.nextInt(256), rand.nextInt(256)));
                            Window window = getActivity().getWindow();
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            translationBinding.txtHeader.setTextColor(Color.argb(150, rand.nextInt(150), rand.nextInt(100), rand.nextInt(150)));
                        }
                    });
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }

}
