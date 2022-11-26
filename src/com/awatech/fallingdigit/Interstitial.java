package com.awatech.fallingdigit;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Environment;
import android.view.View;

public class Interstitial {
	private InterstitialAd mInterstitialAd;
	 private InterstitialAd newInterstitialAd() {
	         mInterstitialAd = new InterstitialAd(MainActivity.HEY_CONTEXT);
	       mInterstitialAd.setAdUnitId(MainActivity.HEY_CONTEXT.getString(R.string.banner_ad_unit_id));
	        mInterstitialAd.setAdListener(new AdListener() {
	            @Override
	            public void onAdLoaded() {
	               // mNextLevelButton.setEnabled(true);
	            }

	            @Override
	            public void onAdFailedToLoad(int errorCode) {
	             //   mNextLevelButton.setEnabled(true);
	            }

	            @Override
	            public void onAdClosed() {
	                // Proceed to the next level.
	                goToNextLevel();
	            }
	        });
	        return mInterstitialAd;
	    }

	    public  void showInterstitial() {
	        // Show the ad if it's ready. Otherwise toast and reload the ad.
	        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
	            mInterstitialAd.show();
	        } else {
	          //  Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show();
	            goToNextLevel();
	        }
	    }

	    private void loadInterstitial() {
	        // Disable the next level button and load the ad.
	        //mNextLevelButton.setEnabled(false);
	        AdRequest adRequest = new AdRequest.Builder()
	              //  .setRequestAgent("android_studio:ad_template")
	        .build();
	        mInterstitialAd.loadAd(adRequest);
	    }

	    private void goToNextLevel() {
	        // Show the next level and reload the ad to prepare for the level after.
	      //  mLevelTextView.setText("Level " + (++mLevel));
	        mInterstitialAd = newInterstitialAd();
	        loadInterstitial();
	    }
	    public static void takeScreenshot(View v) {
	        Date now = new Date();
	        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

	        try {
	            // image naming and path  to include sd card  appending name you choose for file
	            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

	            // create bitmap screen capture
	            View v1 = v;
	            v1.setDrawingCacheEnabled(true);
	            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
	            v1.setDrawingCacheEnabled(false);

	            File imageFile = new File(mPath);

	            FileOutputStream outputStream = new FileOutputStream(imageFile);
	            int quality = 100;
	            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
	            outputStream.flush();
	            outputStream.close();
	            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		        sharingIntent.setType("image/jpeg");
		        sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(imageFile.getAbsolutePath()));
		        MainActivity.HEY_CONTEXT.startActivity(Intent.createChooser(sharingIntent, "Share via"));
	            
	        } catch (Throwable e) {
	            // Several error may come out with file handling or OOM
	            e.printStackTrace();
	        }
	    }
	    public static void sharei(View v)
	    {
	        View view  =  v;
	        String state = Environment.getExternalStorageState();
	       // if (Environment.MEDIA_MOUNTED.equals(state)) 
	       // {
	            File picDir  = MainActivity.HEY_CONTEXT.getFilesDir();
	            if (!picDir.exists())
	            {
	                picDir.mkdir();
	            }
	            view.setDrawingCacheEnabled(true);
	            view.buildDrawingCache(true);
	            Bitmap bitmap = view.getDrawingCache();
//	          Date date = new Date();
	            String fileName = "mylove" + ".jpg";
	            File picFile = new File(picDir + "/" + fileName);
	            try 
	            {
	                picFile.createNewFile();
	                FileOutputStream picOut = new FileOutputStream(picFile);
	                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), (int)(bitmap.getHeight()/1.2));
	                boolean saved = bitmap.compress(CompressFormat.JPEG, 100, picOut);
	                if (saved) 
	                {
	                } else 
	                {
	                    //Error
	                }
	                picOut.close();
	                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
	    	        sharingIntent.setType("text/plain");
	    	        sharingIntent.putExtra(Intent.EXTRA_STREAM, 
	    "See My score with Falling\n Digits play.google.com/apps/publish/id?=com.awatech.fallingdigit.");
	    	        MainActivity.HEY_CONTEXT.startActivity(Intent.createChooser(sharingIntent, "Share via"));
	            } 
	            catch (Exception e) 
	            {
	                e.printStackTrace();
	            }
	            view.destroyDrawingCache();
	        
	      




	        //}
	    }
}
