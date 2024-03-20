package com.example.recipes.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipes.R
import com.example.recipes.adapters.RecipesAdapter
import com.example.recipes.data.daos.RecipeDAO
import com.example.recipes.data.entities.Recipe
import com.example.recipes.data.serviceapis.RecipeServiceApi
import com.example.recipes.databinding.ActivityMainBinding
import com.example.recipes.databinding.ActivityRecipesBinding
import com.example.recipes.utils.RetrofitProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecipesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipesBinding

    private lateinit var adapter: RecipesAdapter
    private var recipesList:List<Recipe> = listOf()

    private lateinit var recipeDAO: RecipeDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRecipesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recipeDAO = RecipeDAO(this)

        initView()
    }

    private fun initView() {
        adapter = RecipesAdapter() {
            onItemClickListener(it)
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        recipesList = recipeDAO.findAll()
        if (recipesList.isEmpty()) {
            binding.recyclerView.visibility = View.GONE
            binding.emptyPlaceholder.visibility = View.VISIBLE
        } else {
            adapter.updateItems(recipesList)
            binding.recyclerView.visibility = View.VISIBLE
            binding.emptyPlaceholder.visibility = View.GONE
        }
        //getAllRecipes()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.recipes_menu, menu)

        initSearchView(menu?.findItem(R.id.menu_search))

        return true
    }

    private fun initSearchView(searchItem: MenuItem?) {
        if (searchItem != null) {
            var searchView = searchItem.actionView as SearchView

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    searchView.clearFocus()
                    return false
                }

                override fun onQueryTextChange(query: String): Boolean {
                    searchAllRecipesByName(query)
                    return true
                }
            })
        }
    }

    private fun searchAllRecipesByName(query: String) {
        val searchList = recipesList.filter { it.name.contains(query, true) }
        adapter.updateItems(searchList)
    }

    private fun onItemClickListener(position:Int) {
        val recipe: Recipe = adapter.items[position]

        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_ID, recipe.id)
        intent.putExtra(DetailActivity.EXTRA_NAME, recipe.name)
        intent.putExtra(DetailActivity.EXTRA_IMAGE, recipe.image)
        startActivity(intent)
        //Toast.makeText(this, getString(horoscope.name), Toast.LENGTH_LONG).show()
    }

    /*private fun getAllRecipes() {
        binding.progress.visibility = View.VISIBLE

        val service: RecipeServiceApi = RetrofitProvider.getRecipeServiceApi()

        CoroutineScope(Dispatchers.IO).launch {
            // Llamada en segundo plano
            val response = service.findAll()

            runOnUiThread {
                // Modificar UI
                binding.progress.visibility = View.GONE

                if (response.body() != null) {
                    Log.i("HTTP", "respuesta correcta :)")
                    recipesList = response.body()?.results.orEmpty()
                    adapter.updateItems(recipesList)

                    if (recipesList.isNotEmpty()) {
                        binding.recyclerView.visibility = View.VISIBLE
                        binding.emptyPlaceholder.visibility = View.GONE
                    } else {
                        binding.recyclerView.visibility = View.GONE
                        binding.emptyPlaceholder.visibility = View.VISIBLE
                    }
                } else {
                    Log.i("HTTP", "respuesta erronea :(")
                    Toast.makeText(this@RecipesActivity, "Hubo un error inesperado, vuelva a intentarlo más tarde", Toast.LENGTH_LONG).show()
                }
            }
        }
    }*/
}