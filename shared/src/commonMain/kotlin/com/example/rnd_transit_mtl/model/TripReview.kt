package com.example.rnd_transit_mtl.model

import kotlinx.serialization.Serializable

/**
 * Saved feedback for a completed trip.
 *
 * Overall is required. Optional ratings remain null when unselected.
 * An empty comment means that no written feedback was supplied.
 *
 * An unfinished review draft must use a separate draft model because
 * its overall rating may still be unselected.
 */
@Serializable
data class TripReview(
    val overall: Int,
    val quality: Int? = null,
    val interesting: Int? = null,
    val fun: Int? = null,
    val comment: String = ""
) {
    init {
        require(overall in 1..5) {
            "Overall rating must be between 1 and 5."
        }
        require(quality == null || quality in 1..5) {
            "Quality rating must be null or between 1 and 5."
        }
        require(interesting == null || interesting in 1..5) {
            "Interesting rating must be null or between 1 and 5."
        }
        require(fun == null || fun in 1..5) {
            "Fun rating must be null or between 1 and 5."
        }
    }
}