package db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import db.DBConnection;
import entity.Order;
import entity.Route;
import entity.Order.OrderBuilder;
import external.GoogleMapAPI;

public class MySQLConnection implements DBConnection {
	private Connection conn;
	
	public MySQLConnection(){
		System.out.println();
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
			conn = DriverManager.getConnection(MySQLDBUtil.URL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void close() {
		// TODO Auto-generated method stub
	  	 if (conn != null) {
	  		 try {
	  			 conn.close();
	  		 } catch (Exception e) {
	  			 e.printStackTrace();
	  		 }
	  	 }
	}

	
	@Override
	public List<Route> searchRoutes(String origin, String destination) {
		// TODO Auto-generated method stub
		GoogleMapAPI googleMapAPI = new GoogleMapAPI();
		List<Route> routes = googleMapAPI.search(origin, destination);
		return routes;
	}
	

	@Override
	public int saveOrder(Order order, int temp_id) throws Exception {
		// TODO Auto-generated method stub
		if (conn == null) {
			System.err.println("DB connection failed");
			return -1;
		}
		
		if (temp_id <= 0) {
			System.err.println("No valid temp id");
			return -1;
		}
		
		int id = -1;
 		String sql;
 		PreparedStatement ps;
 		int robot_id = -1;
 		int drone_id = -1;
 		try {
 			sql = "SELECT * FROM temp WHERE temp_id = ?";
 			PreparedStatement temp_ps = conn.prepareStatement(sql);
 			temp_ps.setInt(1, temp_id);
	  		ResultSet temp_rs = temp_ps.executeQuery();
	  		
	  		if (temp_rs.next()) {
	  			robot_id = temp_rs.getInt("robot_id");
	  			drone_id = temp_rs.getInt("drone_id");
	  		} else {
	  			throw new Exception();
	  		}
 			
			 
 			conn.setAutoCommit(false);
 			
// 			sql = "SELECT @robot_id:=(SELECT robot_id FROM temp WHERE temp_id=?);";
// 			ps = conn.prepareStatement(sql);
// 			ps.setInt(1, temp_id);
// 			ps.execute();
//
// 			sql = "SELECT @drone_id:=(SELECT drone_id FROM temp WHERE temp_id=?);";
// 			ps = conn.prepareStatement(sql);
// 			ps.setInt(1, temp_id);
// 			ps.execute();
 			
			sql = "INSERT IGNORE INTO orders VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
 			ps = conn.prepareStatement(sql);
 			ps.setString(1, order.getUserID());
	//	  		 ps.setString(2, order.getName());
	  		ps.setString(2, order.getOriginAddr());
	  		ps.setString(3, order.getDestAddr());
	  		 
	//	  		 ps.setInt(4, order.getDistanceValue());
	//	  		 ps.setInt(5, order.getDurationValue());
	  		ps.setString(4, order.getDistanceText());
	  		ps.setString(5, order.getDurationText());
	  		 
	  		ps.setString(6, order.getVehicle());
	  		ps.setDouble(7, order.getPrice());
	  		ps.setString(8, order.getTimeStamp());;
	  		ps.setString(9, order.getTrackStatus());
	  		if (order.getVehicle().equals("robot")) {
	  			ps.setInt(10, robot_id); 
	  		} else if (order.getVehicle().equals("drone")) {
	  			ps.setInt(10, drone_id);
	  		} else {
	  			ps.setNull(10, Types.INTEGER);
	  		}
	  		ps.execute();
	  		
	  		sql = "SELECT max(order_id) FROM orders FOR UPDATE;";
	  		PreparedStatement statement = conn.prepareStatement(sql);
	  		ResultSet rs_id = statement.executeQuery();
	  		
	  		
	  		if (order.getVehicle().equals("robot")) {
	  			sql = "UPDATE robots SET busy=0, temp_order=0, order_id=-1 WHERE robot_id=?";
	 			ps = conn.prepareStatement(sql);
	 			ps.setInt(1, drone_id);
 				ps.execute();
	  			
	  			sql = "UPDATE robots SET busy=1, temp_order=0, order_id=(SELECT max(order_id) FROM orders) WHERE robot_id=?";
	 			ps = conn.prepareStatement(sql);
	 			ps.setInt(1, robot_id);
	  			ps.execute();
	  			
	  		} else if (order.getVehicle().equals("drone")) {
	  			sql = "UPDATE robots SET busy=0, temp_order=0, order_id=-1 WHERE robot_id=?";
	 			ps = conn.prepareStatement(sql);
	 			ps.setInt(1, robot_id);
	  			ps.execute();
	  			
	  			sql = "UPDATE robots SET busy=1, temp_order=0, order_id=(SELECT max(order_id) FROM orders) WHERE robot_id=?";
	 			ps = conn.prepareStatement(sql);
	 			ps.setInt(1, drone_id);
	  			ps.execute();
	  		}
	  		
	  		sql = "DELETE FROM temp WHERE temp_id=?";
 			ps = conn.prepareStatement(sql);
	  		ps.setInt(1, temp_id); 
  			ps.execute();
	  		
	  		
	  		conn.commit();
	  		
	  		
	  		while (rs_id.next()) {
	  			id = rs_id.getInt("max(order_id)");
	  		}
	  		
	  		
	  		
 		} catch (Exception e) {
 			e.printStackTrace();
 			throw new Exception("Invalid Input");
	  	}
		return id;
	}	

	
	@Override
	public String getFullname(String userId) {
		// TODO Auto-generated method stub
		if (conn == null) {
			return "";
		}		
		String name = "";
		try {
			String sql = "SELECT first_name, last_name FROM users WHERE user_id = ? ";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				name = rs.getString("first_name") + " " + rs.getString("last_name");
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return name;

	}
	
	
	@Override
	public boolean registerUser(String userId, String password, String firstname, String lastname) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return false;
		}

		try {
			String sql = "INSERT IGNORE INTO users VALUES (?, ?, ?, ?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, userId);
			ps.setString(2, password);
			ps.setString(3, firstname);
			ps.setString(4, lastname);
			
			return ps.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;	
	}
	
	
	@Override
	public boolean verifyLogin(String userId, String password) {
		// TODO Auto-generated method stub
		if (conn == null) {
			return false;
		}
		try {
			String sql = "SELECT user_id FROM users WHERE user_id = ? AND password = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, userId);
			stmt.setString(2, password);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return false;
	}
	
	@Override
	public JSONObject trackByID(int trackID) {
		// TODO Auto-generated method stub
		if (conn == null) {
			return null;
		}
		
		JSONObject obj = new JSONObject();
		try {
			String sql = "SELECT * from orders WHERE order_id = ?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setInt(1, trackID);
			
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				obj.put("order_id", rs.getInt("order_id"));
				obj.put("user_id", rs.getString("user_id"));
				obj.put("origin", rs.getString("origin"));
				obj.put("destination", rs.getString("destination"));
				obj.put("vehicle", rs.getString("vehicle"));
				obj.put("time_stamp", rs.getString("time_stamp"));
				obj.put("track_status", rs.getString("track_status"));
				
				obj.put("distance", rs.getString("distance"));
				obj.put("duration", rs.getString("duration"));
				
				obj.put("price", rs.getDouble("price"));
				obj.put("robot_id", rs.getDouble("robot_id"));

			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return obj;
	}
	
	
	@Override
	public JSONArray trackByUser(String user) {
		// TODO Auto-generated method stub
		if (conn == null) {
			return null;
		}
		
		JSONArray array = new JSONArray();
		try {
			String sql = "SELECT * from orders WHERE user_id = ?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, user);
			
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				JSONObject obj = new JSONObject();
				obj.put("order_id", rs.getInt("order_id"));
				obj.put("user_id", rs.getString("user_id"));
				obj.put("origin", rs.getString("origin"));
				obj.put("destination", rs.getString("destination"));
				obj.put("vehicle", rs.getString("vehicle"));
				obj.put("time_stamp", rs.getString("time_stamp"));
				obj.put("track_status", rs.getString("track_status"));
				
//				obj.put("distance", rs.getInt("distance"));
//				obj.put("duration", rs.getInt("duration"));
				obj.put("distance", rs.getString("distance"));
				obj.put("duration", rs.getString("duration"));
				obj.put("price", rs.getDouble("price"));
				
				array.put(obj.toString());
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return array;
	}
	
	
	@Override
	public int setPickUpByMachine(int order_id, String type) {
//		if (conn == null) {
//			return false;
//		}
		
		String track_status = null;
		int robot_id = -1;	
		try {
			// check if order status valid
			String sql = "SELECT * from orders WHERE order_id = ?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setInt(1, order_id);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				track_status = rs.getString("track_status");
			}
			if (track_status == null || !track_status.equals("OrderPlaced")) {
				System.out.println("Status Failure.");
				return -1;
			}
			
			// check if robot status valid
			sql = "SELECT * from robots WHERE busy = false AND type = ?";
			statement = conn.prepareStatement(sql);
			statement.setString(1, type);
			rs = statement.executeQuery();

			while (rs.next()) {
				robot_id = rs.getInt("robot_id");
				break;
			}
			if (robot_id == -1) {
				System.out.println("No robots available.");
				return -1;
			}
			
			// update order and robot
			sql = "UPDATE robots SET busy = true, order_id = ?, time_stamp = ? WHERE robot_id = ?";
			statement = conn.prepareStatement(sql);
			statement.setInt(1, order_id);
			statement.setString(2, new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
			statement.setInt(3, robot_id);
			statement.execute();

			sql = "UPDATE orders SET track_status = 'PickedUpByMachine', robot_id = ? WHERE order_id = ?";
			statement = conn.prepareStatement(sql);
			statement.setInt(1, robot_id);
			statement.setInt(2, order_id);
			statement.execute();

			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return robot_id;
	}
	
	@Override 
	public boolean setInTransit(int order_id, int robot_id) {
		String track_status = null;
		
		try {
			// check if order status valid
			String sql = "SELECT * from orders WHERE order_id = ?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setInt(1, order_id);
			
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				track_status = rs.getString("track_status");
				break;
			}
			
			if (!track_status.equals("PickedUpByMachine")) {
				System.out.println("Status Failure.");
				return false;
			}
			
			// check if robot status valid
			sql = "SELECT * from robots WHERE robot_id = ?";
			statement = conn.prepareStatement(sql);
			statement.setInt(1, robot_id);
			rs = statement.executeQuery();

			int check_id = -1;
			while (rs.next()) {
				check_id = rs.getInt("robot_id");
				break;
			}
			if (check_id == -1 || check_id != robot_id) {
				System.out.println("Robot ID unavailable.");
				return false;
			}
			
			// update order and robot
			sql = "UPDATE orders SET track_status = 'InTransit', time_stamp = ? WHERE order_id = ?";
			statement = conn.prepareStatement(sql);
			statement.setString(1, new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
			statement.setInt(2, order_id);
			statement.execute();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return true;
	}
	
	@Override 
	public int[] checkAvailability() {
		int[] availabilityArray = new int[] {0, 0, 0};
		if (conn == null) {
			return availabilityArray;
		}
		
		String sql;
		PreparedStatement statement;
		try {
			conn.setAutoCommit(false); // start transaction

			sql ="INSERT INTO temp (drone_id, robot_id) VALUES (NULL, NULL);";
			statement = conn.prepareStatement(sql);
			statement.execute();
			
			sql = "SELECT @temp_order_id:=(SELECT max(temp_id) from temp);";
			statement = conn.prepareStatement(sql);
			ResultSet rs_order = statement.executeQuery();

			sql = "SELECT @temp_robot_id:=(select robot_id from robots where busy = 0 AND type = 'robot' LIMIT 1);";
			statement = conn.prepareStatement(sql);
			ResultSet rs_robot = statement.executeQuery();
			
			sql = "UPDATE temp SET robot_id=@temp_robot_id WHERE temp_id=@temp_order_id;";
			statement = conn.prepareStatement(sql);
			statement.execute();
			
			sql = "UPDATE robots SET busy=1, order_id=@temp_robot_id, temp_order=1 WHERE robot_id=@temp_robot_id;";
			statement = conn.prepareStatement(sql);
			statement.execute();
			
			sql = "SELECT @temp_drone_id:=(select robot_id from robots where busy = 0 AND type = 'drone' LIMIT 1);";
			statement = conn.prepareStatement(sql);
			ResultSet rs_drone = statement.executeQuery();
			
			sql = "UPDATE temp SET drone_id=@temp_drone_id WHERE temp_id=@temp_order_id;";
			statement = conn.prepareStatement(sql);
			statement.execute();
			
			sql = "UPDATE robots SET busy=1, order_id=@temp_robot_id, temp_order=1 WHERE robot_id=@temp_drone_id;";
			statement = conn.prepareStatement(sql);
			statement.execute();
			
			
			conn.commit();
			
			while (rs_order.next()) {
				availabilityArray[0] = rs_order.getInt(1);
			}			
			while (rs_drone.next()) {
				availabilityArray[1] = rs_drone.getInt(1);
			}	
			while (rs_robot.next()) {
				availabilityArray[2] = rs_robot.getInt(1);
			}	
			
			return availabilityArray;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return availabilityArray;
	}
	
	@Override 
	public boolean setDelivered(int order_id, int robot_id) {
		String track_status = null;
		
		try {
			// check if order status valid
			String sql = "SELECT * from orders WHERE order_id = ?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setInt(1, order_id);
			
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				track_status = rs.getString("track_status");
				break;
			}
			
			if (!track_status.equals("InTransit")) {
				System.out.println("Status Failure.");
				return false;
			}
			
			// check if robot status valid
			sql = "SELECT * from robots WHERE robot_id = ?";
			statement = conn.prepareStatement(sql);
			statement.setInt(1, robot_id);
			rs = statement.executeQuery();

			int check_id = -1;
			while (rs.next()) {
				check_id = rs.getInt("robot_id");
				break;
			}
			if (check_id == -1 || check_id != robot_id) {
				System.out.println("Robot ID unavailable.");
				return false;
			}
			
			// update order and robot
			sql = "UPDATE orders SET track_status = 'Delivered', robot_id = NULL WHERE order_id = ?";
			statement = conn.prepareStatement(sql);
			statement.setInt(1, order_id);
			statement.execute();
			
			sql = "UPDATE robots SET busy = false, time_stamp = ? WHERE order_id = ?";
			statement = conn.prepareStatement(sql);
			statement.setString(1, new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
			statement.setInt(2, order_id);
			statement.execute();
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return true;
	}
}
