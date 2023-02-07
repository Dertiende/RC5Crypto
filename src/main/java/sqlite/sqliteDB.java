package sqlite;
import GUI.controllers.Encryption;
import main.rc5;
import main.utils;
import main.rc5Obj;
import jakarta.xml.bind.DatatypeConverter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
public class sqliteDB {
	static Encryption encryption;
	static rc5Obj cli;
	static Connection c;
	public sqliteDB(rc5Obj cli) {
		c = connect();
		sqliteDB.cli = cli;
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
		//System.out.println("Database connected.");
		return c;
	}
	private void createIfNoUserTable() throws SQLException {
		String query = "CREATE TABLE IF NOT EXISTS users "+
				"(name TEXT,pass TEXT)";
		Statement statement = c.createStatement();
		statement.executeUpdate(query);
	}

	private static void createIfNoRC5Table() throws SQLException {
		String query = "CREATE TABLE IF NOT EXISTS RC5 "+
           "(name TEXT, hash TEXT, key TEXT, rounds TEXT, blocksize TEXT, vector TEXT, size TEXT)";
		Statement statement = c.createStatement();
		statement.executeUpdate(query);
	}

	public static void getEncryption(Encryption encryption) {
		sqliteDB.encryption = encryption;
	}

	private static void deleteRowIfExists(String name, String hash) throws SQLException {
		String query = "DELETE FROM RC5 WHERE name = ? AND hash = ?";
		PreparedStatement statement = c.prepareStatement(query);
		statement.setString(1, name);
		statement.setString(2, hash);
		if(statement.executeUpdate() !=0){
			encryption.print("File encrypt data already exists and will be overwritten");
			// System.out.println("File encrypt data already exists and will be overwritten");
		}
	}

	public static void addFileToDB(long vector) throws IOException, SQLException {
		createIfNoRC5Table();
		long hash = utils.getCRC32(cli.input);
		deleteRowIfExists(cli.login,String.valueOf(hash));
		String query = "INSERT INTO RC5 values(?,?,?,?,?,?,?)";
		PreparedStatement statement = c.prepareStatement(query);
		statement.setString(1,cli.login);
		statement.setString(2, String.valueOf(hash));
		statement.setString(3,cli.key);
		statement.setString(4,cli.rounds);
		statement.setString(5,cli.bsize);
		statement.setString(6,String.valueOf(vector));
		statement.setString(7, String.valueOf(rc5.size));
		statement.executeUpdate();
	}

	public boolean isUserExist(String name) throws SQLException {
		createIfNoUserTable();
		String query = "SELECT * FROM users WHERE name = ?";
		PreparedStatement statement = c.prepareStatement(query);
		statement.setString(1,name);
		ResultSet resultSet = statement.executeQuery();

		return resultSet.next();
	}
	public boolean isPassCorrect(String name,String pass) throws SQLException, NoSuchAlgorithmException {
		String query = "SELECT * FROM users WHERE pass = ? AND name = ?";
		PreparedStatement statement = c.prepareStatement(query);
		byte[] bytesOfMessage = pass.getBytes(StandardCharsets.UTF_8);
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] digest = md.digest(bytesOfMessage);
		String hash = DatatypeConverter.printHexBinary(digest);
		statement.setString(1,hash);
		statement.setString(2,name);
		ResultSet resultSet = statement.executeQuery();
		return resultSet.next();
	}
	public static void getRC5Data(String hash) throws SQLException {
		String query = "SELECT key,rounds,blocksize,vector,size FROM RC5 WHERE name = ? AND hash = ?";
		PreparedStatement statement = c.prepareStatement(query);
		statement.setString(1,cli.login);
		statement.setString(2, hash);
		ResultSet resultSet = statement.executeQuery();
		resultSet.next();
		cli.key = resultSet.getString("key");
		cli.rounds = resultSet.getString("rounds");
		cli.bsize = resultSet.getString("blocksize");
		cli.vector = resultSet.getString("vector");
		cli.size = resultSet.getString("size");

	}
	public void createUser(String name, String pass) throws NoSuchAlgorithmException, SQLException {
		byte[] bytesOfMessage = pass.getBytes(StandardCharsets.UTF_8);
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] digest = md.digest(bytesOfMessage);
		String hash = DatatypeConverter.printHexBinary(digest);
		String query = "INSERT INTO users VALUES (?,?)";
		PreparedStatement statement = c.prepareStatement(query);
		statement.setString(1,name);
		statement.setString(2,hash);
		statement.executeUpdate();
	}
}
