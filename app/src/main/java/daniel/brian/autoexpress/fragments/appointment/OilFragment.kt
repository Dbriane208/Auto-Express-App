package daniel.brian.autoexpress.fragments.appointment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import daniel.brian.autoexpress.R
import daniel.brian.autoexpress.databinding.FragmentOilBinding

class OilFragment : Fragment() {
    private lateinit var binding: FragmentOilBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentOilBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val oil = resources.getStringArray(R.array.oilServices)
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, oil)
        binding.oilService.setAdapter(adapter)

        val branches = resources.getStringArray(R.array.branches)
        val oilAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, branches)
        binding.oilBranch.setAdapter(oilAdapter)
    }

}