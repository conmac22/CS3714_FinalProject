package com.example.foodieplanner

enum class Unit {

    TSP,TBSP,FLOZ,CUP,PINT,QUART,GAL,ML,LITER,LB,OUNCE,GRAM,MG,KG,NONE;

    override fun toString(): String {
        return when(this) {
            TSP -> "tsp"
            TBSP -> "tbsp"
            FLOZ -> "fl oz"
            CUP -> "cup"
            PINT -> "pint"
            QUART -> "qt"
            GAL -> "gal"
            ML -> "ml"
            LITER -> "l"
            LB -> "lb"
            OUNCE -> "oz"
            GRAM -> "g"
            MG -> "mg"
            KG -> "kg"
            NONE -> "none"
        }
    }

    fun suffix(): String {
        return when(this) {
            TSP -> "teaspoon"
            TBSP -> "tablespoon"
            FLOZ -> "fluid ounce"
            CUP -> "cup"
            PINT -> "pint"
            QUART -> "quart"
            GAL -> "gallon"
            ML -> "milliliter"
            LITER -> "liter"
            LB -> "pound"
            OUNCE -> "ounce"
            GRAM -> "grams"
            MG -> "milligrams"
            KG -> "kilograms"
            NONE -> ""
        }
    }

    fun id(): Int {
        return when(this) {
            TSP -> R.id.tsp
            TBSP -> R.id.tbsp
            FLOZ -> R.id.floz
            CUP -> R.id.cup
            PINT -> R.id.pint
            QUART -> R.id.quart
            GAL -> R.id.gal
            ML -> R.id.ml
            LITER -> R.id.liter
            LB -> R.id.pound
            OUNCE -> R.id.ounce
            GRAM -> R.id.grams
            MG -> R.id.mg
            KG -> R.id.kg
            NONE -> R.id.none
        }
    }
}