package pl.iszala.travelernotebook;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public class TravelerNotebook extends ListActivity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		database = (new DatabaseHelper(this).getWritableDatabase());
		showTravel();
		
		registerForContextMenu(getListView());

	}
	
	/**
	 * Function creates new list view with all trips
	 */
	public void showTravel(){
		String[] resultComun = new String[] {"_id","FROM_PLACE","TO_PLACE"};
		cursor = database.query(TABLE_NAME, resultComun, null, null, null, null, "_id");
		ListAdapter adapter = new SimpleCursorAdapter(this, R.layout.trip_list_view,
				cursor, new String[] { "FROM_PLACE", "TO_PLACE"},
				new int[] { R.id.list_from, R.id.list_to}); // to
		setListAdapter(adapter);

		this.getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long fk_id) {
				tripActivity(fk_id);
				//database.close();
				//cursor.close();
			}
		});
	}
	
	/**
	 * Creates new Activity- details of trip
	 * list with points
	 */
	public void tripActivity(long fk_id){
		Intent intent = new Intent(TravelerNotebook.this, TravelPoint.class);
		intent.putExtra("fk_id", fk_id);
		startActivity(intent);
	}

	/**
	 * Creates menu on in main window
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;

	}

	/**
	 * Steering in menu
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add_new_trip:
			addNewTravelDialog();
			return true;
		case R.id.close:
			database.close();
			this.finish();
			return true;
		}
		return false;
	}
	
	/**
	 * Creates context menu on travel list
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.travel_context_menu, menu);
	}
	
	/**
	 * Stering in context menu
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item){
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
		switch(item.getItemId()){
		case R.id.show_points:
			tripActivity(getListAdapter().getItemId(info.position));
			return true;
		case R.id.delete_travel:
			deleteTravel(getListAdapter().getItemId(info.position));
			Toast.makeText(this, R.string.delete_info, Toast.LENGTH_SHORT).show();
			return true;
		}
		return true;
	}

	/**
	 * Creates new dialog window, used to add new trip in database
	 */
	private void addNewTravelDialog() {
		LayoutInflater inflater = LayoutInflater.from(this);
		View addView = inflater.inflate(R.layout.new_travel_dialog, null);
		final TravelDialogWrapper wrapper = new TravelDialogWrapper(addView);

		new AlertDialog.Builder(this).setTitle(R.string.new_travel_title)
				.setView(addView).setPositiveButton(R.string.save,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								processAdd(wrapper);

							}
						}).setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO zaimplementowac wybranie anuluj
							}
						}).show();
	}
	
	/**
	 * Function for add new trip to database
	 * @param wrapper
	 */
	private void processAdd(TravelDialogWrapper wrapper) {
		ContentValues values = new ContentValues(8);
		values.put("FROM_PLACE", wrapper.getFrom());
		values.put("TO_PLACE", wrapper.getTo());
		database.insert(TABLE_NAME, null, values);
		cursor.requery();
	}

	/**
	 * Used to remove travel from database
	 * @param travel_id ID entry to remove from database
	 */
	private void deleteTravel(long travel_id){
		String[] arguments = {String.valueOf(travel_id)};
		database.delete(TABLE_NAME, "_id=?", arguments);
		cursor.requery();
	}

	//-----------------------SOME PRIVATE FIELDS----------------------
	private SQLiteDatabase database;
	private Cursor cursor;
	
	private static final String TABLE_NAME = "TRIP";

	
	/**
	 * 
	 * @author Robert
	 * 
	 */
	class TravelDialogWrapper {
		private EditText from_edit;
		private EditText to_edit;

		/**
		 * Default constructor, creates two fields
		 * @param view
		 */
		public TravelDialogWrapper(View view) {
			from_edit = (EditText) view.findViewById(R.id.new_from);
			to_edit = (EditText) view.findViewById(R.id.new_to);
		}

		/**
		 * Getter for FROM field
		 * @return String with name of start place
		 */
		public String getFrom() {
			return from_edit.getText().toString();
		}

		/** 
		 * @return String with name of destination
		 */
		public String getTo() {
			return to_edit.getText().toString();
		}
	}
}
