/*
 * Copyright (c) WhatsApp Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.tugasakhirmobile.cutesticker;

import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.JsonReader;


import com.tugasakhirmobile.cutesticker.model.Sticker;
import com.tugasakhirmobile.cutesticker.model.Sticker_packs;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

class ContentFileParser {

    private static final int LIMIT_EMOJI_COUNT = 2;

    @NonNull
    static List<Sticker_packs> parseStickerPacks(@NonNull InputStream contentsInputStream) throws IOException, IllegalStateException {
        try (JsonReader reader = new JsonReader(new InputStreamReader(contentsInputStream))) {
            return readStickerPacks(reader);
        }
    }

    @NonNull
    private static List<Sticker_packs> readStickerPacks(@NonNull JsonReader reader) throws IOException, IllegalStateException {
        List<Sticker_packs> stickerPackList = new ArrayList<>();
        String androidPlayStoreLink = null;
        String iosAppStoreLink = null;
        reader.beginObject();
        while (reader.hasNext()) {
            String key = reader.nextName();
            if ("android_play_store_link".equals(key)) {
                androidPlayStoreLink = reader.nextString();
            } else if ("ios_app_store_link".equals(key)) {
                iosAppStoreLink = reader.nextString();
            } else if ("sticker_packs".equals(key)) {
                reader.beginArray();
                while (reader.hasNext()) {
                    Sticker_packs stickerPack = readStickerPack(reader);
                    stickerPackList.add(stickerPack);
                }
                reader.endArray();
            } else {
                throw new IllegalStateException("unknown field in json: " + key);
            }
        }
        reader.endObject();
        if (stickerPackList.size() == 0) {
            throw new IllegalStateException("sticker pack list cannot be empty");
        }
        for (Sticker_packs stickerPack : stickerPackList) {
            stickerPack.setAndroidPlayStoreLink(androidPlayStoreLink);
            stickerPack.setIosAppStoreLink(iosAppStoreLink);
        }
        return stickerPackList;
    }

    @NonNull
    private static Sticker_packs readStickerPack(@NonNull JsonReader reader) throws IOException, IllegalStateException {
        reader.beginObject();
        String identifier = null;
        String name = null;
        String publisher = null;
        String trayImageFile = null;
        String publisherEmail = null;
        String publisherWebsite = null;
        String privacyPolicyWebsite = null;
        String licenseAgreementWebsite = null;
        List<Sticker> stickerList = null;
        while (reader.hasNext()) {
            String key = reader.nextName();
            switch (key) {
                case "identifier":
                    identifier = reader.nextString();
                    break;
                case "name":
                    name = reader.nextString();
                    break;
                case "publisher":
                    publisher = reader.nextString();
                    break;
                case "tray_image_file":
                    trayImageFile = reader.nextString();
                    break;
                case "publisher_email":
                    publisherEmail = reader.nextString();
                    break;
                case "publisher_website":
                    publisherWebsite = reader.nextString();
                    break;
                case "privacy_policy_website":
                    privacyPolicyWebsite = reader.nextString();
                    break;
                case "license_agreement_website":
                    licenseAgreementWebsite = reader.nextString();
                    break;
                case "stickers":
                    stickerList = readStickers(reader);
                    break;
                default:
                    reader.skipValue();
            }
        }
        if (TextUtils.isEmpty(identifier)) {
            throw new IllegalStateException("identifier cannot be empty");
        }
        if (TextUtils.isEmpty(name)) {
            throw new IllegalStateException("name cannot be empty");
        }
        if (TextUtils.isEmpty(publisher)) {
            throw new IllegalStateException("publisher cannot be empty");
        }
        if (TextUtils.isEmpty(trayImageFile)) {
            throw new IllegalStateException("tray_image_file cannot be empty");
        }
        if (stickerList == null || stickerList.size() == 0) {
            throw new IllegalStateException("sticker list is empty");
        }
        if (identifier.contains("..") || identifier.contains("/")) {
            throw new IllegalStateException("identifier should not contain .. or / to prevent directory traversal");
        }
        reader.endObject();
        final Sticker_packs stickerPack = new Sticker_packs(identifier, name, publisher, trayImageFile, publisherEmail, publisherWebsite, privacyPolicyWebsite, licenseAgreementWebsite);
        stickerPack.setStickers(stickerList);
        return stickerPack;
    }

    @NonNull
    private static List<Sticker> readStickers(@NonNull JsonReader reader) throws IOException, IllegalStateException {
        reader.beginArray();
        List<Sticker> stickerList = new ArrayList<>();

        while (reader.hasNext()) {
            reader.beginObject();
            String imageFile = null;
            List<String> emojis = new ArrayList<>(LIMIT_EMOJI_COUNT);
            while (reader.hasNext()) {
                final String key = reader.nextName();
                if ("image_file".equals(key)) {
                    imageFile = reader.nextString();
                } else if ("emojis".equals(key)) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        String emoji = reader.nextString();
                        emojis.add(emoji);
                    }
                    reader.endArray();
                } else {
                    throw new IllegalStateException("unknown field in json: " + key);
                }
            }
            reader.endObject();
            if (TextUtils.isEmpty(imageFile)) {
                throw new IllegalStateException("sticker image_file cannot be empty");
            }
            if (!imageFile.endsWith(".webp")) {
                throw new IllegalStateException("image file for stickers should be webp files, image file is: " + imageFile);
            }
            if (imageFile.contains("..") || imageFile.contains("/")) {
                throw new IllegalStateException("the file name should not contain .. or / to prevent directory traversal, image file is:" + imageFile);
            }
            stickerList.add(new Sticker(imageFile, emojis));
        }
        reader.endArray();
        return stickerList;
    }
}
