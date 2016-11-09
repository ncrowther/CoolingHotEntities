package dsi;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DSISendEvent {

	private static final Logger logger = Logger.getLogger(DSISendEvent.class
			.getName());

	private static String[] airplaneNames = {  "L6000", "W9759", "R0832",
			"H8401", "N3902", "E7833", "P4781", "C7103", "B5013", "P8045",
			"M3688", "F9719", "D9438", "R7021", "A5218", "S8362", "C0788",
			"Y9290", "U8414", "K4843", "B3350", "G0152", "X7998", "W3936",
			"B0427", "V4178", "Q0102", "C4523", "S3333", "V4786", "P1476",
			"Q8812", "C4592", "F0365", "U8794", "D4121", "H2752", "S5391",
			"K0942", "Y6100", "N2745", "Q0059", "O3114", "M9734", "S7661",
			"R2263", "F2418", "A1538", "W3561", "E5934", "T9396", "M8387",
			"L9304", "N2010", "O1008", "M7789", "S7663", "U0213", "F6786",
			"A0459", "K5771", "H6393", "H2995", "G8982", "R7103", "Z9444",
			"K5125", "Q8643", "O1594", "I6597", "Q7617", "J9458", "W5206",
			"J8809", "E1669", "U2406", "U6331", "G9758", "H5855", "N1643",
			"C3723", "K2625", "J3256", "W9224", "L9634", "R0049", "X6545",
			"G8369", "F3322", "I1228", "V9405", "Q2681", "K8342", "D5386",
			"G0280", "T6217", "T1273", "Y3305", "O2323", "N3921" };

	public static void sendEvent(final String urlStr, String message) {
		try {
			URL url = new URL(urlStr);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setAllowUserInteraction(false);
			conn.setRequestProperty("Content-Type", "application/xml");
			OutputStream out = conn.getOutputStream();
			Writer writer = new OutputStreamWriter(out, "UTF-8");
			writer.write(message);
			writer.close();
			out.close();
			if (conn.getResponseCode() != 200) {
				throw new IOException(conn.getResponseMessage());
			}
			conn.disconnect();

			// logger.log(Level.INFO, "Event sent to DSI");

		} catch (Exception e) {
			logger.log(Level.SEVERE, "failed to send DSI event to " + urlStr
					+ "\n", e);
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {

		final int NUMBER_OF_AIRPLANES = 1;
		final int NUMBER_OF_ENGINES = 1;
		final int NUMBER_OF_EVENTS = 500;

		final String urlStr = "http://localhost:9080/jetstatus/InboundHttpEndpoint";

		DsiEventXmlFactory xmlFactory = new DsiEventXmlFactory();

		xmlFactory.setTimestamp(ZonedDateTime.now().toString());

		// Send Events
		for (int i = 0; i < NUMBER_OF_EVENTS; i++) {

			if (i % 10 == 0) {
				System.out.print('.');
			}

			final int airplaneId = (int) (Math.random() * NUMBER_OF_AIRPLANES);
			String airplaneName = DSISendEvent.airplaneNames[airplaneId];

			final String engineId = airplaneName + "-Engine"
					+ String.valueOf((int) (Math.random() * NUMBER_OF_ENGINES));

			String engineEventXml = xmlFactory.createEngineEventXml(
					airplaneName, engineId);

			//System.out.println(engineEventXml);

			DSISendEvent.sendEvent(urlStr, engineEventXml);
		}
	}

}
