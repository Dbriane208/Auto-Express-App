package daniel.brian.autoexpress.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import daniel.brian.autoexpress.R
import daniel.brian.autoexpress.adapters.AccessoriesAdapter
import daniel.brian.autoexpress.adapters.ServicesAdapter
import daniel.brian.autoexpress.data.Category
import daniel.brian.autoexpress.databinding.FragmentAccessoriesServicesBinding
import daniel.brian.autoexpress.utils.Resource
import daniel.brian.autoexpress.viewmodel.AccessoriesAndServicesViewModel
import daniel.brian.autoexpress.viewmodel.AccessoriesAndServicesViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@Suppress("DEPRECATION")
@AndroidEntryPoint
class AccessoriesAndServicesFragment : Fragment(){
    @Inject
    lateinit var firestore: FirebaseFirestore
    private lateinit var binding: FragmentAccessoriesServicesBinding
    private lateinit var accessoriesAdapter: AccessoriesAdapter
    private lateinit var servicesAdapter: ServicesAdapter
    private val viewModel by viewModels<AccessoriesAndServicesViewModel>{
        AccessoriesAndServicesViewModelFactory(firestore,Category.AccessoriesAndService)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccessoriesServicesBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //preparing the rvs
        setUpAccessoriesRV()
        setUpServicesRV()

        accessoriesAdapter.onClick = {
            val b  = Bundle().apply {putParcelable("products",it)}
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)
        }

        servicesAdapter.onClick = {
            val b  = Bundle().apply {putParcelable("products",it)}
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.accessories.collectLatest {
                when(it){
                    is Resource.Error -> {
                     Snackbar.make(requireView(),it.message.toString(), Snackbar.LENGTH_LONG).show()
                        binding.accessoriesProgressBar.visibility = View.GONE
                    }
                    is Resource.Loading -> {
                        binding.accessoriesProgressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        accessoriesAdapter.differ.submitList(it.data)
                        binding.accessoriesProgressBar.visibility = View.GONE
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.services.collectLatest {
                when(it){
                    is Resource.Error -> {
                        Snackbar.make(requireView(),it.message.toString(), Snackbar.LENGTH_LONG).show()
                        binding.servicesProgressBar.visibility = View.GONE
                    }
                    is Resource.Loading -> {
                        binding.servicesProgressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        servicesAdapter.differ.submitList(it.data)
                        binding.servicesProgressBar.visibility = View.GONE
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun setUpAccessoriesRV() {
        accessoriesAdapter = AccessoriesAdapter()
        binding.accessoriesRv.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter = accessoriesAdapter
        }
    }

    private fun setUpServicesRV() {
        servicesAdapter = ServicesAdapter()
        binding.servicesRv.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter = servicesAdapter
        }
    }
}