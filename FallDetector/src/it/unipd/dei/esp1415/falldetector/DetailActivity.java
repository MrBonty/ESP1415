package it.unipd.dei.esp1415.falldetector;

import java.util.ArrayList;

import it.unipd.dei.esp1415.falldetector.fragment.DetailSessionFragment;
import it.unipd.dei.esp1415.falldetector.fragment.ListSessionFragment;
import it.unipd.dei.esp1415.falldetector.utility.Session;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

public class DetailActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			finish();
			return;
		}

		Intent i = getIntent();
		setContentView(R.layout.activity_detail);
		ArrayList<Session> tmp = new ArrayList<Session>();
		tmp.add(new Session("a"));
		tmp.add(new Session("b"));
		tmp.add(new Session("c"));
		tmp.add(new Session("d"));
		tmp.add(new Session("e"));
		tmp.add(new Session("f"));
		tmp.add(new Session("g"));
		tmp.add(new Session("h"));
		tmp.add(new Session("i"));
		tmp.add(new Session("l"));
		tmp.add(new Session("m"));
		tmp.add(new Session("n"));
		tmp.add(new Session("o"));
		tmp.add(new Session("p"));
		tmp.add(new Session("q"));
		int index = i.getIntExtra("index", 0);
		Fragment detailSession = DetailSessionFragment.newInstance(index, tmp);
		FragmentManager manager = getSupportFragmentManager();

		FragmentTransaction transaction = manager.beginTransaction();
		transaction.replace(R.id.detail, detailSession);

		transaction.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.detail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
