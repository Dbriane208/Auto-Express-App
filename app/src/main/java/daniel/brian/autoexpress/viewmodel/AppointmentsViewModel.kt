package daniel.brian.autoexpress.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import daniel.brian.autoexpress.data.Appointment
import daniel.brian.autoexpress.utils.Resource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppointmentsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
): ViewModel() {

    private val _batteryAppointment = MutableStateFlow<Resource<Appointment>>(Resource.Unspecified())
    val batteryAppointment = _batteryAppointment.asStateFlow()

    private val _tyreAppointment = MutableStateFlow<Resource<Appointment>>(Resource.Unspecified())
    val tyreAppointment = _tyreAppointment.asStateFlow()

    private val _oilAppointment = MutableStateFlow<Resource<Appointment>>(Resource.Unspecified())
    val oilAppointment = _oilAppointment.asStateFlow()

    private val _brakeAppointment = MutableStateFlow<Resource<Appointment>>(Resource.Unspecified())
    val brakeAppointment = _brakeAppointment.asStateFlow()

    private val _suspensionAppointment = MutableStateFlow<Resource<Appointment>>(Resource.Unspecified())
    val suspensionAppointment = _suspensionAppointment.asStateFlow()

    private val _othersAppointment = MutableStateFlow<Resource<Appointment>>(Resource.Unspecified())
    val othersAppointment = _othersAppointment.asStateFlow()

    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()

    fun getBatteryAppointment(appointment: Appointment) {
        val validateInputs = validateInputs(appointment)
        if(validateInputs){
            viewModelScope.launch {_batteryAppointment.emit(Resource.Loading())}
            firestore.collection("user").document(auth.uid!!).collection("appointments").document()
                .set(appointment).addOnSuccessListener {
                    viewModelScope.launch {_batteryAppointment.emit(Resource.Success(appointment))}
                }.addOnFailureListener {
                    viewModelScope.launch {_batteryAppointment.emit(Resource.Error(it.message.toString()))}
                }
        }else{
            viewModelScope.launch {_error.emit("Please fill all fields")}
        }
    }

    fun getTyreAppointment(appointment: Appointment) {
        val validateInputs = validateInputs(appointment)
        if (validateInputs) {
            viewModelScope.launch { _tyreAppointment.emit(Resource.Loading()) }
            firestore.collection("user").document(auth.uid!!).collection("appointments").document()
                .set(appointment).addOnSuccessListener {
                    viewModelScope.launch { _tyreAppointment.emit(Resource.Success(appointment)) }
                }.addOnFailureListener {
                    viewModelScope.launch { _tyreAppointment.emit(Resource.Error(it.message.toString())) }
                }
        }else{
            viewModelScope.launch {_error.emit("Please fill all fields")}
        }
    }

    fun getOilAppointment(appointment: Appointment) {
        val validateInputs = validateInputs(appointment)
        if(validateInputs){
            viewModelScope.launch {_oilAppointment.emit(Resource.Loading())}
            firestore.collection("user").document(auth.uid!!).collection("appointments").document()
                .set(appointment).addOnSuccessListener {
                    viewModelScope.launch {_oilAppointment.emit(Resource.Success(appointment))}
                }.addOnFailureListener {
                    viewModelScope.launch {_oilAppointment.emit(Resource.Error(it.message.toString()))}
                }
        }else{
            viewModelScope.launch {_error.emit("Please fill all fields")}
        }
    }

    fun getBrakeAppointment(appointment: Appointment) {
        val validateInputs = validateInputs(appointment)
        if(validateInputs){
            viewModelScope.launch {_brakeAppointment.emit(Resource.Loading())}
            firestore.collection("user").document(auth.uid!!).collection("appointments").document()
                .set(appointment).addOnSuccessListener {
                    viewModelScope.launch {_brakeAppointment.emit(Resource.Success(appointment))}
                }.addOnFailureListener {
                    viewModelScope.launch {_brakeAppointment.emit(Resource.Error(it.message.toString()))}
                }
        }else{
            viewModelScope.launch {_error.emit("Please fill all fields")}
        }
    }

    fun getSuspensionAppointment(appointment: Appointment) {
        val validateInputs = validateInputs(appointment)
        if(validateInputs){
            viewModelScope.launch {_suspensionAppointment.emit(Resource.Loading())}
            firestore.collection("user").document(auth.uid!!).collection("appointments").document()
                .set(appointment).addOnSuccessListener {
                    viewModelScope.launch {_suspensionAppointment.emit(Resource.Success(appointment))}
                }.addOnFailureListener {
                    viewModelScope.launch {_suspensionAppointment.emit(Resource.Error(it.message.toString()))}
                }
        }else{
            viewModelScope.launch {_error.emit("Please fill all fields")}
        }
    }

    fun getOthersAppointment(appointment: Appointment) {
        val validateInputs = validateInputs(appointment)
        if(validateInputs){
            viewModelScope.launch {_othersAppointment.emit(Resource.Loading())}
            firestore.collection("user").document(auth.uid!!).collection("appointments").document()
                .set(appointment).addOnSuccessListener {
                    viewModelScope.launch {_othersAppointment.emit(Resource.Success(appointment))}
                }.addOnFailureListener {
                    viewModelScope.launch {_othersAppointment.emit(Resource.Error(it.message.toString()))}
                }
        }else{
            viewModelScope.launch {_error.emit("Please fill all fields")}
        }
    }

    private fun validateInputs(appointment: Appointment): Boolean {
        return appointment.service.trim().isNotEmpty() &&
                appointment.date.trim().isNotEmpty() &&
                appointment.time.trim().isNotEmpty() &&
                appointment.name.trim().isNotEmpty() &&
                appointment.branch.trim().isNotEmpty() &&
                appointment.phone.trim().isNotEmpty() &&
                appointment.carModel.trim().isNotEmpty() &&
                appointment.carReg.trim().isNotEmpty()
    }


}