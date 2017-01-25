package com.filipyoo.todopyoo;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.filipyoo.todopyoo.db.TaskContract;
import com.filipyoo.todopyoo.db.TaskDbHelper;

import java.util.ArrayList;

public class MainActivity extends Activity {

	private EditText addTaskEditText;
	private Button addTaskButton;
	private ListView listTask;
    private ArrayAdapter<String> mAdapter;
    private TaskDbHelper mDbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        addTaskEditText = (EditText) findViewById(R.id.addTaskEditText);
        addTaskButton = (Button) findViewById(R.id.addTaskButton);
        listTask = (ListView) findViewById(R.id.listTask);

	    /** Extra features to visualize the database tables when the addTaskButton is clicked, only useful for development, and must be deleted when deployed. Uncomment to use and visualize database.-----------------------------------------------------------------
	    addTaskButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
	      			Intent dbmanager = new Intent(MainActivity.this, AndroidDatabaseManager.class);
					startActivity(dbmanager);	
	        	}
		});
	    // ----------------------------------------------------------------- **/
    
        mAdapter = new ArrayAdapter<String>(this, R.layout.tasklist_layout, R.id.taskTextView);
        listTask.setAdapter(mAdapter);
        // the ListView appearance is customized in the tasklist_layout.xml file, it includes a TextView and 2 buttons. In the constructor, the id of the textView must be provided, if not, the tasklist_layout.xml file must ONLY include a textView (ie, without any other RelativeLayout or Button)

        mDbHelper = new TaskDbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query(TaskContract.TaskEntry.TABLE, 
        	new String[] {TaskContract.TaskEntry._ID, TaskContract.TaskEntry.COLUMN_TASK_TITLE}, 
        	null, null, null, null, null);
        while(cursor.moveToNext()) {
            int idx = cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_TITLE);
	    	mAdapter.add(cursor.getString(idx));
        }
        cursor.close();
        db.close();
    }


    public void addTask(View view) {
    	String task_text = addTaskEditText.getText().toString().trim();
    	if (task_text.length() != 0){
	    	mAdapter.add(task_text);
	    	SQLiteDatabase db = mDbHelper.getWritableDatabase();
	    	ContentValues values = new ContentValues();
	    	values.put(TaskContract.TaskEntry.COLUMN_TASK_TITLE, task_text);
	    	long newRowId = db.insert(TaskContract.TaskEntry.TABLE, null, values);
	    	addTaskEditText.getText().clear();
	    }
	}

	
    private void updateUI() {
        ArrayList<String> taskList = new ArrayList<>();
		// stock all the db content in taskList, and uploads the mAdapter or create it if not exist
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query(TaskContract.TaskEntry.TABLE,
                new String[]{TaskContract.TaskEntry._ID, TaskContract.TaskEntry.COLUMN_TASK_TITLE},
                null, null, null, null, null);
        while (cursor.moveToNext()) {
            int idx = cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_TITLE);
            taskList.add(cursor.getString(idx));
        }
        if (mAdapter == null) {
            mAdapter = new ArrayAdapter<>(this,
                    R.layout.tasklist_layout,
                    R.id.taskTextView,
                    taskList);
            listTask.setAdapter(mAdapter);
        } else {
            mAdapter.clear();
            mAdapter.addAll(taskList);
            mAdapter.notifyDataSetChanged();
        }
        cursor.close();
        db.close();
    }



    public void deleteTask(final View view){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setMessage("Delete selected ?");
		alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
		    	// delete ALL tasks entries with EXACTLY the same text, by clicking on only one entry button...
		    	View parent = (View) view.getParent();
			    TextView taskTextView = (TextView) parent.findViewById(R.id.taskTextView);
			    String task = String.valueOf(taskTextView.getText());
			    SQLiteDatabase db = mDbHelper.getWritableDatabase();
			    db.delete(TaskContract.TaskEntry.TABLE,
			            TaskContract.TaskEntry.COLUMN_TASK_TITLE + " = ?",
			            new String[]{task});
			    db.close();
			    updateUI();
				Toast.makeText(MainActivity.this,"Task deleted",Toast.LENGTH_LONG).show();
        	}
       	});
       
		alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
		@Override
        public void onClick(DialogInterface dialog, int which) {
        	//Do nothing
        }
       	});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
    }
}
