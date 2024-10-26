package com.example.udp6_android.activities

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.udp6_android.R
import com.example.udp6_android.databinding.ActivityMovieDetailBinding

class MovieDetail : AppCompatActivity() {
    private lateinit var binding: ActivityMovieDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMovieDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val title = intent.getStringExtra("title")
        val poster = intent.getIntExtra("imgResId", 0)
        val position = intent.getIntExtra("moviePosition", 0)
        val editText = binding.movieTitleDetail
        val imgView = binding.moviePoster
        val editButton = binding.editButton
        val cancelButton = binding.cancelButton

        editText.hint = title
        imgView.setImageResource(poster)

        editButton.setOnClickListener { sendDataBack(editText, position) }

        cancelButton.setOnClickListener { cancelEditOperation() }
    }

    private fun cancelEditOperation() {
        val intent = Intent()
        setResult(RESULT_CANCELED, intent)
        finish()
    }

    private fun sendDataBack(editText: EditText, position: Int) {
        val intent = Intent()
        val modifiedTitle = editText.text.toString()
        intent.putExtra("title", modifiedTitle)
        intent.putExtra("moviePosition", position)
        setResult(RESULT_OK, intent)
        finish()
    }
}