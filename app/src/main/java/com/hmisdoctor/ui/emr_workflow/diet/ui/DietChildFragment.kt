package com.hmisdoctor.ui.emr_workflow.diet.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.hmisdoctor.R
import com.hmisdoctor.databinding.FragmentDietChildBinding
import com.hmisdoctor.ui.emr_workflow.diet.view_model.DietViewModel
import com.hmisdoctor.ui.emr_workflow.diet.view_model.DietViewModelFactory

import com.hmisdoctor.utils.Utils


class DietChildFragment :Fragment(){


    private var binding: FragmentDietChildBinding? = null
    private var viewModel: DietViewModel? = null
    private var utils: Utils? = null

    var dietAdapter:DietAdapter ? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_diet_child,
                container,
                false
            )


        viewModel = DietViewModelFactory(
            requireActivity().application
        )
            .create(DietViewModel::class.java)
        binding?.viewModel = viewModel
        binding!!.lifecycleOwner = this

        utils = Utils(requireContext())

        viewModel!!.errorText.observe(viewLifecycleOwner,
            Observer { toastMessage ->
                utils!!.showToast(R.color.negativeToast, binding?.mainLayout!!, toastMessage)
            })

        binding?.favouriteDrawerCardView?.setOnClickListener {
            binding?.drawerLayout!!.openDrawer(GravityCompat.END)
        }

        binding?.drawerLayout?.drawerElevation = 0f
        binding?.drawerLayout?.setScrimColor(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.transparent
            )
        )

        setAdapter()
        setupViewPager(binding?.viewpager!!)
        binding?.tabs!!.setupWithViewPager(binding?.viewpager!!)
        return binding!!.root
    }


    private fun setAdapter() {
        dietAdapter = context?.let { DietAdapter(it) }
        binding?.savelabRecyclerView?.adapter = dietAdapter

    }


    private fun setupViewPager(viewPager: ViewPager) {

        val adapter = ViewPagerAdapter(childFragmentManager)
        adapter.addFragment(DietFavouriteFragment(), "  Favourite")
        adapter.addFragment(DietTempleteFragment(), "  Templete")
        adapter.addFragment(PrevDietFragment(), "Prev.Diet")
        viewPager.offscreenPageLimit = 3

        viewPager.adapter = adapter

    }


    internal inner class ViewPagerAdapter(manager: FragmentManager) :
        FragmentPagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        private val mFragmentList = java.util.ArrayList<Fragment>()
        private val mFragmentTitleList = java.util.ArrayList<String>()
        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }
    }
}