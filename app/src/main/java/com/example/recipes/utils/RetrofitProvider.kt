package com.example.recipes.utils

import com.example.recipes.data.serviceapis.RecipeServiceApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitProvider {
    companion object {
        fun getRecipeServiceApi(): RecipeServiceApi {
            val retrofit = Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(RecipeServiceApi::class.java)
        }
    }
}