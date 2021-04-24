import java.sql.*;
import java.util.ArrayList;


public class Notat {
    private Integer notatID = null;
	private Treningsokt treningsokt = null;
	private Integer personligForm = null;
	private Integer prestasjon = null;
	private String formal = null;
	private String opplevelse = null;
	
	public Notat(Integer notatID, Treningsokt treningsokt, Integer personligForm, Integer prestasjon, String formal, String opplevelse){
	    this.notatID = notatID;
	    this.treningsokt = treningsokt;
		this.personligForm = personligForm;
		this.prestasjon = prestasjon;
		this.formal = formal;
		this.opplevelse = opplevelse;
    }
	
	public Integer getNotatID() {
		return notatID;
	}
	public Treningsokt getTreningsoktid() {
		return treningsokt;
	}
	public Integer getPersonligForm() {
		return personligForm;
	}
	public Integer getPrestasjon() {
		return prestasjon;
	}
	public String getFormal() {
		return formal;
	}
	public String getOpplevelse() {
		return opplevelse;
	}
	public String toString() {
		return this.notatID.toString();
	}
	
	
	public static Notat registerNewNotat(Connection connection, ArrayList<Treningsokt> treningsokts, ArrayList<Notat> notes, Treningsokt treningsokt, Integer personligForm, Integer prestasjon, String formal, String opplevelse ) throws SQLException{
		Statement statement = null;
		try {
			statement = connection.createStatement();
			try{
                PreparedStatement pstmt0 = connection.prepareStatement("SELECT Id FROM Notat where treningsoktID=?");
                pstmt0.setInt(1, treningsokt.getTreningsoktID());
                pstmt0.executeUpdate();
                ResultSet resultSet = pstmt0.executeQuery();
                int notatID = -1;
                while (resultSet.next()){
                    notatID = resultSet.getInt("Id");
                }
                Notat notat = Notat.getNotat(connection, notatID, notes, treningsokt);
                if (notat == null){
                    System.out.println("notat == null");
                    throw new Exception();
                }
                System.out.println("return null");
                return null;
            } catch (Exception exc){
                Notat notat;
                System.out.println("Setter inn notat");
                PreparedStatement pstmt = connection.prepareStatement("INSERT INTO Notat(treningsoktID, Personlig_form, Prestasjon, Treningsformaal, Opplevelse_av_trening) VALUES (?,?,?,?,?)");
                pstmt.setInt(1, treningsokt.getTreningsoktID());
                pstmt.setInt(2, personligForm);
                pstmt.setInt(3, prestasjon);
                pstmt.setString(4, formal);
                pstmt.setString(5, opplevelse);
                pstmt.executeUpdate();
                System.out.println("Query kjoert!");
                //Get the notat ID
                PreparedStatement pstmt1 = connection.prepareStatement("SELECT Id FROM Notat WHERE treningsoktID = ?");
                pstmt1.setInt(1, treningsokt.getTreningsoktID());
                ResultSet resultSet = pstmt1.executeQuery();
                resultSet.next();
                int notatID = resultSet.getInt("Id");
                System.out.println("notatID etter insert: " + notatID);
                notat =  new Notat(notatID, treningsokt, personligForm, prestasjon, formal, opplevelse);
                statement.close();
                return notat;
            }
		}catch (Exception exc){
            //System.out.println(exc);
            if (statement != null){
                statement.close();
            }
            return null;
        }
	}

	public static Notat getNotat(Connection connection, Integer notatID, ArrayList<Notat> notes,Treningsokt treningsokt) throws SQLException{
		Statement statement = null;
        try{
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(String.format("SELECT * FROM Notat WHERE Id = '%s'", notatID));
            Notat notat;
            try{
                notat = notes.stream().filter(x -> notatID.equals(x.getNotatID())).findFirst().get();
            } catch (Exception exc){
                resultSet.next();
                int nID = resultSet.getInt("Id");
                int personligForm = resultSet.getInt("Personlig_form");
                int prestasjon = resultSet.getInt("Prestasjon");
                String formal = resultSet.getString("Treningsformaal");
                String opplevelse = resultSet.getString("Opplevelse_av_trening");
                int treningsoktID = resultSet.getInt("treningsoktID");
                notat = new Notat(nID,treningsokt,personligForm,prestasjon,formal,opplevelse);
                notes.add(notat);
            }
            resultSet.close();
            statement.close();
            return notat;
        } catch (Exception exc){
            if (statement != null){
                statement.close();
            }
            //System.out.println(exc);
            return null;
        }

	}
	// TODO: Ferdig denne
    public static Notat alreadyNotat(Connection connection, Treningsokt treningsokt, ArrayList<Notat> notes) throws SQLException {
        Statement statement = null;
        try{
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(String.format("SELECT * FROM Notat WHERE treningsoktID = '%s'", treningsokt.getTreningsoktID()));
            System.out.println(resultSet.getString("Treningsformaal"));
            Notat notat;
            try{
                notat = notes.stream().filter(x -> treningsokt.getTreningsoktID().equals(x.getTreningsoktid())).findFirst().get();
                System.out.println(notat);
                return notat;
            } catch (Exception exc) {
                resultSet.next();
                notat = new Notat(
                    resultSet.getInt("Id"),
                    treningsokt,
                    resultSet.getInt("Personlig_form"),
                    resultSet.getInt("Prestasjon"),
                    resultSet.getString("Treningsformaal"),
                    resultSet.getString("Opplevelse_av_trening")
                );
                return notat;
            }
        } catch (Exception exc){
            if (statement != null){
                statement.close();
            }
            //System.out.println(exc);
            return null;
        }
    }

}
