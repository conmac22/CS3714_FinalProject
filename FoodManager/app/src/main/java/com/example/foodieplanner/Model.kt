package com.example.foodieplanner

import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Model: ViewModel() {
    var database: DatabaseReference = Firebase.database.reference

    var meals_for_day: ArrayList<Meal> = arrayListOf()
    var latestCompleteDay: CalendarDay? = null

    fun addMeal(meal: Meal) {
        var name = meal.name
        if (name != null) {
            database.child("Meals").child(name).setValue(meal)
        }
    }

    fun deleteMeal(name: String?) {
        if (name != null) {
            database.child("Meals").child(name).removeValue()
        }
    }

    fun addAlbum(album: String) {
        database.child("Albums").child(album).setValue(album)
    }

    fun deleteAlbum(album: String) {
        database.child("Albums").child(album).removeValue()
    }

    fun addMealsForDay(meals: ArrayList<Meal>, month: String, date: String) {
        for (meal in meals) {
            var name = meal.name
            if (name != null) {
                database.child("Dates").child(month).child(date).child("Meals").child(name)
                    .setValue(meal)
            }
        }
    }

    fun addDay(meals: ArrayList<Meal>, calendarDay: CalendarDay) {
        if (!meals.isEmpty())
            database.child("Dates").child(calendarDay.toSmallString()).setValue(Day(meals,calendarDay,false))
    }

//    fun setValidRange(range: Pair<Long,Long>) {
//        database.child("ValidDateRange").setValue(range)
//    }
}