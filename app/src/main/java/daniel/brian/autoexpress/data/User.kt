package daniel.brian.autoexpress.data

data class User (
    val username: String,
    val email: String
){
    // Firebase uses this constructor
    constructor() : this ("","")
}