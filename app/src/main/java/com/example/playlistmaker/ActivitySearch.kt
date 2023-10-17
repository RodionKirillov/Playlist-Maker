package com.example.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toolbar
import com.example.playlistmaker.ActivitySearch.Companion.STRING_DEF

private var stringEditText = STRING_DEF

class ActivitySearch : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val backButton = findViewById<Toolbar>(R.id.backButton)
        val editTextSearch = findViewById<EditText>(R.id.editTextSearch)
        val clearIcon = findViewById<ImageView>(R.id.clear_icon)

        backButton.setOnClickListener {
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
        }

        if (savedInstanceState != null) {
            editTextSearch.setText(savedInstanceState.getString(STRING_EDIT_TEXT))
        }

        clearIcon.setOnClickListener {
            editTextSearch.setText("")
        }

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                stringEditText = p0.toString()
                clearIcon.visibility = clearButtonVisibility(p0)
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        }
        editTextSearch.addTextChangedListener(searchTextWatcher)

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STRING_EDIT_TEXT, stringEditText)
    }

    companion object {
        const val STRING_EDIT_TEXT = "STRING_EDIT_TEXT"
        const val STRING_DEF = " "
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }
}