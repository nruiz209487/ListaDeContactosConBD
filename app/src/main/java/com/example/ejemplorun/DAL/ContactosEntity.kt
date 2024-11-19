package com.example.ejemplorun.DAL

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * creacion Tabla BD
 */
@Entity(tableName = "tablaContactos")
data class ContactosEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var nombre: String = "",
    var tfno: String = "",
    var image: Int = 0

)