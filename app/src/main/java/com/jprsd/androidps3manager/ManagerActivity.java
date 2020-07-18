/*
 * Author: Jaideep Prasad
 * Date: 2020
 */

package com.jprsd.androidps3manager;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jprsd.androidps3manager.ui.identification.IdentificationFragment;
import com.jprsd.androidps3manager.ui.memory.MemoryFragment;
import com.jprsd.androidps3manager.ui.misc.MiscFragment;
import com.jprsd.androidps3manager.ui.system.SystemFragment;

import java.util.ArrayList;
import java.util.List;

import ps3.mods.jprsd.ps3mapi.PS3MAPI;

/**
 * The main activity for this application
 */
public class ManagerActivity extends AppCompatActivity {

    /** PS3 Manager object */
    private final PS3MAPI PS3 = new PS3MAPI();

    /** Fragment manager for dealing with app navigation */
    private final FragmentManager fragmentManager = getSupportFragmentManager();

    /** Main system settings screen */
    private final Fragment systemFragment = new SystemFragment();
    /** Memory settings screen */
    private final Fragment memoryFragment = new MemoryFragment();
    /** Console ID settings screen */
    private final Fragment idFragment = new IdentificationFragment();
    /** Miscellaneous settings screen */
    private final Fragment miscFragment = new MiscFragment();

    /** The active fragment */
    private Fragment visibleFragment;

    /** Tracks dialogs for dismissal to avoid memory leaks */
    private final List<AlertDialog> dialogs = new ArrayList<>();

    /**
     * Activity initialization
     * @param savedInstanceState Any previously saved data
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager.beginTransaction()
                .add(R.id.fragmentPlaceholder, miscFragment).hide(miscFragment).commit();
        fragmentManager.beginTransaction()
                .add(R.id.fragmentPlaceholder, idFragment).hide(idFragment).commit();
        fragmentManager.beginTransaction()
                .add(R.id.fragmentPlaceholder, memoryFragment).hide(memoryFragment).commit();

        fragmentManager.beginTransaction().add(R.id.fragmentPlaceholder, systemFragment).commit();
        visibleFragment = systemFragment;

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_system:
                    fragmentManager.beginTransaction()
                            .hide(visibleFragment).show(systemFragment).commit();
                    visibleFragment = systemFragment;
                    return true;
                case R.id.navigation_memory:
                    fragmentManager.beginTransaction()
                            .hide(visibleFragment).show(memoryFragment).commit();
                    visibleFragment = memoryFragment;
                    return true;
                case R.id.navigation_ids:
                    fragmentManager.beginTransaction()
                            .hide(visibleFragment).show(idFragment).commit();
                    visibleFragment = idFragment;
                    return true;
                case R.id.navigation_misc:
                    fragmentManager.beginTransaction()
                            .hide(visibleFragment).show(miscFragment).commit();
                    visibleFragment = miscFragment;
                    return  true;
            }
            return false;
        });
    }

    /**
     * Activity destruction
     */
    @Override
    protected void onDestroy() {
        for (AlertDialog dialog : dialogs) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        }
        super.onDestroy();
    }

    /**
     * Inflates app menu
     * @param menu The menu
     * @return true if successful
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_menu, menu);
        return true;
    }

    /**
     * Menu item selection handler
     * @param item The item that was selected
     * @return true if the event was handled
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.aboutMenuItem) {
            onAbout();
            return true;
        }
        return false;
    }

    /**
     * Gets the PS3MAPI object
     * @return PS3
     */
    public PS3MAPI getPS3() {
        return PS3;
    }

    /**
     * Displays the application info dialog
     */
    private void onAbout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        builder.setMessage(R.string.message_about);

        final AlertDialog aboutDialog = builder.create();
        dialogs.add(aboutDialog);
        aboutDialog.setButton(DialogInterface.BUTTON_POSITIVE,
                getResources().getText(android.R.string.ok), (dialogInterface, i) -> {
                    dialogs.remove(aboutDialog);
                    aboutDialog.dismiss();
                });
        aboutDialog.show();
    }

}
