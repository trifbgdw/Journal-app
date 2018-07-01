package com.example.godwin.journal;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.Date;
import com.example.godwin.journal.dataHandler.JournalDatabase;
import com.example.godwin.journal.dataHandler.NoteEntry;

public class AddNoteActivity extends AppCompatActivity {

    EditText addNoteText;
    Button saveButton;

    public static final String DEBUGTAG = "GDW";
    // note ID to be received in the intent
    public static final String EXTRA_NOTE_ID = "extraTaskId";
    // note ID to be received after rotation
    public static final String INSTANCE_NOTE_ID = "instanceTaskId";
    // note id to be used when not in update mode
    private static final int DEFAULT_NOTE_ID = -1;
    // Constant for logging
    private static final String TAG = AddNoteActivity.class.getSimpleName();


    private int mNoteId = DEFAULT_NOTE_ID;

    // Member variable for the journal Database
    private JournalDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        addNoteText = (EditText) findViewById(R.id.addNoteText);
        saveButton = (Button) findViewById(R.id.action_save);
        //Log.e(DEBUGTAG, "activity");

        //start data

        mDb = JournalDatabase.getInstance(getApplicationContext());

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_NOTE_ID)) {
            mNoteId = savedInstanceState.getInt(INSTANCE_NOTE_ID, DEFAULT_NOTE_ID);
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_NOTE_ID)) {
            saveButton.setText(R.string.update_button);
            if (mNoteId == DEFAULT_NOTE_ID) {
                // populate the UI

                mNoteId = intent.getIntExtra(EXTRA_NOTE_ID, DEFAULT_NOTE_ID);


                AddNoteViewModelFactory factory = new AddNoteViewModelFactory(mDb, mNoteId);

                final AddNoteViewModel viewModel = ViewModelProviders.of(this, factory).get(AddNoteViewModel.class);


                viewModel.getNote().observe(this, new Observer<NoteEntry>() {
                    @Override
                    public void onChanged(@Nullable NoteEntry noteEntry) {
                        viewModel.getNote().removeObserver(this);
                        populateUI(noteEntry);
                    }
                });
            }
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //logout user
        if (id == R.id.action_save) {
            //return true;
            //startActivity(new Intent(MainActivity.this, LoginActivity.class));
            onSaveButtonClicked();
        }
        else if (id == R.id.action_cancel) {
            //return true;
            //check if note is empty
            String note = addNoteText.getText().toString();
            if(!TextUtils.isEmpty(note)) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.confirm_dialog_title)
                        .setMessage(R.string.confirm_dialog_message)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                startActivity(new Intent(AddNoteActivity.this, MainActivity.class));
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
            else {
                startActivity(new Intent(AddNoteActivity.this, MainActivity.class));
            }
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_NOTE_ID, mNoteId);
        super.onSaveInstanceState(outState);
    }



    // get all notes entered
    private void populateUI(NoteEntry note) {
        if (note == null) {
            return;
        }

        addNoteText.setText(note.getDescription());
    }

    /**
     * onSaveButtonClicked is called when the "save" button is clicked.
     * It retrieves user input and inserts that new task data into the underlying database.
     */
    public void onSaveButtonClicked() {
        String description = addNoteText.getText().toString();
        Date date = new Date();

        final NoteEntry note = new NoteEntry(description, date);
        JournalExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.noteDao().insertNote(note);
                if (mNoteId == DEFAULT_NOTE_ID) {
                    // insert new note
                    mDb.noteDao().insertNote(note);
                } else {
                    //update note
                    note.setId(mNoteId);
                    mDb.noteDao().updateNote(note);
                }
                finish();
            }
        });


    }
}
