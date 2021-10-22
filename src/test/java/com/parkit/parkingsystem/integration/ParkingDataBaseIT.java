package com.parkit.parkingsystem.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static ParkingSpotDAO parkingSpotDAO;
	private static DataBasePrepareService dataBasePrepareService;
	private static TicketDAO ticketDAO;
	@Mock
	private static InputReaderUtil inputReaderUtil;

	@BeforeAll
	private static void setUp() throws Exception {
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = dataBaseTestConfig;
		dataBasePrepareService = new DataBasePrepareService();
	}

	@BeforeEach
	private void setUpPerTest() throws Exception {
		dataBasePrepareService.clearDataBaseEntries();
	}

	@AfterAll
	private static void tearDown() {

	}

	@Test
	public void processIncomingVehicleIT() throws Exception {
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();

		Ticket latestTicket = ticketDAO.getLastTicket();

		assertNotNull(latestTicket);
		assertEquals(parkingSpotDAO.getAvailableParkingSlotsCount(), 4);

	}

	@Test
	public void testParkingACarNoSlots() throws Exception {
		dataBasePrepareService.fullParkingDataBaseEntries();
		when(inputReaderUtil.readSelection()).thenReturn(1);
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();

		assertEquals(parkingSpotDAO.getAvailableParkingSlotsCount(), 0);

	}

	@Test
	public void testParkingLotExit() throws Exception {
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		processIncomingVehicleIT();
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processExitingVehicle();
		Ticket ticket = ticketDAO.getTicket("ABCDEF");

		assertNotNull(ticket.getOutTime());
		assertNotNull(ticket.getPrice());

		// TODO: check that the fare generated and out time are populated correctly in
		// the database
	}

}
