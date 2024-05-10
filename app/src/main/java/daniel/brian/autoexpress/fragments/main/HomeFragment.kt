package daniel.brian.autoexpress.fragments.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import daniel.brian.autoexpress.adapters.HomeViewPagerAdapter
import daniel.brian.autoexpress.databinding.FragmentHomeBinding
import daniel.brian.autoexpress.fragments.categories.AccessoriesAndServicesFragment
import daniel.brian.autoexpress.fragments.categories.BatteriesAndBrakesFragment
import daniel.brian.autoexpress.fragments.categories.MainCategoryFragment
import daniel.brian.autoexpress.fragments.categories.OilServiceAndTyresFragment
import daniel.brian.autoexpress.fragments.categories.SuspensionAndWipersFragment

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       val categoriesFragments = arrayListOf(
           MainCategoryFragment(),
           AccessoriesAndServicesFragment(),
           BatteriesAndBrakesFragment(),
           OilServiceAndTyresFragment(),
           SuspensionAndWipersFragment()
       )

        //preventing tab sliding
        binding.viewPagerHome.isUserInputEnabled = false

        val viewPagerAdapter  =
            HomeViewPagerAdapter(categoriesFragments, childFragmentManager, lifecycle)
        binding.viewPagerHome.adapter = viewPagerAdapter
        TabLayoutMediator(binding.tableLayout, binding.viewPagerHome){ tab, position ->
            when(position){
                0 -> tab.text = "Main Category"
                1 -> tab.text = "Accessories & Services"
                2 -> tab.text = "Batteries & Brakes"
                3 -> tab.text = "Oil Service & Tyres"
                4 -> tab.text = "Suspension & Wipers"
            }
        }.attach()
    }

}