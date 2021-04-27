package com.verizon.location.search.ui

import android.content.Context
import android.location.Location
import android.os.Bundle
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.verizon.location.commonmodels.Coordinate
import com.verizon.location.commonmodels.request.ErrorInfo
import com.verizon.location.search.ui.SearchAdapter.ClickListener
import com.verizon.location.services.search.SearchCallback
import com.verizon.location.services.search.SuggestCallback
import com.verizon.location.services.search.VltSearch
import com.verizon.location.services.search.model.SearchResponse
import com.verizon.location.services.search.model.SearchResult
import com.verizon.location.services.search.model.SuggestResponse
import com.verizon.location.services.search.model.SuggestResult
import com.verizon.location.services.search.model.UserLocationBias
import timber.log.Timber

class SearchFragment : Fragment(R.layout.fragment_search) {

    private val searchAdapter = SearchAdapter(object : ClickListener {
        override fun onSearchResultSelected(result: SearchResult) {
            setFragmentResult(
                requestKey,
                Bundle().apply {
                    this.putString(ARG_NAME, result.name)
                    result.geometry?.position?.lat?.let { lat ->
                        this.putDouble(ARG_LATITUDE, lat.toDouble())
                    }
                    result.geometry?.position?.lng?.let { lng ->
                        this.putDouble(ARG_LONGITUDE, lng.toDouble())
                    }
                }
            )
            navigateUp()
        }

        override fun onSuggestResultSelected(result: SuggestResult) {
            setFragmentResult(
                requestKey,
                Bundle().apply {
                    this.putString(ARG_NAME, result.description)
                    result.geometry?.position?.lat?.let { lat ->
                        this.putDouble(ARG_LATITUDE, lat.toDouble())
                    }
                    result.geometry?.position?.lng?.let { lng ->
                        this.putDouble(ARG_LONGITUDE, lng.toDouble())
                    }
                }
            )
            navigateUp()
        }

        override fun onUserLocationSelected() {
            setFragmentResult(
                requestKey,
                Bundle().apply {
                    this.putString(ARG_NAME, "Current location")
                    this.putDouble(ARG_LATITUDE, currentLocation.lat.toDouble())
                    this.putDouble(ARG_LONGITUDE, currentLocation.lng.toDouble())
                }
            )
            navigateUp()
        }
    })
    private lateinit var vltService: VltSearch
    private lateinit var searchBox: AppCompatEditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        vltService = VltSearch(view.context, API_KEY)

        view.findViewById<Toolbar>(R.id.toolbar).setNavigationOnClickListener {
            navigateUp()
        }

        val clearButton = view.findViewById<AppCompatImageView>(R.id.clear_button)
        clearButton.setOnClickListener { clearSearch() }

        searchBox = view.findViewById(R.id.search_box)
        searchBox.addTextChangedListener { text ->
            if (text.isNullOrBlank()) {
                searchAdapter.clear()
                clearButton.visibility = INVISIBLE
            } else if (text.length < 3) {
                searchAdapter.clear()
            } else {
                performSuggest(text.toString())
                clearButton.visibility = VISIBLE
            }
        }
        searchBox.setOnEditorActionListener { v, actionId, event ->
            performSearch(v.text.toString())
            true
        }
        view.findViewById<RecyclerView>(R.id.search_results_recycler).apply {
            this.adapter = searchAdapter
            this.layoutManager = LinearLayoutManager(view.context)
        }
        showKeyboard()

        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun navigateUp() {
        hideKeyboard()
        parentFragmentManager.popBackStack()
    }

    private fun performSuggest(text: String) {
        vltService.getSuggestPerformer().suggest(
            text,
            10,
            UserLocationBias(Coordinate(currentLocation.lat, currentLocation.lng)),
            object : SuggestCallback {
                override fun onSuccess(suggestResponse: SuggestResponse) {
                    searchAdapter.setSuggestResults(suggestResponse.results)
                }

                override fun onFailure(errorInfo: ErrorInfo) {
                    Timber.e(errorInfo.body)
                }
            }
        )
    }

    private fun performSearch(text: String) {
        vltService.getSearchPerformer().search(
            text,
            10,
            UserLocationBias(Coordinate(currentLocation.lat, currentLocation.lng)),
            object : SearchCallback {
                override fun onSuccess(searchResponse: SearchResponse) {
                    searchAdapter.setSearchResults(searchResponse.results)
                }

                override fun onFailure(errorInfo: ErrorInfo) {
                    Timber.e(errorInfo.body)
                }
            }
        )
    }

    private fun clearSearch() {
        searchBox.setText("")
        searchAdapter.clear()
    }

    private fun showKeyboard() {
        (context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)?.let { imm ->
            imm.showSoftInput(searchBox, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun hideKeyboard() {
        (context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)?.let { imm ->
            imm.hideSoftInputFromWindow(searchBox.windowToken, 0)
        }
    }

    val requestKey: String
        get() = arguments?.getString(ARG_REQUEST) ?: ""

    val currentLocation: Coordinate
        get() = Coordinate(
            arguments?.getDouble(ARG_LATITUDE)?.toFloat() ?: 0.0f,
            arguments?.getDouble(ARG_LONGITUDE)?.toFloat() ?: 0.0f
        )

    companion object {
        const val ARG_REQUEST = "requestKey"
        const val ARG_NAME = "name"
        const val ARG_LATITUDE = "lat"
        const val ARG_LONGITUDE = "lng"

        private const val API_KEY = "Enter VLTApiKey here, contact Customer Success for your key"

        fun newInstance(requestKey: String, currentLocation: Location?): SearchFragment {
            val frag = SearchFragment()
            frag.arguments = Bundle().apply {
                this.putString(ARG_REQUEST, requestKey)
                currentLocation?.let { loc ->
                    this.putDouble(ARG_LATITUDE, loc.latitude)
                    this.putDouble(ARG_LONGITUDE, loc.longitude)
                }
            }
            return frag
        }
    }
}