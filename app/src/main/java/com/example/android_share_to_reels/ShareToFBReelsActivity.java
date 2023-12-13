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
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class ShareToFBReelsActivity extends AppCompatActivity {
    final int videoRequestCode = 42;
    final String fbAppName = "Facebook";
    final String fbReelsIntentName = "com.facebook.reels.SHARE_TO_REEL";
    final String appIDKey = "com.facebook.platform.extra.APPLICATION_ID";
    final String mediaType = "video/*";

    private VideoView videoToShare;
    private Button btnLoadVideo, btnShareToFBReels, btnShareToFBReelsWithSticker;
    private ArrayList<Button> buttons;
    private Uri targetUri;
    private String appID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_to_fb_reels);

        final Activity activity = this;

        appID = getString(R.string.facebook_app_id);
        getTargetElements();
        buttons = new ArrayList<>(Arrays.asList(btnLoadVideo, btnShareToFBReels, btnShareToFBReelsWithSticker));
        loadInitialState();
        Uri stickerAssetUri = Utils.getSampleSticker(getApplicationContext());

        btnLoadVideo.setOnClickListener(view -> {
            Intent intent = Utils.getVideoUploaderIntent();

            activity.startActivityForResult(intent, videoRequestCode);
        });

        btnShareToFBReels.setOnClickListener(view -> {
            Intent intent = buildFBReelsIntent();

            Utils.startActivityWithSharedContent(activity, intent, fbAppName);
        });

        btnShareToFBReelsWithSticker.setOnClickListener(view -> {
            Intent intent = buildFBReelsIntent();
            Utils.configureStickerURI(activity, intent, stickerAssetUri, Utils.fbPackageName);

            Utils.startActivityWithSharedContent(activity, intent, fbAppName);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Utils.loadVideoPreview(videoToShare, targetUri);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == -1 && data != null && requestCode == videoRequestCode) {
            for (Button b : buttons) {
                Utils.changeButtonStatus(b, true);
            }

            targetUri = data.getData();
        }
        Utils.loadVideoPreview(videoToShare, targetUri);
    }

    private void getTargetElements() {
        videoToShare = findViewById(R.id.videoToShare);
        btnLoadVideo = findViewById(R.id.btnLoadVideo);
        btnShareToFBReels = findViewById(R.id.btnShareToFBReels);
        btnShareToFBReelsWithSticker = findViewById(R.id.btnShareToFBReelsWithSticker);
    }

    private void loadInitialState() {
        for (Button b : buttons) {
            Utils.changeButtonStatus(b, true);
        }

        loadSampleVideo();
    }

    private void loadSampleVideo() {
        targetUri = Utils.getSampleVideo(getApplicationContext());

        Utils.loadVideoPreview(videoToShare, targetUri);
    }

    private Intent buildFBReelsIntent() {
        Intent intent = Utils.getInitialIntent(fbReelsIntentName);
        intent.setDataAndType(targetUri, mediaType);
        intent.setPackage(Utils.fbPackageName);

        intent.putExtra(appIDKey, appID);
        intent.putExtra(Intent.EXTRA_STREAM, targetUri);

        Utils.giveUriPermissions(this, intent, targetUri, Utils.fbPackageName);

        return intent;
    }
}
