package services;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import sala.patryk.projekt.wypozyczalniavideo.*;

public class MovieDBManager {

	private Connection conn;
	private Statement stmt;
	private PreparedStatement addMovieStmt;
	private PreparedStatement getMovieStmt;
	private PreparedStatement deleteAllMoviesStmt;
	private PreparedStatement findMovieByTitleStmt;
	private PreparedStatement deleteMovieStmt;

	List<Integer> listID = new ArrayList<Integer>();

	public MovieDBManager() {
		try {
			Properties props = new Properties();

			try {
				props.load(ClassLoader
						.getSystemResourceAsStream("/reso/jdbc.properties"));
			} catch (IOException e) {
				e.printStackTrace();
			}

			conn = DriverManager.getConnection(props.getProperty("url"));

			stmt = conn.createStatement();
			boolean MovieTableExists = false;

			ResultSet rs = conn.getMetaData().getTables(null, null, null, null);

			while (rs.next()) {
				if ("movies".equalsIgnoreCase(rs.getString("TABLE_NAME"))) {
					MovieTableExists = true;
					break;
				}
			}

			if (!MovieTableExists) {
				stmt.executeUpdate("CREATE TABLE movies(id int GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,"
						+ "title varchar(40), price int)");
			}

			addMovieStmt = conn
					.prepareStatement("INSERT INTO movies (name, price) VALUES (?, ?)");

			getMovieStmt = conn.prepareStatement("SELECT * FROM movies");

			deleteAllMoviesStmt = conn.prepareStatement("DELETE FROM movies");

			findMovieByTitleStmt = conn
					.prepareStatement("SELECT id FROM movies WHERE title= ?");

			deleteMovieStmt = conn
					.prepareStatement("DELETE FROM movies WHERE id = ?");
		} catch (SQLException e) {

			e.printStackTrace();
		}
	}

	public void addMovie(Movie movie) {
		try {
			addMovieStmt.setString(1, movie.getTitle());
			addMovieStmt.setFloat(2, movie.getPrice());

			addMovieStmt.executeUpdate();
		} catch (SQLException e) {

			e.printStackTrace();
		}

	}

	public List<Movie> getAllMovies() {
		List<Movie> myMovieList = new ArrayList<Movie>();
		try {
			ResultSet rs = getMovieStmt.executeQuery();
			while (rs.next()) {
				myMovieList.add(new Movie(rs.getString("title"), rs.getFloat("price")));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return myMovieList;
	}

	public void deleteAllMovies() {
		try {
			deleteAllMoviesStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<Integer> findMovieByTitle(String name) {
		try {
			List<Integer> myMovieIDList = new ArrayList<Integer>();
			findMovieByTitleStmt.setString(1, name);
			ResultSet rs = findMovieByTitleStmt.executeQuery();
			while (rs.next())
				myMovieIDList.add(rs.getInt("ID"));
			return myMovieIDList;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void deleteMovie(List<Integer> myMovieList) {
		try {
			for (Integer id : myMovieList) {
				deleteMovieStmt.setInt(1, id);
				deleteMovieStmt.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}