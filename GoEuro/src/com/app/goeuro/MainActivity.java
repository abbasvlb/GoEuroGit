package com.app.goeuro;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends FragmentActivity implements OnClickListener,
		LocationListener {

	private static final String LOG_TAG = MainActivity.class.getName();
	public String data;
	public List<Result> suggest;
	public AutoCompleteTextView autoCompleteFrom;
	public AutoCompleteTextView autoCompleteTo;
	public ArrayAdapter<Result> aAdapter;
	private Button _searchBtn, _calBtn;
	private LocationManager locationManager;
	private static EditText _showDate;
	protected double latitude, longitude;
	private String provider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		suggest = new ArrayList<Result>();
		_searchBtn = (Button) findViewById(R.id.btn_search);
		_searchBtn.setOnClickListener(this);
		_calBtn = (Button) findViewById(R.id.btn_date);
		_calBtn.setOnClickListener(this);
		_showDate = (EditText) findViewById(R.id.txt_date);

		autoCompleteFrom = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
		autoCompleteTo = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView2);
		autoCompleteFrom.addTextChangedListener(textWatcher);
		autoCompleteTo.addTextChangedListener(textWatcher);

	}

	@Override
	protected void onResume() {
		super.onResume();
		// Get the location manager
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// Define the criteria how to select the locatioin provider -> use default
		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);
		locationManager.requestLocationUpdates(provider, 0, 0, this);
	}

	public TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			String newText = s.toString();
			new getJson().execute(newText);
		}
	};

	class getJson extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... key) {
			String newText = key[0];
			newText = newText.trim();
			newText = newText.replace(" ", "+");
			try {
				HttpClient hClient = new DefaultHttpClient();
				HttpGet hGet = new HttpGet(
						"http://pre.dev.goeuro.de:12345/api/v1/suggest/position/en/name/"
								+ newText);
				ResponseHandler<String> rHandler = new BasicResponseHandler();
				data = hClient.execute(hGet, rHandler);
				suggest = new ArrayList<Result>();
				JSONObject jObject = new JSONObject(data);
				JSONArray jArray = jObject.getJSONArray("results");
				if (jArray.length() != 0) {
					for (int i = 0; i < jArray.length(); i++) {
						JSONObject e = jArray.getJSONObject(i);
						JSONObject innerObj = e.getJSONObject("geo_position");
						float distance = distanceFrom(latitude, longitude,
								innerObj.getDouble("latitude"),
								innerObj.getDouble("longitude"));
						Result SuggestKey = new Result(e, distance);
						suggest.add(SuggestKey);
					}
				}
			} catch (Exception e) {
				Log.e(LOG_TAG, e.getMessage());
			}
			runOnUiThread(new Runnable() {
				public void run() {
					Collections.sort(suggest, new Comparator<Result>() {
						public int compare(Result emp1, Result emp2) {
							return (int) emp1.getDistance()
									- emp2.getDistance();
						}
					});
					aAdapter = new ArrayAdapter<Result>(
							getApplicationContext(), R.layout.item, suggest) {

						@Override
						public View getView(int position, View convertView,
								ViewGroup parent) {
							View v = super.getView(position, convertView,
									parent);
							TextView textView = (TextView) v;
							textView.setTextColor(MainActivity.this
									.getResources().getColor(
											android.R.color.black));
							textView.setText(getItem(position).getName());
							return textView;

						}
					};
					autoCompleteFrom.setAdapter(aAdapter);
					autoCompleteTo.setAdapter(aAdapter);
					aAdapter.notifyDataSetChanged();
				}
			});

			return null;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_search) {
			AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
					.create();
			alertDialog.setTitle("Alert");
			alertDialog.setMessage("Search is not yet implemented");
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			alertDialog.show();
		} else if (v.getId() == R.id.btn_date) {
			DialogFragment newFragment = new DatePickerFragment();
			newFragment.show(getSupportFragmentManager(), "datePicker");
		}
	}

	public static class DatePickerFragment extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {

		public String _date;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		public void onDateSet(DatePicker view, int year, int month, int day) {
			_date = day + "/" + month + "/" + year;
			_showDate.setText(_date);
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		latitude = location.getLatitude();
		longitude = location.getLongitude();
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.d(LOG_TAG, "disable");
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.d(LOG_TAG, "enable");
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.d(LOG_TAG, "status");
	}

	public float distanceFrom(double startLatitude, double startLongitude,
			double endLatitude, double endLongitude) {
		float[] dist = new float[1];
		Location.distanceBetween(startLatitude, startLongitude, endLatitude,
				endLongitude, dist);
		return dist[0] * 0.000621371192f;
	}
}
