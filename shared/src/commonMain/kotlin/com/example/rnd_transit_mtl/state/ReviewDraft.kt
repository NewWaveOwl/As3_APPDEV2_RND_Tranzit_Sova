package com.example.rnd_transit_mtl.state

import kotlinx.serialization.Serializable

/**
 * Unsaved feedback for a completed trip.
 *
 * Overall may remain null while the user is editing. Saving requires
 * an overall rating. Optional unselected ratings remain null.
 *
 * The keyword fun must be escaped in Kotlin property references.
 */
@Serializable
data class ReviewDraft(
    val overall: Int? = null,
    val quality: Int? = null,
    val interesting: Int? = null,
    val `fun`: Int? = null,
    val comment: String = ""
) {
    init {
        require(overall == null || overall in 1..5) {
            "Overall rating must be null or between 1 and 5."
        }
        require(quality == null || quality in 1..5) {
            "Quality rating must be null or between 1 and 5."
        }
        require(interesting == null || interesting in 1..5) {
            "Interesting rating must be null or between 1 and 5."
        }
        require(`fun` == null || `fun` in 1..5) {
            "Fun rating must be null or between 1 and 5."
        }
    }
}