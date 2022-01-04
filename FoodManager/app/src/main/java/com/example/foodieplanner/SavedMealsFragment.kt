package com.example.foodieplanner

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlin.random.Random

class SavedMealsFragment : Fragment() {
    private val model: Model by activityViewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private val albums = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_saved_meals, container, false)

        recyclerView = view.findViewById(R.id.saved_meals_albums_list)
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        viewAdapter = AlbumnAdapter(albums)
        recyclerView.adapter = viewAdapter

        // For deleting albums
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
                (viewAdapter as AlbumnAdapter).removeAlbum(viewHolder.adapterPosition)
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        // Load albums from firebase
        model.database.child("Albums").get().addOnSuccessListener { data ->
            for (album in data.children) {
                (viewAdapter as AlbumnAdapter).insertAlbum(album.value.toString())
            }
        }

        view.findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.saved_meals_toolbar).setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        // Add a meal
        if (activity?.resources?.configuration?.orientation != Configuration.ORIENTATION_LANDSCAPE) {
            view.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.savedmeals_new_meal_button).setOnClickListener {
                showDialog()
            }
        }

        // View all meals
        if (activity?.resources?.configuration?.orientation != Configuration.ORIENTATION_LANDSCAPE) {
            view.findViewById<Button>(R.id.saved_meals_all_meals_button).setOnClickListener {
                (viewAdapter as AlbumnAdapter).clearAlbumList()
                view.findNavController().navigate(R.id.action_savedMealsFragment_to_albumsFragment,
                    bundleOf("albumName" to "All_"))
            }
        }

        // Add an album
        if (activity?.resources?.configuration?.orientation != Configuration.ORIENTATION_LANDSCAPE) {
            view.findViewById<Button>(R.id.add_album_button).setOnClickListener {
                val input = EditText(requireContext())
                input.setInputType(InputType.TYPE_CLASS_TEXT)

                val dialogBuilder = AlertDialog.Builder(requireContext())
                dialogBuilder.setTitle("Enter the album name")
                    .setPositiveButton("Save") { dialog, id ->
                        val albumName: String = input.text.toString()
                        if (albumName != "") {
                            (viewAdapter as AlbumnAdapter).insertAlbum(albumName)
                        }
                    }
                    .setNegativeButton("Cancel") { dialog, id -> }
                    .setView(input)

                dialogBuilder.show()
            }
        }

        return view
    }

    private fun showDialog() {
        val fragmentManager = activity?.supportFragmentManager
        val newFragment = NewMealDialog()

        val transaction = fragmentManager?.beginTransaction()
        transaction?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        if (transaction != null) {
            transaction.add(android.R.id.content, newFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    inner class AlbumnAdapter(private val albumList: ArrayList<String>):
        RecyclerView.Adapter<AlbumnAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(
                R.layout.card_saved_meals_album,
                parent, false
            )
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bindItems(albumList[position])
        }

        fun insertAlbum(album: String) {
            albumList.add(album)
            notifyDataSetChanged()
            model.addAlbum(album)
        }

        fun removeAlbum(pos: Int) {
            model.deleteAlbum(albumList[pos])
            albumList.removeAt(pos)
            notifyItemRemoved(pos)
            notifyItemRangeChanged(pos, albumList.size)
        }

        fun clearAlbumList() {
            albumList.clear()
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

        override fun getItemCount() = albumList.size

        inner class ViewHolder(private val view: View) :
            RecyclerView.ViewHolder(view) {
            fun bindItems(album: String) {
                val title: TextView = itemView.findViewById(R.id.meal_card_title)
                val cardColor: Int = getRandomColor()
                title.setBackgroundColor(cardColor)
                title.text = album

                val image: ImageView = itemView.findViewById(R.id.album_card_image)
                when (album) {
                    "Breakfast" -> image.setImageResource(R.drawable.breakfast)
                    "Lunch" -> image.setImageResource(R.drawable.lunch)
                    "Dinner" -> image.setImageResource(R.drawable.dinner)
                    else -> image.setImageResource(R.drawable.default_pic)
                }

                itemView.setOnClickListener {
                    clearAlbumList()
                    view.findNavController().navigate(R.id.action_savedMealsFragment_to_albumsFragment,
                        bundleOf("albumName" to album))
                }
            }
        }
    }
}
