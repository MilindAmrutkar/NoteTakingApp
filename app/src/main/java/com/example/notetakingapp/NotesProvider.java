package com.example.notetakingapp;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Milind Amrutkar on 08-11-2017.
 */

public class NotesProvider extends ContentProvider {

    //This is a globally unique string that identifies the content provider to the Android framework
    private static final String AUTHORITY = "com.example.notetakingapp.notesprovider";

    //This represents the entire data set
    private static final String BASE_PATH = "notes";

    //CONTENT_URI is a uniform resource identifier that identifies the content provider
    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    //Constants to identify the requested operation. The numeric values are arbitrary.
    //These are simply ways of identifying the operations and they're private, so they're only used within
    //this class. Notes means give me the data
    private static final int NOTES = 1;
    //Notes_ID deals with only a single record
    private static final int NOTES_ID = 2;

    private static final UriMatcher uriMatcher =
            new UriMatcher(UriMatcher.NO_MATCH); //the purpose of UriMatcher class is to parse a URI and then tell which operation
    //has been requested

    public static final String CONTENT_ITEM_TYPE = "Note";

    static {
        //this block will execute the first time anything is called from this class.
        uriMatcher.addURI(AUTHORITY, BASE_PATH, NOTES);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", NOTES_ID); // # is a wild card, it means any numerical value,
        //that means if I get a URI that starts with base_path and then ends with a / and a number that means I'm looking
        //for a particular note, a particular row in the database table.


    }

    private SQLiteDatabase database;

    @Override
    public boolean onCreate() {
        DBOpenHelper helper = new DBOpenHelper(getContext());
        database = helper.getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        if(uriMatcher.match(uri) == NOTES_ID) {
            selection = DBOpenHelper.NOTE_ID + "=" + uri.getLastPathSegment();
        }

        return database.query(DBOpenHelper.TABLE_NOTES, DBOpenHelper.ALL_COLUMNS,
                selection, null, null, null,
                DBOpenHelper.NOTE_CREATED + " DESC");
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        //The insert method return a URI. And that URI is supposed to match this pattern: the base_path followed by
        // a / and then the primary key value of the record
        //So first step is to get that primary key value

        long id = database.insert(DBOpenHelper.TABLE_NOTES,
                null, values); //ContentValues is a class that has a collection of name value pairs
        //Content values is very similar to bundle class in android, but the bundle class tends to be used to manage
        //the user interface. Wheareas ContentValues is used to pass data around on the back end.

        return Uri.parse(BASE_PATH + "/" + id); // the parse method lets you put together a string and return the equivalent URI

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return database.delete(DBOpenHelper.TABLE_NOTES, selection, selectionArgs);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return database.update(DBOpenHelper.TABLE_NOTES, values, selection, selectionArgs);
    }
}
