package daniel.brian.autoexpress.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import daniel.brian.autoexpress.data.Order
import daniel.brian.autoexpress.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class OrderViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
): ViewModel(){

    private val _order = MutableStateFlow<Resource<Order>>(Resource.Unspecified())
    val order = _order.asStateFlow()

    fun placeOrder(order: Order){
        // we're creating an order collection to store order of a specific user
        viewModelScope.launch {
            _order.emit(Resource.Loading())
        }

        // batch - read only
        // transaction - read and write
        firestore.runBatch {
            // Add the order into the user-order collection
            firestore.collection("user").document(auth.uid!!).collection("orders")
                .document().set(order)
            // Add the order into orders collection
            firestore.collection("orders").document().set(order)
            // Delete the products from user-cart collection. we're not allowed to delete a whole collection
            // so we will use a loop to delete the document
            firestore.collection("user").document(auth.uid!!).collection("cart").get()
                .addOnSuccessListener {snapshot ->
                    snapshot.documents.forEach {
                        it.reference.delete()
                    }
                }.addOnSuccessListener {
                    viewModelScope.launch {
                        _order.emit(Resource.Success(order))
                    }
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _order.emit(Resource.Error(it.message.toString()))
                    }
                }

        }
    }
}