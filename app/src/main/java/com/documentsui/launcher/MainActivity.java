// Huawei EMUI/HarmonyOS requires that Activity names do not contain 'shortcut' to ensure proper icon cropping
package com.documentsui.launcher;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.documentsui.shortcut.R;

public final class MainActivity extends Activity {
    private static final String ACTION_BROWSE = "android.provider.action.BROWSE";
    private static final String EXTRA_SHOW_ADVANCED = "android.provider.extra.SHOW_ADVANCED";
    private static final String EXTERNAL_STORAGE_AUTHORITY =
            "com.android.externalstorage.documents";
    private static final String[] DOCUMENTS_UI_PACKAGES = {
            "com.google.android.documentsui",
            "com.android.documentsui"
    };
    private static final String EXTRA_FOLDER = "folder";
    private static final String[] FOLDERS = {"Download", "Documents", "DCIM", "Android/data"};

    private final int bg = Color.rgb(7, 31, 34);
    private final int mint = Color.rgb(111, 255, 224);
    private final int pale = Color.rgb(211, 255, 247);
    private final int muted = Color.rgb(78, 190, 173);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String folder = allowedFolder(getIntent().getStringExtra(EXTRA_FOLDER));
        if (folder != null) {
            if (!openFileManager(folder)) Toast.makeText(this, R.string.no_file_manager, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        showDashboard();
    }

    private void showDashboard() {
        getWindow().setStatusBarColor(Color.rgb(8, 36, 39));
        getWindow().setNavigationBarColor(Color.rgb(5, 24, 27));
        getWindow().getDecorView().setSystemUiVisibility(0);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackground(new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                new int[]{Color.rgb(10, 43, 45), bg, Color.rgb(5, 25, 28)}));

        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(24), dp(18), dp(24), dp(24));
        page.setMinimumHeight(getResources().getDisplayMetrics().heightPixels);
        scroll.addView(page, new ScrollView.LayoutParams(-1, -2));

        TextView clock = text("12:00", 14, pale, Typeface.NORMAL);
        clock.setLetterSpacing(.04f);
        page.addView(clock);

        addSpace(page, 42);
        TextView eyebrow = text("A C C È S   D I R E C T", 13, mint, Typeface.BOLD);
        page.addView(eyebrow);

        TextView title = text("F I C H I E R S", 38, pale, Typeface.NORMAL);
        title.setLetterSpacing(.02f);
        page.addView(title);
        addSpace(page, 16);

        View accent = new View(this);
        accent.setBackground(round(mint, 0, 0, 0));
        page.addView(accent, new LinearLayout.LayoutParams(dp(76), dp(4)));
        addSpace(page, 34);

        LinearLayout primary = glassPanel(2.2f);
        primary.setPadding(dp(20), dp(22), dp(18), dp(20));
        primary.setGravity(Gravity.CENTER_VERTICAL);
        primary.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout mainText = new LinearLayout(this);
        mainText.setOrientation(LinearLayout.VERTICAL);
        TextView open = text("Ouvrir le stockage", 22, pale, Typeface.BOLD);
        TextView desc = text("Tout parcourir dans l’app\nFichiers du système", 16, muted, Typeface.NORMAL);
        addSpace(mainText, 3);
        mainText.addView(open);
        addSpace(mainText, 7);
        mainText.addView(desc);
        primary.addView(mainText, new LinearLayout.LayoutParams(0, -2, 1));
        TextView arrow = text("›", 46, mint, Typeface.NORMAL);
        arrow.setGravity(Gravity.CENTER);
        primary.addView(arrow, new LinearLayout.LayoutParams(dp(36), -1));
        primary.setOnClickListener(v -> launchRoot());
        page.addView(primary, new LinearLayout.LayoutParams(-1, dp(128)));

        addSpace(page, 36);
        page.addView(text("A C C È S   R A P I D E", 13, muted, Typeface.BOLD));
        addSpace(page, 18);

        LinearLayout row1 = new LinearLayout(this);
        row1.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout row2 = new LinearLayout(this);
        row2.setOrientation(LinearLayout.HORIZONTAL);
        addQuickTile(row1, "↓", "Download", 1);
        addQuickTile(row1, "▤", "Documents", 1);
        addQuickTile(row2, "▣", "DCIM", 1);
        addQuickTile(row2, "‹/›", "Android/data", 1);
        page.addView(row1, new LinearLayout.LayoutParams(-1, dp(144)));
        addSpace(page, 14);
        page.addView(row2, new LinearLayout.LayoutParams(-1, dp(144)));

        View spacer = new View(this);
        page.addView(spacer, new LinearLayout.LayoutParams(1, 0, 1));
        addSpace(page, 44);
        TextView footer = text("Conçu par  Bienvenu", 15, muted, Typeface.NORMAL);
        footer.setGravity(Gravity.CENTER);
        page.addView(footer);
        addSpace(page, 8);
        TextView version = text("v1.2.0", 14, muted, Typeface.NORMAL);
        version.setGravity(Gravity.CENTER);
        page.addView(version);

        setContentView(scroll);
    }

    private void addQuickTile(LinearLayout row, String symbol, String label, int weight) {
        LinearLayout tile = glassPanel(1.5f);
        tile.setOrientation(LinearLayout.VERTICAL);
        tile.setGravity(Gravity.CENTER_VERTICAL);
        tile.setPadding(dp(22), dp(18), dp(12), dp(16));
        TextView icon = text(symbol, 34, mint, Typeface.NORMAL);
        icon.setGravity(Gravity.CENTER);
        tile.addView(icon, new LinearLayout.LayoutParams(dp(48), dp(54)));
        addSpace(tile, 14);
        TextView name = text(label, 16, pale, Typeface.NORMAL);
        name.setLetterSpacing(.02f);
        tile.addView(name);
        tile.setOnClickListener(v -> {
            if (!openFileManager(label)) Toast.makeText(this, R.string.no_file_manager, Toast.LENGTH_SHORT).show();
        });
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, -1, weight);
        if (row.getChildCount() > 0) lp.leftMargin = dp(14);
        row.addView(tile, lp);
    }

    private LinearLayout glassPanel(float strokeDp) {
        LinearLayout panel = new LinearLayout(this);
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(Color.argb(28, 122, 255, 232));
        shape.setCornerRadius(dp(28));
        shape.setStroke(dp((int) strokeDp), Color.argb(230, 111, 255, 224));
        panel.setBackground(shape);
        panel.setElevation(dp(5));
        panel.setClickable(true);
        panel.setFocusable(true);
        return panel;
    }

    private GradientDrawable round(int color, int radius, int stroke, int strokeColor) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radius));
        if (stroke > 0) d.setStroke(dp(stroke), strokeColor);
        return d;
    }

    private TextView text(String value, int sp, int color, int style) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(sp);
        t.setTextColor(color);
        t.setTypeface(Typeface.create("sans-serif", style));
        return t;
    }

    private void addSpace(LinearLayout parent, int heightDp) {
        View space = new View(this);
        parent.addView(space, new LinearLayout.LayoutParams(1, dp(heightDp)));
    }

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + .5f);
    }

    private static String allowedFolder(String folder) {
        for (String allowed : FOLDERS) if (allowed.equals(folder)) return allowed;
        return null;
    }

    private void launchRoot() {
        if (!openFileManager(null)) Toast.makeText(this, R.string.no_file_manager, Toast.LENGTH_SHORT).show();
    }

    private boolean openFileManager(String folder) {
        Uri primaryRoot = DocumentsContract.buildRootUri(EXTERNAL_STORAGE_AUTHORITY, "primary");
        Uri primaryDirectory = DocumentsContract.buildDocumentUri(EXTERNAL_STORAGE_AUTHORITY, "primary:");

        if (folder != null) {
            Uri folderUri = DocumentsContract.buildDocumentUri(EXTERNAL_STORAGE_AUTHORITY, "primary:" + folder);
            if (tryStartInDocumentsUi(browserIntent(Intent.ACTION_VIEW, folderUri,
                    DocumentsContract.Document.MIME_TYPE_DIR))) return true;
        }
        return tryStartInDocumentsUi(browserIntent(ACTION_BROWSE, primaryRoot,
                        DocumentsContract.Root.MIME_TYPE_ITEM))
                || tryStartInDocumentsUi(browserIntent(Intent.ACTION_VIEW, primaryRoot,
                        DocumentsContract.Root.MIME_TYPE_ITEM))
                || tryStartInDocumentsUi(browserIntent(Intent.ACTION_VIEW, primaryDirectory,
                        DocumentsContract.Document.MIME_TYPE_DIR))
                || launchKnownDocumentsUi();
    }

    private boolean launchKnownDocumentsUi() {
        for (String packageName : DOCUMENTS_UI_PACKAGES) {
            Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
            if (intent != null && tryStart(intent)) return true;
        }
        return false;
    }

    private static Intent browserIntent(String action, Uri data, String mimeType) {
        Intent intent = new Intent(action);
        intent.setDataAndType(data, mimeType);
        intent.putExtra(EXTRA_SHOW_ADVANCED, true);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return intent;
    }

    private boolean tryStartInDocumentsUi(Intent intent) {
        for (String packageName : DOCUMENTS_UI_PACKAGES) {
            Intent packageIntent = new Intent(intent);
            packageIntent.setPackage(packageName);
            if (tryStart(packageIntent)) return true;
        }
        return tryStart(intent);
    }

    private boolean tryStart(Intent intent) {
        if (intent.resolveActivity(getPackageManager()) == null) return false;
        try {
            startActivity(intent);
            return true;
        } catch (ActivityNotFoundException | SecurityException ignored) {
            return false;
        }
    }
}
