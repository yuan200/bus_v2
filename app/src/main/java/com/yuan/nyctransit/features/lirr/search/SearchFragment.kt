package com.yuan.nyctransit.features.lirr.search

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.yuan.nyctransit.R
import com.yuan.nyctransit.core.database.Stop
import com.yuan.nyctransit.databinding.SearchFragmentBinding
import kotlinx.android.synthetic.main.fragment_nearby.*

class SearchFragment : Fragment() {
    private lateinit var stopList: List<Stop>

    private val searchResultVM: SearchResultViewModel by activityViewModels()
    companion object {
        fun newInstance() = SearchFragment()
    }

    private val viewModel: SearchViewModel by lazy {
        ViewModelProviders.of(this)[SearchViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<SearchFragmentBinding>(
            inflater, R.layout.search_fragment,
            container, false
        )
        val root = binding.root
        stopList = mutableListOf()
        val viewManager = LinearLayoutManager(context)
        val stopAdapter = StopAdapter(stopList)
        stopAdapter.clickListener = { lat, lon ->
            val location = Location("searchLocation").apply {
                latitude = lat.toDouble()
                longitude = lon.toDouble()
            }
            searchResultVM.location.value = location
            fragmentManager?.popBackStackImmediate()

        }
        binding.listView.layoutManager = viewManager
        binding.listView.adapter = stopAdapter
        viewModel.stopList.observe(viewLifecycleOwner, Observer {
            stopAdapter.stopList = it
            stopAdapter.notifyDataSetChanged()
        })
        binding.searchView.isIconifiedByDefault = false
        binding.searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    stopAdapter.filter.filter(newText)
                    return false
                }
            }
        )
        return root
    }
}
