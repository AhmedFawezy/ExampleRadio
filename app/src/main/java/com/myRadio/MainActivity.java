package com.myRadio;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.*;
import android.view.animation.*;
import android.graphics.*;

 

public class MainActivity extends Activity {

    private MediaPlayer mediaPlayer;
    private TextView statusText, titleText;
    private LinearLayout soundWaves;
	//private final String RADIO_URL = "http://stream.hayaatfm.com/hayaatfm";
    private final String RADIO_URL = "https://stream.radiojar.com/8s5u5tpdtwzuv";
	//private final String RADIO_URL = "https://edge.mixlr.com/channel/qqgnn";
	//private final String RADIO_URL ="https://hayat.fm/ ";

	//private final String RADIO_URL =("http://m.live.net.sa:1935/live/quransa/playlist.m3u8");	
	
	private enum RadioState {
        CONNECTING,
        PLAYING,
        ERROR,
        STOPPED
		}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        initializeViews();
        setupMediaPlayer();
        playRadio();
    }

    private void initializeViews() {
        statusText = (TextView) findViewById(R.id.statusText);
        titleText = (TextView) findViewById(R.id.titleText);
        soundWaves = (LinearLayout) findViewById(R.id.soundWaves);

        // تطبيق الخط
        Typeface customFont = Typeface.createFromAsset(getAssets(), "fonts/aaa.ttf");
        statusText.setTypeface(customFont);
        titleText.setTypeface(customFont);
    }

    private void setupMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    private void updateRadioState(final RadioState state) {
        runOnUiThread(new Runnable() {
				@Override
				public void run() {
					switch (state) {
						case CONNECTING:
							statusText.setText("جاري الاتصال...");
							soundWaves.setVisibility(View.GONE);
							break;
						case PLAYING:
							statusText.setText("تم التشغيل");
							soundWaves.setVisibility(View.VISIBLE);
							startWaveAnimations();
							break;
						case ERROR:
							statusText.setText("حدث خطأ في الاتصال بالراديو");
							soundWaves.setVisibility(View.GONE);
							break;
						case STOPPED:
							statusText.setText("تم إيقاف التشغيل");
							soundWaves.setVisibility(View.GONE);
							break;
					}
				}
			});
    }

    private void playRadio() {
        try {
            updateRadioState(RadioState.CONNECTING);
            mediaPlayer.setDataSource(RADIO_URL);
            mediaPlayer.prepareAsync();

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
					@Override
					public void onPrepared(MediaPlayer mp) {
						mediaPlayer.start();
						updateRadioState(RadioState.PLAYING);
					}
				});

            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
					@Override
					public boolean onError(MediaPlayer mp, int what, int extra) {
						updateRadioState(RadioState.ERROR);
						return true;
					}
				});

        } catch (Exception e) {
            e.printStackTrace();
            updateRadioState(RadioState.ERROR);
        }
    }

    private void startWaveAnimations() {
        for (int i = 0; i < soundWaves.getChildCount(); i++) {
            View bar = soundWaves.getChildAt(i);
            bar.startAnimation(createWaveAnimation(i * 150, 0.4f, 1.4f, 800));
        }
    }

    private Animation createWaveAnimation(int delay, float fromScale, float toScale, long duration) {
        ScaleAnimation anim = new ScaleAnimation(
            1f, 1f,
            fromScale, toScale,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 1.0f
        );
        anim.setDuration(duration);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        anim.setStartOffset(delay);
        anim.setFillAfter(true);
        return anim;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        stopWaveAnimations();
    }

    private void stopWaveAnimations() {
        for (int i = 0; i < soundWaves.getChildCount(); i++) {
            View child = soundWaves.getChildAt(i);
            if (child != null) {
                child.clearAnimation();
            }
        }
    }
}

