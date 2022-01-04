package com.example.foodieplanner

import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView



/** A simple single line list item.  */
class SingleLineItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val checkbox: CheckBox = view.findViewById(R.id.plan_grocery_checkbox)
    val quantity: TextView = view.findViewById(R.id.plan_grocery_quantity)

}