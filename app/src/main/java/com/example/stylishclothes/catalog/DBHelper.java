package com.example.stylishclothes.catalog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "Catalogs.db", null, 8);
    }

    public void queryData(String sql) {
        SQLiteDatabase DB = getWritableDatabase();
        DB.execSQL(sql);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        DB.execSQL("CREATE TABLE Catalogs(category TEXT, title TEXT PRIMARY KEY, title_image BLOB, " +
                "product_image_1 BLOB, product_image_2 BLOB, product_image_3 BLOB, " +
                "product_image_4 BLOB, product_image_5 BLOB, availability  INTEGER, code TEXT, " +
                "size_S INTEGER, size_M INTEGER, size_L INTEGER, size_XL INTEGER, size_XXL INTEGER, " +
                "title_description TEXT, description TEXT, price TEXT)");

        
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int oldVersion, int newVersion) {
        String query = "DROP TABLE IF EXISTS Catalogs";
        DB.execSQL(query);
        onCreate(DB);
    }

    public void createCatalog(String category,
                              String title,
                              byte[] titleImage,
                              byte[] productImage_1,
                              byte[] productImage_2,
                              byte[] productImage_3,
                              byte[] productImage_4,
                              byte[] productImage_5,
                              boolean availability,
                              String code,
                              boolean size_S,
                              boolean size_M,
                              boolean size_L,
                              boolean size_XL,
                              boolean size_XXL,
                              String titleDescription,
                              String description, String price) {
        int mAvailability = 0;
        int mSize_S = 0, mSize_M = 0, mSize_L = 0, mSize_XL = 0, mSize_XXL = 0;
        if(availability == true) mAvailability = 1;
        if(size_S == true) mSize_S = 1;
        if(size_M == true) mSize_M = 1;
        if(size_L == true) mSize_L = 1;
        if(size_XL == true) mSize_XL = 1;
        if(size_XXL == true) mSize_XXL = 1;
        if (price == null) price = "0";

        SQLiteDatabase DB = this.getWritableDatabase();
        String sql = "INSERT INTO Catalogs VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        SQLiteStatement statement = DB.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1, category);
        statement.bindString(2, title);
        if (titleImage != null) statement.bindBlob(3, titleImage);
        if (productImage_1 != null) statement.bindBlob(4, productImage_1);
        if (productImage_2 != null) statement.bindBlob(5, productImage_2);
        if (productImage_3 != null) statement.bindBlob(6, productImage_3);
        if (productImage_4 != null) statement.bindBlob(7, productImage_4);
        if (productImage_5 != null) statement.bindBlob(8, productImage_5);
        statement.bindLong(9, mAvailability);
        statement.bindString(10, code);
        statement.bindLong(11, mSize_S);
        statement.bindLong(12, mSize_M);
        statement.bindLong(13, mSize_L);
        statement.bindLong(14, mSize_XL);
        statement.bindLong(15, mSize_XXL);
        statement.bindString(16, titleDescription);
        statement.bindString(17, description);
        statement.bindString(18, price);

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

    public Cursor getProduct(String sql, String title) {
        SQLiteDatabase DB = this.getWritableDatabase();
        return DB.rawQuery(sql, new String[] {title});
    }

    public void updateCatalog(String originalTitle,
                              String category,
                              String newTitle,
                              byte[] titleImage,
                              byte[] productImage_1,
                              byte[] productImage_2,
                              byte[] productImage_3,
                              byte[] productImage_4,
                              byte[] productImage_5,
                              boolean availability,
                              String code,
                              boolean size_S,
                              boolean size_M,
                              boolean size_L,
                              boolean size_XL,
                              boolean size_XXL,
                              String titleDescription,
                              String description,
                              String price) {
        int mAvailability = 0, mSize_S = 0, mSize_M = 0, mSize_L = 0, mSize_XL = 0, mSize_XXL = 0;
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", newTitle);
        values.put("title_image", titleImage);
        values.put("product_image_1", productImage_1);
        values.put("product_image_2", productImage_2);
        values.put("product_image_3", productImage_3);
        values.put("product_image_4", productImage_4);
        values.put("product_image_5", productImage_5);
        if(availability == true) mAvailability = 1;
        values.put("availability", mAvailability);
        values.put("code", code);
        if(size_S == true) mSize_S = 1;
        values.put("size_S", mSize_S);
        if(size_M == true) mSize_M = 1;
        values.put("size_M", mSize_M);
        if(size_L == true) mSize_L = 1;
        values.put("size_L", mSize_L);
        if(size_XL == true) mSize_XL = 1;
        values.put("size_XL", mSize_XL);
        if(size_XXL == true) mSize_XXL = 1;
        values.put("size_XXL", mSize_XXL);
        values.put("title_description", titleDescription);
        values.put("description", description);
        values.put("price", price);
        DB.update("Catalogs", values, "title=?", new String[]{originalTitle});
        DB.close();
    }
}

