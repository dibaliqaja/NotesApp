package com.iqbal.notesapp

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.row.view.*

class MainActivity : AppCompatActivity() {

    private var listNotes = ArrayList<Note>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        LoadQuery("%")
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        LoadQuery("%")
    }

    private fun LoadQuery(title: String) {
        val dbManager = DBManager(this)
        val projections = arrayOf("id", "title", "description")
        val selectionArgs = arrayOf(title)
        val cursor = dbManager.Query(projections, "title like ?", selectionArgs, "title")
        listNotes.clear()
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val title = cursor.getString(cursor.getColumnIndex("title"))
                val description = cursor.getString(cursor.getColumnIndex("description"))
                listNotes.add(Note(id, title, description))
            } while (cursor.moveToNext())
        }

        val myNotesAdapter = MyNotesAdapter(this, listNotes)
        lv_notes.adapter = myNotesAdapter
        val total = lv_notes.count
        val ab = supportActionBar
        if (ab != null) {
            ab.subtitle = "You have $total note(s) in list..."
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        val sv: SearchView = menu!!.findItem(R.id.app_bar_search).actionView as SearchView
        val sm = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        sv.setSearchableInfo(sm.getSearchableInfo(componentName))
        sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                LoadQuery("%" + query + "%")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                LoadQuery("%" + newText + "%")
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.addNote -> {
                startActivity(Intent(this, AddNoteActivity::class.java))
            }
            R.id.action_settings -> {
                Toast.makeText(this, R.string.settings, Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    inner class MyNotesAdapter(context: Context, private var listNotesAdapter: ArrayList<Note>) :
        BaseAdapter() {
        private var context: Context? = context

        @SuppressLint("InflateParams", "ViewHolder")
        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            val myView = layoutInflater.inflate(R.layout.row, null)
            val myNote = listNotesAdapter[p0]
            myView.tv_title.text = myNote.nameNote
            myView.tv_desc.text = myNote.descNote

            myView.btn_delete.setOnClickListener {
                val dbManager = DBManager(this.context!!)
                val selectionArgs = arrayOf(myNote.idNote.toString())
                dbManager.Delete("id=?", selectionArgs)
                LoadQuery("%")
            }

            myView.btn_edit.setOnClickListener {
                GoToUpdate(myNote)
            }

            myView.btn_copy.setOnClickListener {
                val title = myView.tv_title.text.toString()
                val desc = myView.tv_desc.toString()
                val a = title + "\n" + desc
                val cb = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cb.text = a
                Toast.makeText(this@MainActivity, "Copied...", Toast.LENGTH_SHORT).show()
            }

            myView.btn_share.setOnClickListener {
                val title = myView.tv_title.text.toString()
                val desc = myView.tv_desc.text.toString()
                val a = title + "\n" + desc
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT, a)
                startActivity(Intent.createChooser(shareIntent, a))
            }
            return myView
        }

        override fun getItem(p0: Int): Any {
            return listNotesAdapter[p0]
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getCount(): Int {
            return listNotesAdapter.size
        }
    }

    private fun GoToUpdate(myNote: Note) {
        val intent = Intent(this, AddNoteActivity::class.java)
        intent.putExtra("id", myNote.idNote)
        intent.putExtra("name", myNote.nameNote)
        intent.putExtra("description", myNote.descNote)
        startActivity(intent)
    }
}
