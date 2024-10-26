package com.example.udp6_android.viewHolder

import android.view.ContextMenu
import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.udp6_android.databinding.MovieItemBinding
import com.example.udp6_android.model.Movie

class MovieViewHolder(view: View): ViewHolder(view), View.OnCreateContextMenuListener {
    val binding = MovieItemBinding.bind(view)
    lateinit var movie: Movie

    fun render(item: Movie, onClick: (Movie) -> Unit) {
        movie = item
        binding.movieImg.setImageResource(item.imgResId)
        Glide.with(binding.movieImg.context).load(item.imgResId).centerCrop().into(binding.movieImg)
        binding.movieTitle.text = item.title
        itemView.setOnClickListener {
            onClick(item)
        }
        itemView.setOnCreateContextMenuListener(this)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        menu!!.setHeaderTitle(movie.title)
        menu.add(this.adapterPosition, 0,0,"Eliminar")
        menu.add(this.adapterPosition, 1, 1, "Editar")
    }
}