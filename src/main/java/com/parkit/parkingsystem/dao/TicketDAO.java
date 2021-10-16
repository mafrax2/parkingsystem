package com.parkit.parkingsystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

public class TicketDAO {

	private static final Logger logger = LogManager.getLogger("TicketDAO");

	public DataBaseConfig dataBaseConfig = new DataBaseConfig();

	public boolean saveTicket(Ticket ticket) {
		Connection con = null;
		PreparedStatement ps = null;
		int updateTicket = 0;
		try {
			con = dataBaseConfig.getConnection();
			try {

				ps = con.prepareStatement(DBConstants.SAVE_TICKET);
				ps.setInt(1, ticket.getParkingSpot().getId());
				ps.setString(2, ticket.getVehicleRegNumber());
				ps.setDouble(3, ticket.getPrice());
				ps.setTimestamp(4, new Timestamp(ticket.getInTime().getTime()));
				ps.setTimestamp(5,
						(ticket.getOutTime() == null) ? null : (new Timestamp(ticket.getOutTime().getTime())));
				updateTicket = ps.executeUpdate();
			} finally {
				dataBaseConfig.closePreparedStatement(ps);
			}
			return (updateTicket == 1);
		} catch (Exception ex) {
			logger.error("Error fetching next available slot", ex);
		} finally {
			dataBaseConfig.closeConnection(con);
		}
		return false;
	}

	public Ticket getTicket(String vehicleRegNumber) {
		Connection con = null;
		Ticket ticket = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			con = dataBaseConfig.getConnection();
			try {

				ps = con.prepareStatement(DBConstants.GET_TICKET);
				ps.setString(1, vehicleRegNumber);
				try {

					rs = ps.executeQuery();
					if (rs.next()) {
						ticket = new Ticket();
						ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1), ParkingType.valueOf(rs.getString(6)),
								false);
						ticket.setParkingSpot(parkingSpot);
						ticket.setId(rs.getInt(2));
						ticket.setVehicleRegNumber(vehicleRegNumber);
						ticket.setPrice(rs.getDouble(3));
						ticket.setInTime(rs.getTimestamp(4));
						ticket.setOutTime(rs.getTimestamp(5));
					}
				} finally {
					dataBaseConfig.closeResultSet(rs);
				}
			} finally {
				dataBaseConfig.closePreparedStatement(ps);
			}
		} catch (Exception ex) {
			logger.error("Error fetching next available slot", ex);
		} finally {
			dataBaseConfig.closeConnection(con);
		}
		return ticket;
	}

	public Ticket getLastTicket() {
		Connection con = null;
		Ticket ticket = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = dataBaseConfig.getConnection();
			try {
				ps = con.prepareStatement(DBConstants.GET_LAST_TICKET);
				try {
					rs = ps.executeQuery();
					if (rs.next()) {
						ticket = new Ticket();
						ticket.setId(rs.getInt(1));
						ticket.setPrice(rs.getInt(4));
						ticket.setVehicleRegNumber(rs.getString(3));
						ticket.setInTime(rs.getTimestamp(5));
						ticket.setOutTime(rs.getTimestamp(6));
					}
				} finally {
					dataBaseConfig.closeResultSet(rs);
				}
			} finally {
				dataBaseConfig.closePreparedStatement(ps);
			}
		} catch (Exception ex) {
			logger.error("Error fetching next available slot", ex);
		} finally {
			dataBaseConfig.closeConnection(con);
		}
		return ticket;
	}

	public Ticket getLastExitTicket() {
		Connection con = null;
		Ticket ticket = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = dataBaseConfig.getConnection();
			try {
				ps = con.prepareStatement(DBConstants.GET_LAST_EXIT);

				try {
					rs = ps.executeQuery();
					if (rs.next()) {
						ticket = new Ticket();

						ticket.setId(rs.getInt(1));
						ticket.setPrice(rs.getInt(4));
						ticket.setVehicleRegNumber(rs.getString(3));
						ticket.setInTime(rs.getTimestamp(5));
						ticket.setOutTime(rs.getTimestamp(6));
					}
				} finally {
					dataBaseConfig.closeResultSet(rs);
				}
			} finally {
				dataBaseConfig.closePreparedStatement(ps);
			}

		} catch (Exception ex) {
			logger.error("Error fetching next available slot", ex);
		} finally {
			dataBaseConfig.closeConnection(con);
		}
		return ticket;
	}

	public boolean updateTicket(Ticket ticket) {
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = dataBaseConfig.getConnection();
			try {

				ps = con.prepareStatement(DBConstants.UPDATE_TICKET);
				ps.setDouble(1, ticket.getPrice());
				ps.setTimestamp(2, new Timestamp(ticket.getOutTime().getTime()));
				ps.setInt(3, ticket.getId());
				ps.executeUpdate();
			} finally {
				dataBaseConfig.closePreparedStatement(ps);
			}
			return true;
		} catch (Exception ex) {
			logger.error("Error saving ticket info", ex);
		} finally {
			dataBaseConfig.closeConnection(con);
		}
		return false;
	}

	public Boolean checkReturn(Ticket ticket) {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = dataBaseConfig.getConnection();
			try {
				ps = con.prepareStatement(DBConstants.EXIST_TICKET);
				ps.setString(1, ticket.getVehicleRegNumber());
				try {
					rs = ps.executeQuery();
					if (rs.next()) {
						dataBaseConfig.closeResultSet(rs);
						dataBaseConfig.closePreparedStatement(ps);
						return true;
					}
				} finally {
					dataBaseConfig.closeResultSet(rs);
				}
			} finally {
				dataBaseConfig.closePreparedStatement(ps);
			}
		} catch (Exception ex) {
			logger.error("Error saving ticket info", ex);
		} finally {
			dataBaseConfig.closeConnection(con);
		}
		return false;
	}

}
