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
import daniel.brian.autoexpress.databinding.FragmentTyresBinding
import daniel.brian.autoexpress.utils.Resource
import daniel.brian.autoexpress.viewmodel.AppointmentsViewModel
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class TyresFragment : Fragment() {
    private lateinit var binding: FragmentTyresBinding
    private val viewModel by viewModels<AppointmentsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTyresBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //setting up the list adapter
        setUpListArrayAdapter()

        binding.apply {
            tyresBook.setOnClickListener {
                val service = tyreService.text.toString()
                val branch = tyreBranch.text.toString()
                val time = tyreTime.text.toString()
                val name = firstNameT.text.toString()
                val date = dateTyres.text.toString()
                val phone = tyrePhone.text.toString()
                val carReg = carRegTyres.text.toString()
                val carModel = carModelTyres.text.toString()

                val appointment = Appointment(service, branch, name, phone, time, date, carReg, carModel)
                viewModel.getTyreAppointment(appointment)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.tyreAppointment.collectLatest {
                when(it){
                    is Resource.Error -> {
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG).show()
                    }
                    is Resource.Loading -> {
                        binding.tyresBook.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.tyresBook.visibility = View.GONE
                        Snackbar.make(requireView(),"Tyres Appointment Booked Successfully", Snackbar.LENGTH_LONG).show()
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
        val tyres = resources.getStringArray(R.array.tyreServices)
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, tyres)
        binding.tyreService.setAdapter(adapter)

        val branches = resources.getStringArray(R.array.branches)
        val branchAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, branches)
        binding.tyreBranch.setAdapter(branchAdapter)

        val time = resources.getStringArray(R.array.time)
        val timeAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, time)
        binding.tyreTime.setAdapter(timeAdapter)
    }

}