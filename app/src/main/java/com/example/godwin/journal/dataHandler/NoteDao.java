package com.example.godwin.journal.dataHandler;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import java.util.List;

@Dao
public interface NoteDao {

    @Query("SELECT * FROM note ORDER BY id")
    //List<NoteEntry> loadAllNotes();
    LiveData<List<NoteEntry>> loadAllNotes();

    @Insert
    void insertNote(NoteEntry noteEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateNote(NoteEntry noteEntry);

    @Delete
    void deleteNote(NoteEntry noteEntry);

    @Query("SELECT * FROM note WHERE id = :id")
    LiveData<NoteEntry> loadNoteById(int id);
}

