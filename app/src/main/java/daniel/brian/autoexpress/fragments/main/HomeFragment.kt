package daniel.brian.autoexpress.fragments.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import daniel.brian.autoexpress.adapters.HomeViewPagerAdapter
import daniel.brian.autoexpress.databinding.FragmentHomeBinding
import daniel.brian.autoexpress.fragments.categories.AccessoriesAndServicesFragment
import daniel.brian.autoexpress.fragments.categories.BatteriesAndBrakesFragment
import daniel.brian.autoexpress.fragments.categories.MainCategoryFragment
import daniel.brian.autoexpress.fragments.categories.OilServiceAndTyresFragment
import daniel.brian.autoexpress.fragments.categories.SuspensionAndWipersFragment
import daniel.brian.autoexpress.viewmodel.ProfileViewModel
import javax.inject.Inject

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

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

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        firestore.collection("user").document(auth.uid!!)
            .addSnapshotListener { value, error ->
                if (error != null){
                    binding.txtUsername.text = error.message
                }else{
                    binding.txtUsername.text = value?.get("username").toString()
                }

            }

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