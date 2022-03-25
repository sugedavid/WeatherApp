package com.sogoamobile.weatherapp.data

import androidx.lifecycle.LiveData

class NotesRepository(private val notesDao: NotesDao) {

    val readAllData: LiveData<List<Notes>> = notesDao.readAllData()

    suspend fun addNote(notes: Notes){
        notesDao.addNote(notes)
    }
}