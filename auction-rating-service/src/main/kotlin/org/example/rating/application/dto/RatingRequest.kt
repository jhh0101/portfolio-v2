package org.example.rating.application.dto

data class RatingRequest(
    val score: Int,
    val comment: String?,
) {

}
