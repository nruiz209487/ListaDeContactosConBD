package com.example.ejemplorun

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room.databaseBuilder
import com.example.ejemplorun.DAL.ContactosDAO
import com.example.ejemplorun.DAL.ContactosDataBase
import com.example.ejemplorun.DAL.ContactosEntity
import com.example.ejemplorun.ENT.Contacto
import com.example.ejemplorun.ui.theme.EjemploRunTheme
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    /**
     * COMPANION OBJECT database de tipo TaskDatabase que sirve para poder usarse en el codigo
     */
    companion object {
        // objeto database ContactosDataBase
        lateinit var database: ContactosDataBase
        // objeto listaDeContactos
        var listaDeContactos: List<Contacto> = listOf()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //inicializacion database
        database = databaseBuilder(this, ContactosDataBase::class.java, "contactos-db").build()
        lifecycleScope.launch {
            //Logica que pasa de objeto ContactosEntity a  Contacto y rellena la lista
            val contactosEntityList = database.Dao().getAll()
            listaDeContactos = contactosEntityList.map { entity ->
                Contacto(
                    id = entity.id,
                    nombre = entity.nombre,
                    tfno = entity.tfno,
                    image = entity.image
                )
            }
        }
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            //variable que contiene el dao
            val dao = database.Dao()
            EjemploRunTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "ItemListScreen",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("ItemListScreen") {
                            ItemListScreen(navController = navController)
                        }
                        composable("NuevoContacto") {
                            NuevoContacto(navController = navController ,dao)
                        }

                    }
                }
            }
        }
    }

}


@Composable
fun ItemListScreen(navController: NavHostController) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    navController.navigate("NuevoContacto")
                },
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text("Agregar contacto")
            }

            ItemList(
                itemContacto = MainActivity.listaDeContactos,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}


/**
 * Genera un numero aleatoria para el contrusctor
 */
fun rand(): Int {
    return (1..2).random()
}

/**
 * Vista que se maneja para crear un nuevoContacto y inserta en la db
 * SI LA PARTE GRAFICA ESTA HECHA CON GPT
 * NO TENGO GANAS DE HACERLA Y ENTRAGAR EL COMPOSALE CON ELEMNTOS SUPERPUESTOS ES CASI PEOR
 */
@Composable
fun NuevoContacto(navController: NavHostController, dao: ContactosDAO) {
    val coroutineScope = rememberCoroutineScope() // Obtén el alcance de corrutinas para Compose

    val id by remember { mutableIntStateOf(MainActivity.listaDeContactos.size + 1) }
    var nombre by remember { mutableStateOf("") }
    var numero by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center // Centra todos los elementos
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp), // Espaciado entre elementos
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(24.dp)

        ) {
            // Título de la pantalla
            Text(
                text = "Agregar Nuevo Contacto",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            // Campo de texto para el nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Campo de texto para el número
            OutlinedTextField(
                value = numero,
                onValueChange = { numero = it },
                label = { Text("Numero") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Botón para agregar el contacto
            Button(
                onClick = {
                    //Crea un nuevoContacto
                    val nuevoContacto = Contacto(id, nombre, numero, rand())
                    //lo insrta en la lisata (NO EN LA BD)
                    MainActivity.listaDeContactos += nuevoContacto
                    // HACE UN INSERT DE ContactosEntity EN LA DB USA COMO CONSTRUCTOR LAS PROPIEDADES DE nuevoContacto
                    coroutineScope.launch {
                        dao.insert(
                            ContactosEntity(
                                id = nuevoContacto.id,
                                nombre = nuevoContacto.nombre,
                                tfno = nuevoContacto.tfno,
                                image = nuevoContacto.image
                            )
                        )
                    }

                    navController.navigate("ItemListScreen")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = "Guardar Contacto",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
            }
        }
    }
}


/**
 * SE ENCARGA DE RECCORER LA LISTA CONTATCOS Y DE LAMAR A LA FUNCION ContactoView
 */
@Composable
fun ItemList(itemContacto: List<Contacto>, modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {

        items(itemContacto) { contacto ->
            ContactoView(contacto = contacto)
        }
    }
}

/**
 * VISTA DE LOS CONTATCOS RECIBE COMO PARAMETRO UN COMTACTATO
 */
@Composable
fun ContactoView(contacto: Contacto) {
    val context = LocalContext.current
    //VARIABLE  QUE INDICA SI EL BOTON A SIDO PULADO O NO
    val mostrarContactoCompleto = remember { mutableStateOf(false) }

    Card(
        Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            val image: Painter = if (contacto.image == 1) {
                painterResource(id = R.drawable.ic_launcher_background)
            } else {
                painterResource(id = R.drawable.ic_launcher_foreground)
            }
            //IMAGEN
            Image(
                painter = image,
                contentDescription = "Foto contacto",
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))
//NOMBRE Y NUMERO
            Text(
                //si el boton esta a true muestra los datos si sta false llama a la funcion que muestra el nombre recortado
                text = if (mostrarContactoCompleto.value) "${contacto.nombre} - ${contacto.tfno}" else obtenerIniciales(
                    contacto.nombre
                ),
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:${contacto.tfno}")
                        }
                        context.startActivity(intent)
                    }
            )

            Spacer(modifier = Modifier.height(8.dp))
//BOTON
            Button(
                onClick = {
                    mostrarContactoCompleto.value = !mostrarContactoCompleto.value
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = ButtonDefaults.buttonColors(Color.LightGray)
            ) {
                Text(
                    text = if (mostrarContactoCompleto.value) "Ocultar" else "Mostrar Contacto",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


/**
 * Funciom que coje las iniciales del nombre recibe como parametro el parametro nombre de contatco
 */
fun obtenerIniciales(nombre: String): String {
    return nombre
        .split(" ")
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .joinToString(".")
}


