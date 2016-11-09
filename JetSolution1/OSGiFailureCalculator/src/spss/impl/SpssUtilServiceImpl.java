/**
 * 
 */
package spss.impl;

import spss.api.SpssUtilService;

/**
 * @author IBM
 *
 */
public class SpssUtilServiceImpl implements SpssUtilService {

	/**
	 * 
	 */
	public SpssUtilServiceImpl() {
		// TODO Auto-generated constructor stub
	}

	// @Override
	public int calculateEngineFailure(int temp, int pressure, int rpm) {

		// Simulate expensive call to spss analytics engine
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		
		if (temp > 600 && pressure > 5 && rpm > 600)
			return 9;
		else
			return 1;
	}

}
