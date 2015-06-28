package at.ac.tuwien.thesis.caddc.service;

import java.util.Date;

import javax.ejb.Schedule;
import javax.ejb.Schedules;
import javax.ejb.Stateless;

import at.ac.tuwien.thesis.caddc.data.IDataFetch;
import at.ac.tuwien.thesis.caddc.data.XMLDataFetch;
import at.ac.tuwien.thesis.caddc.data.parse.NordPoolFinlandParser;
import at.ac.tuwien.thesis.caddc.rest.client.HttpsClient;
import at.ac.tuwien.thesis.caddc.rest.client.RESTClient;

@Stateless
public class Scheduler {

//	Example with more schedules on the same method
//	@Schedules ({
//	    @Schedule(second="*", minute="*", hour="*"),
//	    @Schedule(second="0", minute="*", hour="6", timezone="America/New_York")
//	})
//	@Schedule(second="0", minute="0", hour="*", timezone="America/New_York")
//	public void importData() {
//		System.out.println("Scheduler active on "+new Date());
//		String urlIsoNe = "https://webservices.iso-ne.com/api/v1.1/hourlylmp/da/final/day/20150622/location/4008";
////		IDataFetch dataFetch = new XMLDataFetch();
////		dataFetch.fetch(urlIsoNe);
//		
//		if(System.getProperty("javax.net.ssl.trustStore") == null) {
//			System.setProperty("javax.net.ssl.trustStore","C:\\Program Files\\Java\\jdk1.7.0_79\\jre\\lib\\security\\cacerts");
//		}
//		RESTClient client = new RESTClient();
//		client.fetchURL(urlIsoNe);
//	}
	
//	@Schedule(second="0/30", minute="*", hour="*")
//	public void importNordPoolData() {
//		System.out.println("Scheduler NordPool active on "+new Date());
//		String url = "http://www.nordpoolspot.com/globalassets/marketdata-excel-files/elspot-prices_2014_hourly_eur.xls";
//		RESTClient client = new RESTClient();
//		client.fetchNordPoolSpotData(url);
//	}
	
//	@Schedule(second="0", minute="0", hour="*")
//	public void importNordPoolFinlandData() {
//		System.out.println("NordPoolFinland Scheduler active on "+new Date());
//		String url = "http://www.nordpoolspot.com/Market-data1/Elspot/Area-Prices/FI/Hourly/?view=table";
//		NordPoolFinlandParser.parsePrices(url);
//	}
}
