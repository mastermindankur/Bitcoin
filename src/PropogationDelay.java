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
import java.util.HashMap;



public class PropogationDelay  {
	
	OnTransactionBroadcastListener listener;
	Listener tcListener;
	HashMap nodes= new HashMap();
	
	
	
	
	public void calculatePropogationDelay(final Transaction tx,String txHash, String txHex,TransactionRelay tr,final long startTime, final PeerGroup pg) throws InterruptedException
	{
		
	    listener = new OnTransactionBroadcastListener() {
            @Override
            public void onTransaction(Peer p, Transaction t)
            {
            	if(tx.getHashAsString().equals(t.getHashAsString()))
            	{
            	System.out.println(" Heard back Tx ....");
    			 
    			 
    			 java.util.HashSet <PeerAddress> pas=  (HashSet<PeerAddress>) t.getConfidence().getBroadcastBy();
    			 for (PeerAddress pa : pas) {
    				 	if (!nodes.containsKey(pa.getAddr().toString())) // if node has not been visited before
    				 	{
    				     System.out.print(pa.getAddr());
    				     long stopTime = System.currentTimeMillis();
    	    			 long elapsedTime = (stopTime - startTime);
    				     System.out.println(" Propogation Delay is "+ elapsedTime);
    				     
    				     nodes.put(pa.getAddr().toString(), ""); // inserting in to HashMap
    				 	}
    				}

    			 
    			 
    			 calculateAnnouncedNodes(t.getConfidence(),pg);
    			 //System.exit(1);
            	}
            	else
            	{
            		System.out.println(" Recieved a Tx which was not our Tx !!! ... Ignoring it");
            	}
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
					//System.out.println(arg1.toString());
						 //System.out.println(tc.getConfidenceType());
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
    	   calculateAnnouncedNodes(tc,pg);
        }//end of while
       
        }
	
	void calculateAnnouncedNodes(TransactionConfidence tc, PeerGroup pg)
	{
		//System.out.println("TRANSACTION CONFIDENCE is ::"+ tc.getConfidenceType());
        List <Peer> peers= pg.getConnectedPeers();
        //System.out.println(" ----------LIST OF PEERS ---------------");
        
        int i=0;
        for (i = 0; i < peers.size(); i++) {
			//System.out.println(peers.get(i).getAddress() + " status ");
			//tc.markBroadcastBy(peers.get(i).getAddress())
        }
    
	 int broadcastCount=0;
	 java.util.HashSet <PeerAddress> pas=  (HashSet<PeerAddress>) tc.getBroadcastBy();
	 //System.out.println(" ----------LIST OF PEERS that have announced  Tx ---------------");
	 for (PeerAddress pa : pas) {
		    //System.out.println(pa.getAddr());
		    broadcastCount++;
		}
	 
	 System.out.println("Out of "+i+ " peers "+ broadcastCount +" have announced  the tx");
	 try {
		Thread.sleep(5000);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
        
        
	}

