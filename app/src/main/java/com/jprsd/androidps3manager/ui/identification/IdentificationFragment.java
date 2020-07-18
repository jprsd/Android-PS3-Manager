/*
 * Author: Jaideep Prasad
 * Date: 2020
 */

package com.jprsd.androidps3manager.ui.identification;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.jprsd.androidps3manager.R;
import com.jprsd.androidps3manager.generics.ManagerFragment;
import com.jprsd.androidps3manager.ui.system.SystemViewModel;

import ps3.mods.jprsd.ps3mapi.PS3MAPI;

/**
 * Fragment for the console IDs section of the app
 */
public class IdentificationFragment extends ManagerFragment {

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
        View root = inflater.inflate(R.layout.fragment_ids, container, false);

        final EditText idpsText = root.findViewById(R.id.idpsText);
        final EditText psidText = root.findViewById(R.id.psidText);
        final Button setIdButton = root.findViewById(R.id.setIdButton);

        systemViewModel.isConnected().observe(getViewLifecycleOwner(), connected -> {
            if (connected != null && connected) {
                setIdButton.setEnabled(true);
            }
            else {
                setIdButton.setEnabled(false);
            }
        });
        setIdButton.setOnClickListener(view ->
                onSetIdpsAndPsid(idpsText.getText().toString().trim(),
                        psidText.getText().toString().trim()));
        return root;
    }

    /**
     * Button handler for setting console IDs
     * @param idps New IDPS string
     * @param psid New PSID string
     */
    private void onSetIdpsAndPsid(final String idps, final String psid) {
        final Context context = getContext();
        if (idps.length() != 32 || psid.length() != 32) {
            Toast.makeText(context, R.string.invalid_ids, Toast.LENGTH_SHORT).show();
        }
        else {
            final PS3MAPI PS3 = PS3();
            new Thread() {
                @Override
                public void run() {
                    if (PS3.setIdpsAndPsid(idps.substring(0, 15), idps.substring(16),
                            psid.substring(0, 15), psid.substring(16))) {
                        handler.post(() -> Toast.makeText(context,
                                R.string.success_id_set, Toast.LENGTH_SHORT).show());
                    }
                    else {
                        handler.post(() -> Toast.makeText(context,
                                R.string.failed_set_ids, Toast.LENGTH_SHORT).show());
                    }
                }
            }.start();
        }
    }

}
