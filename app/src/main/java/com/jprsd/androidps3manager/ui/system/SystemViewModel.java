/*
 * Author: Jaideep Prasad
 * Date: 2020
 */

package com.jprsd.androidps3manager.ui.system;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * View model for live data and UI changes in the app's system fragment
 */
public class SystemViewModel extends ViewModel {

    /**
     * Observable boolean value for the PS3 connection state
     */
    private MutableLiveData<Boolean> connected;

    /**
     * Default constructor
     */
    public SystemViewModel() {
        connected = new MutableLiveData<>();
        connected.setValue(false);
    }

    /**
     * Gets the observable connection state
     * @return Boolean live data
     */
    public LiveData<Boolean> isConnected() {
        return connected;
    }

    /**
     * Sets the connection state and notifies observers of changes
     * @param connected The new state
     */
    public void setConnected(boolean connected) {
        this.connected.setValue(connected);
    }

}
