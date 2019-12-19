package com.iqbal.notesapp

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_note.*
import java.lang.Exception
import android.view.View as View

class AddNoteActivity : AppCompatActivity() {

    private var id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        try {
            val bundle: Bundle = intent.extras!!
            id = bundle.getInt("ID", 0)
            if (id != 0) {
                edt_title.setText(bundle.getString("Title"))
                edt_desc.setText(bundle.getString("Description"))
            }
        } catch (ex: Exception) {}
    }

    fun add(view: View) {
        val dbManager = DBManager(this)

        val values = ContentValues()
        values.put("Title", edt_title.text.toString())
        values.put("Description", edt_desc.text.toString())
        if (id == 0) {
            val id = dbManager.insert(values)
            if (id > 0) {
                Toast.makeText(this, "Note is added", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error adding note.", Toast.LENGTH_SHORT).show()
            }
        } else {
            val selectionArgs = arrayOf(id.toString())
            val id = dbManager.update(values, "ID=?", selectionArgs)
            if (id > 0) {
                Toast.makeText(this, "Note is added", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error adding note.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
