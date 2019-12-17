package com.iqbal.notesapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.widget.Toast
import java.nio.ByteOrder

class DBManager {
    var dbName  = "mynotes"            // Database Name
    var dbTable = "notes"              // Table Name
    var idColumn = "id"                // Column Table
    var titleColumn = "title"          // Column Table
    var descColumn = "description"     // Column Table
    var DBVersion = 1                  // Database Version

    val createTableSQL = "CREATE TABLE IF NOT EXISTS " + dbTable + " (" + idColumn + " INTEGER PRIMARY KEY, " + titleColumn + "TEXT, " + descColumn + "TEXT);"

    var dbSQL: SQLiteDatabase? = null

    constructor(context: Context) {
        var db = DatabaseHelperNotes(context)
        dbSQL = db.writableDatabase
    }

    inner class DatabaseHelperNotes(context: Context) :
        SQLiteOpenHelper(context, dbName, null, DBVersion) {

        var context: Context? = context

        override fun onCreate(p0: SQLiteDatabase?) {
            p0!!.execSQL(createTableSQL)
            Toast.makeText(this.context, "Database Created!", Toast.LENGTH_SHORT).show()
        }

        override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
            p0!!.execSQL("Drop table if Exists" + dbTable)
        }
    }

    fun Query(projection: Array<String>, selection: String, selectionArgs: Array<String>, sorOrder: String): Cursor {
        val qB = SQLiteQueryBuilder()
        qB.tables = dbTable
        val cursor = qB.query(dbSQL, projection, selection, selectionArgs, null, null, sorOrder)
        return cursor
    }

    fun Insert(values: ContentValues): Long {
        val id = dbSQL!!.insert(dbTable, "", values)
        return id
    }

    fun Delete(selection: String, selectionArgs: Array<String>): Int {
        val count = dbSQL!!.delete(dbTable, selection, selectionArgs)
        return count
    }

    fun Update(values: ContentValues, selection: String, selectionArgs: Array<String>): Int {
        val count = dbSQL!!.update(dbTable, values, selection, selectionArgs)
        return count
    }
}