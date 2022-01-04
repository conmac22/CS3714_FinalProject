package com.example.foodieplanner

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.core.util.Pair
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.foodieplanner.databinding.FragmentPlannerBinding
import com.google.android.material.datepicker.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [PlannerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlannerFragment : Fragment() {

    private lateinit var binding: FragmentPlannerBinding
    private var groceryListRecyclerView: RecyclerView? = null
    private val model: Model by activityViewModels()
    val adapter = SingleLineItemAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // indicate that there is an options menu present
        setHasOptionsMenu(true)

        arguments?.let {

        }
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPlannerBinding.inflate(layoutInflater)


        val today = MaterialDatePicker.todayInUtcMilliseconds()
        var days = arrayListOf<Day>()
        var ingredients = arrayListOf<Ingredient>()
        var startTime = today
        var endTime = today

        var dateRangePicker =
            MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Select dates")

        // Pull Days From Database
        val locationListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                adapter.clearIngredients()
                for (snapShot in dataSnapshot.children) {
                    val day = snapShot.getValue<Day>()
                    if (day != null) {
                        if (day.day?.timeInMiliSeconds!! >= today && !day.groceriesBought!!) {
                            days.add(day)
                        }
                    }
                }

                if (days.size == 1) {
                    startTime = days.get(0).day?.timeInMiliSeconds ?: today
                    endTime = days.get(0).day?.timeInMiliSeconds ?: today
                }
                else if (days.size > 1) {
                    startTime = days.get(0).day?.timeInMiliSeconds ?: today
                    endTime = days.get(days.size-1).day?.timeInMiliSeconds ?: today
                }

                val constraints = CalendarConstraints.Builder()
                    .setValidator(DateValidatorPointForward.now())
                    .build()
                dateRangePicker.setCalendarConstraints(constraints)
                //dateRangePicker.setSelection(Pair(startTime, endTime))
                var dateRangePickerBuilt = dateRangePicker.build()

                dateRangePickerBuilt.addOnDismissListener {
                    val pair = dateRangePickerBuilt.selection
                    if (pair?.first != null && pair.second != null) {
                        adapter.clearIngredients()
                        binding.planCalendarButton.text =
                            milisecDateToString(pair.first, pair.second)
                        for (day in days) {
                            for (meal in day.meals!!) {
                                for (ig in meal.ingredients!!) {
                                    adapter.addIngredient(ig)
                                }
                            }
                        }
                    }
                }

                binding.planCalendarButton.setOnClickListener {
                    activity?.let {
                        dateRangePickerBuilt.show(
                            it.supportFragmentManager,
                            "groceryDateRange"
                        )
                    }
                 }


                binding.planCalendarButton.text = "Select Dates"
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.e("MainRetrieveData", "loadPost:onCancelled", databaseError.toException())
            }
        }
        model.database.child("Dates").addValueEventListener(locationListener)

        // Initial range of dates

        // Configure dateRangePicker popup
//        val constraintsBuilder =
//            CalendarConstraints.Builder()
//                .setValidator(CompositeDateValidator.allOf(
//                    listOf(DateValidatorPointForward.from(startTime),
//                    DateValidatorPointBackward.before(endTime))
//                ))
//        val dateRangePicker =
//            MaterialDatePicker.Builder.dateRangePicker()
//                .setTitleText("Select dates")
//                .setSelection(
//                    Pair(
//                        startTime,
//                        endTime
//                    )
//                )
//                .setCalendarConstraints(constraintsBuilder.build())
//                .build()
//
//        dateRangePicker.addOnDismissListener {
//            val pair = dateRangePicker.selection
//            if (pair?.first != null && pair.second != null)
//                //binding.planTopBar.title = milisecDateToString(pair.first, pair.second)
//                binding.planCalendarButton.text = milisecDateToString(pair.first, pair.second)
//        }

        // top bar configuration
//      binding.planTopBar.setNavigationOnClickListener {
//            // Handle navigation icon press
//        }
//        binding.planTopBar.setOnMenuItemClickListener { menuItem ->
//            when (menuItem.itemId) {
//                else -> false
//            }
//        }

        // open calendar picker
//        binding.planCalendarButton.setOnClickListener {
//            activity?.let {
//                dateRangePicker.show(
//                    it.supportFragmentManager,
//                    "groceryDateRange"
//                )
//            }
//        }

        // populate recycler view
        groceryListRecyclerView = binding.planGroceryListRecycler
        groceryListRecyclerView?.adapter = adapter

        binding.plannerComplete.setOnClickListener {
            adapter.clearIngredients()
        }

        return return binding.root
    }

    fun milisecDateToString(first : Long, second: Long) : String {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.timeInMillis = first
        var str = "${calendar[Calendar.MONTH]}/${calendar[Calendar.DATE]}"
        calendar.timeInMillis = second
        str += " - ${calendar[Calendar.MONTH]}/${calendar[Calendar.DATE]}"
        return str
    }

    // to populate recycler view
    inner class SingleLineItemAdapter: RecyclerView.Adapter<SingleLineItemViewHolder>() {

        var data = arrayListOf<Pair<String,String>>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleLineItemViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.view_planner_listitem, parent, false) as LinearLayout
            return SingleLineItemViewHolder(view)
        }

        override fun onBindViewHolder(holder: SingleLineItemViewHolder, position: Int) {
            holder.checkbox.text = "  " + data.get(position).first
            holder.quantity.text = data.get(position).second
        }

        override fun getItemCount(): Int {
            return data.size
        }

        fun addIngredient(ingredient: Ingredient) {
            data.add(Pair(ingredient.name, ingredient.quToString()))
            notifyDataSetChanged()
        }

        fun clearIngredients() {
            data.clear()
            notifyDataSetChanged()
        }

    }

    inner class SingleLineItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val checkbox: CheckBox = view.findViewById(R.id.plan_grocery_checkbox)
        val quantity: TextView = view.findViewById(R.id.plan_grocery_quantity)

    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment PlannerFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PlannerFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}