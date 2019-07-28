package com.needapps.birds.birdua;

import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.util.Linkify;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Detail Activity is called for each bird item when pressed the item in RecyclerAdapter
 */
public class BirdDetailActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {
    Toolbar detailToolbar; // define toolbar cause theme is noActionBar
    ImageView detailImage; // images for slider
    TextView detailDescription_; // description
    SoundPool soundPool; // soundPoll for audio button
    int soundId; // certain audio for each bird
    private SliderLayout slider; // slider for photos
    FloatingActionButton ttsButton; // text to speech button
    TextToSpeech tts;
    TextView moreSounds;
    RecyclerAdapter recyclerAdapter;
    private SharedPreferencesManager prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_bird_detail);
        // get chosen data from Settings
        prefs = new SharedPreferencesManager(this);//get SharedPreferencesManager instance
        detailDescription_ = (TextView) findViewById(R.id.text);
        int textSize = prefs.retrieveInt("size", 16); //get stored size from Settings, medium is default
        detailDescription_.setTextSize(textSize);

        recyclerAdapter = new RecyclerAdapter();
        detailToolbar = (Toolbar) findViewById(R.id.my_toolbar_detail);
        setSupportActionBar(detailToolbar);
        // initialize slider for images in activity
        slider = (SliderLayout) findViewById(R.id.slider);
        //INITIALIZE VIEWS
        detailImage = (ImageView) findViewById(R.id.image_detail);

        // call method for text description, Text to Speech, images slider
        moreSounds = (TextView) findViewById(R.id.more_sounds);
        moreSounds.setVisibility(View.GONE);// make button not visible
        setInformation();
        // add up/home button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        // Hardware buttons setting to adjust the media sound
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    /**
     * Sets text description, tts, images slider
     * from RecyclerAdapter
     */
    public void setInformation() {
        // Get the Intent from recycler adapter to extract the string - name and description
        final Intent intent = getIntent();
        String name = intent.getStringExtra(RecyclerAdapter.EXTRA_NAME);
        String description = intent.getStringExtra(RecyclerAdapter.EXTRA_DESCRIPTION);
        final String moreSoundsLink = intent.getStringExtra(RecyclerAdapter.EXTRA_MORE_SOUNDS);

        // add up/home button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(name); // set appropriate bird name
        }
        // intent to extract images from recycler adapter
        Bundle extras = getIntent().getExtras();
        int[] images = extras.getIntArray(RecyclerAdapter.EXTRA_IMAGE_SLIDER); // get images for slider

        // capture the layout's TextView and set the string as its text
        detailDescription_.setText(description);
        // make clickable links in description
        Linkify.addLinks(detailDescription_, Linkify.WEB_URLS);

        //Text To Speech
        ttsButton = (FloatingActionButton) findViewById(R.id.btn_speak);
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {
                        }

                        @Override
                        public void onDone(String utteranceId) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // call method callTextToSpeechOnClick
                                    callTextToSpeechOnClick();
                                }
                            });
                        }

                        @Override
                        public void onError(String utteranceId) {
                            Log.e("TAG", "error on " + utteranceId);
                        }
                    });
                    //add Ukrainian to TTS
                    int result = tts.setLanguage(new Locale("uk_UA"));
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language is not supported");
                    } else {
                        ttsButton.setEnabled(true);
                    }
                } else {
                    Log.e("TTS", "Initialization Failed");
                }
            }
        });
        // call the method callTextToSpeechOnClick to start TTS
        callTextToSpeechOnClick();

        // Images Slider
        ArrayList<Integer> listImages = new ArrayList<>();
        for (int i = 0; i < images.length; i++) {
            listImages.add(images[i]);
            // load with Glide and asBitmap
            // add images from array to slider - detailImage
            Glide.with(this).load(listImages.get(i)).asBitmap().into(detailImage);
        }
        // call setImageSlider method
        setImageSlider(listImages);

        // button to go to web page when pressed "More sounds" button
        if (moreSoundsLink != null) {
            moreSounds.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(moreSoundsLink));
                    startActivity(intent);
                }
            });
        }
    }

    /**
     * Calls TTS
     */
    public void callTextToSpeechOnClick() {
        ttsButton.setTag(1); // when it isn'theme speaking
        ttsButton.setImageResource(R.drawable.icon_text); // play icon
        // when clicked
        ttsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int status = (Integer) v.getTag();
                if (tts.isSpeaking()) {
                    if (tts != null) {
                        tts.stop();
                    }
                    ttsButton.setImageResource(R.drawable.icon_text);
                    v.setTag(1); // pause
                } else {
                    if (status == 1) {
                        String words = detailDescription_.getText().toString();
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "stringId");
                        tts.speak(words, TextToSpeech.QUEUE_FLUSH, params);
                        ttsButton.setImageResource(R.drawable.icon_stop); // stop icon
                        v.setTag(0); // stop
                    }
                }
            }
        });
    }

    /**
     * Sets up Images Slider
     *
     * @param listImages - array of images
     */
    public void setImageSlider(ArrayList<Integer> listImages) {
        // set slider images
        for (int img : listImages) {
            DefaultSliderView defaultSliderView = new DefaultSliderView(this);
            // initialize a SliderLayout
            defaultSliderView
                    .image(img)
                    .setScaleType(BaseSliderView.ScaleType.CenterCrop) // centerCrop Image
                    .setOnSliderClickListener(this);

            slider.addSlider(defaultSliderView);
        }
        slider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        slider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom); // show indicator bottom
        slider.setDuration(4000); // show next image in 4 seconds
        slider.addOnPageChangeListener(this); // change images swipe
    }

    /**
     * Stops TTS
     * when activity is finishing
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (tts != null) {
            tts.stop();
        } //call the method callTextToSpeechOnClick when activity resumed
        callTextToSpeechOnClick();
    }

    /**
     * Is called when activity is resumed
     */
    protected void onResume() {
        super.onResume();
        Bundle extras = getIntent().getExtras();
        int audio = extras.getInt(RecyclerAdapter.EXTRA_AUDIO); // get Audio
        if (soundPool == null) {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
            soundId = soundPool.load(this, audio, 1);
        }
    }

    /**
     * Releases audio
     */
    protected void onPause() {
        super.onPause();
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }

    /**
     * Plays audio when play button is clicked
     *
     * @param v - view
     */
    public void onClick(View v) {
        if (soundId != 0)
            soundPool.play(soundId, 1, 1, 0, 0, 1);
        // make More sounds button visible after on click on soundPool button
        moreSounds.setVisibility(View.VISIBLE);
    }

    /**
     * Stops TTS, audio when activity is destroyed
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        soundPool.stop(soundId);
        if (tts != null) {
            tts.shutdown();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        slider.stopAutoCycle();
    }

    /**
     * Allows back and up/home button
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            this.finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
