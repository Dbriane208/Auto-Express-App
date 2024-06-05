package daniel.brian.autoexpress.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    val totalPrice: Float,
    val products: List<CartProduct>
): Parcelable{
    constructor(): this(0f, products = emptyList())
}
