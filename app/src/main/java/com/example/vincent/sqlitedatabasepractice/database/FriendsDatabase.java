package com.example.vincent.sqlitedatabasepractice.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Vincent on 6/19/2015.
 */
public class FriendsDatabase extends SQLiteOpenHelper {

    private final Context mContext;
    // database name
    private static final String DB_NAME = "friends.db";
    // database version (for upgrading)
    private static final int DB_VERSION = 2;
    // database strings
    private static final String TEXT = " TEXT NOT NULL";
    private static final String COMMA = ", ";
    private static final String INT = " INTEGER PRIMARY KEY AUTOINCREMENT";
    private static final String DROP_TABLE= "DROP TABLE IF EXISTS " + Tables.FRIENDS;

    // tables in the database
    interface Tables{
        String FRIENDS = "friends";
    }

    // database constructor
    public FriendsDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create the friends database
        db.execSQL("CREATE TABLE " + Tables.FRIENDS + " ("
                + BaseColumns._ID + INT + COMMA
                + FriendsContract.FriendsColumns.FRIENDS_NAME + TEXT + COMMA
                + FriendsContract.FriendsColumns.FRIENDS_PHONE + TEXT + COMMA
                + FriendsContract.FriendsColumns.FRIENDS_EMAIL + TEXT + COMMA + " )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    public static void deleteDatabase(Context context){
        context.deleteDatabase(DB_NAME);
    }
}
