package at.ac.tuwien.thesis.caddc.service;

import java.util.Date;

import javax.ejb.Schedule;
import javax.ejb.Schedules;
import javax.ejb.Stateless;

import at.ac.tuwien.thesis.caddc.data.IDataFetch;
import at.ac.tuwien.thesis.caddc.data.XMLDataFetch;
import at.ac.tuwien.thesis.caddc.rest.client.HttpsClient;
import at.ac.tuwien.thesis.caddc.rest.client.RESTClient;

@Stateless
public class Scheduler {

//	Example with more schedules on the same method
//	@Schedules ({
//	    @Schedule(second="*", minute="*", hour="*"),
//	    @Schedule(second="0", minute="*", hour="6", timezone="America/New_York")
//	})
	@Schedule(second="0", minute="*", hour="*", timezone="America/New_York")
	public void importData() {
		System.out.println("Scheduler active on "+new Date());
		String url = "https://webservices.iso-ne.com/api/v1.1/hourlylmp/da/final/day/20150622/location/4008";
//		IDataFetch dataFetch = new XMLDataFetch();
//		dataFetch.fetch(url);
		
		if(System.getProperty("javax.net.ssl.trustStore") == null) {
			System.setProperty("javax.net.ssl.trustStore","C:\\Program Files\\Java\\jdk1.7.0_79\\jre\\lib\\security\\cacerts");
		}
		RESTClient client = new RESTClient();
		client.fetchURL(url);
	}
	
	@Schedule(second="0", minute="0", hour="*")
	public void importData2() {
		System.out.println("Scheduler2 active on "+new Date());
	}
}
