package id.smaraputra.storyapp.view.liststory

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import id.smaraputra.storyapp.R
import id.smaraputra.storyapp.data.local.datastore.LoginPreferences
import id.smaraputra.storyapp.data.local.datastore.PreferencesViewModel
import id.smaraputra.storyapp.data.local.datastore.PreferencesViewModelFactory
import id.smaraputra.storyapp.databinding.FragmentListStoryBinding
import id.smaraputra.storyapp.utils.wrapEspressoIdlingResource
import id.smaraputra.storyapp.view.addstory.AddStoryActivity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ListStoryFragment : Fragment() {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "userSession")
    private lateinit var preferencesViewModel: PreferencesViewModel
    private var _binding: FragmentListStoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var listStoryAdapter: ListStoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)
        setupView()
        setupViewModel()
        scrollTop()
    }

    private fun scrollTop(){
        getData()
        binding.rvUser.smoothScrollToPosition(0)
    }

    private val refreshListener = SwipeRefreshLayout.OnRefreshListener {
        binding.swipeRefreshLayout.isRefreshing = false
        scrollTop()
    }

    private fun setupView(){
        binding.swipeRefreshLayout.setOnRefreshListener(refreshListener)
        binding.fabAddStory.setOnClickListener {
            val intent = Intent(requireContext(), AddStoryActivity::class.java)
            requireActivity().startActivity(intent)
        }
        setupAdapter()
    }

    private fun setupAdapter(){
        listStoryAdapter = ListStoryAdapter(requireContext())
        binding.rvUser.adapter = listStoryAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                listStoryAdapter.retry()
            }
        )
        binding.rvUser.setHasFixedSize(true)
        binding.rvUser.layoutManager = LinearLayoutManager(requireContext())
        listStoryAdapter.setOnItemClickCallback(object : ListStoryAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Int) {
                binding.rvUser.smoothScrollToPosition(data)
            }
        })
    }

    private fun setupViewModel(){
        val pref = LoginPreferences.getInstance(requireContext().dataStore)
        preferencesViewModel = ViewModelProvider(this, PreferencesViewModelFactory(pref)).get(
            PreferencesViewModel::class.java
        )
        preferencesViewModel.getNameUser().observe(viewLifecycleOwner) {
            if(!it.isNullOrEmpty()){
                binding.nameUser.text=it.toString()
            }else{
                binding.nameUser.text=getString(R.string.no_data)
            }
        }
        getData()
    }

    private fun getData(){
        wrapEspressoIdlingResource {
            preferencesViewModel.getTokenUser().observe(viewLifecycleOwner) { token ->
                val factory: ViewModelFactory = ViewModelFactory.getInstance(requireContext(), token)
                val listViewModel: ListViewModel by viewModels {
                    factory
                }
                listViewModel.listStory().observe(viewLifecycleOwner) { result ->
                    if (result != null) {
                        lifecycle.coroutineScope.launch {
                            listStoryAdapter.loadStateFlow.collect {
                                if (it.prepend is LoadState.NotLoading && it.prepend.endOfPaginationReached) {
                                    if(listStoryAdapter.itemCount < 1){
                                        binding.noData.visibility = View.VISIBLE
                                    }
                                }else{
                                    binding.noData.visibility = View.GONE
                                }
                            }
                        }
                        listStoryAdapter.submitData(lifecycle, result)
                    }else{
                        binding.noData.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
}