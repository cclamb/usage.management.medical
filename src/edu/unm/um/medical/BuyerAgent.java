package edu.unm.um.medical;
//package medTrading.buyer;

import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.*;

import jade.domain.*;
import jade.domain.FIPAAgentManagement.*;

import java.util.Vector;
import java.util.Date;
import java.util.ArrayList;



public class BuyerAgent extends Agent {
  // The list of known seller agents
  private Vector sellerAgents = new Vector();

  

  /*Agent initializations*/
  protected void setup() {
    // Printout a welcome message
    System.out.println("Buyer-agent "+getAID().getName()+" is ready.");
    
    // Get names of seller agents as arguments
    Object[] args = getArguments();
    if (args != null && args.length > 0) {
      for (int i = 0; i < args.length; ++i) {
        AID seller = new AID((String) args[i], AID.ISLOCALNAME);
        sellerAgents.addElement(seller);
      }
    }

    // Call purchase method to setup initial search options
    purchase();

    
    // Update the list of seller agents 30 seconds
    addBehaviour(new TickerBehaviour(this, 30000) {
      protected void onTick() {
        // Update the list of seller agents
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("Data-selling");
        template.addServices(sd);
        try {
          DFAgentDescription[] result = DFService.search(myAgent, template);
          sellerAgents.clear();
          for (int i = 0; i < result.length; ++i) {
            sellerAgents.addElement(result[i].getName());
          }
        }
        catch (FIPAException fe) {
          fe.printStackTrace();
        }
      }
    } );
  }

  /*Agent clean-up*/
  protected void takeDown() {
    doDelete();
    // Printout a dismissal message
    System.out.println("Buyer-agent "+getAID().getName()+"terminated.");
  }

  //Method to store search options and send them to the behaviour
  public void purchase(){
    String medCondition = new String("Asthma");
    String conditionOfUse = new String("commercial-use");
    int maxPrice = 80;
    long deadline = System.currentTimeMillis() + 500000; // deadline is about 500 sec from now
    
    addBehaviour(new PurchaseManager(this, medCondition,conditionOfUse, maxPrice, deadline));
  }


  private class PurchaseManager extends TickerBehaviour {
    private String medCondition;
    private String conditionOfUse;
    private int maxPrice, startPrice;
    private long deadline, initTime, deltaT;

    private PurchaseManager(Agent a, String t, String c, int mp, long d) {
      super(a, 30000); // tick every 30 seconds
      medCondition = t;
      conditionOfUse = c;
      maxPrice = mp;
      deadline = d;
      initTime = System.currentTimeMillis();
      deltaT = deadline - initTime;
    }

    public void onTick() {
      long currentTime = System.currentTimeMillis();
      if (currentTime > deadline) {
        // Deadline expired
        System.out.println("Cannot buy data related to  "+medCondition);
        stop();
      }
      else {
        // Compute the currently acceptable price and start a negotiation
        long elapsedTime = currentTime - initTime;
        int acceptablePrice = (int)Math.round(1.0 * maxPrice * (1.0 * elapsedTime / deltaT));
        // System.out.println("elapsedTime"+elapsedTime+"deltaT"+deltaT+"acceptablePrice"+acceptablePrice+"maxPrice="+maxPrice);
        myAgent.addBehaviour(new Negotiator(medCondition, conditionOfUse, acceptablePrice, this));
      }
    }
  }



  /**
   * Inner class Negotiator.
   * This is the behaviour used by Buyer agents to actually
   * negotiate with seller agents the purchase of data.
  **/
  private class Negotiator extends Behaviour {
    private String medCondition;
    private String conditionOfUse;
    private int maxPrice;
    private PurchaseManager manager;
    private AID bestSeller; // The seller agent who provides the best offer
    private ArrayList<AID> agentList = new ArrayList<AID>();
    private int bestPrice; // The best offered price
    private int repliesCnt = 0; // The counter of replies from seller agents
    private MessageTemplate mt; // The template to receive replies
    private int step = 0;

    public Negotiator(String t, String c, int p, PurchaseManager m) {
      super(null);
      medCondition = t;
      conditionOfUse = c;
      maxPrice = p;
      manager = m;
    }

    public void action() {
      switch (step) {
        case 0:
          // Send the cfp to all sellers
          ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
          for (int i = 0; i < sellerAgents.size(); ++i) {
            cfp.addReceiver((AID)sellerAgents.elementAt(i));
          }
          cfp.setContent(medCondition +" " +conditionOfUse);
          cfp.setConversationId("data-trade");
          cfp.setReplyWith("cfp"+System.currentTimeMillis()); // Unique value
          myAgent.send(cfp);
          System.out.println("Sent Call for Proposal");

          // Prepare the template to get proposals
          mt = MessageTemplate.and(
          MessageTemplate.MatchConversationId("data-trade"),
          MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
          step = 1;
          break;
        case 1:
          // Receive all proposals/refusals from seller agents
          ACLMessage reply = myAgent.receive(mt);
          if (reply != null) {
            // Reply received
            if (reply.getPerformative() == ACLMessage.PROPOSE) {
              // This is an offer
              int price = Integer.parseInt(reply.getContent());
              System.out.println("Received Proposal at "+price+" when maximum acceptable price was "+maxPrice);
	      agentList.add(reply.getSender());
              if (bestSeller == null || price < bestPrice) {
                // This is the best offer at present
                bestPrice = price;
                bestSeller = reply.getSender();

              }
            }
            repliesCnt++;
            if (repliesCnt >= sellerAgents.size()) {
              // We received all replies
              step = 2;
            }
          }
          else {
            block();
          }
          break;
        case 2:
          if (bestSeller != null && bestPrice <= maxPrice) {
	    //Send reject-proposal to all the remaining sellers and
            // Send the purchase order to the seller that provided the best offer
	    for(int i = 0; i<agentList.size();i++){
		if(!agentList.get(i).equals(bestSeller)){
			ACLMessage reject = new ACLMessage();
			reject.setPerformative(ACLMessage.REJECT_PROPOSAL);
			reject.addReceiver(agentList.get(i));
			reject.setConversationId("data-trade");
			myAgent.send(reject);
		}
	    }
		
            ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
            order.addReceiver(bestSeller);
            order.setContent(medCondition);
            order.setConversationId("data-trade");
            order.setReplyWith("order"+System.currentTimeMillis());
            myAgent.send(order);
            System.out.println("sent Accept Proposal");
            // Prepare the template to get the purchase order reply
            mt = MessageTemplate.and(
              MessageTemplate.MatchConversationId("data-trade"),
              MessageTemplate.MatchInReplyTo(order.getReplyWith()));
            step = 3;
          }
          else {
            // If we received no acceptable proposals, send reject_proposal
	    for(int i = 0; i<agentList.size();i++){
		if(!agentList.get(i).equals(bestSeller)){
			ACLMessage reject = new ACLMessage();
			reject.setPerformative(ACLMessage.REJECT_PROPOSAL);
			reject.addReceiver(agentList.get(i));
			reject.setConversationId("data-trade");
			myAgent.send(reject);
		}
	    }
            step = 4;
          }
          break;
        case 3:
          // Receive the purchase order reply
          reply = myAgent.receive(mt);
          if (reply != null) {
            // Purchase order reply received
            if (reply.getPerformative() == ACLMessage.INFORM) {
              //extract the message
              System.out.println(reply.getContent());
              // Purchase successful. We can terminate
              System.out.println("Data with "+medCondition+" successfully purchased. Price = " + bestPrice);
              manager.stop();
            }
            step = 4;
          }
          else {
            block();
          }
          break;
      } // end of switch
    }

    public boolean done() {
      return step == 4;
    }
  } // End of inner class Negotiator
}







