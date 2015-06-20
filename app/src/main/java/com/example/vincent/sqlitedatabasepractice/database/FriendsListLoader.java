package com.example.vincent.sqlitedatabasepractice.database;

import android.content.AsyncTaskLoader;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/** THIS CLASS ENABLES ACCESS TO THE CONTENT PROVIDER AND QUERIES IN THE BACKGROUND
 * Created by Vincent on 6/19/2015.
 */
public class FriendsListLoader extends AsyncTaskLoader<List<Friend>> {
    // tag used with Log
    private static final String LOG_TAG = FriendsListLoader.class.getSimpleName();
    // list of friends
    private List<Friend> mFriends;
    // content resolver to access content provider data
    private ContentResolver mContentResolver;
    // cursor for querying
    private Cursor mCursor;

    public FriendsListLoader(Context context, Uri uri, ContentResolver contentResolver) {
        super(context);
        mContentResolver = contentResolver;
    }

    @Override
    public List<Friend> loadInBackground() {
        // projection - tells system fields we want to work with
        String[] projection = {BaseColumns._ID,
        FriendsContract.FriendsColumns.FRIENDS_NAME,
        FriendsContract.FriendsColumns.FRIENDS_PHONE,
        FriendsContract.FriendsColumns.FRIENDS_EMAIL};

        List<Friend> entries = new ArrayList<Friend>();

        mCursor = mContentResolver.query(FriendsContract.URI_TABLE,projection,null, null, null);

        if(mCursor != null){
            if(mCursor.moveToFirst()){
                do{
                    int _id = mCursor.getInt(mCursor.getColumnIndex(BaseColumns._ID));
                    String name = mCursor.getString(
                            mCursor.getColumnIndex(FriendsContract.FriendsColumns.FRIENDS_NAME));
                    String phone = mCursor.getString(
                            mCursor.getColumnIndex(FriendsContract.FriendsColumns.FRIENDS_PHONE));
                    String email = mCursor.getString(
                            mCursor.getColumnIndex(FriendsContract.FriendsColumns.FRIENDS_EMAIL));
                    Friend friend = new Friend(_id, name, phone, email);
                    entries.add(friend);
                } while (mCursor.moveToNext());
            }
        }
        return entries;
    }

    /**
     * Called when there is new data to deliver to the client.  The
     * super class will take care of delivering it
     */
    @Override
    public void deliverResult(List<Friend> data) {
        if(isReset()){
            // An async query came in while the loader is stopped.  We
            // don't need the result.
            if(data != null){
                mCursor.close();
            }
        }
        List<Friend> oldFriends = mFriends;
        mFriends = data;

        if(mFriends == null || mFriends.size() == 0) {
            // if no data exists
            Log.d(LOG_TAG, "++++ No Data returned");
        }

        if(isStarted()){
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(data);
        }
        if(oldFriends != null && oldFriends != data){
            // At this point we can release the resources associated with
            // 'oldFriends' if needed
            mCursor.close();
        }
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override
    protected void onStartLoading() {
        // if there is data, deliver the results
        if(mFriends != null){
            deliverResult(mFriends);
        }
        // If the data has changed since the last time it was loaded
        // or is not currently available, start a load.
        if(takeContentChanged() || mFriends == null){
            forceLoad();
        }
    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @Override
    protected void onReset() {
        super.onReset();
        // Ensure the loader is stopped
        onStopLoading();
        // release the cursor
        if(mCursor != null){
            mCursor.close();
        }
        mFriends = null;
    }

    /**
     * Handles a request to cancel a load.
     */
    @Override
    public void onCanceled(List<Friend> data) {
        super.onCanceled(data);
        if(mCursor != null){
            mCursor.close();
        }
    }

    /**
     * Handles a request to force start a load
     */
    @Override
    public void forceLoad() {
        super.forceLoad();
    }
}
