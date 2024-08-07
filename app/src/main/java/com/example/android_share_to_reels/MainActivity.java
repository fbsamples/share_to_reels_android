/* Copyright (c) Meta Platforms, Inc. and affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.example.android_share_to_reels;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TableRow;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HashMap<TableRow, Class<?>> m = new HashMap<TableRow, Class<?>>() {{
            put(findViewById(R.id.fb_reels_row), ShareToFBReelsActivity.class);
            put(findViewById(R.id.ig_reels_row), IGReelsMenuActivity.class);
        }};

        for (Map.Entry<TableRow, Class<?>> entry : m.entrySet()) {
            entry.getKey().setOnClickListener(view -> {
                Intent i = new Intent(MainActivity.this, entry.getValue());
                startActivity(i);
            });
        }
    }

}
