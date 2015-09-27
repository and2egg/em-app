package at.ac.tuwien.thesis.caddc.service;

import java.util.Date;

import javax.ejb.Schedule;
import javax.ejb.Stateless;

@Stateless
public class Scheduler2 {

	@Schedule(second="0", minute="*", hour="12")
	public void importData() {
		System.out.println("Scheduler2 active on "+new Date());
	}
}
