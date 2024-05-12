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

class BrakesAndBatteriesViewModel(
    private val firestore: FirebaseFirestore,
    private val category: Category
): ViewModel() {

    private val _brakes = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val brakes = _brakes.asStateFlow()

    private val _batteries = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val batteries = _batteries.asStateFlow()

    private val pagingInfo = MainCategoryViewModel.PagingInfo()

     init {
         fetchBrakes()
         fetchBatteries()
     }

    private fun fetchBrakes() {
        if (!pagingInfo.isPagingEnd){
            viewModelScope.launch {
                _brakes.emit(Resource.Loading())
            }

            firestore.collection("products").whereEqualTo("category",category.category)
                .limit(pagingInfo.newItemsPage * 10).get()
                .addOnSuccessListener { result->
                    val brakes = result.toObjects(Product::class.java)
                    pagingInfo.isPagingEnd = brakes == pagingInfo.oldItems
                    pagingInfo.oldItems = brakes
                    viewModelScope.launch {
                        _brakes.emit(Resource.Success(brakes))
                    }
                    pagingInfo.newItemsPage++
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _brakes.emit(Resource.Error(it.message.toString()))
                    }
                }

        }
    }

    private fun fetchBatteries() {
        if (!pagingInfo.isPagingEnd){
            viewModelScope.launch {
                _batteries.emit(Resource.Loading())
            }
             firestore.collection("products").whereEqualTo("category",category.category)
                 .limit(pagingInfo.newItemsPage * 10).get()
                 .addOnSuccessListener { result->
                     val batteries = result.toObjects(Product::class.java)
                     pagingInfo.isPagingEnd = batteries == pagingInfo.oldItems
                     pagingInfo.oldItems = batteries
                     viewModelScope.launch {
                         _batteries.emit(Resource.Success(batteries))
                     }
                     pagingInfo.newItemsPage++
                 }.addOnFailureListener {
                     viewModelScope.launch {
                         _batteries.emit(Resource.Error(it.message.toString()))
                     }
                 }
        }
    }

}