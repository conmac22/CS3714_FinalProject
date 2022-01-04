package com.example.foodieplanner

data class Day(
    var meals: ArrayList<Meal>? = null,
    var day: CalendarDay? = null,
    var groceriesBought: Boolean? = false)