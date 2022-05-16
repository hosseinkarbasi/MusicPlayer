package com.example.mymusicplayer.utils

import com.example.mymusicplayer.data.Album
import com.example.mymusicplayer.data.Artist
import com.example.mymusicplayer.data.Music

object Helper {

    fun buildSortedAlbums(Songs: List<Music>?): List<Album> {

        val sortedAlbums = mutableListOf<Album>()

        Songs?.let {
            try {
                val groupedSongs = it.groupBy { song -> song.album }
                val iterator = groupedSongs.keys.iterator()
                while (iterator.hasNext()) {
                    val albumKey = iterator.next()
                    val musicList = groupedSongs.getValue(albumKey).toMutableList()
                    sortedAlbums.add(
                        Album(
                            albumKey,
                            musicList,
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return sortedAlbums
    }

    fun buildSortedArtist(Songs: List<Music>?): List<Artist> {

        val sortedArtist = mutableListOf<Artist>()

        Songs?.let {
            try {
                val groupedSongs = it.groupBy { song -> song.artist }
                val iterator = groupedSongs.keys.iterator()
                while (iterator.hasNext()) {
                    val artistKey = iterator.next()
                    val musicList = groupedSongs.getValue(artistKey).toMutableList()
                    sortedArtist.add(
                        Artist(
                            artistKey,
                            musicList,
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return sortedArtist
    }

    fun buildSongsFromAlbums(albumKey: String, songs: List<Music>): List<Music> {
        val sortedMusic = mutableListOf<Music>()
        for (i in 0..songs.size ) {
            if (albumKey == songs[i].album) {
                sortedMusic.add(songs[i])
            }
        }
//        val album = songs.groupBy { song -> song.album }
//        val iterator = album.keys.iterator()
//        while (iterator.hasNext()) {
//            iterator.next()
//            sortedMusic.addAll(album.getValue(albumKey).toMutableList())
//        }
        return sortedMusic
    }
}