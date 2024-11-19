package com.example.ejemplorun.DAL

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * CREACION BD
 */
@Database(entities = [ContactosEntity::class], version = 2, exportSchema = true)
abstract class ContactosDataBase : RoomDatabase() {
    abstract fun Dao(): ContactosDAO
}
