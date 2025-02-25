package com.parkit.parkingsystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;

public class ParkingSpotDAO {
	private static final Logger logger = LogManager.getLogger("ParkingSpotDAO");

	public DataBaseConfig dataBaseConfig = new DataBaseConfig();

	public int getNextAvailableSlot(ParkingType parkingType) {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int result = -1;
		try {
			con = dataBaseConfig.getConnection();
			try {
				try {

					ps = con.prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT);
					ps.setString(1, parkingType.toString());
					rs = ps.executeQuery();
					if (rs.next()) {
						result = rs.getInt(1);
						;
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
		return result;
	}

	public int getAvailableParkingSlotsCount() {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int result = -1;
		try {
			con = dataBaseConfig.getConnection();
			try {
				ps = con.prepareStatement(DBConstants.AVAILABLE_PARKING_SPOTS);
				try {
					rs = ps.executeQuery();
					if (rs.next()) {
						result = rs.getInt(1);
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
		return result;
	}

	public boolean updateParking(ParkingSpot parkingSpot) {
		Connection con = null;
		PreparedStatement ps = null;
		int updateRowCount = 0;
		try {
			con = dataBaseConfig.getConnection();
			try {

				ps = con.prepareStatement(DBConstants.UPDATE_PARKING_SPOT);
				ps.setBoolean(1, parkingSpot.isAvailable());
				ps.setInt(2, parkingSpot.getId());
				updateRowCount = ps.executeUpdate();
			} finally {
				dataBaseConfig.closePreparedStatement(ps);
			}
			return (updateRowCount == 1);
		} catch (Exception ex) {
			logger.error("Error updating parking info", ex);
			return false;
		} finally {
			dataBaseConfig.closeConnection(con);
		}
	}

}
