import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import org.bitcoinj.core.*;
import org.bitcoinj.core.Wallet.BalanceType;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.params.MainNetParams;

import java.util.List;
import java.io.File;

import org.bitcoin.*;

import javax.xml.bind.DatatypeConverter;

/**
 * The following code sends bitcoins to a wallet in TestNet Network
 */

//http://private-anon-7041e2666-coinding.apiary-proxy.com/bitcoin/transaction/ae9ca69e93944c6430532b18d2fb9ea68290bcca12ec64a2f0f486153af7e15e/confidence
public class TransferToWallet {
	
	static TransactionStatus ts=new TransactionStatus();
	static TransactionRelay tr= new TransactionRelay();
	static PropogationDelay pd =new PropogationDelay();

    public static void main(String[] args) throws Exception {
    	
    	 long startTime = System.currentTimeMillis();
        // We use the WalletAppKit that handles all the boilerplate for us. Have a look at the Kit.java example for more details.
        NetworkParameters params = TestNet3Params.get();
        WalletAppKit kit = new WalletAppKit(params, new File("."), "sendrequest-example");
        System.out.println("Wallet Initialized");
        kit.startAsync();
        kit.awaitRunning();
        System.out.println("Send money from: " + kit.wallet().currentReceiveAddress().toString());
        
        
        //The Amount of Coins that need to be sent
        //float coinsTobeTransferred=.0000008;
        Coin value = Coin.parseCoin(".0008");

       // Address of the wallet in which the tranfer is being done
        String ashWallet="mx8E7Q5AXdsWRsgTo8DsKCZdwZC2ooJDjx";
        //String wallet="moVpQ1QFVeteSqcicqtDNC5rrLBpG7aY2v";
        
        
        Address to = new Address(params, ashWallet);
        try {
        	System.out.println("The following amount of coins are being sent "+0.0008*1000 +" mBTC");
            Wallet.SendResult result = kit.wallet().sendCoins(kit.peerGroup(), to, value);
            System.out.println("coins sent. transaction hash: " + result.tx.getHashAsString());
            
            
           String txHash= result.tx.getHashAsString();
           String txHex = DatatypeConverter.printHexBinary(result.tx.unsafeBitcoinSerialize());
           
           pd.calculatePropogationDelay(result.tx,txHash, txHex, tr,startTime ,kit.peerGroup());
           
           // tr.relayTransaction(txHex, startTime);
            
           // System.out.println(" Calling the function to calculate delay");
            //ts.calculateTime(result.tx, result.tx.getHashAsString(), txHex,tr,startTime);
            
           
            
            
        } catch (InsufficientMoneyException e) {
            System.out.println("Not enough coins in your wallet. Missing " + e.missing.getValue() + " satoshis are missing (including fees)");
            e.printStackTrace();
            System.out.println("Send money from: " + kit.wallet().currentReceiveAddress().toString() + "failed, please add coins to your wallet");

        }     
            }

    
}