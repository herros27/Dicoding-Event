package com.example.submission_1_fundamental_android.data.local

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "event")
@Parcelize
data class EventEntity(
    @PrimaryKey
    val id : Int,

    @field:ColumnInfo(name = "name")
    val name : String,
    @field:ColumnInfo(name = "summary")
    val summary : String,
    @field:ColumnInfo(name = "description")
    val description : String,
    @field:ColumnInfo(name = "imageLogo")
    val imageLogo : String,
    @field:ColumnInfo(name = "mediaCover")
    val mediaCover : String,
    @field:ColumnInfo(name = "category")
    val category : String,
    @field:ColumnInfo(name = "ownerName")
    val ownerName : String,
    @field:ColumnInfo(name = "cityName")
    val cityName : String,
    @field:ColumnInfo(name = "quota")
    val quota : Int,
    @field:ColumnInfo(name = "registrant")
    val registrants : Int,
    @field:ColumnInfo(name = "beginTime")
    val beginTime : String,
    @field:ColumnInfo(name = "endTime")
    val endTime : String,
    @field:ColumnInfo(name =" link")
    val link : String,
    @field:ColumnInfo(name = "isUpcoming")
    val isUpcoming : Boolean,
    @field:ColumnInfo(name = "isFavourite")
    var isFavorite : Boolean ,
    @field:ColumnInfo(name = "isFinished")
    val isFinished : Boolean
) : Parcelable