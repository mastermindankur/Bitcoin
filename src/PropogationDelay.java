import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;

import org.bitcoinj.core.Peer;
import org.bitcoinj.core.PeerAddress;
import org.bitcoinj.core.listeners.OnTransactionBroadcastListener;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionConfidence;
import org.bitcoinj.core.TransactionConfidence.Listener;



public class PropogationDelay  {
	
	OnTransactionBroadcastListener listener;
	Listener tcListener;
	
	
	
	
	
	public void calculatePropogationDelay(Transaction tx,String txHash, String txHex,TransactionRelay tr,long startTime, PeerGroup pg) throws InterruptedException
	{
		
		
	    listener = new OnTransactionBroadcastListener() {
            @Override
            public void onTransaction(Peer p, Transaction t)
            {
            	System.out.println(" Heard back Tx ....");
    			 long stopTime = System.currentTimeMillis();
    			 long elapsedTime = (stopTime - startTime);
    			 System.out.println("Propogation Delay is "+elapsedTime);
    			 //System.exit(1);
            }

        };
        
        tcListener =new Listener(){

			@Override
			public void onConfidenceChanged(TransactionConfidence tc,
					ChangeReason arg1) {
				// TODO Auto-generated method stub
				if(tc.getConfidenceType()==TransactionConfidence.ConfidenceType.BUILDING)
				{
					System.out.println("Onconfidence changed tx confidence is ........"+ tc.getConfidenceType());
					System.out.println(arg1.toString());
						 System.out.println(tc.getConfidenceType());
		    			 long stopTime = System.currentTimeMillis();
		    			 long elapsedTime = (stopTime - startTime);
		    			 System.out.println("Propogation Delay is "+elapsedTime);
		    			 System.exit(1);
				} // end of if
				
			}
  
        };
     
        //code for broadcast transaction
        System.out.println("Broadcasted the tx");
        pg.broadcastTransaction(tx, 1);
        System.out.println("Setting the listener on broadcast Tx");
        pg.addOnTransactionBroadcastListener(listener);
        
        System.out.println("Trasaction Confidence is ::"+ tx.getConfidence().getConfidenceType());
        TransactionConfidence tc= tx.getConfidence();
        tc.addEventListener(tcListener);
       
        
       while(true)
        {	
    	   System.out.println("TRANSACTION CONFIDENCE is ::"+ tx.getConfidence().getConfidenceType());
	        List <Peer> peers= pg.getConnectedPeers();
	        System.out.println(" ----------LIST OF PEERS ---------------");
	        
	        int i=0;
	        for (i = 0; i < peers.size(); i++) {
				System.out.println(peers.get(i).getAddress() + " status ");
				//tc.markBroadcastBy(peers.get(i).getAddress())
	        }
        
		 int broadcastCount=0;
		 java.util.HashSet <PeerAddress> pas=  (HashSet<PeerAddress>) tc.getBroadcastBy();
		 System.out.println(" ----------LIST OF PEERS that have announced  Tx ---------------");
		 for (PeerAddress pa : pas) {
			    System.out.println(pa.getAddr());
			    broadcastCount++;
			}
		 
		 System.out.println("Out of "+i+ " peers "+ broadcastCount +" have announced  the tx");
		 
		 /*if (tx.getConfidence().toString().contains("best chain"))
		 {
			 long stopTime = System.currentTimeMillis();
			 long elapsedTime = (stopTime - startTime)/1000;
			 System.out.println("Propogation Delay is "+elapsedTime);
			 System.exit(1);
		 }*/
		 
		 Thread.sleep(5000);
        }//end of while
       
        }
        
        
	}

