package assignment;

import jade.core.Agent;
import java.util.Random;

import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

public abstract class NWayJunctionAgent extends Agent {
	
	protected AdjacentAgentKey[] adjacentAgentKeys;
	protected int reportingPeriod;
	protected int trafficCyclePeriod;
	
	protected int degreeOfJunction;
	
	protected NWayJunctionAgent(int degreeOfJunction) {
		this.degreeOfJunction = degreeOfJunction;
		this.adjacentAgentKeys = new AdjacentAgentKey[this.degreeOfJunction];
	}
	
	protected void setup() {
		
		System.out.println("Initializing a new " + this.degreeOfJunction + "-way agent with local name " + 
				getAID().getLocalName() + " and GUID " + getAID().getName());
				
		// Retrieve the nearest junction agent
		Object[] args = getArguments();
		if (args != null && args.length >= (this.degreeOfJunction + 2)) {
			
			// load settings for adjacent junctions
			int currArgIndex = 0;
			for(; currArgIndex < this.degreeOfJunction; currArgIndex++) {
				adjacentAgentKeys[currArgIndex] = new AdjacentAgentKey((String)args[currArgIndex]);
			}
			
			// load settings for traffic cycle period
			String trafficCyclePeriodAsString = (String)args[currArgIndex];
			this.trafficCyclePeriod = Integer.parseInt(trafficCyclePeriodAsString);
			
			currArgIndex++;
			
			// load settings for reporting period
			// a multiple (>= 1) of trafficCyclePeriod is better
			// otherwise no-use
			String reportingPeriodAsString = (String)args[currArgIndex];
			this.reportingPeriod = Integer.parseInt(reportingPeriodAsString);
			
			// add a behaviour to report outgoing vehicle rate for the adjacent junctions periodically
			addBehaviour(new TickerBehaviour(this, this.reportingPeriod) {

				@Override
				protected void onTick() {
					
					for(int currAdjAgentIdx = 0; currAdjAgentIdx < adjacentAgentKeys.length; currAdjAgentIdx++) {
						
						AdjacentAgentKey currAdjAgent = adjacentAgentKeys[currAdjAgentIdx];
						
						if(currAdjAgent != null && currAdjAgent.isRelatedWithAJunction()) {
							
							int latestOutgoingVehicleRateForCurrAgent = 
								getLatestOutgoingVehicleRate(currAdjAgentIdx);
						
							currAdjAgent.notifyLatestOutgoingVehicleRate(
								latestOutgoingVehicleRateForCurrAgent, myAgent);
						}
					}
				}
			});
			
			// add a behaviour to prepare the next schedule based on incoming messages from
			// adjacent agents
			addBehaviour(new TickerBehaviour(this, this.trafficCyclePeriod) {

				@Override
				protected void onTick() {
					
					boolean messageProcessingDoneForThisRound = false;
					int messagesProcessedForThisRound = 0;
					while(!messageProcessingDoneForThisRound) {
						
						ACLMessage nextMessage = myAgent.receive();
						
						if(nextMessage != null && 
								messagesProcessedForThisRound <= adjacentAgentKeys.length + 2) {
							updateAdjacentAgentPointers(nextMessage);							
							messagesProcessedForThisRound++;
						} else {
							messageProcessingDoneForThisRound = true;
						}
					}
					
					generateTrafficSchedule();					
				}
			});
			
		} else {
			System.out.println("Wrong configuration for the " + this.degreeOfJunction + "-way agent : " + getAID().getLocalName());
		}
	}
	
	// this is a dummy method returning the latest vehicle rate from a camera
	// simulates reading inputs from the percepts of the agent
	protected int getLatestOutgoingVehicleRate(int adjacentAgentIndex) {
		Random rand = new Random();
		int next = rand.nextInt(10);
		return next;
	}
	
	private void updateAdjacentAgentPointers(ACLMessage nextMessage) {
		
		AID senderAgentID = nextMessage.getSender();
		AdjacentAgentKey messageRelatedAgent = null;
		
		for(int currAdjAgentIdx = 0; currAdjAgentIdx < adjacentAgentKeys.length; currAdjAgentIdx++) {
			
			AdjacentAgentKey currAdjAgent = adjacentAgentKeys[currAdjAgentIdx];
			
			if(currAdjAgent != null) {
				
				if(currAdjAgent.getNeighbourId().getLocalName().
						equalsIgnoreCase(senderAgentID.getLocalName())) {
					
					messageRelatedAgent = currAdjAgent;
					break;
				}
			}
									
		}
		
		if(messageRelatedAgent != null) {
			String latestIncomingVehicleRateAsStr = nextMessage.getContent();
			int latestIncomingVehicleRate = Integer.parseInt(latestIncomingVehicleRateAsStr);
			messageRelatedAgent.setLatestIncomingVehicleRate(latestIncomingVehicleRate);
			
			System.out.println("junction-agent " + getAID().getLocalName() + " is receiving latest vehicle incoming rate " + 
					latestIncomingVehicleRate + " from " + senderAgentID.getLocalName());
			
		} else {
			System.out.println("[ERROR] Message from a non-adjacent agent " + senderAgentID.getLocalName() + 
					" is received by junction-agent " + getAID().getLocalName());
		}
		
	}
	
	protected abstract void generateTrafficSchedule();
}


