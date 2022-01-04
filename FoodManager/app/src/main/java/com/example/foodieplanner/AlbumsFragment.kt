package com.example.foodieplanner

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.random.Random

import androidx.recyclerview.widget.ItemTouchHelper

class AlbumsFragment : Fragment() {
    private val model: Model by activityViewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private val meals = ArrayList<Meal>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_albums, container, false)

        recyclerView = view.findViewById(R.id.albums_meal_list)
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        viewAdapter = MealAdapter(meals)
        recyclerView.adapter = viewAdapter

        // For deleting meals
        val itemTouchHelperCallback = object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    (viewAdapter as MealAdapter).removeMeal(viewHolder.adapterPosition)
                }

            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        val albumName = this.arguments?.getString("albumName")

        // Set title
        if (albumName == "All_") {
            view.findViewById<com.google.android.material.appbar.MaterialToolbar>(
                R.id.saved_meals_album_toolbar).title = "All Meals"
        }
        else {
            view.findViewById<com.google.android.material.appbar.MaterialToolbar>(
                R.id.saved_meals_album_toolbar).title = albumName
        }


        // Load albums from firebase
        model.database.child("Meals").get().addOnSuccessListener { data ->
            for (meal in data.children) {
                // Extract meal data
                var name = ""
                var ingredients: ArrayList<Ingredient> = arrayListOf()
                var instructions: ArrayList<String> = arrayListOf()
                var album = ""
                var rating = 0.0f
                var calories = 0
                var cost = ""
                for (mealAttr in meal.children) {
                    when (mealAttr.key) {
                        "name" -> name = mealAttr.value.toString()
                        "ingredients" -> {
                            for (ingredient in mealAttr.children) {
                                var ingrName = ""
                                var ingrQuantity = ""
                                var ingrUnit: Unit? = null
                                var ingrMeasure = ""
                                var ingrStandard = ""
                                for (ingrAttr in ingredient.children) {
                                    when (ingrAttr.key) {
                                        "name" -> ingrName = ingrAttr.value.toString()
                                        "quantity" -> ingrQuantity = ingrAttr.value.toString()
                                        "unit" -> ingrUnit = toUnit(ingrAttr.value.toString())
                                        "measure" -> ingrMeasure = ingrAttr.value.toString()
                                        "standard" -> ingrStandard = ingrAttr.value.toString()
                                    }
                                }
                                ingredients.add(Ingredient(ingrName, ingrQuantity, ingrUnit, ingrMeasure, ingrStandard))
                            }
                        }
                        "instructions" -> {
                            for (instruction in mealAttr.children) {
                                instructions.add(instruction.value.toString())
                            }
                        }
                        "albumName" -> album = mealAttr.value.toString()
                        "rating" -> rating = mealAttr.value.toString().toFloat()
                        "calories" -> calories = mealAttr.value.toString().toInt()
                        "cost" -> cost = mealAttr.value.toString()
                    }
                }
                // Rebuild meal
                val meal = Meal(name, ingredients, instructions, album, rating, calories, cost)

                // Display only items associated with album or all meals if
                // "All Meals" button was pressed
                if (albumName == album || albumName == "All_") {
                    (viewAdapter as MealAdapter).insertMeal(meal)
                }
            }
        }

        view.findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.saved_meals_album_toolbar).setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        return view
    }

    private fun toUnit(str: String): Unit? {
        when (str) {
            "TSP" -> return Unit.TSP
            "TBSP" -> return Unit.TBSP
            "FLOZ" -> return Unit.FLOZ
            "CUP"-> return Unit.CUP
            "PINT:" -> return Unit.PINT
            "QUART" -> return Unit.QUART
            "GAL" -> return Unit.GAL
            "ML" -> return Unit.ML
            "LITER" -> return Unit.LITER
            "LB" -> return Unit.LB
            "OUNCE" -> return Unit.OUNCE
            "GRAM" -> return Unit.GRAM
            "MG" -> return Unit.MG
            "KG" -> return Unit.KG
            "NONE" -> return Unit.NONE
        }
        return Unit.NONE
    }

    inner class MealAdapter(private val mealList: ArrayList<Meal>):
        RecyclerView.Adapter<MealAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(
                R.layout.card_albums_meal,
                parent, false
            )
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bindItems(mealList[position])
        }

        fun insertMeal(meal: Meal) {
            mealList.add(meal)
            notifyDataSetChanged()
        }

        fun removeMeal(pos: Int) {
            model.deleteMeal(mealList[pos].name)
            mealList.removeAt(pos)
            notifyItemRemoved(pos)
            notifyItemRangeChanged(pos, mealList.size)
        }

        fun clearMealList() {
            mealList.clear()
        }

        private fun getRandomColor(): Int {
            val rand = Random.nextInt(0, 3)
            when (rand) {
                0 -> return Color.BLACK
                1 -> return Color.DKGRAY
                2 -> return Color.GRAY
                3 -> return Color.LTGRAY
            }
            return Color.BLACK
        }

        override fun getItemCount() = mealList.size

        inner class ViewHolder(private val view: View) :
            RecyclerView.ViewHolder(view) {
            fun bindItems(meal: Meal) {
                val title: TextView = itemView.findViewById(R.id.meal_card_title)
                val cardColor: Int = getRandomColor()
                title.setBackgroundColor(cardColor)
                title.text = meal.name

                val rating: TextView = itemView.findViewById(R.id.meal_card_rating)
                rating.text = meal.rating.toString()

                val image: ImageView = itemView.findViewById(R.id.album_card_image)
                image.setImageResource(R.drawable.default_pic)

                itemView.setOnClickListener {
                    clearMealList()
                    view.findNavController().navigate(R.id.action_albumsFragment_to_mealFragment,
                        bundleOf("mealName" to meal.name))
                }
            }
        }
    }
}