package spss.api;

public interface SpssUtilService {
	
	/**
	 * Return likely-hood of engine failure 
	 */
	public int calculateEngineFailure(int temp, int pressureRatio, int rpm );

}
