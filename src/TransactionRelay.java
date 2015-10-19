import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class TransactionRelay {
	
	public void relayTransaction(String txHex, long startTime)  {
		
		try{

		String url = "http://test.webbtc.com/relay_tx.json";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		//add reuqest header
		con.setRequestMethod("POST");
		//con.setRequestProperty("User-Agent", USER_AGENT);
		//con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		String urlParameters = "wait=10&tx="+txHex;
		
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		//System.out.println("\nSending 'POST' request to URL : " + url);
		//System.out.println("Post parameters : " + urlParameters);
		//System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		//print result
		//System.out.println(response.toString());
		
		JSONObject jsonParsedResponse = new JSONObject(response.toString());
		int propagation=jsonParsedResponse.getJSONObject("propagation").getInt("received");
		int sent=jsonParsedResponse.getJSONObject("propagation").getInt("sent");
		Double percent=jsonParsedResponse.getJSONObject("propagation").getDouble("percent");
		
		 long stopTime = System.currentTimeMillis();
		 long elapsedTime = (stopTime - startTime)/1000;
		
		if(percent>0)
		{
			System.out.println("Transaction has been relayed and propogated through approximately "+percent+" of network in "+elapsedTime+" seconds");
		}
		
		
		}
		
		catch(java.io.IOException e)
		{
			System.out.println("http post request could not go through ... skip...trying");
			relayTransaction( txHex, startTime);
		}

	}

	
	private final String USER_AGENT = "Mozilla/5.0";

	public static void main(String[] args) throws Exception {

		TransactionRelay http = new TransactionRelay();
		

		System.out.println("\nTesting 2 - Send Http POST request");
		
		//http.relayTransaction("010000000101D6C85DCE5CC3811E4FED25EEF4E6A75ACA1597471E5D1BADFFB26B41010BC7010000006A47304402202550784FFE8036298A7582DD779A3612318AAFC634254A96F38516A00026C77B02203667ED8CF89F650706A78394C011B59A3A1F3120881E546D7D4757262635E17E012102695E059FD4F26A471452D426CEB785A2AC658E57A21C482648A9DCA1F5B11BF9FFFFFFFF02A0CDE605000000001976A914BCC39093DCE8C914F81EDFA1F8AAD0765E6C6B6D88AC80380100000000001976A914B62DC62323D2144C78897E853DC5EA1B7439004D88AC00000000");
		

	}

}
