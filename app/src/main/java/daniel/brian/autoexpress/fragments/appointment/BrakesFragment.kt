package daniel.brian.autoexpress.fragments.appointment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import daniel.brian.autoexpress.R
import daniel.brian.autoexpress.databinding.FragmentBrakesBinding

class BrakesFragment : Fragment() {
    private lateinit var binding: FragmentBrakesBinding

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

        val brakes = resources.getStringArray(R.array.brakeServices)
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, brakes)
        binding.brakesService.setAdapter(adapter)

        val branches = resources.getStringArray(R.array.branches)
        val brakesAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, branches)
        binding.brakesBranch.setAdapter(brakesAdapter)
    }

}