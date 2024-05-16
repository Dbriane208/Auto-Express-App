@file:Suppress("DEPRECATION")

package daniel.brian.autoexpress.fragments.appointment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import daniel.brian.autoexpress.R
import daniel.brian.autoexpress.data.Appointment
import daniel.brian.autoexpress.databinding.FragmentBrakesBinding
import daniel.brian.autoexpress.utils.Resource
import daniel.brian.autoexpress.viewmodel.AppointmentsViewModel
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class BrakesFragment : Fragment() {
    private lateinit var binding: FragmentBrakesBinding
    private val viewModel by viewModels<AppointmentsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBrakesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //setting up the list adapter
        setUpListArrayAdapter()

       binding.apply {
           brakesBook.setOnClickListener {
               val service = brakesService.text.toString()
               val branch = brakesBranch.text.toString()
               val time = timeBrakes.text.toString()
               val date = dateBrakes.text.toString()
               val name = firstNameB.text.toString()
               val phone = brakesPhone.text.toString()
               val carReg = carRegBrakes.text.toString()
               val carModel = carModelBrakes.text.toString()

               val appointment = Appointment(service, branch, time, date, name, phone, carReg, carModel)
               viewModel.getBrakeAppointment(appointment)
           }
       }

        lifecycleScope.launchWhenStarted {
            viewModel.brakeAppointment.collectLatest {
                when(it){
                    is Resource.Error -> {
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG).show()
                    }
                    is Resource.Loading -> {
                        binding.brakesBook.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.brakesBook.visibility = View.GONE
                        Snackbar.make(requireView(), "Brakes Appointment Booked Successfully", Snackbar.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.error.collectLatest {
                Snackbar.make(requireView(), it, Snackbar.LENGTH_LONG).show()
            }
        }

    }

    private fun setUpListArrayAdapter() {
        val brakes = resources.getStringArray(R.array.brakeServices)
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, brakes)
        binding.brakesService.setAdapter(adapter)

        val branches = resources.getStringArray(R.array.branches)
        val brakesAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, branches)
        binding.brakesBranch.setAdapter(brakesAdapter)

        val time = resources.getStringArray(R.array.time)
        val timeAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, time)
        binding.timeBrakes.setAdapter(timeAdapter)
    }

}