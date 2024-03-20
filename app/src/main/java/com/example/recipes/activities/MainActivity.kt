package com.example.recipes.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.recipes.R
import com.example.recipes.data.daos.RecipeDAO
import com.example.recipes.data.entities.Recipe
import com.example.recipes.data.serviceapis.RecipeServiceApi
import com.example.recipes.databinding.ActivityMainBinding
import com.example.recipes.utils.RetrofitProvider
import com.example.recipes.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)
        if (session.didFetchData) {
            navigateToRecipesList()
        } else {
            getAllRecipes()
        }
    }

    private fun getAllRecipes() {
        val service: RecipeServiceApi = RetrofitProvider.getRecipeServiceApi()

        CoroutineScope(Dispatchers.IO).launch {
            // Llamada en segundo plano
            val response = service.findAll()

            runOnUiThread {
                // Modificar UI
                if (response.body() != null) {
                    Log.i("HTTP", "respuesta correcta :)")
                    var recipesList = response.body()?.results.orEmpty()

                    val recipeDAO = RecipeDAO(this@MainActivity)
                    recipeDAO.deleteAll()
                    for (recipe in recipesList) {
                        recipeDAO.insert(recipe)
                    }

                    session.didFetchData = true
                    navigateToRecipesList()
                } else {
                    Log.i("HTTP", "respuesta erronea :(")
                    Toast.makeText(this@MainActivity, "Hubo un error inesperado, vuelva a intentarlo más tarde", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun navigateToRecipesList() {
        val intent = Intent(this, RecipesActivity::class.java)
        startActivity(intent)
    }
}