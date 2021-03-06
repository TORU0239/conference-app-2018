package io.github.droidkaigi.confsched2018.presentation.search

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import io.github.droidkaigi.confsched2018.databinding.FragmentSearchSpeakersBinding
import io.github.droidkaigi.confsched2018.di.Injectable
import io.github.droidkaigi.confsched2018.presentation.NavigationController
import io.github.droidkaigi.confsched2018.presentation.Result
import io.github.droidkaigi.confsched2018.presentation.common.binding.FragmentDataBindingComponent
import io.github.droidkaigi.confsched2018.presentation.search.item.SpeakerItem
import io.github.droidkaigi.confsched2018.presentation.search.item.SpeakersSection
import io.github.droidkaigi.confsched2018.util.ext.observe
import timber.log.Timber
import javax.inject.Inject

class SearchSpeakersFragment : Fragment(), Injectable {
    private lateinit var binding: FragmentSearchSpeakersBinding
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject lateinit var navigationController: NavigationController
    private val searchSpeakersViewModel: SearchSpeakersViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(SearchSpeakersViewModel::class.java)
    }

    private val fragmentDataBindingComponent = FragmentDataBindingComponent(this)
    private val speakersSection = SpeakersSection(fragmentDataBindingComponent)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentSearchSpeakersBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        searchSpeakersViewModel.speakers.observe(this, { result ->
            when (result) {
                is Result.Success -> {
                    speakersSection.updateSpeakers(result.data)
                }
                is Result.Failure -> {
                    Timber.e(result.e)
                }
            }
        })
    }

    private fun setupRecyclerView() {
        val groupAdapter = GroupAdapter<ViewHolder>().apply {
            setOnItemClickListener { item, _ ->
                val speakerItem = item as? SpeakerItem ?: return@setOnItemClickListener
                navigationController.navigateToSpeakerDetailActivity(speakerItem.speaker.id)
            }
            add(speakersSection)
        }
        binding.searchSessionRecycler.apply {
            adapter = groupAdapter
        }
        binding.searchSessionRecycler.addItemDecoration(DividerItemDecoration(context,
                LinearLayoutManager(activity).orientation))
    }

    companion object {
        fun newInstance(): SearchSpeakersFragment = SearchSpeakersFragment()
    }
}
