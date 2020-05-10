package com.hmisdoctor.ui.emr_workflow.lab.ui

import com.hmisdoctor.ui.emr_workflow.model.favourite.FavouritesModel

interface OnItemFavListener {
    fun onItemFavClick(
        favmodel: FavouritesModel?,
        position: Int,
        selected: Boolean
    )

}