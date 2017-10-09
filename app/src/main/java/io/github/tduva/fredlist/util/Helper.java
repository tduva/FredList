package io.github.tduva.fredlist.util;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;

import java.io.File;

import io.github.tduva.fredlist.R;
import io.github.tduva.fredlist.d.M;

/**
 * Created by tduva on 23.08.2017.
 */

public class Helper {

    public static void debug(String line) {
        System.out.println("[FredList] "+line);
    }

    public static void scanMediaFiles(Context context, String path) {
        // https://stackoverflow.com/questions/13507789/folder-added-in-android-not-visible-via-usb
        // initiate media scan and put the new things into the path array to
        // make the scanner aware of the location and the files you want to see
        Helper.debug("Media scan: "+path);
        MediaScannerConnection.scanFile(context, new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String path, Uri uri) {
                Helper.debug("Media scan completed: "+path);
            }
        });
    }

    public static String getListShortName(Context context, int listId) {
        if (listId == -1) {
            return context.getString(R.string.all_lists);
        }
        String shortName = M.db(context).getListShortName(listId);
        if (shortName != null) {
            return shortName;
        }
        return M.db(context).getListName(listId);
    }

    public static String getCategoryShortName(Context context, int categoryId) {
        if (categoryId == -1) {
            return context.getString(R.string.all_categories);
        }
        String shortName = M.db(context).getCategoryShortName(categoryId);
        if (shortName != null) {
            return shortName;
        }
        return M.db(context).getCategoryName(categoryId);
    }

}