import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionConfidence;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class TransactionStatus {
	
	static int count=1;
	
	
	public void calculateTime(Transaction tx,String txHash, String txHex,TransactionRelay tr,long startTime) throws Exception
	{
		
        //trying Transaction Confidence
		
		long stopTime = System.currentTimeMillis();
		long elapsedTime = (stopTime - startTime)/1000;
		 
        TransactionConfidence tc= tx.getConfidence();
        System.out.println("Transaction Confidence is "+ tc.getConfidenceType());
        //System.out.println(tc.numBroadcastPeers()+ " no of immediate peers have announced the transaction in "+elapsedTime +" seconds");
        //System.out.println(tc.getDepthInBlocks());
        
		TimeUnit.SECONDS.sleep(5);
		
		//tr.relayTransaction(txHex,startTime);
		
		count++;
		String MY_APIKEY="75e9ed40a55336d1c00f1d21a7216063ca99e789";
		//We Are using BlockTrail API to detect the Tx
		String UrlString= "https://api.blocktrail.com/v1/tbtc/transaction/"+txHash+"?api_key="+MY_APIKEY;
		
		try{
		URL url = new URL(UrlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
		if (conn.getResponseCode() != 200) {
			
			System.out.println(" The transaction has not yet been propogated in the network");
			calculateTime(tx,txHash,txHex,tr,startTime);
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(
			(conn.getInputStream())));

		String output;
	
		while ((output = br.readLine()) != null) {
			//System.out.println(output);
			
			  String JSON_DATA=output;
			  JSONObject obj = new JSONObject(JSON_DATA);
			  //The UTC timestamp (in ISO8601 format) when the transaction was first seen by BlockTrail.
			  String first_seen_at = obj.getString("first_seen_at");
			  //The UTC timestamp (in ISO8601 format) when the transaction was last propagated through the network and seen by BlockTrail.
			  String last_seen_at = obj.getString("last_seen_at");
			  
			  try{
			//The UTC timestamp (in ISO8601 format) the block containing the transaction was created by the miner. An unconfirmed transaction will have a null block_time.
			  String block_time=obj.getString("block_time");
			  //The total number of blocks that have been processed since the transaction, including the block containing the transaction. An unconfirmed transaction will have 0 confirmations.
			  int confirmations=obj.getInt("confirmations");
			  int block_height=obj.getInt("block_height");
			  
			  SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			  Date date1 = df.parse(first_seen_at);
			  Date date2 = df.parse(block_time);
			  long difference = date1.getTime() - date2.getTime();
			  Date myDate = new Date(difference);
			  
			  if(difference<0)
			  {
				  difference= difference*-1;
			  }
			  
			  System.out.println(confirmations +" Blocks added to the blockchain after the txBlock was added to the BlockChain, the transaction has been added to the block with block height " +block_height );
			  System.out.println("The time for the transaction to be propogated to 100% of the network is "+ difference/(1000) + " seconds");
			  System.out.println("Counter"+count);
			  }
			  catch(org.json.JSONException e)
			  {
				  System.out.println("The Transaction has not been added to a block yet. UnConfirmed Transaction");
				  calculateTime(tx,txHash,txHex,tr,startTime);
			  }
		}

		conn.disconnect();
		
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		  } 
		catch(java.net.UnknownHostException e)
		{
			System.out.println("The internet went down");
			System.out.println(" Trying to get the connection back ...");
			calculateTime(tx,txHash,txHex,tr,startTime);
		}catch (Exception e) {
			e.printStackTrace();
		  }

		
	
		
	}
		
	
	public static void main (String args[]) throws ParseException, InterruptedException 
	{
		TransactionStatus t= new TransactionStatus();
		//t.calculateTime("b18d5e49a0fa1392ed12947789293d7547e9e81ef97acb6a76a5085d5e04fc51");
	}
		
		

}
