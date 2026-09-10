package com.example.amancheck.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AmanCheckDao {

    // Verified Items
    @Query("SELECT * FROM verified_items ORDER BY id DESC")
    fun getAllVerifiedItems(): Flow<List<VerifiedItemEntity>>

    @Query("SELECT * FROM verified_items WHERE LOWER(value) = LOWER(:value) LIMIT 1")
    suspend fun findVerifiedItem(value: String): VerifiedItemEntity?

    @Query("SELECT * FROM verified_items WHERE LOWER(value) LIKE '%' || LOWER(:search) || '%' ORDER BY id DESC")
    fun searchVerifiedItems(search: String): Flow<List<VerifiedItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerifiedItem(item: VerifiedItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllVerifiedItems(items: List<VerifiedItemEntity>): List<Long>

    // Reports
    @Query("SELECT * FROM scam_reports ORDER BY createdAt DESC")
    fun getAllReports(): Flow<List<ScamReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ScamReportEntity): Long

    // Alerts
    @Query("SELECT * FROM alerts ORDER BY createdAt DESC")
    fun getAllAlerts(): Flow<List<AlertEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAlerts(alerts: List<AlertEntity>): List<Long>

    // Education
    @Query("SELECT * FROM education_content ORDER BY id ASC")
    fun getAllEducationContent(): Flow<List<EducationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllEducation(content: List<EducationEntity>): List<Long>

    @Query("SELECT COUNT(*) FROM verified_items")
    suspend fun getVerifiedItemsCount(): Int
}
