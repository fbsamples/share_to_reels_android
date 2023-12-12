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
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class ShareToIGReelsSingleImageActivity extends AppCompatActivity {
    final int imageRequestCode = 42;
    final String igAppName = "Instagram";
    final String igReelsIntentName = "com.instagram.share.ADD_TO_REEL";
    final String sourceApplicationKey = "source_application";
    final String appIDKey = "com.instagram.platform.extra.APPLICATION_ID";
    final String mediaType = "image/*";

    private ImageView imageToShare;
    private Button btnLoadImage, btnShareToIGReels, btnShareToIGReelsWithSticker;
    private ArrayList<Button> buttons;
    private Uri targetUri;
    private String appID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_to_ig_reels_single_image);

        final Activity activity = this;

        appID = getString(R.string.facebook_app_id);
        getTargetElements();
        buttons = new ArrayList<>(Arrays.asList(btnLoadImage, btnShareToIGReels, btnShareToIGReelsWithSticker));
        loadInitialState();
        Uri stickerAssetUri = Utils.getSampleSticker(getApplicationContext());

        btnLoadImage.setOnClickListener(view -> {
            Intent intent = Utils.getImageUploaderIntent();

            activity.startActivityForResult(intent, imageRequestCode);
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

        Utils.loadImagePreview(imageToShare,getContentResolver(), getApplicationContext(), targetUri);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == -1 && data != null && requestCode == imageRequestCode) {
            for (Button b : buttons) {
                Utils.changeButtonStatus(b, true);
            }

            Uri imageUri = data.getData();
            targetUri = imageUri;
        }

        Utils.loadImagePreview(imageToShare,getContentResolver(), getApplicationContext(), targetUri);
    }

    private void getTargetElements() {
        imageToShare = findViewById(R.id.imageToShare);
        btnLoadImage = findViewById(R.id.btnLoadImage);
        btnShareToIGReels = findViewById(R.id.btnShareToIGReels);
        btnShareToIGReelsWithSticker = findViewById(R.id.btnShareToIGReelsWithSticker);
    }

    private void loadInitialState() {
        for (Button b : buttons) {
            Utils.changeButtonStatus(b, true);
        }

        loadSampleImage();
    }

    private void loadSampleImage() {
        targetUri = Utils.getSampleImage(getApplicationContext());
        imageToShare.setImageURI(targetUri);
    }

    private Intent buildIGReelsIntent() {
        Intent intent = Utils.getInitialIntent(igReelsIntentName);
        intent.setDataAndType(targetUri, mediaType);
        intent.setPackage(Utils.igPackageName);

        intent.putExtra(sourceApplicationKey, this.getPackageName());
        intent.putExtra(appIDKey, appID);

        intent.putExtra(Intent.EXTRA_STREAM, targetUri);
        Utils.giveUriPermissions(this, intent, targetUri, Utils.igPackageName);

        return intent;
    }
}
