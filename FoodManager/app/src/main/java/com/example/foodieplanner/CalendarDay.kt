package com.example.foodieplanner
import com.google.firebase.database.Exclude

data class CalendarDay(
    var month: String? = null,
    var day: String? = null,
    var date: String? = null,
    var timeInMiliSeconds: Long? = null,
) {

    @Exclude
    fun toSmallString(): String {
        return "$month $date"
    }
}
