package com.kelsos.mbrc.content.library.albums

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.kelsos.mbrc.covers.AlbumCover

@Dao
interface AlbumDao {
  @Query("DELETE from album")
  fun deleteAll()

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insert(list: List<AlbumEntity>)

  @Query("select * from album")
  fun getAll(): PagingSource<Int, AlbumEntity>

  @Query("select * from album where album like '%' || :term || '%'")
  fun search(term: String): PagingSource<Int, AlbumEntity>

  @Query("select count(*) from album")
  fun count(): Long

  @Query("select * from album")
  fun all(): List<AlbumEntity>

  @Query("delete from album where date_added < :added")
  fun removePreviousEntries(added: Long)

  @Query(
    """
      select distinct album.album, album.artist, album.id, album.date_added, album.cover from album
      inner join track on album.album = track.album and album.artist = track.album_artist
      where album.artist = :artist order by album.album asc
    """
  )
  fun getAlbumsByArtist(artist: String): PagingSource<Int, AlbumEntity>

  @Query("select album, artist, cover as hash from album")
  fun getCovers(): List<AlbumCover>

  @Query("update album set cover = :cover where artist = :artist and album = :album")
  fun updateCover(artist: String, album: String, cover: String)

  @Transaction
  fun updateCovers(updated: List<AlbumCover>) {
    for ((artist, album, hash) in updated) {
      if (hash == null) {
        continue
      }
      updateCover(
        artist = artist,
        album = album,
        cover = hash
      )
    }
  }
}
