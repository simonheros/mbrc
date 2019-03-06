package com.kelsos.mbrc.ui.navigation.library.genres

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.genres.Genre
import com.kelsos.mbrc.ui.navigation.library.MenuItemSelectedListener
import com.kelsos.mbrc.ui.navigation.library.popup

class GenreEntryAdapter : PagingDataAdapter<Genre, GenreViewHolder>(DIFF_CALLBACK) {

  private var listener: MenuItemSelectedListener<Genre>? = null

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
    val holder = GenreViewHolder.create(parent)
    holder.onIndicatorClick { view, position ->
      view.popup(R.menu.popup_genre) { id ->
        val genre = getItem(position) ?: return@popup
        listener?.onMenuItemSelected(id, genre)
      }
    }
    holder.onPress { position ->
      val genre = getItem(position) ?: return@onPress
      listener?.onItemClicked(genre)
    }
    return holder
  }

  override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
    val genre = getItem(holder.bindingAdapterPosition)
    if (genre != null) {
      holder.bindTo(genre)
    } else {
      holder.clear()
    }
  }

  fun setMenuItemSelectedListener(listener: MenuItemSelectedListener<Genre>) {
    this.listener = listener
  }

  companion object {
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Genre>() {
      override fun areItemsTheSame(oldItem: Genre, newItem: Genre): Boolean {
        return oldItem.id == newItem.id
      }

      override fun areContentsTheSame(oldItem: Genre, newItem: Genre): Boolean {
        return oldItem.genre == newItem.genre
      }
    }
  }
}
