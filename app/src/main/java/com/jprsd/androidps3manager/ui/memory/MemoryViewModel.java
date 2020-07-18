/*
 * Author: Jaideep Prasad
 * Date: 2020
 */

package com.jprsd.androidps3manager.ui.memory;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * View model for live data and UI changes in the app's memory fragment
 */
public class MemoryViewModel extends ViewModel {

    /**
     * Observable boolean value for the process-attached state
     */
    private MutableLiveData<Boolean> attached;

    /**
     * Default constructor
     */
    public MemoryViewModel() {
        attached = new MutableLiveData<>();
        attached.setValue(false);
    }

    /**
     * Gets the observable attachment state
     * @return Boolean live data
     */
    public LiveData<Boolean> isAttached() {
        return attached;
    }

    /**
     * Updates the attachment state and notifies observers of changes
     * @param attached The new state
     */
    public void setAttached(boolean attached) {
        this.attached.setValue(attached);
    }

}
