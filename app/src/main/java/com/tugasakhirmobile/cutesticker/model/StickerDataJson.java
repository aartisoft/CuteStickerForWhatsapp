package com.tugasakhirmobile.cutesticker.model;

import java.util.ArrayList;

public class StickerDataJson {
    private String ios_app_store_link;

    private ArrayList<Sticker_packs> sticker_packs;

    private String android_play_store_link;

    public String getIos_app_store_link() {
        return ios_app_store_link;
    }

    public void setIos_app_store_link(String ios_app_store_link) {
        this.ios_app_store_link = ios_app_store_link;
    }

    public ArrayList<Sticker_packs> getSticker_packs() {
        return sticker_packs;
    }

    public void setSticker_packs(ArrayList<Sticker_packs> sticker_packs) {
        this.sticker_packs = sticker_packs;
    }

    public String getAndroid_play_store_link() {
        return android_play_store_link;
    }

    public void setAndroid_play_store_link(String android_play_store_link) {
        this.android_play_store_link = android_play_store_link;
    }

    @Override
    public String toString() {
        return "ClassPojo [ios_app_store_link = " + ios_app_store_link + ", sticker_packs = " + sticker_packs + ", android_play_store_link = " + android_play_store_link + "]";
    }
}
