package com.example.godwin.journal;


import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.example.godwin.journal.dataHandler.JournalDatabase;


public class AddNoteViewModelFactory extends ViewModelProvider.NewInstanceFactory {


    private final JournalDatabase mDb;
    private final int mNoteId;


    public AddNoteViewModelFactory(JournalDatabase database, int noteId) {
        mDb = database;
        mNoteId = noteId;
    }


    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {

        return (T) new AddNoteViewModel(mDb, mNoteId);
    }
}

