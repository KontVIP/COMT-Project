package com.example.stylishclothes.catalog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "Catalogs.db", null, 3);
    }

    public void queryData(String sql) {
        SQLiteDatabase DB = getWritableDatabase();
        DB.execSQL(sql);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        DB.execSQL("CREATE TABLE Catalogs(category TEXT, title TEXT PRIMARY KEY, image BLOB)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int oldVersion, int newVersion) {
        String query = "DROP TABLE IF EXISTS Catalogs";
        DB.execSQL(query);
        onCreate(DB);
    }

    public void createCatalog(String category, String title, byte[] image) {
        SQLiteDatabase DB = this.getWritableDatabase();
        String sql = "INSERT INTO Catalogs VALUES (?, ?, ?)";

        SQLiteStatement statement = DB.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1, category);
        statement.bindString(2, title);
        statement.bindBlob(3, image);

        statement.executeInsert();

    }

    public void deleteCatalog(String title) {
        SQLiteDatabase DB = this.getWritableDatabase();
        DB.delete("Catalogs", "title=?", new String[] {title});
    }


    public Cursor getCatalogs(String sql, String category) {
        SQLiteDatabase DB = this.getWritableDatabase();
        return DB.rawQuery(sql, new String[] {category});
    }

    public void updateCatalog(String originalTitle, String newTitle, byte[] image) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", newTitle);
        values.put("image", image);
        DB.update("Catalogs", values, "title=?", new String[]{originalTitle});
        DB.close();
    }
}

