package com.sogoamobile.weatherapp.repository

import androidx.lifecycle.LiveData
import com.sogoamobile.weatherapp.data.notes.Notes
import com.sogoamobile.weatherapp.data.notes.NotesDao

class NotesRepository(private val notesDao: NotesDao) {

    val readAllData: LiveData<List<Notes>> = notesDao.readAllData()

    suspend fun addNote(notes: Notes){
        notesDao.addNote(notes)
    }
}