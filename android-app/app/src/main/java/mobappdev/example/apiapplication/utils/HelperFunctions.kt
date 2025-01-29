package mobappdev.example.apiapplication.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun localDateToString(date: LocalDate) : String {
    val dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    return date.format(dateTimeFormatter)
}
