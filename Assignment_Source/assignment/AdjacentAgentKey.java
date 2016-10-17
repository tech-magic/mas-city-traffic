package assignment;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

public class AdjacentAgentKey {
	
	private static final String JUNCTION_AGENT_PREFIX = "J";
	
	private AID neighbourId;
	private int latestIncomingVehicleRate;
	
	public AdjacentAgentKey(String localName) {
		this.neighbourId = new AID(localName, AID.ISLOCALNAME);
		this.latestIncomingVehicleRate = -1;
	}

	public void setLatestIncomingVehicleRate(int latestIncomingVehicleRate) {
		this.latestIncomingVehicleRate = latestIncomingVehicleRate;
	}

	public int getLatestIncomingVehicleRate() {
		return latestIncomingVehicleRate;
	}

	public String getLocalName() {
		return this.neighbourId.getLocalName();
	}

	public AID getNeighbourId() {
		return neighbourId;
	}
	
	public boolean isRelatedWithAJunction() {
		return this.getLocalName().toUpperCase().startsWith(JUNCTION_AGENT_PREFIX);
	}
	
	public void notifyLatestOutgoingVehicleRate(int latestOutgoingVehicleRate, Agent parent) {
		try {
			
			// send the latest outgoing vehicle rate to the adjacent agent
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.addReceiver(this.neighbourId);
			msg.setLanguage("English");
			msg.setOntology("road-ontology");
			msg.setContent(Integer.toString(latestOutgoingVehicleRate));
			parent.send(msg);
			
			System.out.println("junction-agent " + parent.getAID().getLocalName() + " is sending latest vehicle outgoing rate " + 
					latestOutgoingVehicleRate + " to " + neighbourId.getLocalName());

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
