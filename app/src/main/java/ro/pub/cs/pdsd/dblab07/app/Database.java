package ro.pub.cs.pdsd.dblab07.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteOpenHelper
{

    public static final String DATABASE_NAME = "profi_cursuri";
    public static final int DATABASE_VERSION = 1;

    private static Database instance;

    public static Database getInstance(Context context)
    {
        if( instance == null )
        {
            instance = new Database(context.getApplicationContext());
        }

        return instance;
    }

    private Database(Context context)
    {
        super( context , DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String createProf = "CREATE TABLE profesori(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL);";
        db.execSQL(createProf);

        String createProfIndex = "CREATE UNIQUE INDEX IF NOT EXISTS prof_name ON profesori (name ASC)";
        db.execSQL(createProfIndex);

        String createCurs = "CREATE TABLE cursuri(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL)";
        db.execSQL(createCurs);

        String createCursIndex = "CREATE UNIQUE INDEX IF NOT EXISTS curs_name ON cursuri (name ASC)";
        db.execSQL(createCursIndex);

        String createAssoc = "CREATE TABLE asocieri_pc(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "prof_id INTEGER NOT NULL, curs_id INTEGER NOT NULL, " +
                "FOREIGN KEY (prof_id) REFERENCES profesori(id), FOREIGN KEY (curs_id) REFERENCES cursuri(id))";
        db.execSQL(createAssoc);

        String createAssocIndex = "CREATE UNIQUE INDEX IF NOT EXISTS prof_curs ON asocieri_pc (prof_id ASC, curs_id ASC)";
        db.execSQL(createAssocIndex);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE profesori");
        db.execSQL("DROP TABLE cursuri");
        db.execSQL("DROP TABLE asocieri_pc");

        onCreate(db);
    }

    boolean insertProfesor(String prof) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        boolean ret = false;

        contentValues.put("name", prof);

        try {
            db.insertOrThrow("profesori", null, contentValues);
            ret = true;
        } catch (SQLiteException e) {
            Log.w("sqlite", e.getMessage());
        }
        db.close();
        return ret;
    }

    boolean insertCurs(String curs) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        boolean ret = false;

        contentValues.put("name", curs);

        try {
            db.insertOrThrow("cursuri", null, contentValues);
            ret = true;
        } catch (SQLiteException e) {
            Log.w("sqlite", e.getMessage());
        }
        db.close();
        return ret;
    }

    boolean insertAsociere(int prof_id, int curs_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        boolean ret = false;

        contentValues.put("prof_id", prof_id);
        contentValues.put("curs_id", curs_id);

        try {
            db.insertOrThrow("asocieri_pc", null, contentValues);
            ret = true;
        } catch (SQLiteException e) {
            Log.w("sqlite", e.getMessage());
        }
        db.close();
        return ret;
    }

    Profesor getProf(String name) {
        String sql = "SELECT id, name FROM profesori WHERE name = '" + name + "'";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        Profesor profesor = new Profesor(cursor.getInt(0), cursor.getString(1));

        cursor.close();
        db.close();

        return profesor;
    }

    List<Profesor> getProfessors(int curs_id) {
        ArrayList<Profesor> profesors = new ArrayList<Profesor>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql;
        if (curs_id <= 0)
            sql = "SELECT id, name FROM profesori";
        else
            sql = "SELECT p.id, p.name FROM profesori p, asocieri_pc a WHERE a.curs_id = " + curs_id + " AND a.prof_id = p.id";

        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            profesors.add(new Profesor(cursor.getInt(0), cursor.getString(1)));
            cursor.moveToNext();
        }

        cursor.close();
        db.close();
        return profesors;
    }

    Curs getCourse(String name) {
        String sql = "SELECT id, name FROM cursuri WHERE name = '" + name + "'";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        Curs curs = new Curs(cursor.getInt(0), cursor.getString(1));

        cursor.close();
        db.close();

        return curs;
    }

    List<Curs> getCourses(int prof_id) {
        List<Curs> courses = new ArrayList<Curs>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql;
        if (prof_id <= 0)
            sql = "SELECT id, name FROM cursuri";
        else
            sql = "SELECT c.id, c.name FROM cursuri c, asocieri_pc a WHERE a.prof_id = " + prof_id + " AND a.curs_id = c.id";

        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            courses.add(new Curs(cursor.getInt(0), cursor.getString(1)));
            cursor.moveToNext();
        }

        cursor.close();
        db.close();
        return courses;
    }

    void clearDb() {
        SQLiteDatabase db = getWritableDatabase();

        db.delete("profesori", null, null);
        db.delete("cursuri", null, null);
        db.delete("asocieri_pc", null, null);

        db.close();
    }
}