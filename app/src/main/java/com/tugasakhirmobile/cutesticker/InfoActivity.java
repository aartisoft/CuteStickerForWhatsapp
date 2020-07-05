/*
 * Copyright (c) WhatsApp Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.tugasakhirmobile.cutesticker;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import android.view.View;
import android.widget.TextView;

public class InfoActivity extends BaseActivity implements View.OnClickListener {

    CardView menuApp, menuRateApp, menuShareApp, menuPrivacypolicy;
    TextView txtVersionCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_activity_info);
        }

        txtVersionCode = (TextView) findViewById(R.id.txtVersionCode);
        menuApp = (CardView) findViewById(R.id.menuApp);
        menuRateApp = (CardView) findViewById(R.id.menuRateApp);
        menuShareApp = (CardView) findViewById(R.id.menuShareApp);
        menuPrivacypolicy = (CardView) findViewById(R.id.menuPrivacypolicy);
        menuRateApp.setOnClickListener(this);
        menuShareApp.setOnClickListener(this);
        menuPrivacypolicy.setOnClickListener(this);

        txtVersionCode.setText("V" + BuildConfig.VERSION_NAME);
    }

    private void RateUsApp() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    private void shareApp() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        String shareText = "Invite you to install this app : https://play.google.com/store/apps/details?id=" + getPackageName();
        i.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(i, "Share App"));
    }

    private void privacyPolicy() {
        //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://shopostreet.in/WAStickers_Privacy_Policy.html")));
        startActivity(new Intent(this, PrivacyPolicyActivity.class));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menuRateApp:
                RateUsApp();
                break;
            case R.id.menuShareApp:
                shareApp();
                break;
            case R.id.menuPrivacypolicy:
                privacyPolicy();
                break;
        }
    }
}
