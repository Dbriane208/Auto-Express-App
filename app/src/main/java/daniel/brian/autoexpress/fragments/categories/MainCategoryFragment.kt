@file:Suppress("DEPRECATION")

package daniel.brian.autoexpress.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import daniel.brian.autoexpress.adapters.BestDealAdapter
import daniel.brian.autoexpress.adapters.PopularProductAdapter
import daniel.brian.autoexpress.databinding.FragmentMainCategoryBinding
import daniel.brian.autoexpress.utils.Resource
import daniel.brian.autoexpress.viewmodel.MainCategoryViewModel
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MainCategoryFragment: Fragment() {
    private lateinit var binding: FragmentMainCategoryBinding
    private lateinit var bestDealAdapter: BestDealAdapter
    private lateinit var popularProductAdapter: PopularProductAdapter
    private val viewModel by viewModels<MainCategoryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainCategoryBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //preparing the rvs
        setUpBestDealsRV()
        setUpPopularProductsRV()

        lifecycleScope.launchWhenStarted {
            viewModel.bestDeals.collectLatest {
                when(it){
                    is Resource.Error -> {
                        binding.bestDealsProgressBar.visibility = View.GONE
                        Snackbar.make(requireView(),it.message.toString(),Snackbar.LENGTH_LONG).show()
                    }
                    is Resource.Loading -> {
                        binding.bestDealsProgressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        bestDealAdapter.differ.submitList(it.data)
                        binding.bestDealsProgressBar.visibility = View.GONE
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.popularProducts.collectLatest {
                when(it){
                    is Resource.Error -> {
                        binding.popularDealsProgressBar.visibility = View.GONE
                        Snackbar.make(requireView(),it.message.toString(),Snackbar.LENGTH_LONG).show()
                    }
                    is Resource.Loading -> {
                        binding.popularDealsProgressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        popularProductAdapter.differ.submitList(it.data)
                        binding.popularDealsProgressBar.visibility = View.GONE
                    }
                    else -> Unit
                }
            }
        }

        // detecting we're at the end of the nested scroll view
        binding.nestedScrollViewMainCategory.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener{v,_,scrollY,_,_ ->
                if (v.getChildAt(0).bottom <= v.height + scrollY){
                    viewModel.fetchPopularProducts()
                }
            }
        )
    }
    private fun setUpBestDealsRV() {
        bestDealAdapter = BestDealAdapter()
        binding.bestDeals.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter = bestDealAdapter
        }
    }

    private fun setUpPopularProductsRV() {
        popularProductAdapter = PopularProductAdapter()
        binding.popularDeals.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter = popularProductAdapter
        }
    }


}