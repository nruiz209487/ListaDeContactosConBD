package com.example.ejemplorun.DAL

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/**
 * DAO CON METDODS getAll,update,insert
 */
@Dao
interface ContactosDAO {
    @Query("SELECT * FROM tablaContactos")
    suspend fun getAll(): List<ContactosEntity>

    @Insert
    suspend fun insert(contactoOBJ: ContactosEntity)
}