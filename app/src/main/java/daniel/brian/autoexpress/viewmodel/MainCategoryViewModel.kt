package daniel.brian.autoexpress.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import daniel.brian.autoexpress.data.Product
import daniel.brian.autoexpress.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainCategoryViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
): ViewModel(){

    private val _bestDeals = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestDeals = _bestDeals.asStateFlow()

    private val _popularProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val popularProducts = _popularProducts.asStateFlow()

    private val pagingInfo = PagingInfo()
    data class PagingInfo(
        var newItemsPage: Long = 1,
        var oldItems: List<Product> = emptyList(),
        var isPagingEnd: Boolean = false
    )

    init {
        fetchBestDeals()
        fetchPopularProducts()
    }

     fun fetchBestDeals() {
        if (!pagingInfo.isPagingEnd){
            viewModelScope.launch {
                _bestDeals.emit(Resource.Loading())
            }

            firestore.collection("products").whereEqualTo("category","Best Deals")
                .limit(pagingInfo.newItemsPage * 5) // loading 5 items dynamically
                .get().addOnSuccessListener { result->
                    val bestDeals = result.toObjects(Product::class.java)
                    pagingInfo.isPagingEnd = bestDeals == pagingInfo.oldItems
                    pagingInfo.oldItems = bestDeals
                    viewModelScope.launch {
                        _bestDeals.emit(Resource.Success(bestDeals))
                    }
                    pagingInfo.newItemsPage++ // incrementing the old best deals products
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _bestDeals.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }

     fun fetchPopularProducts() {
        if (!pagingInfo.isPagingEnd){
            viewModelScope.launch {
                _bestDeals.emit(Resource.Loading())
            }

            firestore.collection("products").whereEqualTo("category","Popular Products")
                .limit(pagingInfo.newItemsPage * 5)
                .get().addOnSuccessListener { result ->
                    val popularProducts = result.toObjects(Product::class.java)
                    pagingInfo.isPagingEnd = popularProducts == pagingInfo.oldItems
                    pagingInfo.oldItems = popularProducts
                    viewModelScope.launch {
                        _popularProducts.emit(Resource.Success(popularProducts))
                    }
                    pagingInfo.newItemsPage++
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _bestDeals.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }

}