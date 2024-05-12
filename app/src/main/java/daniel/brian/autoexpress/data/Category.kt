package daniel.brian.autoexpress.data

sealed class Category(val category: String) {
    data object AccessoriesAndService: Category("Accessories & Services")
    data object BatteriesAndBrakes: Category("Batteries & Brakes")
    data object OilAndTyres: Category("Oil Service & Tyres")
    data object SuspensionAndBlades: Category("Suspension & Wipers")
}