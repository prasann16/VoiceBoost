package com.example.android.voiceboost;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.GainProcessor;
import be.tarsos.dsp.effects.DelayEffect;
import be.tarsos.dsp.filters.LowPassFS;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.android.AndroidAudioPlayer;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;


public class MainActivity extends AppCompatActivity {
    Button start, stop;

    private static final long serialVersionUID = -5020248948695915733L;
    private AudioDispatcher dispatcher;
    private DelayEffect delayEffect;
    private LowPassFS lowPassFS;
    private GainProcessor inputGain;

    private int defaultInputGain = 100;//%
    private int defaultDelay = 200;//ms
    private int defaultDecay = 50;//%
    private int defaultLowPassFS = 4000;//%
    float sampleRate = 22050;
    int bufferSize = 1024;
    int overlap = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start = (Button)findViewById(R.id.start);
        stop = (Button)findViewById(R.id.stop);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioProcessing(true);
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioProcessing(false);
            }
        });
    }


    public void audioProcessing(boolean bool) {
        if(bool == true){
            double echoLength = defaultDelay / 1000.0;
            double decay = defaultDecay / 100.0;
            double gainValue = defaultInputGain / 100.0;
            double lowpassfs = defaultLowPassFS ;
            if (delayEffect != null) {
                delayEffect.setEchoLength(echoLength);
            }
            if (delayEffect != null) {
                delayEffect.setDecay(decay);
            }
            if (inputGain != null) {
                inputGain.setGain(gainValue);
            }
            final TarsosDSPAudioFormat format = new TarsosDSPAudioFormat(sampleRate, 16, 1, true, false);
            AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, bufferSize, overlap);

            //delay in signal
            delayEffect = new DelayEffect(defaultDelay/1000.0,defaultDecay/100.0,sampleRate);
            //inputGain = new GainProcessor(defaultInputGain/100.0);
            //dispatcher.addAudioProcessor(inputGain);
            dispatcher.addAudioProcessor(new AndroidAudioPlayer(format));
            new Thread(dispatcher, "Audio dispatching").start();//run the dispatcher
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}