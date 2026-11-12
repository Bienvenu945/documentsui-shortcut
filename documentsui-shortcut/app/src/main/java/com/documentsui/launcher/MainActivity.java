// Huawei EMUI/HarmonyOS requires that Activity names do not contain 'shortcut' to ensure proper icon cropping
package com.documentsui.launcher;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
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

    // Optional extra set by the long-press launcher shortcuts (see res/xml/shortcuts.xml).
    // Only the folder names below are accepted, so an outside caller cannot request anything else.
    static final String EXTRA_FOLDER = "folder";
    private static final String[] FOLDERS = {"Download", "Documents", "DCIM", "Android/data"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String folder = allowedFolder(getIntent().getStringExtra(EXTRA_FOLDER));
        if (!openFileManager(folder)) {
            Toast.makeText(this, R.string.no_file_manager, Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    private static String allowedFolder(String folder) {
        for (String allowed : FOLDERS) {
            if (allowed.equals(folder)) {
                return allowed;
            }
        }
        return null;
    }

    private boolean openFileManager(String folder) {
        Uri primaryRoot = DocumentsContract.buildRootUri(EXTERNAL_STORAGE_AUTHORITY, "primary");
        Uri primaryDirectory = DocumentsContract.buildDocumentUri(
                EXTERNAL_STORAGE_AUTHORITY,
                "primary:");

        // A requested folder is tried first; if this device's DocumentsUI cannot open it,
        // fall back to the storage root exactly like a plain launcher tap.
        if (folder != null) {
            Uri folderUri = DocumentsContract.buildDocumentUri(
                    EXTERNAL_STORAGE_AUTHORITY,
                    "primary:" + folder);
            if (tryStartInDocumentsUi(
                    browserIntent(Intent.ACTION_VIEW, folderUri,
                            DocumentsContract.Document.MIME_TYPE_DIR))) {
                return true;
            }
        }

        return tryStartInDocumentsUi(
                        browserIntent(ACTION_BROWSE, primaryRoot,
                                DocumentsContract.Root.MIME_TYPE_ITEM))
                || tryStartInDocumentsUi(
                        browserIntent(Intent.ACTION_VIEW, primaryRoot,
                                DocumentsContract.Root.MIME_TYPE_ITEM))
                || tryStartInDocumentsUi(
                        browserIntent(Intent.ACTION_VIEW, primaryDirectory,
                                DocumentsContract.Document.MIME_TYPE_DIR))
                || launchKnownDocumentsUi();
    }

    private boolean launchKnownDocumentsUi() {
        for (String packageName : DOCUMENTS_UI_PACKAGES) {
            Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
            if (intent != null && tryStart(intent)) {
                return true;
            }
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
            if (tryStart(packageIntent)) {
                return true;
            }
        }

        return tryStart(intent);
    }

    private boolean tryStart(Intent intent) {
        if (intent.resolveActivity(getPackageManager()) == null) {
            return false;
        }

        try {
            startActivity(intent);
            return true;
        } catch (ActivityNotFoundException | SecurityException ignored) {
            return false;
        }
    }
}
