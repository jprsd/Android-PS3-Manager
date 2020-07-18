/*
 * Author: Jaideep Prasad
 * Date: 2020
 */

package com.jprsd.androidps3manager.ui.system;

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

import ps3.mods.jprsd.ps3mapi.PS3MAPI;

/**
 * Fragment for the main system settings section of the app
 */
public class SystemFragment extends ManagerFragment {

    /**
     * View model for live data and UI changes in this fragment
     */
    private SystemViewModel systemViewModel;

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
        systemViewModel = new ViewModelProvider(requireActivity()).get(SystemViewModel.class);
        View root = inflater.inflate(R.layout.fragment_system, container, false);

        final EditText ipAddressText = root.findViewById(R.id.ipAddressText);
        final Button connectButton = root.findViewById(R.id.connectButton);
        final Button disconnectButton = root.findViewById(R.id.disconnectButton);
        final RadioGroup powerRadioGroup = root.findViewById(R.id.powerRadioGroup);
        final RadioButton shutdownRadio = root.findViewById(R.id.shutdownButton);
        final RadioButton rebootRadio = root.findViewById(R.id.rebootButton);
        final Button powerExecuteButton = root.findViewById(R.id.powerExecuteButton);

        systemViewModel.isConnected().observe(getViewLifecycleOwner(), connected -> {
            if (connected != null && connected) {
                ipAddressText.setEnabled(false);
                connectButton.setEnabled(false);
                shutdownRadio.setEnabled(true);
                rebootRadio.setEnabled(true);
                powerExecuteButton.setEnabled(true);
            }
            else {
                ipAddressText.setEnabled(true);
                connectButton.setEnabled(true);
                shutdownRadio.setEnabled(false);
                rebootRadio.setEnabled(false);
                powerExecuteButton.setEnabled(false);
            }
        });

        connectButton.setOnClickListener(view ->
                onConnect(ipAddressText.getText().toString().trim()));
        disconnectButton.setOnClickListener(view -> onDisconnect());
        powerExecuteButton.setOnClickListener(view ->
                onPowerExecute(powerRadioGroup.getCheckedRadioButtonId()));

        return root;
    }

    /**
     * Button handler for connecting to the PS3
     * @param ipAddress The PS3 IP address
     */
    private void onConnect(String ipAddress) {
        final PS3MAPI PS3 = PS3();
        final Context context = getContext();

        try {
            PS3.setIpAddress(ipAddress);
        } catch (Exception e) {
            Toast.makeText(context, R.string.invalid_ip, Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(context, R.string.connecting, Toast.LENGTH_SHORT).show();
        new Thread() {
            @Override
            public void run() {
                if (PS3.connect()) {
                    handler.post(() -> systemViewModel.setConnected(true));
                    handler.post(() -> Toast.makeText(context,
                            R.string.connected, Toast.LENGTH_SHORT).show());
                }
                else {
                    handler.post(() -> Toast.makeText(context,
                            R.string.connection_failed, Toast.LENGTH_SHORT).show());
                }
            }
        }.start();
    }

    /**
     * Button handler for disconnecting from the PS3
     */
    private void onDisconnect() {
        PS3().disconnect();
        systemViewModel.setConnected(false);
    }

    /**
     * Button handler for system power settings
     * @param mode The selected power option
     */
    private void onPowerExecute(int mode) {
        final PS3MAPI PS3 = PS3();
        final Context context = getContext();
        switch (mode) {
            case R.id.shutdownButton:
                new Thread() {
                    @Override
                    public void run() {
                        if (PS3.shutdown()) {
                            handler.post(() -> onDisconnect());
                        }
                        else {
                            handler.post(() -> Toast.makeText(context,
                                    R.string.shutdown_fail, Toast.LENGTH_SHORT).show());
                        }
                    }
                }.start();
                break;
            case R.id.rebootButton:
            default:
                new Thread() {
                    @Override
                    public void run() {
                        if (PS3.reboot()) {
                            handler.post(() -> onDisconnect());
                        }
                        else {
                            handler.post(() -> Toast.makeText(context,
                                    R.string.reboot_fail, Toast.LENGTH_SHORT).show());
                        }
                    }
                }.start();
                break;
        }
    }

}
