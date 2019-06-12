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
 * class for Detail Activity when pressed item in RecyclerAdapter
 */
public class BirdDetailActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    Toolbar myToolbar; // define toolbar cause theme is noActionBar
    ImageView img_detail; // images for slider
    TextView description_detail; // description
    SoundPool soundPool; // soundPoll for audio button
    int soundID; // certain audio for each bird
    private SliderLayout slider; // slider for photos
    FloatingActionButton ttsButton; // text to speech button
    TextToSpeech tts;
    TextView moresounds;
    RecyclerAdapter recyclerAdapter;
    private SharedPreferencesManager prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_bird_detail);

        prefs = new SharedPreferencesManager(this);//get SharedPreferencesManager  instance
        description_detail = (TextView) findViewById(R.id.text);
        int ts = prefs.retrieveInt("size", 16); //get stored size from Settings, medium is default
        description_detail.setTextSize(ts);

        recyclerAdapter = new RecyclerAdapter();
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar_detail);
        setSupportActionBar(myToolbar);
        // initialize slider for images in activity
        slider = (SliderLayout) findViewById(R.id.slider);
        //INITIALIZE VIEWS
        img_detail = (ImageView) findViewById(R.id.image_detail);

        // call method for text description, tts, images slider
        moresounds = (TextView) findViewById(R.id.more_sounds);
        moresounds.setVisibility(View.GONE);// make button not visible
        setInformation();
        // add up/home button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        //Hardware buttons setting to adjust the media sound
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }


    /**
     * method for text description, tts, images slider
     */
    public void setInformation() {
        // Get the Intent from recycler adapter to extract the string - name and description
        final Intent intent = getIntent();
        String name = intent.getStringExtra(RecyclerAdapter.EXTRA_NAME);
        String description = intent.getStringExtra(RecyclerAdapter.EXTRA_DESCRIPTION);
        final String moresoundshttps = intent.getStringExtra(RecyclerAdapter.EXTRA_MORESOUNDS);

        // add up/home button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(name);// set appropriate bird's name
        }
        // intent to extract images from recycler adapter
        Bundle extras = getIntent().getExtras();
        int[] images = extras.getIntArray(RecyclerAdapter.EXTRA_IMAGE_SLIDER); // get images for slider

        // Capture the layout's TextView and set the string as its text
        description_detail.setText(description);
        //make clickable links in description
        Linkify.addLinks(description_detail, Linkify.WEB_URLS);

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
                                    // call method speak
                                    speak();
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
        //call the method speak to start TTS
        speak();

        // Image Slider
        ArrayList<Integer> listImages = new ArrayList<>();
        for (int i = 0; i < images.length; i++) {
            listImages.add(images[i]);
            //load with Glide and asBitmap
            //add images from array to slider - img_detail
            Glide.with(this).load(listImages.get(i)).asBitmap().into(img_detail);
        }
        //call setSlider method
        setSlider(listImages);
        // button to go to web page
        if (moresoundshttps != null) {
            moresounds.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(moresoundshttps));
                    startActivity(intent);
                }
            });
        }

    }

    /**
     * speak to call TTS
     */
    public void speak() {
        ttsButton.setTag(1); //when it isn't speaking
        ttsButton.setImageResource(R.drawable.icon_text); // play icon
        //when clicked
        ttsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int status = (Integer) v.getTag();
                if (tts.isSpeaking()) {
                    if (tts != null) {
                        tts.stop();
                    }
                    ttsButton.setImageResource(R.drawable.icon_text);
                    v.setTag(1); //pause
                } else {
                    if (status == 1) {
                        String words = description_detail.getText().toString();
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "stringId");
                        tts.speak(words, TextToSpeech.QUEUE_FLUSH, params);
                        ttsButton.setImageResource(R.drawable.icon_stop); // stop icon
                        v.setTag(0); //stop
                    }
                }
            }
        });
    }

    /**
     * Images Slider
     *
     * @param listImages
     */
    public void setSlider(ArrayList<Integer> listImages) {
        // make slider images
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
        slider.setDuration(4000); // show 4 seconds every image
        slider.addOnPageChangeListener(this); // change images by clicking
    }


    /**
     * when activity is finishing
     * stop TTS
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (tts != null) {
            tts.stop();
        } //call the method speak when activity resumed
        speak();
    }

    /**
     * when activity resumed
     */
    protected void onResume() {
        super.onResume();
        Bundle extras = getIntent().getExtras();
        int audio = extras.getInt(RecyclerAdapter.EXTRA_AUDIO); // get Audio
        if (soundPool == null) {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
            soundID = soundPool.load(this, audio, 1);
        }
    }

    /**
     * release audio
     */
    protected void onPause() {
        super.onPause();
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }

    /**
     * play audio when is clicked play button
     *
     * @param v
     */
    public void onClick(View v) {
        if (soundID != 0)
            soundPool.play(soundID, 1, 1, 0, 0, 1);
        moresounds.setVisibility(View.VISIBLE); // make button visible after on click on soundpool button
    }

    /**
     * stop TTS,audio when activity destroyed
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        soundPool.stop(soundID);
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
     * allow back and up/home button
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
