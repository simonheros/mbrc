package com.kelsos.mbrc.content.playlists

import com.kelsos.mbrc.interfaces.data.Mapper

object PlaylistDtoMapper : Mapper<PlaylistDto, PlaylistEntity> {
  override fun map(from: PlaylistDto): PlaylistEntity {
    return PlaylistEntity(from.name, from.url)
  }
}

fun PlaylistDto.toEntity(): PlaylistEntity {
  return PlaylistDtoMapper.map(this)
}
