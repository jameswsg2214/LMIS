package com.hmisdoctor.ui.dashboard.view

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.hmisdoctor.ui.dashboard.model.ChiefComplients
import com.hmisdoctor.ui.dashboard.model.Diagnosis

class PatientsPagerAdapter(
    fm: FragmentManager,
    var diagnosisData: ArrayList<Diagnosis>,
    var chiefComplaintsData: ArrayList<ChiefComplients>
) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 ->
                FragmentOne.newInstance(diagnosisData)

            else ->  FragmentTwo.newInstance(chiefComplaintsData)
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "Top Diagnosis"
            else  -> "Top Complaints"
        }
    }

    fun updateAdapter(diagnosisData: ArrayList<Diagnosis>,
                       chiefComplaintsData: ArrayList<ChiefComplients>){
        this.diagnosisData = diagnosisData
        this.chiefComplaintsData = chiefComplaintsData
    }
}