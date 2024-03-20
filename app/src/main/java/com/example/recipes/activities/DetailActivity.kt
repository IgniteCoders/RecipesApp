package com.example.recipes.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.example.recipes.R
import com.example.recipes.data.daos.RecipeDAO
import com.example.recipes.data.entities.Recipe
import com.example.recipes.data.serviceapis.RecipeServiceApi
import com.example.recipes.databinding.ActivityDetailBinding
import com.example.recipes.utils.RetrofitProvider
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ID = "RECIPE_ID"
        const val EXTRA_NAME = "RECIPE_NAME"
        const val EXTRA_IMAGE = "RECIPE_IMAGE"
    }


    private lateinit var binding: ActivityDetailBinding

    private var recipeId:Int = -1
    private lateinit var recipe: Recipe

    private lateinit var recipeDAO: RecipeDAO
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recipeId = intent.getIntExtra(EXTRA_ID, -1)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.title = ""//name

        recipeDAO = RecipeDAO(this)

        recipe = recipeDAO.find(recipeId)
        loadData()
        //findRecipeById(recipeId)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadData() {
        Picasso.get().load(recipe.image).into(binding.photoImageView)
        binding.nameTextView.text = recipe.name
        binding.descriptionTextView.text = recipe.cuisine
        binding.ratingBar.rating = recipe.rating
        binding.reviewsTextView.text = "(${recipe.reviewCount} Reviews)"

        binding.prepTimeTextView.text = "${recipe.prepTimeMinutes} min"
        binding.cookTimeTextView.text = "${recipe.cookTimeMinutes} min"
        binding.difficultyTextView.text = recipe.difficulty
        binding.servingsTextView.text = "Recipe for ${recipe.servings} servings"

        var ingredientsText = ""
        recipe.ingredients.forEachIndexed { index, element ->
            if (index > 0) ingredientsText += "\n"
            ingredientsText += " - $element"
        }
        binding.ingredientsTextView.text = ingredientsText

        var instructionsText = ""
        recipe.instructions.forEachIndexed { index, element ->
            if (index > 0) instructionsText += "\n"
            instructionsText += " - $element"
        }
        binding.instructionsTextView.text = instructionsText
    }

    /*private fun findRecipeById(id: Int) {
        //binding.content.progress.visibility = View.VISIBLE

        val service: RecipeServiceApi = RetrofitProvider.getRecipeServiceApi()

        CoroutineScope(Dispatchers.IO).launch {
            // Llamada en segundo plano
            val response = service.findById(id)

            runOnUiThread {
                // Modificar UI
                //binding.content.progress.visibility = View.GONE
                if (response.body() != null) {
                    Log.i("HTTP", "respuesta correcta :)")
                    recipe = response.body()!!
                    loadData()
                } else {
                    Log.i("HTTP", "respuesta erronea :(")
                }
            }
        }
    }*/
}