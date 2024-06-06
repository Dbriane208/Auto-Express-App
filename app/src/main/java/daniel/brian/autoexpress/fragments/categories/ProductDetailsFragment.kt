@file:Suppress("DEPRECATION")

package daniel.brian.autoexpress.fragments.categories

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import daniel.brian.autoexpress.R
import daniel.brian.autoexpress.adapters.ViewPagerAdapter
import daniel.brian.autoexpress.data.CartProduct
import daniel.brian.autoexpress.databinding.FragmentProductDetailsBinding
import daniel.brian.autoexpress.utils.Resource
import daniel.brian.autoexpress.viewmodel.DetailsViewModel
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProductDetailsFragment : Fragment() {
    private val args by navArgs<ProductDetailsFragmentArgs>()
    private lateinit var binding: FragmentProductDetailsBinding
    private val viewPagerAdapter by lazy { ViewPagerAdapter() }
    private val viewModel by viewModels<DetailsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        // Inflate the layout for this fragment
        binding = FragmentProductDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = args.products

        setUpViewPager()

        binding.apply {
            productName.text = product.name
            itemPrice.text = product.price.toString()
            productDescription.text = product.description
        }

        binding.btnAddToCart.setOnClickListener {
            viewModel.addUpdateInCart(CartProduct(product, 1))
        }

        binding.imageClose.setOnClickListener {
            findNavController().navigateUp()
        }

        viewPagerAdapter.differ.submitList(product.images)

        lifecycleScope.launchWhenStarted {
            viewModel.addToCart.collectLatest {
                when(it){
                    is Resource.Error -> {
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG).show()
                    }
                    is Resource.Loading -> {
                        binding.btnAddToCart.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.btnAddToCart.revertAnimation()
                        binding.btnAddToCart.setBackgroundColor(resources.getColor(R.color.g_black))
                        Snackbar.make(requireView(), "Product Added Successfully to Cart!!", Snackbar.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun setUpViewPager() {
        binding.apply {
            viewPagerProductImages.adapter = viewPagerAdapter
        }
    }

}