package com.example.vincent.sqlitedatabasepractice.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

/** THIS CONTENT PROVIDER CLASS ALLOWS USERS TO UPDATE, INSERT, QUERY, DELETE FROM THE DATABASE
 * Created by Vincent on 6/19/2015.
 */
public class FriendsProvider extends ContentProvider {

    private FriendsDatabase mOpenHelper;

    private static String TAG = FriendsProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static final int FRIENDS = 100;
    private static final int FRIENDS_ID = 101;

    private static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FriendsContract.CONTENT_AUTHORITY;

        // work with all record
        matcher.addURI(authority, "friends", FRIENDS);
        // work with a single record * represents unique id
        matcher.addURI(authority, "friends/*", FRIENDS_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new FriendsDatabase(getContext());
        return true;
    }

    private void deleteDatabase(){
        mOpenHelper.close();
        FriendsDatabase.deleteDatabase(getContext());
        mOpenHelper = new FriendsDatabase(getContext());
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch(match){
            // the cases returns MIMETYPES representing single or multi-row access
            case FRIENDS:
                // multi-row access
                return FriendsContract.Friends.CONTENT_TYPE;
            case FRIENDS_ID:
                // single-row access
                return FriendsContract.Friends.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        final int match = sUriMatcher.match(uri);

        // build queryBuilder to create statement for query
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(FriendsDatabase.Tables.FRIENDS);

        switch(match){
            case FRIENDS:
                // list of all friends in entire database
                // do nothing
                break;
            case FRIENDS_ID:
                // retrieve data for one contact
                String id = FriendsContract.Friends.getFriendId(uri);
                // Statement: Get friends where the ID field = id from passed in URI
                queryBuilder.appendWhere(BaseColumns._ID + "=" + id);
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
        // represents the final query statement
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs,
                null, null, sortOrder);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.v(TAG, "insert(uri =" + uri + ", values= " + values.toString());

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch(match){
            // one case only because you can't insert into a record, insert into the friends db list
            case FRIENDS:
                // insert the record and return the row id, then return the record URI
                long recordId = db.insertOrThrow(FriendsDatabase.Tables.FRIENDS, null, values);
                return FriendsContract.Friends.buildFriendUri(String.valueOf(recordId));
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.v(TAG, "delete(uri= " + uri);

        // if user calls the delete with uri to the base content uri, delete entire db
        if(uri.equals(FriendsContract.BASE_CONTENT_URI)){
            deleteDatabase();
            return 0;
        }

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch(match){
            // if user calls delete with uri to a record, find record and delete
            case FRIENDS_ID:
                // get id of the record
                String id = FriendsContract.Friends.getFriendId(uri);
                // set the selection criteria for the query
                String selectionCriteria = BaseColumns._ID + "=" + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection+ ")" : "");
                // delete the record based on the query filters
                return db.delete(FriendsDatabase.Tables.FRIENDS, selectionCriteria, selectionArgs);
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.v(TAG, "insert(uri =" + uri + ", values= " + values.toString());

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        String selectionCriteria = selection;
        switch(match){
            case FRIENDS:
                // do nothing, you don't want to update entire list
                break;
            case FRIENDS_ID:
                // if uri is for a record, get record id and update the record in db with that id
                String id = FriendsContract.Friends.getFriendId(uri);
                // this is the where clause that filters the search
                selectionCriteria = BaseColumns._ID + "=" + id +
                    (!TextUtils.isEmpty(selection) ? " AND (" + selection+ ")" : "");
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
        // update the database
        return db.update(FriendsDatabase.Tables.FRIENDS, values, selectionCriteria,selectionArgs);
    }
}
