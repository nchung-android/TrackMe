package com.nchungdev.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.nchungdev.data.model.SessionEntity

@Dao
interface SessionDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sessionEntity: SessionEntity)

    @Delete
    suspend fun delete(sessionEntity: SessionEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(sessionEntity: SessionEntity)

    @Query("SELECT * FROM session_table WHERE isCompleted=0 ORDER BY timestamp DESC LIMIT 1")
    fun getLatestSession(): LiveData<SessionEntity?>

    @Query("SELECT * FROM session_table WHERE id=:id")
    fun getSession(id: Int): LiveData<SessionEntity?>

    @Query("SELECT * FROM session_table ORDER BY timestamp DESC")
    fun getAllRunsSortedByDate(): LiveData<List<SessionEntity>>

    @Query("SELECT * FROM session_table WHERE isCompleted=0 ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestSessionAsync(): SessionEntity?
}
