package sqlite;
import javax.swing.plaf.nimbus.State;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
public class sqliteDB {
	Connection c;
	public sqliteDB() {
		this.c = connect();
	}

	private Connection connect(){
		Connection c = null;
		try {

			c = DriverManager.getConnection("jdbc:sqlite:data.sqlite");
		}
		catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
		System.out.println("database connected");
		return c;
	}
	private void createIfNoTable() throws SQLException {
		String query = "CREATE TABLE IF NOT EXISTS users "+
				"(name TEXT,pass TEXT)";
		Statement statement = c.createStatement();
		statement.executeUpdate(query);
	}
	public boolean isUserExist(String name) throws SQLException {
		createIfNoTable();
		String query = "SELECT * FROM users WHERE name = ?";
		PreparedStatement statement = c.prepareStatement(query);
		statement.setString(1,name);
		statement.execute();
		ResultSet resultSet = statement.getResultSet();
		return resultSet.getFetchSize() !=0;
	}
	public boolean isPassCorrect(String name,String pass) throws SQLException, NoSuchAlgorithmException, UnsupportedEncodingException {
		String query = "SELECT * FROM users WHERE pass = ? AND name = ?";
		PreparedStatement statement = c.prepareStatement(query);
		byte[] bytesOfMessage = pass.getBytes(StandardCharsets.UTF_8);
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] digest = md.digest(bytesOfMessage);
		BigInteger bigInt = new BigInteger(1,digest);
		String hashtext = bigInt.toString(16);
		statement.setString(1,hashtext);
		statement.setString(2,name);
		statement.execute();
		ResultSet resultSet = statement.getResultSet();
		return resultSet.getFetchSize() !=0;
	}
	public void createUser(String name, String pass) throws NoSuchAlgorithmException, SQLException {
		byte[] bytesOfMessage = pass.getBytes(StandardCharsets.UTF_8);
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] digest = md.digest(bytesOfMessage);
		BigInteger bigInt = new BigInteger(1,digest);
		String hashtext = bigInt.toString(16);
		String query = "INSERT INTO users VALUES (?,?)";
		PreparedStatement statement = c.prepareStatement(query);
		statement.setString(1,name);
		statement.setString(2,hashtext);
		statement.executeUpdate();
	}
}
