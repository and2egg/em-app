package at.ac.tuwien.thesis.caddc.data.market;

import java.io.IOException;
import java.net.ConnectException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import at.ac.tuwien.thesis.caddc.data.fetch.DataFetch;
import at.ac.tuwien.thesis.caddc.data.fetch.DataFetcher;
import at.ac.tuwien.thesis.caddc.data.fetch.DataFetcherFinland;
import at.ac.tuwien.thesis.caddc.data.fetch.FetchDataException;
import at.ac.tuwien.thesis.caddc.data.fetch.FileDataFetch;
import at.ac.tuwien.thesis.caddc.data.fetch.MissingDataException;
import at.ac.tuwien.thesis.caddc.data.fetch.URLDataFetch;
import at.ac.tuwien.thesis.caddc.data.parse.ParserFinland;
import at.ac.tuwien.thesis.caddc.data.parse.ParserFactory;
import at.ac.tuwien.thesis.caddc.data.parse.types.HTMLTableParser;
import at.ac.tuwien.thesis.caddc.data.parse.types.impl.HTMLTableParserFinland;
import at.ac.tuwien.thesis.caddc.data.resource.ResourceManagerFinland;
import at.ac.tuwien.thesis.caddc.data.resource.types.ResourceType;
import at.ac.tuwien.thesis.caddc.model.Location;
import at.ac.tuwien.thesis.caddc.rest.client.RESTClient;



/**
 * Defines a MarketData Instance responsible for retrieving energy
 * market data from Finland for different data sources
 */
public class MarketDataFinland extends MarketData {
	
	/**
	 * Create a MarketData Instance with the given location
	 * @param location the location for this MarketData Instance
	 */
	public MarketDataFinland(Location location) {
		super(location);
		this.resourceManager = new ResourceManagerFinland();
	}

	/**
	 * @return
	 * @see at.ac.tuwien.thesis.caddc.data.market.MarketData#getResourceType()
	 */
	@Override
	public ResourceType getResourceType() {
		return resourceManager.get();
	}

}
