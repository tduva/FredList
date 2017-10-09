package io.github.tduva.fredlist.d;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import io.github.tduva.fredlist.util.Helper;

/**
 * Created by tduva on 22.08.2017.
 */

public class AutoBackup {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd--HH-mm");

    private static final long MINUTE = 60*1000;
    private static final long HOUR = 60*MINUTE;

    private static final String FILE_PREFIX = "fredlist_backup_";
    private static final long BACKUP_CHECK_INTERVAL = 30*MINUTE;
    private static final long BACKUP_INTERVAL = 24*HOUR;
    private static final int KEEP_NUM_BACKUPS = 14;

    private long lastBackupAttempt;

    public void perform(File dir, DataBasis db) {
        if (System.currentTimeMillis() - lastBackupAttempt < BACKUP_CHECK_INTERVAL) {
            return;
        }

        // Actually perform some checking
        lastBackupAttempt = System.currentTimeMillis();
        if (!dir.isDirectory()) {
            if (!dir.mkdir()) {
                Helper.debug("Unable to make directory: "+dir);
            }
        }
        long latestBackupTime = deleteOldBackups(dir);
        String readableTime = DATE_FORMAT.format(new Date(latestBackupTime));
        Helper.debug("Latest found backup from "+readableTime+" (now: "+getCurrentDate()+")");
        if (System.currentTimeMillis() - latestBackupTime > BACKUP_INTERVAL) {
            File file = new File(dir, getFileName());
            if (file.exists()) {
                file = new File(dir, getFileName()+"_");
            }
            try {
                db.backupTo(file);
            } catch (IOException ex) {
                Helper.debug("Failed to backup ("+file+"): "+ex);
            }
        }
    }

    private long deleteOldBackups(File dir) {
        File[] files = getBackupFiles(dir);
        if (files == null) {
            Helper.debug("Couldn't list files in for deletion: "+dir);
            return 0;
        }
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File file1, File file2) {
                return Long.valueOf(file1.lastModified()).compareTo(file2.lastModified());
            }
        });
        for (int i = 0; i < files.length - KEEP_NUM_BACKUPS; i++) {
            File file = files[i];
            if (!file.delete()) {
                Helper.debug("Failed to delete " + file);
            } else {
                Helper.debug("Deleted old backup " + file);
            }
        }
        if (files.length == 0) {
            return 0;
        }
        return files[files.length - 1].lastModified();
    }

    private File[] getBackupFiles(File dir) {
        return dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().startsWith(FILE_PREFIX);
            }
        });
    }

    private String getFileName() {
        return FILE_PREFIX+getCurrentDate();
    }

    private String getCurrentDate() {
        return DATE_FORMAT.format(new Date());
    }

}
