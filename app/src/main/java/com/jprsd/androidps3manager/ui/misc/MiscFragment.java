/*
 * Author: Jaideep Prasad
 * Date: 2020
 */

package com.jprsd.androidps3manager.ui.misc;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.jprsd.androidps3manager.R;
import com.jprsd.androidps3manager.generics.ManagerFragment;
import com.jprsd.androidps3manager.ui.system.SystemViewModel;

import ps3.mods.jprsd.ps3mapi.PS3MAPI;

/**
 * Fragment for the miscellaneous section of the app
 */
public class MiscFragment extends ManagerFragment {

    /**
     * Initializer
     * @param inflater Inflater for developing the layout as specified in the XML
     * @param container The layout group this fragment is housed in
     * @param savedInstanceState Any previously saved data
     * @return The fragment root view
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SystemViewModel systemViewModel =
                new ViewModelProvider(requireActivity()).get(SystemViewModel.class);
        View root = inflater.inflate(R.layout.fragment_misc, container, false);

        final EditText notifyText = root.findViewById(R.id.notifyText);
        final Button notifyButton = root.findViewById(R.id.notifyButton);

        final Button singleRingButton = root.findViewById(R.id.singleRingButton);
        final Button doubleRingButton = root.findViewById(R.id.doubleRingButton);
        final Button tripleRingButton = root.findViewById(R.id.tripleRingButton);

        final RadioGroup lightColorRadioGroup = root.findViewById(R.id.lightColorRadioGroup);
        final RadioButton redRadioButton = root.findViewById(R.id.redRadioButton);
        final RadioButton greenRadioButton = root.findViewById(R.id.greenRadioButton);
        final RadioButton yellowRadioButton = root.findViewById(R.id.yellowRadioButton);

        final RadioGroup lightModeRadioGroup = root.findViewById(R.id.lightModeRadioGroup);
        final RadioButton offRadioButton = root.findViewById(R.id.offRadioButton);
        final RadioButton onRadioButton = root.findViewById(R.id.onRadioButton);
        final RadioButton blinkFastRadioButton = root.findViewById(R.id.blinkFastRadioButton);
        final RadioButton blinkSlowRadioButton = root.findViewById(R.id.blinkSlowButton);

        final Button lightSetButton = root.findViewById(R.id.lightSetButton);

        systemViewModel.isConnected().observe(getViewLifecycleOwner(), connected -> {
            if (connected != null && connected) {
                notifyButton.setEnabled(true);
                singleRingButton.setEnabled(true);
                doubleRingButton.setEnabled(true);
                tripleRingButton.setEnabled(true);
                redRadioButton.setEnabled(true);
                greenRadioButton.setEnabled(true);
                yellowRadioButton.setEnabled(true);
                offRadioButton.setEnabled(true);
                onRadioButton.setEnabled(true);
                blinkFastRadioButton.setEnabled(true);
                blinkSlowRadioButton.setEnabled(true);
                lightSetButton.setEnabled(true);
            }
            else {
                notifyButton.setEnabled(false);
                singleRingButton.setEnabled(false);
                doubleRingButton.setEnabled(false);
                tripleRingButton.setEnabled(false);
                redRadioButton.setEnabled(false);
                greenRadioButton.setEnabled(false);
                yellowRadioButton.setEnabled(false);
                offRadioButton.setEnabled(false);
                onRadioButton.setEnabled(false);
                blinkFastRadioButton.setEnabled(false);
                blinkSlowRadioButton.setEnabled(false);
                lightSetButton.setEnabled(false);
            }
        });

        notifyButton.setOnClickListener(view -> onNotify(notifyText.getText().toString().trim()));
        singleRingButton.setOnClickListener(view -> onRingBuzzer(PS3MAPI.PS3Buzzer.Mode.SINGLE));
        doubleRingButton.setOnClickListener(view -> onRingBuzzer(PS3MAPI.PS3Buzzer.Mode.DOUBLE));
        tripleRingButton.setOnClickListener(view -> onRingBuzzer(PS3MAPI.PS3Buzzer.Mode.TRIPLE));

        lightSetButton.setOnClickListener(view -> {
            PS3MAPI.PS3Light.Color color;
            switch (lightColorRadioGroup.getCheckedRadioButtonId()) {
                case R.id.redRadioButton:
                    color = PS3MAPI.PS3Light.Color.RED;
                    break;
                case R.id.greenRadioButton:
                    color = PS3MAPI.PS3Light.Color.GREEN;
                    break;
                case R.id.yellowRadioButton:
                default:
                    color = PS3MAPI.PS3Light.Color.YELLOW;
                    break;
            }
            PS3MAPI.PS3Light.Mode mode;
            switch (lightModeRadioGroup.getCheckedRadioButtonId()) {
                case R.id.offRadioButton:
                    mode = PS3MAPI.PS3Light.Mode.OFF;
                    break;
                case R.id.onRadioButton:
                    mode = PS3MAPI.PS3Light.Mode.ON;
                    break;
                case R.id.blinkFastRadioButton:
                    mode = PS3MAPI.PS3Light.Mode.BLINK_FAST;
                    break;
                case R.id.blinkSlowButton:
                default:
                    mode = PS3MAPI.PS3Light.Mode.BLINK_SLOW;
                    break;
            }
            onSetLights(color, mode);
        });

        return root;
    }

    /**
     * Button handler for sending PS3 message notifications
     * @param message The message to send
     */
    private void onNotify(final String message) {
        final PS3MAPI PS3 = PS3();
        final Context context = getContext();
        new Thread() {
            @Override
            public void run() {
                if (!PS3.notify(message)) {
                    handler.post(() -> Toast.makeText(context,
                            R.string.notify_fail, Toast.LENGTH_SHORT).show());
                }
            }
        }.start();
    }

    /**
     * Button handler for ringing the PS3 buzzer
     * @param mode The buzzer mode
     */
    private void onRingBuzzer(final PS3MAPI.PS3Buzzer.Mode mode) {
        final PS3MAPI PS3 = PS3();
        final Context context = getContext();
        new Thread() {
            @Override
            public void run() {
                if (!PS3.ringBuzzer(mode)) {
                    handler.post(() -> Toast.makeText(context,
                            R.string.buzzer_fail, Toast.LENGTH_SHORT).show());
                }
            }
        }.start();
    }

    /**
     * Button handler for adjusting the PS3 LEDs
     * @param color The light color
     * @param mode The light mode
     */
    private void onSetLights(final PS3MAPI.PS3Light.Color color, final PS3MAPI.PS3Light.Mode mode) {
        final PS3MAPI PS3 = PS3();
        final Context context = getContext();
        new Thread() {
            @Override
            public void run() {
                if (!PS3.setLights(color, mode)) {
                    handler.post(() -> Toast.makeText(context,
                            R.string.lights_fail, Toast.LENGTH_SHORT).show());
                }
            }
        }.start();
    }

}
