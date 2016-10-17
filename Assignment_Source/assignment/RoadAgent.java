package assignment;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Random;

/**
 * Road agent representing entry points for the city
 * java -cp <. . .> jade.Boot r<road_outlet_index>:RoadAgent(j<nearest_junction_index> <report_period>);
 * @author wp40155
 *
 */
public class RoadAgent extends Agent {
	
	private AID nearestJunctionAgentID;
	private int reportingPeriod;
	
	protected void setup() {
		
		System.out.println("Setting up a new road agent!");
		System.out.println("Local-name is "+ getAID().getLocalName());
		System.out.println("GUID is "+ getAID().getName());
		
		// Retrieve the nearest junction agent
		Object[] args = getArguments();
		if (args != null && args.length > 1) {
			
			// load settings for nearest junction and reporting period
			String nearestJunctionName = (String)args[0];
			String reportingPeriodAsString = (String)args[1]; 
			this.reportingPeriod = Integer.parseInt(reportingPeriodAsString);
			this.nearestJunctionAgentID = new AID(nearestJunctionName, AID.ISLOCALNAME);
			
			// add a behaviour to report incoming vehicle rate periodically
			addBehaviour(new TickerBehaviour(this, this.reportingPeriod) {

				@Override
				protected void onTick() {
					
					// get latest statistics on incoming vehicle rate
					int latestIncomingVehicleRate = this.getLatestIncomingVehicleRate();					
					
					try {
						
						// send the latest statistics to nearest junction
						ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
						msg.addReceiver(nearestJunctionAgentID);
						msg.setLanguage("English");
						msg.setOntology("road-ontology");
						msg.setContent(Integer.toString(latestIncomingVehicleRate));
						myAgent.send(msg);
						
						System.out.println("Road-agent " + getAID().getLocalName() + 
								" is sending latest incoming vehicle rate " + latestIncomingVehicleRate + 
								" to junction-agent " + nearestJunctionAgentID.getLocalName());

					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				
				// this is a dummy method returning the latest vehicle rate from a camera
				// simulates reading inputs from the percepts of the agent
				private int getLatestIncomingVehicleRate() {
					Random rand = new Random();
					int next = rand.nextInt(10);
					return next;
				}
				
			});
		} else {
			System.out.println("Wrong configuration for the road agent : " + getAID().getLocalName());
		}
	}
}
