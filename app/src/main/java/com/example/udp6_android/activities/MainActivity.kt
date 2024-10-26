package com.example.udp6_android.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.udp6_android.R
import com.example.udp6_android.adapter.MovieAdapter
import com.example.udp6_android.databinding.ActivityMainBinding
import com.example.udp6_android.model.Movie
import com.example.udp6_android.provider.MovieProvider
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var movieList: MutableList<Movie> = mutableListOf()
    private lateinit var adapter: MovieAdapter
    private lateinit var layoutManager: LayoutManager
    private lateinit var intentLaunch: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        movieList = getMovies()
        layoutManager = LinearLayoutManager(this)
        binding.rvMovies.layoutManager = layoutManager
        this.adapter = MovieAdapter(movieList) { onSelectedItem(it) }
        binding.rvMovies.adapter = this.adapter
        intentLaunch = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            result: ActivityResult ->
            if(result.resultCode == RESULT_OK) {
                val movieTitle = result.data?.extras?.getString("title").toString()
                val moviePosition = result.data?.extras?.getInt("moviePosition")
                moviePosition?.let {
                    movieList[it].title = movieTitle
                    adapter = MovieAdapter(movieList) { movie -> onSelectedItem(movie) }
                    binding.rvMovies.adapter = adapter
                }
            }
        }

        binding.rvMovies.setHasFixedSize(true)
        binding.rvMovies.itemAnimator = DefaultItemAnimator()
        setUpSwipeRefresh()
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val modifiedMovie: Movie = movieList[item.groupId]

        when(item.itemId) {
            0 -> {
                snackBarDialog(modifiedMovie, item)
            }
            1 -> {
                sendDataToDetail(modifiedMovie, item)
            }
            else -> return super.onContextItemSelected(item)
        }
        return true
    }

    private fun sendDataToDetail(
        modifiedMovie: Movie,
        item: MenuItem
    ) {
        val intent = Intent(this, MovieDetail::class.java)
        intent.putExtra("title", modifiedMovie.title)
        intent.putExtra("imgResId", modifiedMovie.imgResId)
        intent.putExtra("moviePosition", item.groupId)
        intentLaunch.launch(intent)
    }

    private fun removeOneMovie(item: MenuItem) {
        movieList.removeAt(item.groupId)
        adapter.notifyItemRemoved(item.groupId)
        adapter.notifyItemRangeChanged(item.groupId, movieList.size)
        binding.rvMovies.adapter = MovieAdapter(movieList) {
            onSelectedItem(it)
        }
    }

    private fun snackBarDialog(
        modifiedMovie: Movie,
        item: MenuItem
    ) {
        val dialog =
            AlertDialog.Builder(this).setTitle("Delete ${modifiedMovie.title}")
                .setMessage("Are you sure you want to delete ${modifiedMovie.title}")
                .setNeutralButton(getString(R.string.close_dialog_option), null).setPositiveButton(
                    getString(R.string.accept_dialog_option)
                ) { _, _ ->
                    display("Deleted ${modifiedMovie.title}")
                    removeOneMovie(item)
                }.create()
        dialog.show()
    }

    private fun snackBarDialog() {
        val dialog =
            AlertDialog.Builder(this).setTitle("Eliminar All Movies")
                .setMessage(
                    "Are you sure that you want to delete all the movies?"
                )
                .setNeutralButton(getString(R.string.close_dialog_option), null).setPositiveButton(
                    getString(R.string.accept_dialog_option)
                ) { _, _ ->
                    display("Deleted ${movieList.size} movies")
                    clearMovies()
                }.create()
        dialog.show()
    }

    private fun getMovies(): MutableList<Movie> {
        val list = mutableListOf<Movie>()
        for (movie in MovieProvider.movieList) {
            list.add(movie)
        }
        return list
    }

    fun onSelectedItem(movie: Movie) {
        Toast.makeText(
            this,
            "Duracion: ${movie.duration} minutos - Año: ${movie.releaseYear}",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun setUpSwipeRefresh() {
        binding.lySwipe.setOnRefreshListener {
            loadMovies()
            binding.lySwipe.isRefreshing = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.clear_Movies -> {
                snackBarDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun clearMovies() {
        val len = movieList.size
        movieList.clear()
        adapter = MovieAdapter(movieList) { onSelectedItem(it) }
        adapter.notifyItemRangeChanged(0, len)
        binding.rvMovies.adapter = adapter
    }

    private fun loadMovies() {
        movieList = getMovies()
        adapter = MovieAdapter(movieList) { onSelectedItem(it) }
        adapter.notifyItemRangeChanged(0, movieList.size)
        binding.rvMovies.adapter = adapter
    }

    private fun display(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }
}