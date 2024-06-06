package daniel.brian.autoexpress.fragments.categories

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import daniel.brian.autoexpress.adapters.BillingProductsAdapter
import daniel.brian.autoexpress.data.CartProduct
import daniel.brian.autoexpress.data.Order
import daniel.brian.autoexpress.databinding.FragmentBillingBinding
import daniel.brian.autoexpress.payments.MpesaPaymentActivity
import daniel.brian.autoexpress.utils.HorizontalItemDecoration
import daniel.brian.autoexpress.utils.Resource
import daniel.brian.autoexpress.viewmodel.OrderViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BillingFragment : Fragment() {
    private lateinit var binding: FragmentBillingBinding
    private val billingProductsAdapter by lazy { BillingProductsAdapter() }
    private val viewModel by viewModels<OrderViewModel>()

    // receiving price and products from the cartFragment
    private val args by navArgs<BillingFragmentArgs>()
    private  var products = emptyList<CartProduct>()
    private var totalPrice = 0f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBillingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        products = args.cartProducts.toList()
        totalPrice = args.totalPrice
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!args.payments){
            binding.apply {
                buttonPlaceOrder.visibility = View.INVISIBLE
                totalBoxContainer.visibility = View.INVISIBLE
                middleLine.visibility = View.INVISIBLE
                bottomLine.visibility = View.INVISIBLE
            }
        }

        binding.imageCloseBilling.setOnClickListener{
            findNavController().navigateUp()
        }


        lifecycleScope.launch {
            viewModel.order.collectLatest {
                when(it){
                    is Resource.Error -> {
                        binding.buttonPlaceOrder.revertAnimation()
                        Snackbar.make(requireView(), "Error ${it.message}", Snackbar.LENGTH_LONG).show()
                    }
                    is Resource.Loading -> {
                        binding.buttonPlaceOrder.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.buttonPlaceOrder.revertAnimation()
                        Snackbar.make(requireView(), "Products successfully purchased!", Snackbar.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        billingProductsAdapter.differ.submitList(products)
        "Ksh $totalPrice".also { binding.tvTotalPrice.text = it }

        binding.buttonPlaceOrder.setOnClickListener {
            val phoneNumber = binding.phoneNumber.text.toString()
            if(phoneNumber.isNotEmpty()){
                val intent = Intent(context,MpesaPaymentActivity::class.java)
                intent.putExtra("phone",phoneNumber)
                intent.putExtra("price",totalPrice.toInt())
                startActivity(intent)
                viewModel.clearCart()
            }else{
                Snackbar.make(requireView(), "Phone Number cannot be empty!", Snackbar.LENGTH_SHORT).show()
            }
        }

        // setting up the adapter
        setupBillingProductsRV()
    }

    private fun setupBillingProductsRV() {
        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(),RecyclerView.HORIZONTAL,false)
            adapter = billingProductsAdapter
            addItemDecoration(HorizontalItemDecoration())
        }
    }

}