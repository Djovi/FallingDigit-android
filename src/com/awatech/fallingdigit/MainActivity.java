package com.awatech.fallingdigit;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.awatech.fallingdigit.IabBroadcastReceiver.IabBroadcastListener;
import com.awatech.fallingdigit.IabHelper.IabAsyncInProgressException;
import com.awatech.fdhelpers.AssetLoader;
import com.awatech.gameworld.GameWorld;
import com.awatech.screens.BillingProcessor;
import com.awatech.screens.GameScreen;
import com.awatech.screens.IGoogleServices;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.GameHelper;
import com.google.example.games.basegameutils.GameHelper.GameHelperListener;


public class MainActivity extends AndroidApplication implements IGoogleServices,BillingProcessor.IBillingHandler,IabBroadcastListener {
	private GameHelper _gameHelper;
	public static Context HEY_CONTEXT;
	protected AdView adView;
	int i=0;
	IabHelper mHelper;
	Preferences	 prefs;
	//TransactionDetails details;
	BillingProcessor bp;
    private final static int REQUEST_CODE_UNUSED = 9002;
    private final int SHOW_ADS = 1;
    private final int HIDE_ADS = 0;
    private final String base64EncodedPublicKey="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnKB7vZk1P53uP1qx4d3yTA0Q8isZhSjs4Y8EVblEsbjpCf947ylvmfRA/pE1wGIsEwqJH0pY+VgMX2bO19JP4BSpZT0adFlF6VglURak2MYYi/3zLLl0aj8cuJJQusWiTMLvYSFk/oTFTi157FLBWQmIa0s1M4ChcACF846ZzpmUbylgB/6oj+Ym6eCiZdgM3XA2WZdV7tuh/J2iVnulhTgJWnS5lQY582IQCoaCtfxur3fpzidMgfwsmmpXEy+fx5dx0Z7Z67p02xg+sOIKFolW64tnAeV811bdQQvqGX3qZcBv4KGFavP/IsxmvCZHeCgNcHbGP9wxQKyUSb6N2QIDAQAB";
    static String ITEM_SKU = "android.test.purchased";
    String TAG="fd";
    IabBroadcastReceiver mBroadcastReceiver;
    protected Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case SHOW_ADS:
                {
                    adView.setVisibility(View.VISIBLE);
                    break;
                }
                case HIDE_ADS:
                {
                    adView.setVisibility(View.GONE);
                    break;
                }
            }
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HEY_CONTEXT=this;
       // FacebookSdk.sdkInitialize(getApplicationContext());
      //  AppEventsLogger.activateApp(this);
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
     //    prefs = Gdx.app.getPreferences("FallingDigit");
        adView = new AdView(this);
        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.startSetup(new 
        		IabHelper.OnIabSetupFinishedListener() {
        	      	  @Override
					public void onIabSetupFinished(IabResult result) 
        		  {
        	    	       if (!result.isSuccess()) {
        	    	          Log.d("TAG", "In-app Billing setup failed: " + 
        					result);
        	         } else {             
        	    	      	    Log.d("TAG", "In-app Billing is set up OK");
        	    	      	  //  mHelper.enableDebugLogging(true, TAG);
        	    	      	  mBroadcastReceiver = new IabBroadcastReceiver(MainActivity.this);
        	                  IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
        	                  registerReceiver(mBroadcastReceiver, broadcastFilter);

        	                  // IAB is fully set up. Now, let's get an inventory of stuff we own.
        	                 // Log.d(TAG, "Setup successful. Querying inventory.");
        	                  try {
        	                      mHelper.queryInventoryAsync(mGotInventoryListener);
        	                  } catch (IabAsyncInProgressException e) {
        	                    //  complain("Error querying inventory. Another async operation in progress.");
        	                  }
        		  }
        	   }
        	  });
        

     // Create the layout
        RelativeLayout layout = new RelativeLayout(this);
       // RelativeLayout layout = new RelativeLayout(this);

        // Do the stuff that initialize() would do for you
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        // Create the libgdx View
        View gameView = initializeForView(new FDGame(this),cfg);

        // Create and setup the AdMob view
        adView = new AdView(this); // Put in your secret key here
        adView.setAdUnitId(this.getString(R.string.banner_ad_unit_id));
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdClosed() {
             //   Toast.makeText(getApplicationContext(), "Ad is closed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
              //  Toast.makeText(getApplicationContext(), "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLeftApplication() {
            //  Toast.makeText(getApplicationContext(), "Ad left application!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });
        AdRequest adRequest = new AdRequest.Builder()
        .setRequestAgent("android_studio:ad_template").build();
        LinearLayout loy=new LinearLayout(getApplicationContext());
loy.setBackgroundResource(R.drawable.ic_launcher);;
        // Add the libgdx view
       layout.addView(gameView);

        // Add the AdMob view
        RelativeLayout.LayoutParams adParams = 
            new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, 
                    LayoutParams.MATCH_PARENT);
        adParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        adParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
    
        adView.loadAd(adRequest);adParams.setMargins(0,10, 0, 0);
        layout.addView(adView, adParams);
        
      
    //    cfg.useGL20 = false;
        GameScreen.igs=this;
        GameScreen.iab=this;
        
        setContentView(layout);
        adView.setVisibility(View.VISIBLE);
       // initialize(new FDGame(this), cfg);
     // Create the GameHelper.
        _gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
        _gameHelper.enableDebugLog(false);

        GameHelperListener gameHelperListener = new GameHelper.GameHelperListener()
        {
        @Override
        public void onSignInSucceeded()
        {
        }

        @Override
        public void onSignInFailed()
        {
        }
        };

        _gameHelper.setup(gameHelperListener);
        
        // The rest of your onCreate() code here...
        }
 // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        @Override
		public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
           // Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
              //  complain("Failed to query inventory: " + result);
                return;
            }

            //Log.d(TAG, "Query inventory was successful.");

            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             * verifyDeveloperPayload().
             */

            // Do we have the premium upgrade?
           
           
           // Log.d(TAG, "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));

            // First find out which subscription is auto renewing
            Purchase R_RMonthly = inventory.getPurchase(GameScreen.RAGE_ROYALE);
            
            if (R_RMonthly != null && R_RMonthly.isAutoRenewing()) {
               // mInfiniteGasSku = SKU_INFINITE_GAS_MONTHLY;
                prefs.putInteger(GameScreen.RAGE_ROYALE, 1);
            } else {
              //  mInfiniteGasSku = "";
            	if(prefs!=null)
            	prefs.putInteger(GameScreen.RAGE_ROYALE, 0);
            }

            // The user is subscribed if either subscription exists, even if neither is auto
            // renewing
            //mSubscribedToInfiniteGas = (gasMonthly != null && verifyDeveloperPayload(gasMonthly));
           // Log.d(TAG, "User " + (mSubscribedToInfiniteGas ? "HAS" : "DOES NOT HAVE")
              //      + " infinite gas subscription.");
            //if (mSubscribedToInfiniteGas) mTank = TANK_MAX;

            // Check for gas delivery -- if we own gas, we should fill up the tank immediately
            Purchase epinPurchase = inventory.getPurchase(GameScreen.EPIN);
            if (epinPurchase != null && verifyDeveloperPayload(epinPurchase)) {
                Log.d(TAG, "We have gas. Consuming it.");
                prefs.putInteger(GameScreen.EPIN, 1);
                try {
                    mHelper.consumeAsync(inventory.getPurchase(GameScreen.EPIN), mConsumeFinishedListener);
                } catch (IabAsyncInProgressException e) {
              //      complain("Error consuming gas. Another async operation in progress.");
                }
                return;
            }
            Purchase ragePurchase = inventory.getPurchase(GameScreen.RAGE);
            if (ragePurchase != null && verifyDeveloperPayload(ragePurchase)) {
            	prefs.putInteger(GameScreen.RAGE, 1);
                Log.d(TAG, "We have gas. Consuming it.");
                try {
                    mHelper.consumeAsync(inventory.getPurchase(GameScreen.EPIN), mConsumeFinishedListener);
                } catch (IabAsyncInProgressException e) {
              //      complain("Error consuming gas. Another async operation in progress.");
                }
                return;
            }
         //   updateUi();
         //   setWaitScreen(false);
          //  Log.d(TAG, "Initial inventory query finished; enabling main UI.");
        }
    };
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();
        return true;
        }
@Override
public void onResume(){
	super.onResume();
//new Interstitial().showInterstitial();
}
@Override
public void onPause(){
super.onPause();
	//new Interstitial().showInterstitial();
}
    @Override
    public void signIn()
    {
    try
    {
    runOnUiThread(new Runnable()
    {
    //@Override
    @Override
	public void run()
    {
    _gameHelper.beginUserInitiatedSignIn();
    }
    });
    }
    catch (Exception e)
    {
    Gdx.app.log("MainActivity", "Log in failed: " + e.getMessage() + ".");
    }
    }

    @Override
    public void signOut()
    {
    try
    {
    runOnUiThread(new Runnable()
    {
    //@Override
    @Override
	public void run()
    {
    	
    _gameHelper.signOut();
    }
    });
    }
    catch (Exception e)
    {
    Gdx.app.log("MainActivity", "Log out failed: " + e.getMessage() + ".");
    }
    }

    @Override
    public void rateGame()
    {
    // Replace the end of the URL with the package of your game
    	//Toast.makeText(super.getContext(), "trying to rate", Toast.LENGTH_LONG).show();
    String str ="https://play.google.com/store/apps/details?id=com.awatech.fallingdigit";
    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(str)));
    }

    @Override
    public void submitScore(long score)
    {//Gdx.app.exit();
    	//Toast.makeText(this, "up", Toast.LENGTH_LONG).show();
    if (isSignedIn() == true)
    {
    
    Games.Leaderboards.submitScore(_gameHelper.getApiClient(), getString(R.string.leaderboard_super_player), score);
    startActivityForResult(Games.Leaderboards.getLeaderboardIntent(_gameHelper.getApiClient(), getString(R.string.leaderboard_super_player)), REQUEST_CODE_UNUSED);
    showScores();
    }
    else
    {
    // Maybe sign in here then redirect to submitting score?
    	 this.signIn();
    }
    }

    @Override
    public void showScores()
    {
    if (isSignedIn() == true){
    startActivityForResult(Games.Leaderboards.getLeaderboardIntent(_gameHelper.getApiClient(), getString(R.string.leaderboard_super_player)), REQUEST_CODE_UNUSED);
    
    }else
    {
    	
    // Maybe sign in here then redirect to showing scores?
    }
    }

    @Override
    public boolean isSignedIn()
    {
    return _gameHelper.isSignedIn();
    }
	@Override
	protected void onStart()
	{
	super.onStart();
	_gameHelper.onStart(this);
	}

	@Override
	protected void onStop()
	{
	//	new Interstitial().showInterstitial();
	super.onStop();
	_gameHelper.onStop();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (!mHelper.handleActivityResult(requestCode, 
	              resultCode, data)) {     
	    	super.onActivityResult(requestCode, resultCode, data);
	    	_gameHelper.onActivityResult(requestCode, resultCode, data);
	      }
		
	
	 
	}
	public void consumeItem() {
		try {
			mHelper.queryInventoryAsync(mGotInventoryListener);
		} catch (IabAsyncInProgressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener 
	= new IabHelper.OnIabPurchaseFinishedListener() {
	@Override
	public void onIabPurchaseFinished(IabResult result, 
                    Purchase purchase) 
	{
	   if (result.isFailure()) {
	      // Handle error
	      return;
	 }      
	 
	     consumeItem();
	  
	
	      
   }
};
	IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener 
	   = new IabHelper.QueryInventoryFinishedListener() {
		   @Override
		public void onQueryInventoryFinished(IabResult result,
		      Inventory inventory) {

			   		   
		      if (result.isFailure()) {
			  // Handle failure
		      } else {
	                 try {
						mHelper.consumeAsync(inventory.getPurchase(ITEM_SKU), 
mConsumeFinishedListener);
					} catch (IabAsyncInProgressException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		      }
	    }
	};
	IabHelper.OnConsumeFinishedListener mConsumeFinishedListener =
			  new IabHelper.OnConsumeFinishedListener() {
		   @Override
		public void onConsumeFinished(Purchase purchase, 
	             IabResult result) {

		 if (result.isSuccess()) {	
			 	    	 
		   	 // clickButton.setEnabled(true);
			 if(purchase.getSku().equals(GameScreen.EPIN))
		
		prefs.putInteger(GameScreen.EPIN, 1);
			 else
				 if(purchase.getSku().equals(GameScreen.RAGE))
						
						prefs.putInteger(GameScreen.RAGE, 1);
		 } else if(purchase.getSku().equals(GameScreen.RAGE_ROYALE)){
		         // handle error
			 prefs.putInteger(GameScreen.RAGE_ROYALE, 1);
		 }
	  }
	};
	@Override
	public void submitAchievement(long score) {
		
		
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mHelper != null)
			try {
				mHelper.dispose();
			} catch (IabAsyncInProgressException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		mHelper = null;
	}
	@Override
	public void update(GameWorld GW,boolean show) {
		
		// TODO Auto-generated method stub
		//if(i>0){i=1;
	//	if(GW.isReady())new Interstitial().showInterstitial();
	//	handler.sendEmptyMessage(show ? SHOW_ADS : HIDE_ADS);
		//}i++;
		
	}
	@Override
	public void share() {
		//Interstitial.shareit(getWindow().getDecorView().getRootView());
		//Interstitial.takeScreenshot(getWindow().getDecorView().getRootView());
		Intent i=new Intent();
		i.setAction(Intent.ACTION_SEND);
		i.putExtra(Intent.EXTRA_TEXT, "See my best score playing Falling Digits:"+AssetLoader.getHighScore()+
				" points\n play.google.com/store/apps/details?id=com.awatech.fallingdigit");
		i.setType("text/plain");
		startActivity(Intent.createChooser(i, "Share"));
	}
	@Override
	public void onBillingInitialized() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onProductPurchased(String productId, Object details) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void launchPurchaseFlow(String id) {
		// TODO Auto-generated method stub
		ITEM_SKU=id;
		try {
			mHelper.launchPurchaseFlow(this, id, 10001,   
				   mPurchaseFinishedListener, "");
		} catch (IabAsyncInProgressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void receivedBroadcast() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void showAds(boolean show) {
		// TODO Auto-generated method stub
		
	//	handler.sendEmptyMessage(show ? SHOW_ADS : HIDE_ADS);
		Log.e("pub", "normalement pub");
	}
	boolean doubleBackToExitPressedOnce = false;

	@Override
	public void onBackPressed() {
	    if (doubleBackToExitPressedOnce) {
	        super.onBackPressed();
	        
	        return;
	    }

	    this.doubleBackToExitPressedOnce = true;
	    Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

	    new Handler().postDelayed(new Runnable() {

	        @Override
	        public void run() {
	            doubleBackToExitPressedOnce=false;                       
	        }
	    }, 2000);
	} 


}