package com.example.reminder

//data class Reminder(
//  val id: Int,
//  val text: String,
//  var date: String,
//  val time: String)

data class Reminder(
    val id: Int,
    val text: String,
    var date: String,
    val time: String,
    var isCompleted: Boolean = false
)