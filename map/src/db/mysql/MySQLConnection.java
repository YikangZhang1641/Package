package db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	public void saveOrder(Order order) {
		// TODO Auto-generated method stub
		if (conn == null) {
			System.err.println("DB connection failed");
			return;
		}
		
		 try {
	  		 String sql = "INSERT IGNORE INTO orders VALUES (NULL, ?, ?, ?, ?, ?, ?, ?)";
	  		 PreparedStatement ps = conn.prepareStatement(sql);
	  		 ps.setString(1, order.getUserID());
//	  		 ps.setString(2, order.getName());
	  		 ps.setString(2, order.getOriginAddr());
	  		 ps.setString(3, order.getDestAddr());
	  		 ps.setInt(4, order.getDistanceValue());
	  		 ps.setInt(5, order.getDurationValue());
	  		 ps.setString(6, order.getVehicle());
	  		 ps.setDouble(7, order.getPrice());
//	  		 ps.setNull(8, Types.INTEGER);;
	  		 ps.execute();
//	  		
//	  		 sql = "INSERT IGNORE INTO categories VALUES(?, ?)";
//	  		 ps = conn.prepareStatement(sql);
//	  		 ps.setString(1, order.getorderId());
//	  		 for(String category : order.getCategories()) {
//	  			 ps.setString(2, category);
//	  			 ps.execute();
//	  		 }
	  		
	  	 } catch (Exception e) {
	  		 e.printStackTrace();
	  	 }
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
}
