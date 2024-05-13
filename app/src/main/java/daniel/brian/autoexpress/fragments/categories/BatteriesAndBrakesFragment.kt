@file:Suppress("DEPRECATION")

package daniel.brian.autoexpress.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import daniel.brian.autoexpress.adapters.BatteriesAdapter
import daniel.brian.autoexpress.adapters.BrakesAdapter
import daniel.brian.autoexpress.data.Category
import daniel.brian.autoexpress.databinding.FragmentBrakeBatteriesBinding
import daniel.brian.autoexpress.utils.Resource
import daniel.brian.autoexpress.viewmodel.BrakesAndBatteriesViewModel
import daniel.brian.autoexpress.viewmodel.BrakesAndBatteriesViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class BatteriesAndBrakesFragment: Fragment() {
    @Inject
    lateinit var firestore: FirebaseFirestore
    private lateinit var binding: FragmentBrakeBatteriesBinding
    private lateinit var batteriesAdapter: BatteriesAdapter
    private lateinit var brakesAdapter: BrakesAdapter
    private val viewModel by viewModels<BrakesAndBatteriesViewModel> {
        BrakesAndBatteriesViewModelFactory(firestore,Category.BatteriesAndBrakes)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBrakeBatteriesBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //preparing the rvs
        setUpBatteriesRV()
        setUpBrakesRV()

        lifecycleScope.launchWhenStarted {
            viewModel.batteries.collectLatest {
                when(it){
                    is Resource.Error -> {
                        Snackbar.make(requireView(),it.message.toString(), Snackbar.LENGTH_LONG).show()
                        binding.batteriesProgressBar.visibility = View.GONE
                    }
                    is Resource.Loading -> {
                        binding.batteriesProgressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        batteriesAdapter.differ.submitList(it.data)
                        binding.batteriesProgressBar.visibility = View.GONE
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.brakes.collectLatest {
                when(it){
                    is Resource.Error -> {
                        Snackbar.make(requireView(),it.message.toString(), Snackbar.LENGTH_LONG).show()
                        binding.brakesProgressBar.visibility = View.GONE
                    }
                    is Resource.Loading -> {
                        binding.brakesProgressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        brakesAdapter.differ.submitList(it.data)
                        binding.brakesProgressBar.visibility = View.GONE
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun setUpBatteriesRV() {
        batteriesAdapter = BatteriesAdapter()
        binding.batteriesRv.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter = batteriesAdapter
        }
    }

    private fun setUpBrakesRV() {
        brakesAdapter = BrakesAdapter()
        binding.brakesRv.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter = brakesAdapter
        }
    }
}