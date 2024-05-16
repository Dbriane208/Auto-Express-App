package daniel.brian.autoexpress.utils

fun Float?.getProductPrice(price: Float): Float{
    if(this == null)
        return price
    val remainingPrice = 1f - this
    return remainingPrice * price
}