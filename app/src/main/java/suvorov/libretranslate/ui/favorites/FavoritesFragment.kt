package suvorov.libretranslate.ui.favorites

import android.app.AlertDialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import suvorov.libretranslate.databinding.FragmentFavoritesBinding

@AndroidEntryPoint
class FavoritesFragment: Fragment() {
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val favoritesViewModel: FavoritesViewModel by viewModels()
    private val favoritesAdapter = FavoritesAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews()

        observeViewModel()

        deleteItemSwipe()

        binding.deleteAllButton.setOnClickListener {
            deleteAll()
        }
    }

    private fun initializeViews() {
        binding.historyRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = favoritesAdapter
        }
    }

    private fun observeViewModel() {
        favoritesViewModel.getAllFromDatabase().observe(viewLifecycleOwner) {
            favoritesAdapter.data = it
        }
    }

    private fun deleteItemSwipe() {
        val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val item = favoritesAdapter.getItemPosition(position)
                favoritesViewModel.deleteFromDatabase(item)
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.historyRecyclerView)
    }

    private fun deleteAll() {
        val builder = AlertDialog.Builder(requireActivity())
            .setMessage("Удалить избранное?")
            .setPositiveButton("Очистить") { _, _ ->
                favoritesViewModel.deleteAllDatabase()
            }
            .setNeutralButton("Отмена") { _, _ ->
            }
        builder.create()

        val messageView: TextView = builder.show().findViewById(android.R.id.message)
        messageView.gravity = Gravity.CENTER
    }

    override fun onDestroyView(){
        super.onDestroyView()
        _binding = null
    }
}