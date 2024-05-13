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
import daniel.brian.autoexpress.adapters.SuspensionAdapter
import daniel.brian.autoexpress.adapters.WiperBladesAdapter
import daniel.brian.autoexpress.data.Category
import daniel.brian.autoexpress.databinding.FragmentSuspensionWiperBladesBinding
import daniel.brian.autoexpress.utils.Resource
import daniel.brian.autoexpress.viewmodel.SuspensionAndBladesViewModel
import daniel.brian.autoexpress.viewmodel.SuspensionAndBladesViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class SuspensionAndWipersFragment: Fragment() {
    @Inject
    lateinit var firestore: FirebaseFirestore
    private lateinit var binding: FragmentSuspensionWiperBladesBinding
    private lateinit var suspensionAdapter: SuspensionAdapter
    private lateinit var wiperBladesAdapter: WiperBladesAdapter
    private val viewModel by viewModels<SuspensionAndBladesViewModel> {
        SuspensionAndBladesViewModelFactory(firestore,Category.SuspensionAndBlades)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSuspensionWiperBladesBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //preparing the rvs
        setUpSuspensionsRV()
        setUpBladeWipersRV()

        lifecycleScope.launchWhenStarted {
            viewModel.suspensions.collectLatest {
                when(it){
                    is Resource.Error -> {
                        Snackbar.make(requireView(),it.message.toString(), Snackbar.LENGTH_LONG).show()
                        binding.suspensionProgressBar.visibility = View.GONE
                    }
                    is Resource.Loading -> {
                        binding.suspensionProgressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        suspensionAdapter.differ.submitList(it.data)
                        binding.suspensionProgressBar.visibility = View.GONE
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.wipers.collectLatest {
                when(it){
                    is Resource.Error -> {
                        Snackbar.make(requireView(),it.message.toString(), Snackbar.LENGTH_LONG).show()
                        binding.wipersProgressBar.visibility = View.GONE
                    }
                    is Resource.Loading -> {
                        binding.wipersProgressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        wiperBladesAdapter.differ.submitList(it.data)
                        binding.wipersProgressBar.visibility = View.GONE
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun setUpSuspensionsRV() {
        suspensionAdapter = SuspensionAdapter()
        binding.suspensionRv.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter = suspensionAdapter
        }
    }

    private fun setUpBladeWipersRV() {
        wiperBladesAdapter = WiperBladesAdapter()
        binding.wipersRv.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter = wiperBladesAdapter
        }
    }
}