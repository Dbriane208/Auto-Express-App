package daniel.brian.autoexpress.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import daniel.brian.autoexpress.data.CartProduct
import daniel.brian.autoexpress.databinding.BillingProductsRvItemBinding
import daniel.brian.autoexpress.utils.getProductPrice

class BillingProductsAdapter : RecyclerView.Adapter<BillingProductsAdapter.BillingProductViewHolder>() {

    inner class BillingProductViewHolder(private val binding: BillingProductsRvItemBinding): ViewHolder(binding.root) {
        fun bind(billingProduct: CartProduct) {
            binding.apply {
                Glide.with(itemView).load(billingProduct.product.images[0]).into(imageCartProduct)
                tvProductCartName.text = billingProduct.product.name
                tvBillingProductQuantity.text = billingProduct.quantity.toString()

                val priceAfterPercentage = billingProduct.product.offerPercentage.getProductPrice(billingProduct.product.price)
                "Ksh ${String.format("%.2f",priceAfterPercentage)}".also { tvProductCartPrice.text = it }
            }
        }
    }

    val diffUtil = object : DiffUtil.ItemCallback<CartProduct>(){
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.product == newItem.product
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
           return oldItem == newItem
        }

    }
    
    val differ = AsyncListDiffer(this,diffUtil)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BillingProductViewHolder {
        return BillingProductViewHolder(
            BillingProductsRvItemBinding.inflate(
                LayoutInflater.from(parent.context),parent,false
            )
        )
    }

    override fun onBindViewHolder(
        holder: BillingProductViewHolder,
        position: Int
    ) {
        val billingProduct = differ.currentList[position]
        holder.bind(billingProduct)
    }

    override fun getItemCount(): Int {
       return differ.currentList.size
    }
}