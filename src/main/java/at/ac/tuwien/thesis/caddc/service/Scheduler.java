package at.ac.tuwien.thesis.caddc.service;

import java.util.Date;

import javax.ejb.Schedule;
import javax.ejb.Stateless;

@Stateless
public class Scheduler {

	@Schedule(second="0", minute="0", hour="*")
	public void importData() {
		System.out.println("Scheduler active on "+new Date());
	}
}
