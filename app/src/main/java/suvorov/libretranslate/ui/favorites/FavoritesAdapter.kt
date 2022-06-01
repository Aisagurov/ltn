package suvorov.libretranslate.ui.favorites

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import suvorov.libretranslate.R
import suvorov.libretranslate.data.database.TranslationEntity

class FavoritesAdapter: RecyclerView.Adapter<FavoritesAdapter.HistoryViewHolder>() {

    var data: List<TranslationEntity> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(data) {
            field = data
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        return HistoryViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorites, parent, false)
        )
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val originalText = data[position].originalData
        val translatedText = data[position].translatedData
        holder.originalText.text = originalText
        holder.translatedText.text = translatedText
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun getItemPosition(position: Int): TranslationEntity {
        return data[position]
    }

    class HistoryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var originalText: TextView = itemView.findViewById(R.id.originalTextView)
        var translatedText: TextView = itemView.findViewById(R.id.translatedTextView)
    }
}