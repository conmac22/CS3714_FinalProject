package com.example.foodieplanner

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MealFragment : Fragment() {
    private val model: Model by activityViewModels()

    private lateinit var ingredientRecylerView: RecyclerView
    private lateinit var instructionRecyclerView: RecyclerView
    private lateinit var ingredientViewAdapter: RecyclerView.Adapter<*>
    private lateinit var instructionViewAdapter: RecyclerView.Adapter<*>

    private var ingredients: ArrayList<Ingredient> = arrayListOf()
    private var instructions: ArrayList<String> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_meal, container, false)

        val mealName = this.arguments?.getString("mealName")
        view.findViewById<com.google.android.material.appbar.MaterialToolbar>(
            R.id.saved_meals_album_meal_toolbar).title = mealName

        ingredientRecylerView = view.findViewById(R.id.ingredient_list)
        ingredientRecylerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        ingredientViewAdapter = IngredientsAdapter(ingredients)
        ingredientRecylerView.adapter = ingredientViewAdapter

        instructionRecyclerView = view.findViewById(R.id.instructions_list)
        instructionRecyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        instructionViewAdapter = InstructionsAdapter(instructions)
        instructionRecyclerView.adapter = instructionViewAdapter

        model.database.child("Meals").get().addOnSuccessListener { data ->
            for (meal in data.children) {
                if (meal.key.toString() == mealName) {
                    // Extract meal data
                    for (mealAttr in meal.children) {
                        when (mealAttr.key) {
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
                                    (ingredientViewAdapter as IngredientsAdapter).insertIngredient(Ingredient(ingrName, ingrQuantity, ingrUnit, ingrMeasure, ingrStandard))
                                }
                            }
                            "instructions" -> {
                                for (instruction in mealAttr.children) {
                                    (instructionViewAdapter as InstructionsAdapter).insertInstruction(instruction.value.toString())
                                }
                            }
                            "rating" -> {
                                view.findViewById<RatingBar>(R.id.meal_rating_bar).rating = mealAttr.value.toString().toFloat()
                            }
                            "calories" -> view.findViewById<TextView>(R.id.meal_calories).text = mealAttr.value.toString() + " calories"
                            "cost" -> view.findViewById<TextView>(R.id.meal_cost).text = mealAttr.value.toString()
                        }
                    }
                }
            }
        }


        view.findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.saved_meals_album_meal_toolbar).setNavigationOnClickListener {
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

    inner class IngredientsAdapter(private val ingredientList: ArrayList<Ingredient>):
        RecyclerView.Adapter<IngredientsAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(
                R.layout.view_meal_ingredient_item,
                parent, false
            )
            return ViewHolder(v)
        }

        fun insertIngredient(ingredient: Ingredient) {
            ingredientList.add(ingredient)
            notifyDataSetChanged()
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bindItems(ingredientList[position])
        }

        override fun getItemCount() = ingredientList.size

        inner class ViewHolder(private val view: View) :
            RecyclerView.ViewHolder(view) {
            fun bindItems(ingredient: Ingredient) {
                val amount: TextView = itemView.findViewById(R.id.ingredient_amount)
                amount.text = ingredient.quToString()

                val name: TextView = itemView.findViewById(R.id.ingredient_name)
                name.text = ingredient.name
            }
        }
    }

    inner class InstructionsAdapter(private val instructionList: ArrayList<String>):
        RecyclerView.Adapter<InstructionsAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(
                R.layout.view_meal_instruction_item,
                parent, false
            )
            return ViewHolder(v)
        }

        fun insertInstruction(instruction: String) {
            instructionList.add(instruction)
            notifyDataSetChanged()
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bindItems(instructionList[position], position)
        }

        override fun getItemCount() = instructionList.size

        inner class ViewHolder(private val view: View) :
            RecyclerView.ViewHolder(view) {
            fun bindItems(instructionData: String, pos: Int) {
                val number: TextView = itemView.findViewById(R.id.instruction_number)
                number.text = (pos+1).toString()

                val instruction: TextView = itemView.findViewById(R.id.instruction_data)
                instruction.text = instructionData
            }
        }
    }
}