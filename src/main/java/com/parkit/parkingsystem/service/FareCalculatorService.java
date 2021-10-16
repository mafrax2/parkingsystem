package com.parkit.parkingsystem.service;

import java.util.concurrent.TimeUnit;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

	public void calculateFare(Ticket ticket) {
		TicketDAO dao = new TicketDAO();
		calculateFare(ticket, dao);
	}

	public void calculateFare(Ticket ticket, TicketDAO dao) {
		calculateFare(ticket, dao.checkReturn(ticket));
	}

	public void calculateFare(Ticket ticket, Boolean discount) {
		if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

		long inHour = ticket.getInTime().getTime();
		long outHour = ticket.getOutTime().getTime();

		long duration = outHour - inHour;

		float time = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS);
		time = time / 60;

		if (time < 0.5) {
			ticket.setPrice(0);
		} else {

			switch (ticket.getParkingSpot().getParkingType()) {
			case CAR: {
				if (discount) {
					ticket.setPrice(time * Fare.CAR_RATE_PER_HOUR * 0.05);
				} else {
					ticket.setPrice(time * Fare.CAR_RATE_PER_HOUR);
				}
				break;
			}
			case BIKE: {
				ticket.setPrice(time * Fare.BIKE_RATE_PER_HOUR);
				break;
			}
			default:
				throw new IllegalArgumentException("Unkown Parking Type");
			}

		}
	}

}