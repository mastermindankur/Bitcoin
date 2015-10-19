
import org.bitcoinj.core.*;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.MemoryBlockStore;
import org.bitcoinj.utils.BriefLogFormatter;
import org.bitcoinj.params.MainNetParams;

import com.google.common.net.InetAddresses;
import com.google.common.util.concurrent.ListenableFuture;

import java.net.InetAddress;
import java.util.List;


import java.util.concurrent.TimeUnit;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Downloads the given transaction and its dependencies from a peers memory pool then prints them out.
 */
public class FetchTransactions {
    public static void main(String[] args) throws Exception {
        //BriefLogFormatter.init();
        System.out.println("Connecting to node");
        final NetworkParameters params = MainNetParams.get();

        BlockStore blockStore = new MemoryBlockStore(params);
        BlockChain chain = new BlockChain(params, blockStore);
        PeerGroup peerGroup = new PeerGroup(params, chain);
        peerGroup.start();
        
        System.out.println(InetAddress.getLocalHost());
        System.out.println(params.getPort());
        System.out.println(args[0]);
        
        
        String myPublicIp=getIp();
        System.out.println(myPublicIp);
        
        InetAddress addr = InetAddress.getByName("52.10.170.186");
        
        peerGroup.addAddress(new PeerAddress(addr,8333));
        peerGroup.waitForPeers(1).get();
        System.out.println("here");
        Peer peer = peerGroup.getConnectedPeers().get(0);
        
        Sha256Hash txHash = Sha256Hash.wrap("ecf18ea28f41c9e9075f494a444868ff4a594289a87902a6405a6b72c9e4290c");
        
        ListenableFuture<Transaction> future = peer.getPeerMempoolTransaction(txHash);
        
        
        System.out.println(future.isCancelled());
        System.out.println(future.isDone());

        while (!future.isCancelled()) {
            System.out.println("running..");
            Thread.sleep(10000);
            future.cancel(false);
        }
        
        System.out.println("Waiting for node to send us the requested transaction: " + txHash);
      
       Transaction tx = future.get();
       
      //  System.out.println(tx);

        System.out.println("Waiting for node to send us the dependencies ...");
        List<Transaction> deps = peer.downloadDependencies(tx).get();
        for (Transaction dep : deps) {
            System.out.println("Got dependency " + dep.getHashAsString());
        }

        System.out.println("Done.");
        peerGroup.stop();
    }
    
    
    public static String getIp() throws Exception {
        URL whatismyip = new URL("http://checkip.amazonaws.com");
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));
            String ip = in.readLine();
            return ip;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
