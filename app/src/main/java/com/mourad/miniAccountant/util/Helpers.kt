package com.mourad.miniAccountant.util

class Helpers() {
    companion object{
        fun minutesToRoundedHours(minutes: Int): Double {
            var hours: Double = minutes.toDouble() / 60
            hours = Math.round(hours * 10.0) / 10.0
            return hours
        }
    }
}