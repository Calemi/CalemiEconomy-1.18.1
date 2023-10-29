package com.tm.calemieconomy.file;

import com.google.gson.reflect.TypeToken;
import com.tm.calemieconomy.util.helper.FileHelper;

public class DirtyFile {

    public static long dirtyDate;

    public static void init() {
        dirtyDate = FileHelper.readFileOrCreate("Dirty", (long)0, new TypeToken<Long>(){});
    }

    public static void markDirty() {
        FileHelper.saveToFile("Dirty", System.nanoTime());
        dirtyDate = System.nanoTime();
    }
}