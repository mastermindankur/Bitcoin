import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;

import org.bitcoinj.core.Peer;
import org.bitcoinj.core.PeerAddress;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionConfidence;


public class PropogationDelay {
	
	public void calculatePropogationDelay(Transaction tx,String txHash, String txHex,TransactionRelay tr,long startTime, PeerGroup pg) throws InterruptedException
	{
		
		
		 //code for broadcast transaction
        pg.broadcastTransaction(tx);
        
        
        while(true)
        {
        	
        Thread.sleep(5000);
        List <Peer> peers= pg.getConnectedPeers();
        System.out.println(" ----------LIST OF PEERS ---------------");
        
        int i=0;
        for (i = 0; i < peers.size(); i++) {
			System.out.println(peers.get(i).getAddress());
        }
        
		 TransactionConfidence tc= tx.getConfidence();
		 System.out.println("Transaction Confidence is "+ tc.getConfidenceType());
        
		 int broadcastCount=0;
		 java.util.HashSet <PeerAddress> pas=  (HashSet<PeerAddress>) tc.getBroadcastBy();
		 System.out.println(" ----------LIST OF PEERS that have broadcasted Tx ---------------");
		 for (PeerAddress pa : pas) {
			    System.out.println(pa.getAddr());
			    broadcastCount++;
			}
		 
		 System.out.println("Out of +"+i+ " peers "+ broadcastCount+" peers have broadcasted the transaction.");

		 
        }//end of while
        
	}

}
