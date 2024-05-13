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

class SuspensionAndBladesViewModel (
    private val firestore: FirebaseFirestore,
    private val category: Category
): ViewModel() {

    private val _suspensions = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val suspensions = _suspensions.asStateFlow()

    private val _wipers = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val wipers = _wipers.asStateFlow()

    private val pagingInfo = MainCategoryViewModel.PagingInfo()

    init {
        fetchSuspensions()
        fetchWipers()
    }

    private fun fetchSuspensions() {
        if(!pagingInfo.isPagingEnd){
            viewModelScope.launch {
                _suspensions.emit(Resource.Loading())
            }

            firestore.collection("products").whereEqualTo("category","Suspensions")
                .limit(pagingInfo.newItemsPage * 10).get()
                .addOnSuccessListener { result ->
                    val suspensions = result.toObjects(Product::class.java)
                    pagingInfo.isPagingEnd = suspensions == pagingInfo.oldItems
                    pagingInfo.oldItems = suspensions
                    viewModelScope.launch {
                        _suspensions.emit(Resource.Success(suspensions))
                    }
                    pagingInfo.newItemsPage++
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _suspensions.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }

    private fun fetchWipers() {
        if (!pagingInfo.isPagingEnd){
            viewModelScope.launch {
                _suspensions.emit(Resource.Loading())
            }

            firestore.collection("products").whereEqualTo("category","Wipers")
                .limit(pagingInfo.newItemsPage * 10).get()
                .addOnSuccessListener { result->
                    val wipers = result.toObjects(Product::class.java)
                    pagingInfo.isPagingEnd = wipers == pagingInfo.oldItems
                    pagingInfo.oldItems = wipers
                    viewModelScope.launch {
                        _wipers.emit(Resource.Success(wipers))
                    }
                    pagingInfo.newItemsPage++
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _wipers.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }
}