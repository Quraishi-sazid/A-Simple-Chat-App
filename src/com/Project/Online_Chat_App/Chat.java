package com.Project.Online_Chat_App;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.Project.Online_Chat_App.custom.CustomActivity;
import com.Project.Online_Chat_App.model.Conversation;
import com.Project.Online_Chat_App.utils.Const;
import com.chatt.demo.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

 // The Class Chat is the Activity class that holds main chat screen. It shows
 // all the conversation messages between two users and also allows the user to
 // send and receive messages.
 
public class Chat extends CustomActivity
{

	// The Conversation list. 
	private ArrayList<Conversation> convList;
	//msg deoar jonno java r built in class conversion

	// The chat adapter. 
	private ChatAdapter adp;
	//adapter class view ebong data r moddhe bridge.ChatAdapter class rcvd & sent msg dekhabe.
	
	
	
	// The Editext to compose the message. 
	private EditText txt;

	// The user name of buddy. 
	private String buddy;

	/** The date of last message in conversation. */
	private Date lastMsgDate;

	/** Flag to hold if the activity is running or not. */
	private boolean isRunning;

	/** The handler to handle delays and other */
	private static Handler handler;

	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat);

		convList = new ArrayList<Conversation>();
		ListView list = (ListView) findViewById(R.id.list);
		adp = new ChatAdapter();
		list.setAdapter(adp);
		//ager msg gulo dekhacche..
		list.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		list.setStackFromBottom(true);
		//list file sobsomoy bottom theke shuru hobe

		txt = (EditText) findViewById(R.id.txt);
		txt.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		
		setTouchNClick(R.id.btnSend);
		//send button e chap deoar por..
		//buddy = getIntent().getStringExtra(Const.EXTRA_DATA);
		//getActionBar().setTitle(buddy);

		//handler = new Handler();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		isRunning = true;
		loadConversationList();
		// activity ache kina aita test korbe..activity jodi running thake tahole conversation  
		//list load hobe..q1 
	}

	
	@Override
	protected void onPause()
	{
		super.onPause();
		isRunning = false;
	}

	
	@Override
	public void onClick(View v)
	{
		super.onClick(v);
		if (v.getId() == R.id.btnSend)
		{
			sendMessage();
		}

	}

	/**
	 * Call this method to Send message to opponent. It does nothing if the text
	 * is empty otherwise it creates a Parse object for Chat message and send it
	 * to Parse server.
	 */
	private void sendMessage()
	{
		if (txt.length() == 0)
			return;
		
		String s = txt.getText().toString();
		final Conversation c = new Conversation(s, new Date(),
				UserList.user.getUsername());
		c.setStatus(Conversation.STATUS_SENDING);
		convList.add(c);
		adp.notifyDataSetChanged();
		//layout e new data asle change korbe
		txt.setText(null);
	/*The ParseObject is a local representation of data that can be saved 
	 * and retrieved from the Parse cloud.
	*The basic workflow for creating new data is to construct a new ParseObject, 
	*use ParseObject.put(String, Object) to fill it with data, and then use ParseObject.saveInBackground() to persist to the cloud.
*	The basic workflow for accessing existing data is to use 
	a ParseQuery to specify which existing data to retrieve. 
	 */
		ParseObject po = new ParseObject("Chat");
		po.put("sender", UserList.user.getUsername());
		po.put("receiver", buddy);
		//parseObject.put er maddhome local vabe data hold kora hoy..put er 2i ta value
		//thake..ekta string key ar ekta object value.jemon upore sender holo string key..
		//ar UserList.user.getUsername() holo object value..string key object value er porichoy dey.
		
		po.put("message", s);
		po.saveEventually(new SaveCallback() {
				//sarver e msg pathaia dilo..save call back er maddhome..
			@Override
			public void done(ParseException e)
			{
				if (e == null)
					c.setStatus(Conversation.STATUS_SENT);
				else
					c.setStatus(Conversation.STATUS_FAILED);
				adp.notifyDataSetChanged();
				//activity te data change korbe
			}
		});
	}

	/**
	 * Load the conversation list from Parse server and save the date of last
	 * message that will be used to load only recent new messages
	 */
	private void loadConversationList()
	{
		ParseQuery<ParseObject> q = ParseQuery.getQuery("Chat");
		
			ArrayList<String> al = new ArrayList<String>();
			al.add(buddy);
			al.add(UserList.user.getUsername());
			q.whereContainedIn("sender", al);
			q.whereContainedIn("receiver", al);
		
		q.orderByDescending("createdAt");
		q.setLimit(30);
		//descending order e list ta sajabe date onujai..maximum limit 30 
		q.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> li, ParseException e)
			{
				//cloud er object er list create kora hoyeche
				if (li != null && li.size() > 0)
				{
					for (int i = li.size() - 1; i >= 0; i--)
					{
						ParseObject po = li.get(i);
						Conversation c = new Conversation(po
								.getString("message"), po.getCreatedAt(), po
								.getString("sender"));
						//conversation parameter(msg,date,sender)
						convList.add(c);
						
						adp.notifyDataSetChanged();
						//change korar por for loop use kore next parse object e jabe.
					}
				}
				handler.postDelayed(new Runnable() {

					@Override
					public void run()
					{
						if (isRunning)
							loadConversationList();
					}
				}, 1000);
				//1 sec delay korbe load korte..jodi activity running thake
			}
		});

	}

	/**
	 * The Class ChatAdapter is the adapter class for Chat ListView. This
	 * adapter shows the Sent or Receieved Chat message in each list item.
	 */
	//ekhane base adapter er function gulo ke override kora hoiche..
	//msg sent or recived konta hoiche sei decision nie related xml load korbe
	private class ChatAdapter extends BaseAdapter
	{

		
		@Override
		public int getCount()
		{
			return convList.size();
		}

	
		@Override
		public Conversation getItem(int arg0)
		{
			return convList.get(arg0);
		}

		
		@Override
		public long getItemId(int arg0)
		{
			return arg0;
		}

		
		@Override
		public View getView(int pos, View v, ViewGroup arg2)
		{
			Conversation c = getItem(pos);
			if (c.isSent())
				v = getLayoutInflater().inflate(R.layout.chat_item_sent, null);
			//sent msg er background load hobe.
			else
				v = getLayoutInflater().inflate(R.layout.chat_item_rcv, null);
			//recieved msg er background load hobe

			TextView lbl = (TextView) v.findViewById(R.id.lbl1);
			lbl.setText(DateUtils.getRelativeDateTimeString(Chat.this, c
					.getDate().getTime(), DateUtils.SECOND_IN_MILLIS,
					DateUtils.DAY_IN_MILLIS, 0));

			lbl = (TextView) v.findViewById(R.id.lbl2);
			lbl.setText(c.getMsg());

			lbl = (TextView) v.findViewById(R.id.lbl3);
			if (c.isSent())
			{
				if (c.getStatus() == Conversation.STATUS_SENT)
					lbl.setText("Delivered");
				else if (c.getStatus() == Conversation.STATUS_SENDING)
					lbl.setText("Sending...");
				else
					lbl.setText("Failed");
			}
			else
				lbl.setText("");

			return v;
		}

	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId() == android.R.id.home)
		{
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
}
