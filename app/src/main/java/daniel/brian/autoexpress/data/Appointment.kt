package daniel.brian.autoexpress.data

data class Appointment(
    val service: String,
    val branch: String,
    val name: String,
    val phone: String,
    val time: String,
    val date: String,
    val carReg: String,
    val carModel: String
){
    constructor(): this( "","", "", "", "", "", "", "")
}
