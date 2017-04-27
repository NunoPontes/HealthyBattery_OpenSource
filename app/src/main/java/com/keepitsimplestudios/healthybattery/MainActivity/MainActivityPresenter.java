package com.keepitsimplestudios.healthybattery.MainActivity;

/**
 * Created by nunop on 27/01/2017.
 */

interface MainActivityPresenter {

    void startServices();

    void registerBroadcasts();

    void unregisterBroadcasts();

    void threadGetDataFiles();

    void alerDialogClickListener();

    void updateAll();

    void stopAllSound();

    void definitionsClickListener();

    void resetDataClickListener();

    void resetData();
}
