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

class TyresAndOilViewModel(
    private val firestore: FirebaseFirestore,
    private val category: Category
): ViewModel() {

    private val _tyres = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val tyres = _tyres.asStateFlow()

    private val _oil = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val oil = _oil.asStateFlow()

    private val pagingInfo = MainCategoryViewModel.PagingInfo()

    init {
        fetchTyres()
        fetchOils()
    }

    private fun fetchTyres() {
        if (!pagingInfo.isPagingEnd){
            viewModelScope.launch {
                _tyres.emit(Resource.Loading())
            }

            firestore.collection("products").whereEqualTo("category","Tyres")
                .limit(pagingInfo.newItemsPage * 10).get()
                .addOnSuccessListener { result->
                    val tyres = result.toObjects(Product::class.java)
                    pagingInfo.isPagingEnd = tyres == pagingInfo.oldItems
                    pagingInfo.oldItems = tyres
                    viewModelScope.launch {
                        _tyres.emit(Resource.Success(tyres))
                    }
                    pagingInfo.newItemsPage++
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _tyres.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }

    private fun fetchOils() {
        if (!pagingInfo.isPagingEnd){
            viewModelScope.launch {
                _oil.emit(Resource.Loading())
            }

            firestore.collection("products").whereEqualTo("category","Oils")
                .limit(pagingInfo.newItemsPage * 10).get()
                .addOnSuccessListener { result->
                    val oil = result.toObjects(Product::class.java)
                    pagingInfo.isPagingEnd = oil == pagingInfo.oldItems
                    pagingInfo.oldItems = oil
                    viewModelScope.launch {
                        _oil.emit(Resource.Success(oil))
                    }
                    pagingInfo.newItemsPage++
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _oil.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }

}