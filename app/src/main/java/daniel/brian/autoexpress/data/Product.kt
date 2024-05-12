package daniel.brian.autoexpress.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class Product(
    val id: String,
    val brandName: String,
    val productName: String,
    val category: String,
    val price: Float,
    val offerPercentage: Float? = null,
    val description: String? = null,
    val images: List<String>
): Parcelable{
    constructor() : this("","","","",0f, images = emptyList())
}