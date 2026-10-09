package com.doritech.api.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BroadcastDataService {

	@Autowired
	private DataSource dataSource;

	public List<String> fetchDataAndBroadcast() throws SQLException {

		List<String> responseData = new ArrayList<>();

		String query = "SELECT \r\n" + "    device_master.branch_code,\r\n" + "    SMS_DETAILS.SENDERMOBILENO,\r\n"
				+ "    SMS_DETAILS.id,\r\n" + "    SMS_DETAILS.PANELSERIALNO,\r\n"
				+ "    COALESCE(device_master.device_name, SMS_DETAILS.PANELSERIALNO) AS DEVICE_NAME,\r\n"
				+ "    SMS_DETAILS.DATETIME,\r\n" + "    SMS_DETAILS.MAIN,\r\n" + "    SMS_DETAILS.SOLAR,\r\n"
				+ "    SMS_DETAILS.BATTERY,\r\n" + "    SMS_DETAILS.SIGNALFLAG,\r\n" + "    SMS_DETAILS.ARMED,\r\n"
				+ "    SMS_DETAILS.COMPLETEMESSAGE,\r\n" + "    device_master.device_id,\r\n" + "    CONCAT_WS(',',\r\n"

				// ACTIVE ZONES
				
				+ "        IF(SMS_DETAILS.ZONE1 = 'A', CONCAT('ZONE1 :', SMS_DETAILS.ZONE1), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE2 = 'A', CONCAT('ZONE2 :', SMS_DETAILS.ZONE2), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE3 = 'A', CONCAT('ZONE3 :', SMS_DETAILS.ZONE3), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE4 = 'A', CONCAT('ZONE4 :', SMS_DETAILS.ZONE4), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE5 = 'A', CONCAT('ZONE5 :', SMS_DETAILS.ZONE5), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE6 = 'A', CONCAT('ZONE6 :', SMS_DETAILS.ZONE6), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE7 = 'A', CONCAT('ZONE7 :', SMS_DETAILS.ZONE7), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE8 = 'A', CONCAT('ZONE8 :', SMS_DETAILS.ZONE8), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE9 = 'A', CONCAT('ZONE9 :', SMS_DETAILS.ZONE9), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE10 = 'A', CONCAT('ZONE10 :', SMS_DETAILS.ZONE10), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE11 = 'A', CONCAT('ZONE11 :', SMS_DETAILS.ZONE11), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE12 = 'A', CONCAT('ZONE12 :', SMS_DETAILS.ZONE12), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE13 = 'A', CONCAT('ZONE13 :', SMS_DETAILS.ZONE13), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE14 = 'A', CONCAT('ZONE14 :', SMS_DETAILS.ZONE14), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE15 = 'A', CONCAT('ZONE15 :', SMS_DETAILS.ZONE15), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE16 = 'A', CONCAT('ZONE16 :', SMS_DETAILS.ZONE16), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE17 = 'A', CONCAT('ZONE17 :', SMS_DETAILS.ZONE17), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE18 = 'A', CONCAT('ZONE18 :', SMS_DETAILS.ZONE18), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE19 = 'A', CONCAT('ZONE19 :', SMS_DETAILS.ZONE19), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE20 = 'A', CONCAT('ZONE20 :', SMS_DETAILS.ZONE20), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE21 = 'A', CONCAT('ZONE21 :', SMS_DETAILS.ZONE21), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE22 = 'A', CONCAT('ZONE22 :', SMS_DETAILS.ZONE22), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE23 = 'A', CONCAT('ZONE23 :', SMS_DETAILS.ZONE23), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE24 = 'A', CONCAT('ZONE24 :', SMS_DETAILS.ZONE24), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE25 = 'A', CONCAT('ZONE25 :', SMS_DETAILS.ZONE25), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE26 = 'A', CONCAT('ZONE26 :', SMS_DETAILS.ZONE26), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE27 = 'A', CONCAT('ZONE27 :', SMS_DETAILS.ZONE27), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE28 = 'A', CONCAT('ZONE28 :', SMS_DETAILS.ZONE28), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE29 = 'A', CONCAT('ZONE29 :', SMS_DETAILS.ZONE29), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE30 = 'A', CONCAT('ZONE30 :', SMS_DETAILS.ZONE30), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE31 = 'A', CONCAT('ZONE31 :', SMS_DETAILS.ZONE31), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE32 = 'A', CONCAT('ZONE32 :', SMS_DETAILS.ZONE32), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE33 = 'A', CONCAT('ZONE33 :', SMS_DETAILS.ZONE33), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE34 = 'A', CONCAT('ZONE34 :', SMS_DETAILS.ZONE34), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE35 = 'A', CONCAT('ZONE35 :', SMS_DETAILS.ZONE35), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE36 = 'A', CONCAT('ZONE36 :', SMS_DETAILS.ZONE36), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE37 = 'A', CONCAT('ZONE37 :', SMS_DETAILS.ZONE37), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE38 = 'A', CONCAT('ZONE38 :', SMS_DETAILS.ZONE38), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE39 = 'A', CONCAT('ZONE39 :', SMS_DETAILS.ZONE39), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE40 = 'A', CONCAT('ZONE40 :', SMS_DETAILS.ZONE40), NULL)\r\n"
				+ "    ) AS ACTIVE_ZONES,\r\n"

				// INACTIVE ZONES
				+ "    CONCAT_WS('_',\r\n"
				+ "        IF(SMS_DETAILS.ZONE1 != 'A', CONCAT('ZONE1 :', SMS_DETAILS.ZONE1), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE2 != 'A', CONCAT('ZONE2 :', SMS_DETAILS.ZONE2), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE3 != 'A', CONCAT('ZONE3 :', SMS_DETAILS.ZONE3), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE4 != 'A', CONCAT('ZONE4 :', SMS_DETAILS.ZONE4), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE5 != 'A', CONCAT('ZONE5 :', SMS_DETAILS.ZONE5), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE6 != 'A', CONCAT('ZONE6 :', SMS_DETAILS.ZONE6), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE7 != 'A', CONCAT('ZONE7 :', SMS_DETAILS.ZONE7), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE8 != 'A', CONCAT('ZONE8 :', SMS_DETAILS.ZONE8), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE9 != 'A', CONCAT('ZONE9 :', SMS_DETAILS.ZONE9), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE10 != 'A', CONCAT('ZONE10 :', SMS_DETAILS.ZONE10), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE11 != 'A', CONCAT('ZONE11 :', SMS_DETAILS.ZONE11), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE12 != 'A', CONCAT('ZONE12 :', SMS_DETAILS.ZONE12), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE13 != 'A', CONCAT('ZONE13 :', SMS_DETAILS.ZONE13), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE14 != 'A', CONCAT('ZONE14 :', SMS_DETAILS.ZONE14), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE15 != 'A', CONCAT('ZONE15 :', SMS_DETAILS.ZONE15), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE16 != 'A', CONCAT('ZONE16 :', SMS_DETAILS.ZONE16), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE17 != 'A', CONCAT('ZONE17 :', SMS_DETAILS.ZONE17), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE18 != 'A', CONCAT('ZONE18 :', SMS_DETAILS.ZONE18), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE19 != 'A', CONCAT('ZONE19 :', SMS_DETAILS.ZONE19), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE20 != 'A', CONCAT('ZONE20 :', SMS_DETAILS.ZONE20), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE21 != 'A', CONCAT('ZONE21 :', SMS_DETAILS.ZONE21), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE22 != 'A', CONCAT('ZONE22 :', SMS_DETAILS.ZONE22), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE23 != 'A', CONCAT('ZONE23 :', SMS_DETAILS.ZONE23), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE24 != 'A', CONCAT('ZONE24 :', SMS_DETAILS.ZONE24), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE25 != 'A', CONCAT('ZONE25 :', SMS_DETAILS.ZONE25), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE26 != 'A', CONCAT('ZONE26 :', SMS_DETAILS.ZONE26), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE27 != 'A', CONCAT('ZONE27 :', SMS_DETAILS.ZONE27), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE28 != 'A', CONCAT('ZONE28 :', SMS_DETAILS.ZONE28), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE29 != 'A', CONCAT('ZONE29 :', SMS_DETAILS.ZONE29), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE30 != 'A', CONCAT('ZONE30 :', SMS_DETAILS.ZONE30), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE31 != 'A', CONCAT('ZONE31 :', SMS_DETAILS.ZONE31), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE32 != 'A', CONCAT('ZONE32 :', SMS_DETAILS.ZONE32), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE33 != 'A', CONCAT('ZONE33 :', SMS_DETAILS.ZONE33), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE34 != 'A', CONCAT('ZONE34 :', SMS_DETAILS.ZONE34), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE35 != 'A', CONCAT('ZONE35 :', SMS_DETAILS.ZONE35), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE36 != 'A', CONCAT('ZONE36 :', SMS_DETAILS.ZONE36), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE37 != 'A', CONCAT('ZONE37 :', SMS_DETAILS.ZONE37), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE38 != 'A', CONCAT('ZONE38 :', SMS_DETAILS.ZONE38), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE39 != 'A', CONCAT('ZONE39 :', SMS_DETAILS.ZONE39), NULL),\r\n"
				+ "        IF(SMS_DETAILS.ZONE40 != 'A', CONCAT('ZONE40 :', SMS_DETAILS.ZONE40), NULL)\r\n"
				+ "    ) AS INACTIVE_ZONES\r\n"

				+ "FROM SMS_DETAILS\r\n" + "LEFT JOIN device_master\r\n"
				+ "    ON device_master.device_serial_no = SMS_DETAILS.PANELSERIALNO\r\n"
				+ "WHERE SMS_DETAILS.broadcastFlag = 'Y'\r\n" + "ORDER BY SMS_DETAILS.DATETIME DESC";

		try (Connection connection = dataSource.getConnection();
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(query)) {

			while (resultSet.next()) {

				String senderMobileNo = resultSet.getString("SENDERMOBILENO");
				String panelSerialNo = resultSet.getString("PANELSERIALNO");
				int smsDetailDevicePrimaryId = resultSet.getInt("id");

				String deviceName = resultSet.getString("DEVICE_NAME");
				String dateTime = resultSet.getString("DATETIME");
				String main = resultSet.getString("MAIN");
				String solar = resultSet.getString("SOLAR");
				String battery = resultSet.getString("BATTERY");
				String signalFlag = resultSet.getString("SIGNALFLAG");
				String armed = resultSet.getString("ARMED");

				String activeZones = resultSet.getString("ACTIVE_ZONES");
				String inactiveZones = resultSet.getString("INACTIVE_ZONES");

				String deviceId = resultSet.getString("device_id");
				String branchCode = resultSet.getString("branch_code");
				String completeMessage = resultSet.getString("COMPLETEMESSAGE");

				System.out.println("COMPLETE MSG :: " + completeMessage);

				String activeZonesDesiredName = "";
				String zoneDesiredName = "";

				// Normalize active zones
				activeZones = normalizeActiveZones(activeZones);

				if (activeZones != null && !activeZones.trim().isEmpty()) {

					String[] activeZoneArray = activeZones.split(",");

					for (String activeZone : activeZoneArray) {

						activeZone = activeZone.trim();

						if (activeZone.isEmpty()) {
							continue;
						}

						/*
						 * Example: activeZone = "ZONE8 : A"
						 *
						 * originalZone = "ZONE8" zoneStatus = "A"
						 */
						int colonIndex = activeZone.lastIndexOf(":");

						if (colonIndex <= 0) {
							System.out.println("INVALID ACTIVE ZONE FORMAT :: [" + activeZone + "]");
							continue;
						}

						String originalZone = activeZone.substring(0, colonIndex).trim();

						String zoneStatus = activeZone.substring(colonIndex + 1).trim();

						System.out.println("=================================");
						System.out.println("ACTIVE ZONE   :: [" + activeZone + "]");
						System.out.println("ORIGINAL ZONE :: [" + originalZone + "]");
						System.out.println("ZONE STATUS   :: [" + zoneStatus + "]");
						System.out.println("DEVICE ID     :: [" + deviceId + "]");

						/*
						 * Find alert configuration for EXACT zone + device.
						 */
						String subquery = "SELECT d.alert_type, d.zone_desired_name " + "FROM device_zone_details d "
								+ "WHERE TRIM(d.zone_original_name) = '" + originalZone + "' " + "AND d.device_id = '"
								+ deviceId + "'";

						System.out.println("SUBQUERY :: " + subquery);

						try (Statement statementSub = connection.createStatement();
								ResultSet resultSetSub = statementSub.executeQuery(subquery)) {

							if (resultSetSub.next()) {

								String alertType = resultSetSub.getString("alert_type");

								String desiredName = resultSetSub.getString("zone_desired_name");

								System.out.println("DB ZONE         :: [" + originalZone + "]");

								System.out.println("DB ALERT TYPE   :: [" + alertType + "]");

								System.out.println("DB DESIRED NAME :: [" + desiredName + "]");

								/*
								 * IMPORTANT FIX:
								 *
								 * Earlier: Alert Name 7:A Alert Name 8:A Alert Name 9:A
								 *
								 * Now: ZONE7:Alert Name 7:A ZONE8:Alert Name 8:A ZONE9:Alert Name 9:A
								 *
								 * Zone number stays attached to its alert.
								 */
								if (activeZonesDesiredName.isEmpty()) {

									activeZonesDesiredName = originalZone + ":" + alertType + ":" + zoneStatus;

								} else {

									activeZonesDesiredName += "_" + originalZone + ":" + alertType + ":" + zoneStatus;
								}

								if (desiredName != null && !desiredName.trim().isEmpty()) {

									if (zoneDesiredName.isEmpty()) {

										zoneDesiredName = desiredName;

									} else {

										zoneDesiredName += "_" + desiredName;
									}
								}

								System.out.println("FINAL ZONE MAPPING :: " + originalZone + " -> " + alertType + " -> "
										+ zoneStatus);

							} else {

								System.out.println(
										"NO MAPPING FOUND :: " + "ZONE=" + originalZone + ", DEVICE=" + deviceId);
							}
						}
					}
				}

				/*
				 * Final data sent to WebSocket.
				 */
				String data = branchCode + "," + deviceName + "," + dateTime + "," + main + "," + solar + "," + battery
						+ "," + signalFlag + "," + armed + "," + activeZonesDesiredName + "," + zoneDesiredName + ","
						+ completeMessage + "," + inactiveZones + "," + smsDetailDevicePrimaryId;

				System.out.println("=================================");
				System.out.println("ACTIVE ZONE ALERT DATA :: " + activeZonesDesiredName);
				System.out.println("ZONE DESIRED DATA      :: " + zoneDesiredName);
				System.out.println("FINAL DATA              :: " + data);
				System.out.println("=================================");

				responseData.add(data);

				/*
				 * Mark this SMS as broadcasted.
				 */
				try (Statement updateStatement = connection.createStatement()) {

					String updateQuery = "UPDATE SMS_DETAILS SET broadcastFlag='N' " + "WHERE PANELSERIALNO='"
							+ panelSerialNo + "' AND id='" + smsDetailDevicePrimaryId + "'";

					System.out.println("UPDATE QUERY :: " + updateQuery);

					int updateStatus = updateStatement.executeUpdate(updateQuery);

					System.out.println("BROADCAST FLAG UPDATE STATUS :: " + updateStatus);

				} catch (Exception e) {
					e.printStackTrace();
				}

				// Broadcast data to connected WebSocket clients
				broadcastData(data);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return responseData;
	}

	private void broadcastData(String data) {
		System.out.println("Broadcasting data: " + data);
	}

	private String normalizeActiveZones(String activeZones) {

		if (activeZones == null || activeZones.trim().isEmpty()) {
			return activeZones;
		}

		Set<Integer> activeZoneSet = new HashSet<>();

		for (String z : activeZones.split(",")) {

			if (z.contains("ZONE")) {

				int colonIndex = z.indexOf(":");

				if (colonIndex > 0) {

					int zoneNo = Integer.parseInt(z.substring(z.indexOf("ZONE") + 4, colonIndex).trim());

					activeZoneSet.add(zoneNo);
				}
			}
		}

		// Rule 1: Zone 5 & 6 → keep only Zone 6
		if (activeZoneSet.contains(5) && activeZoneSet.contains(6)) {
			activeZoneSet.remove(5);
		}

		// Rule 2:
		// If Zone 7 and Zone 8 are active, keep Zone 8.
		// If Zone 7 and Zone 9 are active, keep Zone 9.
		// Zone 8 remains Zone 8.
		if (activeZoneSet.contains(7) && activeZoneSet.contains(8)) {
			activeZoneSet.remove(7);
		}

		if (activeZoneSet.contains(7) && activeZoneSet.contains(9)) {
			activeZoneSet.remove(7);
		}

		// Rebuild string
		List<Integer> sorted = new ArrayList<>(activeZoneSet);
		Collections.sort(sorted);

		StringBuilder sb = new StringBuilder();

		for (int z : sorted) {
			sb.append("ZONE").append(z).append(" :A,");
		}

		return sb.length() > 0 ? sb.substring(0, sb.length() - 1) : "";
	}

}

