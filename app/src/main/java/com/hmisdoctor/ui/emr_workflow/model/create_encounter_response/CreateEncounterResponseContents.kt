package com.hmisdoctor.ui.emr_workflow.model.create_encounter_response

data class CreateEncounterResponseContents(
    val encounter: Encounter? = null,
    val encounterDoctor: EncounterDoctor? = null
)