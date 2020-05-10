package com.hmisdoctor.ui.emr_workflow.model.create_encounter_request

data class CreateEncounterRequestModel(
    var encounter: Encounter? = null,
    var encounterDoctor: EncounterDoctor? = null
)