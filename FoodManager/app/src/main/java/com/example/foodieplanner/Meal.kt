package com.example.foodieplanner

data class Meal(
    var name: String? = null,
    var ingredients: ArrayList<Ingredient>? = null,
    var instructions: ArrayList<String>? = null,
    var albumName: String? = null,
    var rating: Float? = null,
    var calories: Int? = null,
    var cost: String? = null
)