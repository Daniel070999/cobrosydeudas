package com.example.cobrosydeudas;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.cobrosydeudas.utilidades.Utilidades;

public class BDDcobrosHelper extends SQLiteOpenHelper {

   public BDDcobrosHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Utilidades.CREAR_TABLA_COBRO);
        db.execSQL(Utilidades.CREAR_TABLA_REGISTRO_COBRO);
        db.execSQL(Utilidades.CREAR_TABLA_PERMISO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists cobros");
        db.execSQL("drop table if exists registro_cobro");
        onCreate(db);
    }



}
