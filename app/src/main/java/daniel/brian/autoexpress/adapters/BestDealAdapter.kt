package daniel.brian.autoexpress.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import daniel.brian.autoexpress.data.Product
import daniel.brian.autoexpress.databinding.BestDealsRvBinding

class BestDealAdapter: RecyclerView.Adapter<BestDealAdapter.BestDealViewHolder>() {

    inner class BestDealViewHolder(private val binding: BestDealsRvBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.apply {
                bestBrandName.text = product.brand
                Glide.with(itemView).load(product.images[0]).into(bestDealImage)
                bestProductName.text = product.name
                bestProductPrice.text = product.price.toString()
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BestDealViewHolder {
        return BestDealViewHolder(
            BestDealsRvBinding.inflate(
                LayoutInflater.from(parent.context),parent,false
            )
        )
    }

    private val diffUtil = object : DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this,diffUtil)

    override fun onBindViewHolder(holder: BestDealViewHolder, position: Int) {
        val bestDeals = differ.currentList[position]
        holder.bind(bestDeals)

        holder.itemView.setOnClickListener {
            onClick?.invoke(bestDeals)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onClick: ((Product) -> Unit) ?= null
}