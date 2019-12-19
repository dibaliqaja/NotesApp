package com.iqbal.notesapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.widget.Toast

class DBManager(context: Context) {

    var dbName              = "MyNotes"         // Database Name
    var dbTable             = "Notes"           // Table Name
    private var idColumn    = "ID"              // Column Table
    private var titleColumn = "Title"           // Column Table
    private var descColumn  = "Description"     // Column Table
    var dbVersion           = 1                 // Database Version

    val sqlCreateTable =
        "CREATE TABLE IF NOT EXISTS $dbTable ($idColumn INTEGER PRIMARY KEY,$titleColumn TEXT, $descColumn TEXT);"

    private var dbSQL: SQLiteDatabase? = null

    init {
        val db = DatabaseHelperNotes(context)
        dbSQL = db.writableDatabase
    }

    inner class DatabaseHelperNotes(context: Context) :
        SQLiteOpenHelper(context, dbName, null, dbVersion) {
        private var context: Context? = context

        override fun onCreate(p0: SQLiteDatabase?) {
            p0!!.execSQL(sqlCreateTable)
            Toast.makeText(this.context, "Database Created", Toast.LENGTH_SHORT).show()
        }

        override fun onUpgrade(p0: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            p0!!.execSQL("DROP TABLE IF EXISTS $dbTable")
        }
    }

    fun insert(values: ContentValues): Long {
        return dbSQL!!.insert(dbTable, "", values)
    }

    fun query(projection: Array<String>, selection: String, selectionArgs: Array<String>, sorOrder: String): Cursor {
        val qb = SQLiteQueryBuilder()
        qb.tables = dbTable
        return qb.query(dbSQL, projection, selection, selectionArgs, null, null, sorOrder)
    }

    fun delete(selection: String, selectionArgs: Array<String>): Int {
        return dbSQL!!.delete(dbTable, selection, selectionArgs)
    }

    fun update(values: ContentValues, selection: String, selectionArgs: Array<String>): Int {
        return dbSQL!!.update(dbTable, values, selection, selectionArgs)
    }
}