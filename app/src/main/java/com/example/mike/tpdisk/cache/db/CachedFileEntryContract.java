package com.example.mike.tpdisk.cache.db;

import android.provider.BaseColumns;

/**
 * Created by Andrey
 * 26.11.2014.
 */
@Deprecated
public class CachedFileEntryContract {
    private CachedFileEntryContract() {}

    public static abstract class CachedFileEntry implements BaseColumns {
        public static final String TABLE_NAME = "cached_images";
        public static final String COLUMN_NAME_PATH = "path";
        public static final String COLUMN_NAME_HASH = "hash";
    }

}
