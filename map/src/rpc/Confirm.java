package rpc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Order.OrderBuilder;

/**
 * Servlet implementation class Confirm
 */
@WebServlet("/confirm")
public class Confirm extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Confirm() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		HttpSession session = request.getSession(false);
//		if (session == null) {
//			response.setStatus(403);
//			return;
//		}
<<<<<<< HEAD
		
=======
//		
>>>>>>> 0d9c9352a8ae485b715052057e52b029f774723e
		DBConnection connection = DBConnectionFactory.getConnection();
	  	 try {
			JSONObject input = RpcHelper.readJSONObject(request);

<<<<<<< HEAD
//			String user_id = session.getAttribute("user_id").toString(); 
	  		String user_id = "1111";
			
=======
			//String user_id = session.getAttribute("user_id").toString(); 
//	  		String user_id = input.getString("user_id");
	  		
>>>>>>> 0d9c9352a8ae485b715052057e52b029f774723e
			String origin = input.getString("start_location");
			String destination = input.getString("destination");
			String vehicle = input.getString("vehicle");
			
			String distance_text = input.getString("distance");
			String duration_text = input.getString("duration");
			double price = Double.parseDouble(input.getString("price"));	  		 
	  		
			OrderBuilder builder = new OrderBuilder();
			//builder.setUserID(user_id);
			builder.setOriginAddr(origin);
			builder.setDestAddr(destination);
			builder.setDistanceText(distance_text);
			builder.setDurationText(duration_text);
			builder.setPrice(price);
			builder.setVehicle(vehicle);
			builder.setTimeStamp();
			builder.setTrackStatus("OrderPlaced");
			
	  		int id = connection.saveOrder(builder.build());
	  		
	  		
	  		JSONObject res = new JSONObject();
	  		res.put("result", "SUCCESS");
	  		res.put("order_id", id);
	  		RpcHelper.writeJsonObject(response, res);
	  		
	  	 } catch (Exception e) {
	  		 e.printStackTrace();
	  	 } finally {
	  		 connection.close();
	  	 }
	}
}
