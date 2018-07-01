package com.example.godwin.journal;



import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.godwin.journal.dataHandler.JournalDatabase;
import com.example.godwin.journal.dataHandler.NoteEntry;

public class AddNoteViewModel extends ViewModel {


    private LiveData<NoteEntry> note;


    public AddNoteViewModel(JournalDatabase database, int noteId) {
        note = database.noteDao().loadNoteById(noteId);
    }


    public LiveData<NoteEntry> getNote() {
        return note;
    }
}
