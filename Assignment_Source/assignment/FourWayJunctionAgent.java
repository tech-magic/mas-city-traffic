package assignment;


public class FourWayJunctionAgent extends NWayJunctionAgent {
	
	public FourWayJunctionAgent() {
		super(4);
	}

	@Override
	protected void generateTrafficSchedule() {
		
		int maxVehicleRate1 = this.getMaxTrafficTimeForRoad(this.adjacentAgentKeys[0], this.adjacentAgentKeys[2]);
		int maxVehicleRate2 = this.getMaxTrafficTimeForRoad(this.adjacentAgentKeys[1], this.adjacentAgentKeys[3]);
		
		float timeForRoad1 = 0.0f;
		float timeForRoad2 = 0.0f;
		if(maxVehicleRate1 < 0 || maxVehicleRate2 < 0) {
			//schedule equal time
			timeForRoad1 = this.trafficCyclePeriod / 2.0f;
			timeForRoad2 = this.trafficCyclePeriod / 2.0f;
		} else {
			//schedule time based on proportion
			timeForRoad1 = this.trafficCyclePeriod * (maxVehicleRate1 / (float)(maxVehicleRate1 + maxVehicleRate2));
			timeForRoad2 = this.trafficCyclePeriod * (maxVehicleRate2 / (float)(maxVehicleRate1 + maxVehicleRate2));
		}
		
		System.out.println("Generating next traffic schedule by 4-way junction-agent " + getAID().getLocalName() + " ...");
		
		System.out.println("4-way junction-agent " + getAID().getLocalName() + " allocating " + 
				timeForRoad1 + " out of the total traffic cycle of " + this.trafficCyclePeriod + 
				" for the road from " + this.adjacentAgentKeys[0].getLocalName() + " to " +
				this.adjacentAgentKeys[2].getLocalName());
		
		System.out.println("4-way junction-agent " + getAID().getLocalName() + " allocating " + 
				timeForRoad2 + " out of the total traffic cycle of " + this.trafficCyclePeriod + 
				" for the road from " + this.adjacentAgentKeys[1].getLocalName() + " to " +
				this.adjacentAgentKeys[3].getLocalName());
	}
	
	private int getMaxTrafficTimeForRoad(AdjacentAgentKey agent1Pointer, AdjacentAgentKey agent2Pointer) {
		return Math.max(
				agent1Pointer.getLatestIncomingVehicleRate(), 
				agent2Pointer.getLatestIncomingVehicleRate());
	}
}
