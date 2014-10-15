package com.koobest.database;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.NameValuePair;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.message.BasicNameValuePair;
import org.xml.sax.SAXException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.provider.SyncStateContract.Constants;
import android.widget.Toast;

import com.koobest.R;
import com.koobest.RemoteController;
import com.koobest.RemoteControllerMain;
import com.koobest.constant.URLConstant;
import com.koobest.parse.DomParse;
import com.koobest.socket.HttpConnection;

public class UpdateInfo {
	private String[] tables = { "manufacturer", "category", "model" };
	private DatabaseHelper myHelper;
	private SQLiteDatabase db;
	private HttpConnection con;
	private ArrayList<NameValuePair> params;
	private Object tag_id;
	private Object tag_name;
	private String tagName;
	private Context mContext;
	private int tryTimes = 0;
	
	private int CONNECTED_TIMES = 3;
	private int LatestVersion;
	private final int CONNECTION_OUT_TIME = -10;

	public UpdateInfo(Context context, HttpConnection co) {
		myHelper = new DatabaseHelper(context, "rc_test_db");
		db = myHelper.getReadableDatabase();
		con = co;
		this.mContext = context;
	}

	public void closeDataBase() {
		if (db.isOpen()) {
			db.close();
			System.out.println("database close");
		}
	}

	public void updateAll() {
		tryTimes++;
		for (int i = 0; i < 2; i++) {
			params = new ArrayList<NameValuePair>();
			this.update(i);
		}
		
		// db.close();
	}

	private void update(int num) {
		tag_id = tables[num] + "_id";
		tag_name = tables[num] + "_name";
		tagName = tables[num];
		switch (num) {
		case 1:
			params.add(new BasicNameValuePair(URLConstant.PREFIX,
					URLConstant.CATEGORIES_ROUTE));
			break;
		case 0:
			params.add(new BasicNameValuePair(URLConstant.PREFIX,
					URLConstant.MANUFACTURERS_ROUTE));
			break;
		case 2:
			params.add(0, new BasicNameValuePair(URLConstant.PREFIX,
					URLConstant.MODELS_ROUTE));
			break;
		}
		try {
			// final DialogExtension.MyBuilder dialog = new
			// DialogExtension.MyBuilder(mContext);
			// rc_list.setDataSource(params);
			System.out.println(params.size());
			DomParse dom = con.getResultsByGet(params, null);
			params.clear();

			HashMap<Integer, HashMap<String, String>> results = dom
					.getAllWidgets(tagName);

			String tag_id = tagName + "_id";
			String tag_name = tagName + "_name";
			Cursor cursor = db.query(tagName,
					new String[] { tag_id, tag_name }, null, null, null, null,
					null);
			if (cursor.getColumnCount() < results.size())
				db.delete(tagName, null, null);
			for (int i = 0; i < results.size(); i++) {
				Set<String> keyset = results.get(i).keySet();
				Object[] keyarray = keyset.toArray();
				for (int j = 0; j < results.get(i).size(); j++) {

					ContentValues value = new ContentValues();
					value.put(tag_id, i);
					value.put(tag_name, results.get(i).get(
							keyarray[j].toString()));
					db.insert(tagName, null, value);
					// this.notifyDataSetChanged();

				}
			}
			cursor = db.query(tagName, new String[] { tag_id, tag_name }, null,
					null, null, null, null);

			cursor.close();
			System.out.println("DB UPDATE SUCCESS");
		} catch (SAXException saxe) {
			System.out.println(saxe.getMessage() + " SAXException DB UPDATE");
			return;
		} catch (ParserConfigurationException e) {
			System.out.println(e.getMessage()
					+ " ParserConfigurationException from DB UPDATE");
			return;
		} catch (ConnectTimeoutException e) {
			System.out.println(e.getMessage() + " " + this.CONNECTED_TIMES
					+ " ConnectionException from DB UPDATE");
			if (tryTimes < this.CONNECTED_TIMES)
				this.updateAll();

			return;
		} catch (IOException e) {
			System.out.println(e.getMessage() + " IOException from DB UPDATE");
			if (tryTimes < this.CONNECTED_TIMES)
				this.updateAll();
			return;

		} catch (URISyntaxException e) {
			System.out.println(e.getMessage()
					+ " URISyntaxException from DB UPDATE");

			return;
		} catch (StackOverflowError sofe) {
			System.out.println(sofe.getMessage()
					+ " StackOverflowError from DB UPDATE");
			return;
		} catch (Exception e) {

			System.out.println(e.getMessage() + " Excpetion from DB UPDATE");
			if (tryTimes < this.CONNECTED_TIMES)
				this.updateAll();

			return;
		}

	}

	public boolean shallUpdate() {
		// TODO Auto-generated method stub

		this.getLatestVersion();
		int LocalVersion = -1;
		Cursor cursor = db.query("version", new String[] { "version_id" },
				null, null, null, null, null);
		if (cursor.moveToNext()) {
			LocalVersion = cursor.getInt(cursor
					.getColumnIndex("version_id"));
		}
		cursor.close();
		System.out.println("latest:"+LatestVersion + " Local:"+LocalVersion);
		if(LatestVersion == this.CONNECTION_OUT_TIME){
			return false;
		}
		if (LocalVersion == LatestVersion)
			return false;
		else
			return true;
	}

	private void getLatestVersion(){
		params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair(URLConstant.PREFIX,
				URLConstant.VERSION_ROUTE));
		System.out.println("check start");
		DomParse dom = null;
		try {
			this.tryTimes++;
			dom = con.getResultsByGet(params, null);
		} catch (ConnectTimeoutException e) {
			System.out.println(e.getMessage() + " " + this.tryTimes + " ConnectionException from DB UPDATE");
			LatestVersion = CONNECTION_OUT_TIME;
			if (tryTimes < this.CONNECTED_TIMES)
				this.getLatestVersion();
			return;
		} catch (IOException e) {
			System.out.println(e.getMessage() + " IOException from DB UPDATE");
			if (tryTimes < this.CONNECTED_TIMES)
				this.getLatestVersion();
			return;

		}  catch (StackOverflowError sofe) {
			System.out.println(sofe.getMessage()
					+ " StackOverflowError from DB UPDATE");
			return;
		} catch (Exception e) {

			System.out.println(e.getMessage() + " Excpetion from DB UPDATE");
			if (tryTimes < this.CONNECTED_TIMES)
				this.getLatestVersion();
			return;
		}
		String version = dom.getSingleContent("version");
		if(version != null)
			LatestVersion = Integer.parseInt(version);
		else
			LatestVersion = CONNECTION_OUT_TIME;
			

	}
	
	public void updateVersion() {
		// TODO Auto-generated method stub
		ContentValues value = new ContentValues();
		value.put("version_id", LatestVersion);
		db.delete("version", null, null);
		db.insert("version", null, value);
		this.updateAll();
	}
}
