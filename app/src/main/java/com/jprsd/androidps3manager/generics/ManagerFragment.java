/*
 * Author: Jaideep Prasad
 * Date: 2020
 */

package com.jprsd.androidps3manager.generics;

import android.os.Handler;
import android.os.Looper;

import androidx.fragment.app.Fragment;

import com.jprsd.androidps3manager.ManagerActivity;

import ps3.mods.jprsd.ps3mapi.PS3MAPI;

/**
 * Common generic class for all fragments used in this app
 */
public abstract class ManagerFragment extends Fragment {

    /**
     * Handler for posting UI updates in background threads
     */
    protected final Handler handler = new Handler(Looper.getMainLooper());

    /**
     * Gets the PS3MAPI object from the main activity
     * @return PS3
     */
    protected final PS3MAPI PS3() {
        return  ((ManagerActivity) requireActivity()).getPS3();
    }

}
