/*
 * Author: Jaideep Prasad
 * Date: 2020
 */

package com.jprsd.androidps3manager.ui.memory;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.jprsd.androidps3manager.R;
import com.jprsd.androidps3manager.generics.ManagerFragment;
import com.jprsd.androidps3manager.ui.system.SystemViewModel;

import ps3.mods.jprsd.ps3mapi.PS3MAPI;

/**
 * Fragment for the PS3 memory management section of the app
 */
public class MemoryFragment extends ManagerFragment {

    /**
     * View model for live data and UI changes in this fragment
     */
    private MemoryViewModel memoryViewModel;

    /**
     * Initializer
     * @param inflater Inflater for developing the layout as specified in the XML
     * @param container The layout group this fragment is housed in
     * @param savedInstanceState Any previously saved data
     * @return The fragment root view
     */
    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SystemViewModel systemViewModel =
                new ViewModelProvider(requireActivity()).get(SystemViewModel.class);
        memoryViewModel = new ViewModelProvider(this).get(MemoryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_memory, container, false);

        final TextView processIdText = root.findViewById(R.id.processIdText);
        final Button attachButton = root.findViewById(R.id.attachButton);
        final EditText memAddressGetText = root.findViewById(R.id.memAddressGetText);
        final EditText bytesNumText = root.findViewById(R.id.bytesNumText);
        final Button readMemoryButton = root.findViewById(R.id.readMemoryButton);
        final TextView memoryOutputText = root.findViewById(R.id.memoryOutputText);
        final EditText memAddressSetText = root.findViewById(R.id.memAddressSetText);
        final EditText memValueSetText = root.findViewById(R.id.memValueSetText);
        final Button writeMemoryButton = root.findViewById(R.id.writeMemoryButton);

        systemViewModel.isConnected().observe(getViewLifecycleOwner(), connected -> {
            if (connected != null && connected) {
                attachButton.setEnabled(true);
            }
            else {
                attachButton.setEnabled(false);
                memoryViewModel.setAttached(false);
            }
        });
        memoryViewModel.isAttached().observe(getViewLifecycleOwner(), attached -> {
            if (attached != null && attached) {
                attachButton.setEnabled(false);
                processIdText.setText(Long.toString(PS3().getProcessId()));
                readMemoryButton.setEnabled(true);
                writeMemoryButton.setEnabled(true);
            }
            else {
                attachButton.setEnabled(true);
                processIdText.setText(getResources().getText(R.string.not_attached));
                readMemoryButton.setEnabled(false);
                writeMemoryButton.setEnabled(false);
            }
        });

        attachButton.setOnClickListener(view -> onAttach());
        readMemoryButton.setOnClickListener(view -> {
            String numBytesString = bytesNumText.getText().toString().trim();
            if (numBytesString.equals("")) {
                numBytesString = "1";
            }
            else {
                for (int i = 0; i < numBytesString.length(); i++) {
                    if (!Character.isDigit(numBytesString.charAt(i))) {
                        numBytesString = "1";
                        break;
                    }
                }
            }
            onReadMemory(memAddressGetText.getText().toString().trim(),
                    Integer.parseInt(numBytesString),
                    memoryOutputText);
        });
        writeMemoryButton.setOnClickListener(view ->
                onWriteMemory(memAddressSetText.getText().toString().trim(),
                        memValueSetText.getText().toString().trim()));

        return root;
    }

    /**
     * Attach button handler
     */
    private void onAttach() {
        final PS3MAPI PS3 = PS3();
        final Context context = getContext();
        Toast.makeText(context, R.string.attaching, Toast.LENGTH_SHORT).show();
        new Thread() {
            @Override
            public void run() {
                if (PS3.attach()) {
                    handler.post(() -> Toast.makeText(context,
                            R.string.attached, Toast.LENGTH_SHORT).show());
                    handler.post(() -> memoryViewModel.setAttached(true));
                }
                else {
                    handler.post(() -> Toast.makeText(context,
                            R.string.attaching_failed, Toast.LENGTH_SHORT).show());
                }
            }
        }.start();
    }

    /**
     * Button handler for reading PS3 memory
     * @param address PS3 memory address
     * @param length Number of bytes to read
     * @param output The TextView to be updated with the address values
     */
    private void onReadMemory(String address, final int length, final TextView output) {
        final PS3MAPI PS3 = PS3();
        final Context context = getContext();
        final String addressTrimmed = trimHex(address);
        new Thread() {
            @Override
            public void run() {
                String[] result = new String[1];
                if (PS3.getMemory(addressTrimmed, length, result)) {
                    handler.post(() -> output.setText(result[0]));
                }
                else {
                    handler.post(() -> Toast.makeText(context,
                            R.string.read_mem_fail, Toast.LENGTH_SHORT).show());
                }
            }
        }.start();
    }

    /**
     * Button handler for writing memory to the PS3
     * @param address PS3 memory address
     * @param value Hex value
     */
    private void onWriteMemory(String address, final String value) {
        final PS3MAPI PS3 = PS3();
        final Context context = getContext();
        final String addressTrimmed = trimHex(address);
        new Thread() {
            @Override
            public void run() {
                if (PS3.setMemory(addressTrimmed, value)) {
                    handler.post(() -> Toast.makeText(context,
                            R.string.write_mem_success, Toast.LENGTH_SHORT).show());
                }
                else {
                    handler.post(() -> Toast.makeText(context,
                            R.string.write_mem_fail, Toast.LENGTH_SHORT).show());
                }
            }
        }.start();
    }

    /**
     * Removes the 0x prefix on memory address strings if present
     * @param string The hex string to trim
     * @return The trimmed string
     */
    private String trimHex(String string) {
        if (string.length() > 2 && string.toLowerCase().contains("0x")) {
            return string.substring(2);
        }
        return string;
    }

}
