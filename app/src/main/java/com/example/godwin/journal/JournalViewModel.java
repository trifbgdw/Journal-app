package com.example.godwin.journal;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.example.godwin.journal.dataHandler.JournalDatabase;
import com.example.godwin.journal.dataHandler.NoteEntry;

import java.util.List;

public class JournalViewModel extends AndroidViewModel {

    // Constant for logging
    private static final String TAG = JournalViewModel.class.getSimpleName();

    private LiveData<List<NoteEntry>> notes;

    public JournalViewModel(Application application) {
        super(application);
        JournalDatabase database = JournalDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving note from the DataBase");
        notes = database.noteDao().loadAllNotes();
    }

    public LiveData<List<NoteEntry>> getNotes() {
        return notes;
    }
}

