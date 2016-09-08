package com.wikitude.example;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class LazyAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private static LayoutInflater inflater = null;
	public ImageLoader imageLoader;

	public LazyAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
		activity = a;
		data = d;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(activity.getApplicationContext());
	}

	public int getCount() {
		return data.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (convertView == null)
			vi = inflater.inflate(R.layout.list_item, null);

		TextView judul = (TextView) vi.findViewById(R.id.name);
		ImageView thumb_image = (ImageView) vi.findViewById(R.id.gambar);
		TextView description = (TextView) vi.findViewById(R.id.description);
		TextView latitude = (TextView) vi.findViewById(R.id.latitude);
		TextView longitude = (TextView) vi.findViewById(R.id.longitude);
		TextView category = (TextView) vi.findViewById(R.id.category);
		TextView polygon = (TextView) vi.findViewById(R.id.polygon);

		HashMap<String, String> daftar_berita = new HashMap<String, String>();
		daftar_berita = data.get(position);

		judul.setText(daftar_berita.get(ListTour.TAG_NAME));
		imageLoader.DisplayImage(daftar_berita.get(ListTour.TAG_PICT), thumb_image);
		description.setText(daftar_berita.get(ListTour.TAG_DESCRIPTION));
		latitude.setText(daftar_berita.get(ListTour.TAG_LATITUDE));
		longitude.setText(daftar_berita.get(ListTour.TAG_LONGITUDE));
		category.setText(daftar_berita.get(ListTour.TAG_CATEGORY));
		polygon.setText(daftar_berita.get(ListTour.TAG_POLYGON));
		return vi;
	}

	private Resources getResources() {
		return null;

	}
}