package com.mourad.miniAccountant.util

class Helpers() {
    companion object{
        fun minutesToRoundedHours(minutes: Int): Double {
            var hours: Double = minutes.toDouble() / 60
            hours = Math.round(hours * 100.0) / 100.0
            return hours
        }
    }
}