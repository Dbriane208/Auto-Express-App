@file:Suppress("DEPRECATION")

package daniel.brian.autoexpress.fragments.main

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import daniel.brian.autoexpress.R
import daniel.brian.autoexpress.adapters.CartAdapter
import daniel.brian.autoexpress.databinding.FragmentCartBinding
import daniel.brian.autoexpress.firebase.FirebaseCommon
import daniel.brian.autoexpress.utils.Resource
import daniel.brian.autoexpress.utils.VerticalItemDecoration
import daniel.brian.autoexpress.viewmodel.CartViewModel
import kotlinx.coroutines.flow.collectLatest

class CartFragment : Fragment() {
    private lateinit var binding: FragmentCartBinding
    private val cartAdapter by lazy { CartAdapter() }
    private val viewModel by activityViewModels<CartViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCartBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setUpCartRV()

        // collecting the price from the flow to update our textview price
        var totalPrice = 0f
        lifecycleScope.launchWhenStarted {
            viewModel.productsPrice.collectLatest { price ->
                price?.let {
                    totalPrice = it
                    "$ $price".also { binding.tvTotalPrice.text = it }
                }
            }
        }

        // navigating to the details fragment from the cart fragment once the product is clicked
        cartAdapter.onProductClick = {
            val b = Bundle().apply { putParcelable("products", it.product) }
            findNavController().navigate(R.id.action_cartFragment_to_productDetailsFragment, b)
        }

        // implementing the add and remove form cart product item
        cartAdapter.onPlusClick = {
            viewModel.changeQuantity(it, FirebaseCommon.QuantityChanging.INCREASE)
        }

        cartAdapter.onMinusClick = {
            viewModel.changeQuantity(it, FirebaseCommon.QuantityChanging.DECREASE)
        }

        binding.imageCloseCart.setOnClickListener {
            findNavController().navigateUp()
        }

        lifecycleScope.launchWhenStarted {
            viewModel.cartProducts.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressbarCart.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressbarCart.visibility = View.INVISIBLE
                        if (it.data!!.isEmpty())
                        {
                            showEmptyCart()
                            // hiding other views when we have empty data
                            hideOtherViews()
                        } else {
                            hideEmptyCart()
                            // showing the views when we have some data
                            showOtherViews()
                            cartAdapter.differ.submitList(it.data)
                        }
                    }
                    is Resource.Error -> {
                        binding.progressbarCart.visibility = View.INVISIBLE
                        // Change this to ensure that when the phone is rotated
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }
        // collecting the dialog state
        lifecycleScope.launchWhenStarted {
            viewModel.deleteDialog.collectLatest {
                val alertDialog =
                    AlertDialog.Builder(requireContext()).apply {
                        setTitle("Delete item from cart")
                        setMessage("Do you want to delete this item from your cart?")
                        setNegativeButton("Cancel") { dialog, _ ->
                            dialog.dismiss()
                        }
                        setPositiveButton("Yes") { dialog, _ ->
                            viewModel.deleteCartProducts(it)
                            dialog.dismiss()
                        }
                    }
                alertDialog.create()
                alertDialog.show()
            }
        }
    }

    private fun showOtherViews() {
        binding.apply {
            rvCart.visibility = View.VISIBLE
            totalBoxContainer.visibility = View.VISIBLE
            buttonCheckout.visibility = View.VISIBLE
        }
    }

    private fun hideOtherViews() {
        binding.apply {
            rvCart.visibility = View.GONE
            totalBoxContainer.visibility = View.GONE
            buttonCheckout.visibility = View.GONE
        }
    }

    private fun hideEmptyCart() {
        binding.apply {
            layoutCarEmpty.visibility = View.GONE
        }
    }

    private fun showEmptyCart() {
        binding.apply {
            layoutCarEmpty.visibility = View.VISIBLE
        }
    }

    private fun setUpCartRV() {
        binding.rvCart.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = cartAdapter
            // to add some space in the cart product recyclerview
            addItemDecoration(VerticalItemDecoration())
        }
    }
}
