package com.bsreeinf.jobapp.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class SQLiteOperations {
    SQLiteDatabase db;

    public SQLiteOperations(Context context) {
        db = context.openOrCreateDatabase("VTUShelfDB", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS bookmarks(filename VARCHAR, pageno INTEGER, " +
                "added_at DATETIME DEFAULT CURRENT_TIMESTAMP);");
        db.execSQL("CREATE TABLE IF NOT EXISTS mynotes(id INTEGER PRIMARY KEY, note VARCHAR, " +
                "added_at DATETIME DEFAULT CURRENT_TIMESTAMP);");
    }

    public void addBookmark(String filename, String pageno) {
        db.execSQL("INSERT INTO bookmarks (filename, pageno) VALUES('" + filename + "'," + pageno + ");");
    }

    public void deleteBookmark(String filename, String pageno) {
        db.execSQL("DELETE FROM bookmarks WHERE filename = '" + filename + "' AND pageno = " + pageno);
    }

    public void clearBookmarks() {
        db.execSQL("DELETE FROM bookmarks");
    }

    public List<String[]> getBookmarks(String filename) {
        Cursor c = db.rawQuery("SELECT * FROM bookmarks " +
                (filename == null ? "" : " WHERE filename = '" + filename + "'"), null);
        if (c.getCount() == 0) {
            c.close();
            return null;
        }
        List<String[]> bookmarks = new ArrayList<>();
        while (c.moveToNext()) {
            bookmarks.add(new String[]{c.getString(0), c.getString(1)});
        }
        c.close();
        return bookmarks;
    }

    public void addNote(String note) {
        db.execSQL("INSERT INTO mynotes (note) VALUES('" + note + "');");
    }

    public void deleteNote(String index) {
        db.execSQL("DELETE FROM mynotes WHERE id = " + index);
    }

    public void clearNotes() {
        db.execSQL("DELETE FROM mynotes");
    }

    public List<String[]> getNotes() {
        Cursor c = db.rawQuery("SELECT * FROM mynotes", null);
        if (c.getCount() == 0) {
            c.close();
            return null;
        }
        List<String[]> myNotes = new ArrayList<>();
        while (c.moveToNext()) {
            myNotes.add(new String[]{c.getString(0),c.getString(1),c.getString(2)});
        }
        c.close();
        return myNotes;
    }
}