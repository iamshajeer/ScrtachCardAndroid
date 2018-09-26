package com.droidev.app.scratchcard;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements ScratchTextView.RevealListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ScratchTextView scratchTextView = findViewById(R.id.scratch_card);

        scratchTextView.setRevealListener(this);

        //PUBLIC METHODS
        /**
         setting image background to show before scratching CALL THIS METHOD ON POSTING THE VIEW
         eq : scratchTextView.post(new Runnable() {
        @Override public void run() {

        }
        });
         */
        /*scratchTextView.setScratchBitmap(R.drawable.ic_launcher_background);*/
        /**
         will reveal the scratch effect
         */
        /*scratchTextView.reveal();*/
        /**
         method to check if scratch has been revealed or not
         */
        /*scratchTextView.isRevealed();*/

    }

    @Override
    public void onRevealed(ScratchTextView tv) {

    }

    @Override
    public void onRevealPercentChangedListener(ScratchTextView stv, float percent) {
        if (percent >= 80) {
            stv.reveal();
        }
    }
}
