package com.example.amancheck.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "verified_items")
data class VerifiedItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // "phone", "url", "app"
    val value: String,
    val riskLevel: String, // "high", "suspicious", "low"
    val source: String,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "scam_reports")
data class ScamReportEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // "phone", "url", "app"
    val target: String,
    val description: String,
    val proofUri: String? = null,
    val status: String = "pending", // "pending", "verified", "rejected"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "alerts")
data class AlertEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val titleFr: String,
    val titleHa: String,
    val contentFr: String,
    val contentHa: String,
    val type: String, // "urgent", "warning", "info"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "education_content")
data class EducationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val titleFr: String,
    val titleHa: String,
    val excerptFr: String,
    val excerptHa: String,
    val contentFr: String,
    val contentHa: String,
    val category: String, // "security", "mobile_money", "phishing"
    val createdAt: Long = System.currentTimeMillis()
)
