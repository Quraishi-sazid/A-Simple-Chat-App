package com.Project.Online_Chat_App;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.Project.Online_Chat_App.custom.CustomActivity;
import com.Project.Online_Chat_App.utils.Const;
import com.Project.Online_Chat_App.utils.Utils;
import com.chatt.demo.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * The Class UserList is the Activity class. It shows a list of all users of
 * this app. It also shows the Offline/Online status of users.
 */
public class UserList extends CustomActivity
{

	/** The Chat list. */
	private ArrayList<ParseUser> uList;
		//login user bade baki user er list dekhabe
	/** The user. */
	public static ParseUser user;
	//current login user
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_list);

		getActionBar().setDisplayHomeAsUpEnabled(false);

		updateUserStatus(true);
		//user list display korar jonno ai code tuku
	}

	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		updateUserStatus(false);
	}
	//chole gele offline dekhabe

	
	@Override
	protected void onResume()
	{
		super.onResume();
		loadUserList();

	}

	
	private void updateUserStatus(boolean online)
	{
		user.put("online", online);
		user.saveEventually();
	}
	//user login korar sathe sathe take onnanno user er kache online e dekhabe

	
	private void loadUserList()
	{
		final ProgressDialog dia = ProgressDialog.show(this, null,
				getString(R.string.alert_loading));
		ParseUser.getQuery().whereNotEqualTo("username", user.getUsername())
				.findInBackground(new FindCallback<ParseUser>() {
					//username bade baki user gulor nam load korbe..findInBackground method parse database theke khuje anbe
					//erpor FindCallback interface er maddhome done method take call korbe
					@Override
					public void done(List<ParseUser> li, ParseException e)
					{
						dia.dismiss();
						if (li != null)
						{
							if (li.size() == 0)
								Toast.makeText(UserList.this,
										R.string.msg_no_user_found,
										Toast.LENGTH_SHORT).show();
							//jodi list 0 hoy tahole msg dibe..
							/*The Toast.makeText() method is a factory method which creates a Toast object. 
							 * The method takes 3 parameters. First the methods needs a Context object which is obtained by calling getApplicationContext().
							 *  Note: The getApplicationContext() method is a method that exists inside activities, 
							 *  so the above code has to be located in an Activity subclass to work.
								
							The second parameter is the text to be displayed in the Toast.
							 The third parameter is the time duration the Toast is to be displayed.
							  The Toast class contains two predefined constants you can use: Toast.LENGTH_SHORT and Toast.LENGTH_LONG. 
							  You will have to experiment with these two values to see which fits your situation better.
							 */

							uList = new ArrayList<ParseUser>(li);
							ListView list = (ListView) findViewById(R.id.list);
							list.setAdapter(new UserAdapter());
							//data r layout er moddhe bridge gothon korche..
							list.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> arg0,
										View arg1, int pos, long arg3)
								{
									startActivity(new Intent(UserList.this,
											Chat.class).putExtra(
											Const.EXTRA_DATA, uList.get(pos)
													.getUsername()));
									//je user er sathe chat korbo sei user er nam er upor click korle chat activity open
									//korar jonno ekta intention pass kora hobe.er sathe selected user er user name o jabe
								}
							});
						}
						else
						{
							Utils.showDialog(
									UserList.this,
									getString(R.string.err_users) + " "
											+ e.getMessage());
							e.printStackTrace();
							//network na thakle othoba onno kono problem thakle error msg dibe..
							//error gulo string.xml e rakha ache
						}
					}
				});
	}

	/**
	 * The Class UserAdapter is the adapter class for User ListView. This
	 * adapter shows the user name and it's only online status for each item.
	 */
	//list view er jonno adapter class
	private class UserAdapter extends BaseAdapter
	{

		
		@Override
		public int getCount()
		{
			return uList.size();
		}

		
		@Override
		public ParseUser getItem(int arg0)
		{
			return uList.get(arg0);
		}

		
		@Override
		public long getItemId(int arg0)
		{
			return arg0;
		}

		
		@Override
		public View getView(int pos, View v, ViewGroup arg2)
		{
			if (v == null)
				v = getLayoutInflater().inflate(R.layout.chat_item, null);

			ParseUser c = getItem(pos);
			TextView lbl = (TextView) v;
			lbl.setText(c.getUsername());
			lbl.setCompoundDrawablesWithIntrinsicBounds(
					c.getBoolean("online") ? R.drawable.ic_online
							: R.drawable.ic_offline, 0, R.drawable.arrow, 0);
				//online e thakle online er pic
				//offline e thakle offline er pic
			return v;
		}

	}
}
