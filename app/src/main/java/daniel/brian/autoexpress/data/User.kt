package daniel.brian.autoexpress.data

data class User (
    val username: String,
    val email: String,
    val imagePath: String = ""
){
    // Firebase uses this constructor
    constructor() : this ("","")
}