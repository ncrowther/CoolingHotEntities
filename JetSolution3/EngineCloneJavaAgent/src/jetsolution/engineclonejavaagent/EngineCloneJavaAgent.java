package jetsolution.engineclonejavaagent;

import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import com.ibm.ia.common.AgentException;
import com.ibm.ia.common.EntityTypeException;
import com.ibm.ia.agent.EntityAgent;
import com.ibm.ia.model.Event;
import com.ibm.ia.model.Entity;

import entityModel.EngineClone;
import eventModel.ConceptFactory;
import eventModel.EngineEvent;
import eventModel.EngineSummaryEvent;

public class EngineCloneJavaAgent extends EntityAgent<Entity> {
   
	private ConceptFactory conceptFactory;

	@Override
	public void process(Event event) throws AgentException {

		// Summarize the Engine event
		if (event instanceof EngineEvent) {

			summarizeEngineEvent((EngineEvent) event);
		}
	}

	/**
	 * Summarize the Engine event
	 * 
	 * @param engineEvent the engine event to be summarized
	 * @throws AgentException
	 */
	private void summarizeEngineEvent(EngineEvent engineEvent) throws AgentException,
			EntityTypeException {

		EngineClone engineClone = (EngineClone) getBoundEntity();

		String EngineCloneName = engineEvent.getEngineCloneId();
		String engineName = engineEvent.getEngineId();

		if (conceptFactory == null) {
			conceptFactory = getConceptFactory(ConceptFactory.class);
		}

		if (engineClone == null) {

			printToLog(Level.INFO, "Creating a new engine Clone: "
					+ EngineCloneName + " associated to engine: " + engineName);

			engineClone = (EngineClone) createBoundEntity();
			engineClone.setEngineId(engineEvent.getEngineId());
			engineClone.setEngineCloneId(EngineCloneName);
			engineClone.setAircraftId(engineEvent.getAircraftId());
			engineClone.setAverageExhaustTemperature(engineEvent.getExhaustTemperature());
			engineClone.setAverageRpm(engineEvent.getRpm());
			engineClone.setAveragePressureRatio(engineEvent.getPressureRatio());
			engineClone.setEventCount(1);
			engineClone.set$CreationTime(engineEvent.getTimestamp());

			// Load the bound entity back into the grid
			updateBoundEntity(engineClone);
		} else {

			printToLog(Level.INFO,
					"Calculate averages in engine Clone: " + EngineCloneName);
	
			int eventCount = engineClone.getEventCount() + 1;
			engineClone.setEventCount(eventCount);
			
			int averagePressureRatio = (engineClone.getAveragePressureRatio() + engineEvent.getPressureRatio()) / 2;
			engineClone.setAveragePressureRatio(averagePressureRatio);
	
			int averageRpm = (engineClone.getAverageRpm() + engineEvent.getRpm()) / 2;
			engineClone.setAverageRpm(averageRpm);
	
			int averageExhaustTemperature = (engineClone.getAverageExhaustTemperature() + engineEvent.getExhaustTemperature()) / 2;
			engineClone.setAverageExhaustTemperature(averageExhaustTemperature);
		}
		
		// Schedule call back to emit summary event after n seconds
		if (!engineClone.isTimerRunning()) {

			engineClone.setTimerRunning(true);

			printToLog(Level.INFO,
					"Timer started for : " + engineClone.getEngineCloneId());

			final int TIMER_INTERVAL = 30;
			schedule(TIMER_INTERVAL, TimeUnit.SECONDS, "");

		}

		updateBoundEntity(engineClone);

	}

	@Override
	// Timer callback method
	public void process(String key, String cookie) throws AgentException {

		EngineClone engineClone = (EngineClone) getBoundEntity();

		if (engineClone != null) {

			// Emit summary event to Engine Rule Agent
			emitCounterEvent(engineClone);

			// Delete the entity as its job is done
			deleteBoundEntity();
		}
	}

	/**
	 * Emit an Engine Summary Event
	 * 
	 * @param DbbeamException
	 */
	private void emitCounterEvent(EngineClone EngineClone) {

		if (conceptFactory == null) {
			conceptFactory = getConceptFactory(ConceptFactory.class);
		}

		EngineSummaryEvent engineSummaryEvent = conceptFactory.createEngineSummaryEvent(ZonedDateTime.now());
		
		engineSummaryEvent.setEngineId(EngineClone.getEngineId());
		engineSummaryEvent.setAircraftId(EngineClone.getAircraftId());
		engineSummaryEvent.setAveragePressureRatio(EngineClone.getAveragePressureRatio());
		engineSummaryEvent.setAverageRpm(EngineClone.getAverageRpm());
		engineSummaryEvent.setAverageExhaustTemperature(EngineClone.getAverageExhaustTemperature());
		engineSummaryEvent.setEventCount(EngineClone.getEventCount());
		
		try {
			printToLog(Level.INFO,
					"Emit Engine Summary Event from : " + EngineClone.getEngineCloneId() + " to "  + EngineClone.getEngineId());
			emit(engineSummaryEvent);
		} catch (AgentException e) {
			printToLog(Level.SEVERE, "Error emitting Engine Summary Event : "
					+ engineSummaryEvent.get$Id());
			e.printStackTrace();
		}
	}
}
