package at.ac.tuwien.thesis.caddc.model.type;

/**
 * 
 */
public class EnergyPrice {

	private String date;
	private Double price;
	
	
	public EnergyPrice(String date, Double price) {
		this.date = date;
		this.price = price;
	}


	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}


	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}


	/**
	 * @return the price
	 */
	public Double getPrice() {
		return price;
	}


	/**
	 * @param price the price to set
	 */
	public void setPrice(Double price) {
		this.price = price;
	}
	
}
