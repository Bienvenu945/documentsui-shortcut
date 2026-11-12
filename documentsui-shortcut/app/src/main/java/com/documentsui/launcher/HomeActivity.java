package com.documentsui.launcher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.documentsui.shortcut.BuildConfig;
import com.documentsui.shortcut.R;

/** Home screen: one big button for the storage root, four folder cards, and the author's signature. */
public final class HomeActivity extends Activity {
    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Android 15+ draws edge-to-edge: keep the content clear of the system bars.
        findViewById(R.id.root).setOnApplyWindowInsetsListener((view, insets) -> {
            view.setPadding(
                    insets.getSystemWindowInsetLeft(),
                    insets.getSystemWindowInsetTop(),
                    insets.getSystemWindowInsetRight(),
                    insets.getSystemWindowInsetBottom());
            return insets;
        });

        // Each card carries the folder name as its tag; the storage card has none.
        View.OnClickListener open = view -> {
            Intent intent = new Intent(this, MainActivity.class);
            Object folder = view.getTag();
            if (folder != null) {
                intent.putExtra(MainActivity.EXTRA_FOLDER, folder.toString());
            }
            startActivity(intent);
        };
        int[] cards = {R.id.card_storage, R.id.card_download, R.id.card_documents,
                R.id.card_dcim, R.id.card_data};
        for (int id : cards) {
            findViewById(id).setOnClickListener(open);
        }

        String author = getString(R.string.hub_author);
        String text = getString(R.string.hub_signature, author);
        SpannableString signature = new SpannableString(text);
        int start = text.indexOf(author);
        if (start >= 0) {
            signature.setSpan(new ForegroundColorSpan(getColor(R.color.ice)),
                    start, start + author.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        ((TextView) findViewById(R.id.signature)).setText(signature);
        ((TextView) findViewById(R.id.version)).setText("v" + BuildConfig.VERSION_NAME);
    }
}
