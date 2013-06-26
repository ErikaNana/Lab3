package edu.calpoly.android.lab3;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.SubMenu;


public class AdvancedJokeList extends SherlockActivity {

	/** Contains the name of the Author for the jokes. */
	protected String m_strAuthorName;

	/** Contains the list of Jokes the Activity will present to the user. */
	protected ArrayList<Joke> m_arrJokeList;
	
	/** Contains the list of filtered Jokes the Activity will present to the user. */
	protected ArrayList<Joke> m_arrFilteredJokeList;

	/** Adapter used to bind an AdapterView to List of Jokes. */
	protected JokeListAdapter m_jokeAdapter;

	/** ViewGroup used for maintaining a list of Views that each display Jokes. */
	protected ListView m_vwJokeLayout;

	/** EditText used for entering text for a new Joke to be added to m_arrJokeList. */
	protected EditText m_vwJokeEditText;

	/** Button used for creating and adding a new Joke to m_arrJokeList using the
	 *  text entered in m_vwJokeEditText. */
	protected Button m_vwJokeButton;
	
	/** Menu used for filtering Jokes. */
	protected Menu m_vwMenu;

	/** Background Color values used for alternating between light and dark rows
	 *  of Jokes. Add a third for text color if necessary. */
	protected int m_nDarkColor;
	protected int m_nLightColor;

	protected int m_nTextColor;
		
	/**
	 * Context-Menu MenuItem IDs.
	 * IMPORTANT: You must use these when creating your MenuItems or the tests
	 * used to grade your submission will fail. These are commented out for now.
	 * These help identify which type of filter has been chosen when a user selects a SubMenu item
	 */
	protected static final int FILTER = Menu.FIRST;
	protected static final int FILTER_LIKE = SubMenu.FIRST;
	protected static final int FILTER_DISLIKE = SubMenu.FIRST + 1;
	protected static final int FILTER_UNRATED = SubMenu.FIRST + 2;
	protected static final int FILTER_SHOW_ALL = SubMenu.FIRST + 3;
	
	//filter value
	protected int filter = FILTER_UNRATED;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.initLayout();
		
		//getting the colors from the XML file, which automatically generates code in
		//R.java.  Then use R.java to get the colors
		Resources resources = this.getResources();
		this.m_nDarkColor = resources.getInteger(R.color.dark);
		this.m_nLightColor = resources.getInteger(R.color.light);
		this.m_nTextColor = resources.getInteger(R.color.text);
		
		this.m_arrJokeList = new ArrayList<Joke>(); //initialize to new instance
		this.m_arrFilteredJokeList = new ArrayList<Joke>();
		
		//get array of joke strings
		String[] joke_strings = resources.getStringArray(R.array.jokeList); 
		
		//set the author name
		this.m_strAuthorName = resources.getString(R.string.author_name);
		
		//for each of the strings in joke_strings, make call to addJoke
		for (String joke_string : joke_strings) {
			Joke joke = new Joke(joke_string, this.m_strAuthorName);
			this.addJoke(joke);
		}
	
		//initialize m_jokeAdapter member variable with ArrayList of jokes
		//need to bind to m_arrFilteredJokeList
		this.m_jokeAdapter = new JokeListAdapter(this, m_arrFilteredJokeList);
			
		//set m_vwJokeLayout's adapter to be m_jokeAdapter
		this.m_vwJokeLayout.setAdapter(m_jokeAdapter);
	}
	
	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
		switch(item.getItemId()) {
			//set the filter for sorting and display the appropriate jokes
			case R.id.submenu_like:
				filter = AdvancedJokeList.FILTER_LIKE;
				//Toast.makeText(this,"like jokes",Toast.LENGTH_SHORT).show();
				updateFilteredJokes(filter);			
				this.m_jokeAdapter.notifyDataSetChanged();				
				return true;

			case R.id.submenu_dislike:
				filter = AdvancedJokeList.FILTER_DISLIKE;
				updateFilteredJokes(filter);			
				this.m_jokeAdapter.notifyDataSetChanged();	
				return true;
				
			case R.id.submenu_unrated:
				filter = AdvancedJokeList.FILTER_UNRATED;
				updateFilteredJokes(filter);			
				this.m_jokeAdapter.notifyDataSetChanged();	
				return true;
				
			case R.id.submenu_show_all:
				filter = AdvancedJokeList.FILTER_SHOW_ALL;
				updateFilteredJokes(filter);
				this.m_jokeAdapter.notifyDataSetChanged();
				return true;
		}
		return false;
	};
	/**
	 * Create the filter menu
	 */
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		//use ABS's compatibility package
        MenuInflater inflater = getSupportMenuInflater();
        
        //menu = menu to inflate to
        inflater.inflate(R.menu.mainmenu, menu);
        //initialize m_vwMenu
        this.m_vwMenu = menu;
        return true;
    }

	/**
	 * Method is used to encapsulate the code that initializes and sets the
	 * Layout for this Activity.
	 */
	protected void initLayout() {
		setContentView(R.layout.advanced);
		this.m_vwJokeLayout = (ListView) findViewById(R.id.jokeListViewGroup);
		this.m_vwJokeEditText = (EditText) findViewById(R.id.newJokeEditText);
		this.m_vwJokeButton = (Button) findViewById(R.id.addJokeButton);
		initAddJokeListeners();
	}

	/**
	 * Method is used to encapsulate the code that initializes and sets the
	 * Event Listeners which will respond to requests to "Add" a new Joke to the
	 * list.
	 */
	protected void initAddJokeListeners() {
		//setup the onClickListener for the "Add Joke" button.  
		//pass in reference to an Anonymous Inner Class that implements the OnClickListener interface
		//anonymous inner class = one-time use class that implements some interface
		//you declare the class and instantiate it in one motion
		
		m_vwJokeButton.setOnClickListener(new OnClickListener() {
			  public void onClick(View view) {
				  //retrieve text entered by user
				  String input_text = m_vwJokeEditText.getText().toString();
				  Joke joke = new Joke(input_text, m_strAuthorName);
				  //clear the text in EditText
				  m_vwJokeEditText.setText("");
				  //add joke
				  addJoke(joke);
				  
				 //hide the Soft Keyboard that appears when the EditText has focus:
				 InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromInputMethod(m_vwJokeEditText.getWindowToken(), 0);
			 } 
			});
		//implementation for the enter key
        m_vwJokeEditText.setOnKeyListener(new OnKeyListener(){

            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
                if (arg2.getAction() == KeyEvent.ACTION_DOWN){
                	if (arg1 == KeyEvent.KEYCODE_ENTER) {
                        String input_text = m_vwJokeEditText.getText().toString();
                        Joke joke = new Joke(input_text, m_strAuthorName);
                        addJoke(joke);
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                        imm.hideSoftInputFromWindow(
                                m_vwJokeEditText.getWindowToken(), 0);
                	}
                }
                m_vwJokeEditText.setText("");
                return false;

            }

        });
	}
	
	protected void updateFilteredJokes (int filter) {
		//show only filtered jokes and clear the filtered list before starting
		if (!m_arrFilteredJokeList.isEmpty()) {
			m_arrFilteredJokeList.clear();
			this.m_jokeAdapter.notifyDataSetChanged();
			String size = String.valueOf(m_arrFilteredJokeList.size());
			Toast.makeText(getBaseContext(), "JokeList clearing: " + "size:  " + size, Toast.LENGTH_SHORT).show();
		}
		
		switch (filter) {
			case AdvancedJokeList.FILTER_LIKE:{
				for(Joke joke: this.m_arrJokeList) {
					if(joke.getRating() == Joke.LIKE) {
						this.m_arrFilteredJokeList.add(joke);
					}
				}
				break;
			}
			case AdvancedJokeList.FILTER_DISLIKE:{
				for(Joke joke: this.m_arrJokeList) {
					if(joke.getRating() == Joke.DISLIKE) {
						this.m_arrFilteredJokeList.add(joke);
					}
				}
				break;
			}
			case AdvancedJokeList.FILTER_UNRATED:{
				for(Joke joke: this.m_arrJokeList) {
					if(joke.getRating() == Joke.UNRATED) {
						this.m_arrFilteredJokeList.add(joke);
					}
				}
				break;
			}
			case AdvancedJokeList.FILTER_SHOW_ALL:{
				for(Joke joke: this.m_arrJokeList) {
					this.m_arrFilteredJokeList.add(joke);
				}
				break;
			}
		}				
	}
	
	/**
	 * Method used for encapsulating the logic necessary to properly add a new
	 * Joke to m_arrJokeList, and display it on screen.
	 * 
	 * @param joke
	 *            The Joke to add to list of Jokes.
	 */
	protected void addJoke(Joke joke) {
	
		//send message to LogCat for each Joke that is added to the Joke List
		Log.d("Lab2_JokeList", "Adding new joke:  " + joke.getJoke());
		this.m_arrJokeList.add(joke); 
		this.m_arrFilteredJokeList.add(joke);
	}
	
	
}