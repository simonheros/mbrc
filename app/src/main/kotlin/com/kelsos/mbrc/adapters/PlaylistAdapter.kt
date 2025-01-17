package com.kelsos.mbrc.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.kelsos.mbrc.R
import com.kelsos.mbrc.data.Playlist
import com.raizlabs.android.dbflow.list.FlowCursorList
import javax.inject.Inject

class PlaylistAdapter
@Inject
constructor(context: Activity) : RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {

  private val inflater: LayoutInflater = LayoutInflater.from(context)
  private var data: FlowCursorList<Playlist>? = null
  private var playlistPressedListener: OnPlaylistPressedListener? = null

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = inflater.inflate(R.layout.listitem_single, parent, false)
    val viewHolder = ViewHolder(view)

    viewHolder.itemView.setOnClickListener {
      val path = data?.getItem(viewHolder.adapterPosition.toLong())?.url
      path?.let {
        playlistPressedListener?.playlistPressed(it)
      }

    }
    return viewHolder
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val playlist = data?.getItem(holder.adapterPosition.toLong())
    playlist?.let {
      holder.name.text = playlist.name
    }
    holder.context.visibility = View.GONE

  }

  override fun getItemCount(): Int {
    return data?.count?.toInt() ?: 0
  }

  fun update(cursor: FlowCursorList<Playlist>) {
    this.data = cursor
    notifyDataSetChanged()
  }

  fun setPlaylistPressedListener(playlistPressedListener: OnPlaylistPressedListener) {
    this.playlistPressedListener = playlistPressedListener
  }

  interface OnPlaylistPressedListener {
    fun playlistPressed(path: String)
  }

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @BindView(R.id.line_one)
    lateinit var name: TextView
    @BindView(R.id.ui_item_context_indicator)
    lateinit var context: LinearLayout

    init {
      ButterKnife.bind(this, itemView)
    }
  }
}
