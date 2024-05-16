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
import daniel.brian.autoexpress.adapters.OilAdapter
import daniel.brian.autoexpress.adapters.TyresAdapter
import daniel.brian.autoexpress.data.Category
import daniel.brian.autoexpress.databinding.FragmentTyresOilServiceBinding
import daniel.brian.autoexpress.utils.Resource
import daniel.brian.autoexpress.viewmodel.TyresAndOilViewModel
import daniel.brian.autoexpress.viewmodel.TyresAndOilViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@Suppress("DEPRECATION")
@AndroidEntryPoint
class OilServiceAndTyresFragment: Fragment() {
    @Inject
    lateinit var firestore: FirebaseFirestore
    private lateinit var binding: FragmentTyresOilServiceBinding
    private lateinit var oilAdapter: OilAdapter
    private lateinit var tyresAdapter: TyresAdapter
    private val viewModel by viewModels<TyresAndOilViewModel> {
        TyresAndOilViewModelFactory(firestore,Category.OilAndTyres)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTyresOilServiceBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // preparing the rvs
        setUpOilServiceRV()
        setUpTyresRV()

        oilAdapter.onClick = {
            val b = Bundle().apply { putParcelable("products",it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)
        }

        tyresAdapter.onClick = {
            val b = Bundle().apply { putParcelable("products",it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.oil.collectLatest {
                when(it){
                    is Resource.Error -> {
                        Snackbar.make(requireView(),it.message.toString(), Snackbar.LENGTH_LONG).show()
                        binding.oilProgressBar.visibility = View.GONE
                    }
                    is Resource.Loading -> {
                        binding.oilProgressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        oilAdapter.differ.submitList(it.data)
                        binding.oilProgressBar.visibility = View.GONE
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.tyres.collectLatest {
                when(it){
                    is Resource.Error -> {
                        Snackbar.make(requireView(),it.message.toString(), Snackbar.LENGTH_LONG).show()
                        binding.tyresProgressBar.visibility = View.GONE
                    }
                    is Resource.Loading -> {
                        binding.tyresProgressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                       tyresAdapter.differ.submitList(it.data)
                        binding.tyresProgressBar.visibility = View.GONE
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun setUpOilServiceRV() {
        oilAdapter = OilAdapter()
        binding.oilServicesRv.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter = oilAdapter
        }
    }

    private fun setUpTyresRV() {
         tyresAdapter = TyresAdapter()
        binding.tyresRv.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter = tyresAdapter
        }
    }
}