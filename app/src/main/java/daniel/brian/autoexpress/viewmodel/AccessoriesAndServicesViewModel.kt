package daniel.brian.autoexpress.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import daniel.brian.autoexpress.data.Category
import daniel.brian.autoexpress.data.Product
import daniel.brian.autoexpress.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AccessoriesAndServicesViewModel(
    private val firestore: FirebaseFirestore,
    private val category: Category
):ViewModel() {

    private val _accessories = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val accessories = _accessories.asStateFlow()

    private val _services = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val services = _services

    private val pagingInfo = MainCategoryViewModel.PagingInfo()

    init {
        fetchAccessories()
        fetchServices()
    }

    private fun fetchServices() {
        if (!pagingInfo.isPagingEnd){
            viewModelScope.launch {
                _services.emit(Resource.Loading())
            }

            firestore.collection("products").whereEqualTo("category","Services")
                .limit(pagingInfo.newItemsPage * 10).get()
                .addOnSuccessListener {result->
                    val services = result.toObjects(Product::class.java)
                    pagingInfo.isPagingEnd = services == pagingInfo.oldItems
                    pagingInfo.oldItems = services
                    viewModelScope.launch {
                        _services.emit(Resource.Success(services))
                    }
                    pagingInfo.newItemsPage++
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _services.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }

    private fun fetchAccessories() {
        if(!pagingInfo.isPagingEnd){
            viewModelScope.launch {
                _accessories.emit(Resource.Loading())
            }

            firestore.collection("products").whereEqualTo("category","Accessories")
                .limit(pagingInfo.newItemsPage * 10).get()
                .addOnSuccessListener { result->
                    val accessories = result.toObjects(Product::class.java)
                    pagingInfo.isPagingEnd = accessories == pagingInfo.oldItems
                    pagingInfo.oldItems = accessories
                    viewModelScope.launch {
                        _accessories.emit(Resource.Success(accessories))
                    }
                    pagingInfo.newItemsPage++
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _accessories.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }
}