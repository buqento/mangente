package com.wikitude.example;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListTour extends Activity implements AdapterView.OnItemClickListener {

	private ProgressDialog pDialog;

	JSONParser jParser = new JSONParser();

	ArrayList<HashMap<String, String>> DaftarWisata = new ArrayList<HashMap<String, String>>();

	public static String url = "http://merahputihbridge.com/webservice/maps_service.php";

	public static final String TAG_NAME = "name";
	public static final String TAG_PICT = "pict";
	public static final String TAG_DESCRIPTION = "description";
	public static final String TAG_LATITUDE = "latitude";
	public static final String TAG_LONGITUDE = "longitude";
	public static final String TAG_CATEGORY = "category";
	public static final String TAG_POLYGON = "polygon";
	public static final String TAG_VIDEO = "video";


	JSONArray string_json = null;

	ListView list;
	LazyAdapter adapter;
	private ArrayAdapter<String> arrayAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);

		DaftarWisata = new ArrayList<HashMap<String, String>>();

		new AmbilData().execute();

		list = (ListView) findViewById(R.id.list);
		list.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		String name=((TextView) view.findViewById(R.id.name)).getText().toString();
		String latitude=((TextView) view.findViewById(R.id.latitude)).getText().toString();
		String longitude=((TextView) view.findViewById(R.id.longitude)).getText().toString();
		String polygon=((TextView) view.findViewById(R.id.polygon)).getText().toString();
		String description=((TextView) view.findViewById(R.id.description)).getText().toString();
		String category=((TextView) view.findViewById(R.id.category)).getText().toString();
		String video=((TextView) view.findViewById(R.id.video)).getText().toString();
		Intent intent = new Intent(getApplicationContext(),ShowDirectionRoute.class);

		intent.putExtra(TAG_LATITUDE,latitude);
		intent.putExtra(TAG_LONGITUDE,longitude);
		intent.putExtra(TAG_NAME, name);
		intent.putExtra(TAG_POLYGON, polygon);
		intent.putExtra(TAG_DESCRIPTION,description);
		intent.putExtra(TAG_CATEGORY, category);
		intent.putExtra(TAG_VIDEO,video);
		startActivity(intent);
	}

	public void SetListViewAdapter(ArrayList<HashMap<String, String>> berita) {
		adapter = new LazyAdapter(this, berita);
		list.setAdapter(adapter);
	}

	class AmbilData extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ListTour.this);
			pDialog.setMessage("Memuat Data...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		protected String doInBackground(String... args) {

			List<NameValuePair> params = new ArrayList<NameValuePair>();

			JSONObject json = jParser.getJSONFromUrl(url);

			try {
					string_json = json.getJSONArray("wisata");

					for (int i = 0; i < string_json.length(); i++) {
						JSONObject c = string_json.getJSONObject(i);

						String name = c.getString(TAG_NAME);
						String pict = c.getString(TAG_PICT);
						String description = c.getString(TAG_DESCRIPTION);
						String latitude = c.getString(TAG_LATITUDE);
						String longitude = c.getString(TAG_LONGITUDE);
						String category = c.getString(TAG_CATEGORY);
						String polygon = c.getString(TAG_POLYGON);

						HashMap<String, String> map = new HashMap<String, String>();

						map.put(TAG_NAME, name);
						map.put(TAG_PICT, pict);
						map.put(TAG_DESCRIPTION, description);
						map.put(TAG_LATITUDE, latitude);
						map.put(TAG_LONGITUDE, longitude);
						map.put(TAG_CATEGORY, category);
						map.put(TAG_POLYGON, polygon);

						DaftarWisata.add(map);
					}

			} catch (JSONException e) {
				e.printStackTrace();
				Log.d("Background Task", e.toString());
				Intent i = new Intent(getApplicationContext(),
						MenuActivity.class);
				startActivity(i);
				finish();
			}

			return null;
		}

		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
			runOnUiThread(new Runnable() {
				public void run() {
					SetListViewAdapter(DaftarWisata);
					Toast.makeText(getApplicationContext(), "Pilih salah satu lokasi wisata", Toast.LENGTH_LONG).show();
				}
			});

		}

	}

	public void kembaliMenu(View view){
		finish();
	}

}
