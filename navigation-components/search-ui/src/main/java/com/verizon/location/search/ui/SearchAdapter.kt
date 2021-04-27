package com.verizon.location.search.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.verizon.location.services.search.model.SuggestResult
import com.verizon.location.services.search.model.SearchResult
import java.lang.IllegalArgumentException

internal class SearchAdapter(val listener: ClickListener) : Adapter<ViewHolder>() {

    private var suggestResults: List<SuggestResult>? = null
    private var searchResults: List<SearchResult>? = null

    fun setSuggestResults(results: List<SuggestResult>?) {
        suggestResults = results?.filter { it.resultType == "place" || it.resultType == "address" }
        searchResults = null
        notifyDataSetChanged()
    }

    fun setSearchResults(results: List<SearchResult>?) {
        searchResults = results
        notifyDataSetChanged()
    }

    fun clear() {
        suggestResults = null
        searchResults = null
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        when (viewType) {
            VIEW_TYPE_CURRENT_LOCATION -> CurrentLocationHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_current_location, parent, false)
            ).apply {
                this.itemView.setOnClickListener {
                    listener.onUserLocationSelected()
                }
            }
            VIEW_TYPE_RESULT_SUGGEST -> SuggestResultHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_search_result, parent, false)
            ).apply {
                this.itemView.setOnClickListener {
                    suggestResults?.get(this.adapterPosition)?.let { result ->
                        listener.onSuggestResultSelected(result)
                    }
                }
            }
            VIEW_TYPE_RESULT_SEARCH -> SearchResultHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_search_result, parent, false)
            ).apply {
                this.itemView.setOnClickListener {
                    searchResults?.get(this.adapterPosition)?.let { result ->
                        listener.onSearchResultSelected(result)
                    }
                }
            }
            else -> throw IllegalArgumentException("Unknown viewType")
        }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_RESULT_SUGGEST -> suggestResults?.get(position)?.let {
                (holder as SuggestResultHolder).bind(it)
            }
            VIEW_TYPE_RESULT_SEARCH -> searchResults?.get(position)?.let {
                (holder as SearchResultHolder).bind(it)
            }
        }
    }

    override fun getItemCount(): Int {
        return searchResults?.size ?: suggestResults?.size ?: 1
    }

    override fun getItemViewType(position: Int): Int =
        if (searchResults == null) {
            if (suggestResults == null) {
                VIEW_TYPE_CURRENT_LOCATION
            } else {
                VIEW_TYPE_RESULT_SUGGEST
            }
        } else {
            VIEW_TYPE_RESULT_SEARCH
        }

    class CurrentLocationHolder(itemView: View) : ViewHolder(itemView)

    inner class SuggestResultHolder(itemView: View) : ViewHolder(itemView) {
        private val nameView = itemView.findViewById<TextView>(R.id.name)
        private val distanceView = itemView.findViewById<TextView>(R.id.distance)
        private val vicinityView = itemView.findViewById<TextView>(R.id.vicinity)

        fun bind(result: SuggestResult) {
            nameView.text = result.description
            distanceView.text = formattedDistance(result.distance)
            vicinityView.text = result.vicinity
        }
    }

    inner class SearchResultHolder(itemView: View) : ViewHolder(itemView) {
        private val nameView = itemView.findViewById<TextView>(R.id.name)
        private val distanceView = itemView.findViewById<TextView>(R.id.distance)
        private val vicinityView = itemView.findViewById<TextView>(R.id.vicinity)

        fun bind(result: SearchResult) {
            nameView.text = result.name
            distanceView.text = formattedDistance(result.distance)
            vicinityView.text = result.vicinity
        }
    }

    fun formattedDistance(meters: Double?): String {
        return meters?.let { m ->
            val feet = convertMetersToFeet(m)
            return if (feet > 999) {
                val miles = convertFeetToMiles(feet)
                "$miles mi"
            } else {
                "${feet.toInt()} ft"
            }
        } ?: ""
    }

    fun convertMetersToFeet(meters: Double): Double {
        return FEET_PER_METER * meters
    }

    fun convertFeetToMiles(feet: Double): Double {
        return Math.round(feet * 10.0 / FEET_PER_MILE).toInt() / 10.0
    }

    interface ClickListener {
        fun onSearchResultSelected(result: SearchResult)
        fun onSuggestResultSelected(result: SuggestResult)
        fun onUserLocationSelected()
    }

    private companion object {
        const val VIEW_TYPE_CURRENT_LOCATION = 0
        const val VIEW_TYPE_RESULT_SUGGEST   = 1
        const val VIEW_TYPE_RESULT_SEARCH    = 2

        const val FEET_PER_METER = 3.28084
        const val FEET_PER_MILE = 5280.0
    }
}