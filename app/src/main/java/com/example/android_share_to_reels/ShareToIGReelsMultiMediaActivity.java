/* Copyright (c) Meta Platforms, Inc. and affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.example.android_share_to_reels;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class ShareToIGReelsMultiMediaActivity extends AppCompatActivity {
    final int requestCode = 42;
    final String igAppName = "Instagram";
    final String igReelsIntentName = "com.instagram.share.ADD_TO_REEL_MULTIPLE";
    final String sourceApplicationKey = "source_application";
    final String appIDKey = "com.instagram.platform.extra.APPLICATION_ID";
    final String mediaType = "*/*";

    private Button btnLoadMedia, btnShareToIGReels, btnShareToIGReelsWithSticker;
    private ArrayList<Button> buttons;
    private ArrayList<Uri> mediaList;
    private Uri targetUri;
    private String appID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_to_ig_reels_multi_media);

        final Activity activity = this;

        mediaList = new ArrayList();
        appID = getString(R.string.facebook_app_id);
        getTargetElements();
        buttons = new ArrayList<>(Arrays.asList(btnLoadMedia, btnShareToIGReels, btnShareToIGReelsWithSticker));
        loadInitialState();
        Uri stickerAssetUri = Utils.getSampleSticker(getApplicationContext());

        btnLoadMedia.setOnClickListener(view -> {
            Intent intent = Utils.getMediaUploaderIntent();
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

            activity.startActivityForResult(intent, requestCode);
        });

        btnShareToIGReels.setOnClickListener(view -> {
            Intent intent = buildIGReelsIntent();

            Utils.startActivityWithSharedContent(activity, intent, igAppName);
        });

        btnShareToIGReelsWithSticker.setOnClickListener(view -> {
            Intent intent = buildIGReelsIntent();
            Utils.configureStickerURI(activity, intent, stickerAssetUri, Utils.igPackageName);

            Utils.startActivityWithSharedContent(activity, intent, igAppName);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1 && data != null && requestCode == requestCode) {
            for (Button b : buttons) {
                Utils.changeButtonStatus(b, true);
            }

            mediaList.clear();

            if (data.getClipData() != null) {
                for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                    Uri uri = data.getClipData().getItemAt(i).getUri();
                    if (i == 0) {
                        targetUri = uri;
                    }
                    mediaList.add(uri);
                }
            } else {
                Uri uri = data.getData();
                mediaList.add(uri);
                targetUri = uri;
            }
        }
    }

    private void getTargetElements() {
        btnLoadMedia = findViewById(R.id.btnLoadMedia);
        btnShareToIGReels = findViewById(R.id.btnShareToIGReels);
        btnShareToIGReelsWithSticker = findViewById(R.id.btnShareToIGReelsWithSticker);
    }

    private void loadInitialState() {
        for (Button b : buttons) {
            Utils.changeButtonStatus(b, true);
        }

        loadSampleMedia();
    }

    private void loadSampleMedia() {
        targetUri = Utils.getSampleVideo(this.getApplicationContext());
        mediaList.add(targetUri);
        mediaList.add(Utils.getSampleImage(this.getApplicationContext()));
    }

    private Intent buildIGReelsIntent() {
        Intent intent = Utils.getInitialIntent(igReelsIntentName);
        intent.setDataAndType(targetUri, mediaType);
        intent.setPackage(Utils.igPackageName);

        intent.putExtra(sourceApplicationKey, this.getPackageName());
        intent.putExtra(appIDKey, appID);

        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, mediaList);
        for (Uri uri : mediaList) {
            Utils.giveUriPermissions(this, intent, uri, Utils.igPackageName);
        }

        return intent;
    }
}
