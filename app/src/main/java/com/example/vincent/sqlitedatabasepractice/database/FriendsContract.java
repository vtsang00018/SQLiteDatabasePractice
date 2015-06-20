package com.example.vincent.sqlitedatabasepractice.database;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Vincent on 6/19/2015.
 */
public class FriendsContract {

    interface FriendsColumns{
        // fields of the record
        String FRIENDS_ID = "_id";
        String FRIENDS_NAME = "friends_name";
        String FRIENDS_EMAIL = "friends_email";
        String FRIENDS_PHONE = "friends_phone";
    }

    // create pieces of the URI: authority, path, etc
    public static final String CONTENT_AUTHORITY = "org.example.android.friends.provider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_FRIENDS = "friends";

    // URI of the table
    public static final Uri URI_TABLE = Uri.parse(BASE_CONTENT_URI.toString() + "/" + PATH_FRIENDS);

    public static final String[] TOP_LEVEL_PATHS = {PATH_FRIENDS};

    public  static class Friends implements FriendsColumns, BaseColumns{

        // access to the Content Provider path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendEncodedPath(PATH_FRIENDS).build();

        // MIMETYPE For multiple rows
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
                + CONTENT_AUTHORITY + ".friends";

        // MIMETYPE for single row
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
                + CONTENT_AUTHORITY + ".friends";

        // access to the record with an id
        public static Uri buildFriendUri(String friendId){
            return CONTENT_URI.buildUpon().appendEncodedPath(friendId).build();
        }

        // returns the record id from the uri
        public static String getFriendId(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }
}
