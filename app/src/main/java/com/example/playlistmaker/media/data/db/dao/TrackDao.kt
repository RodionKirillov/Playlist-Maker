package com.example.playlistmaker.media.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.media.data.db.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackToFavorite(track: TrackEntity)

    @Delete(entity = TrackEntity::class)
    suspend fun deleteTrackFromFavorite(track: TrackEntity)

    @Query("SELECT * FROM track_table")
    suspend fun getFavoriteTracks(): List<TrackEntity>

    @Query("SELECT 1 FROM track_table WHERE trackId = :trackId")
    suspend fun getFavoriteTracksId(trackId: String): List<String>
}