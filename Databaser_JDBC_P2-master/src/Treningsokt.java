import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.sql.Date;
import java.util.ArrayList;

public class Treningsokt {

    private Integer treningsoktID= null;
    private Bruker bruker = null;
    private Date dato = null;
    private Time tidspunkt = null;
    private Integer varighet = null;

    public Treningsokt(Integer treningsoktID, Bruker bruker, Date dato, Time tidspunkt, Integer varighet){
        this.treningsoktID = treningsoktID;
        this.bruker = bruker;
        this.dato = dato;
        this.tidspunkt = tidspunkt;
        this.varighet = varighet;
    }

    public Integer getTreningsoktID() {
        return treningsoktID;
    }

    public Bruker getBruker() {
        return bruker;
    }

    public Date getDato() {
        return dato;
    }

    public Time getTidspunkt() {
        return tidspunkt;
    }

    public Integer getVarighet() {
        return varighet;
    }

    public String toString(){
        return this.treningsoktID.toString()+" | "+this.bruker.getBrukernavn()+" | "+this.dato+" | "+this.tidspunkt+" | "+this.varighet;
    }

    // Registrere ny treningsokt
    public static Treningsokt registerNewTreningsokt(Connection connection, Bruker bruker, Date date, Time time, Integer varighet, ArrayList<Treningsokt> treningsokts, ArrayList<Bruker> brukere) throws SQLException {
        Statement statement = null;
        try{
            statement = connection.createStatement();
            PreparedStatement pstmt = connection.prepareStatement("INSERT INTO Treningsokt(brukernavn, dato, tidspunkt, varighet) VALUES (?,?,?,?)");
            //pstmt.setInt(1, treningsoktID);
            pstmt.setString(1, bruker.getBrukernavn());
            pstmt.setDate(2, date);
            pstmt.setTime(3, time);
            pstmt.setInt(4, varighet);
            pstmt.executeUpdate();
            //Extract ID as it is auto_incrementing
            PreparedStatement pstmt1 = connection.prepareStatement("SELECT treningsoktID FROM Treningsokt WHERE brukernavn=?");
            pstmt1.setString(1, bruker.getBrukernavn());
            ResultSet resultSet = pstmt1.executeQuery();
            int treningsoktID = 0;
            while (resultSet.next()){
                treningsoktID = resultSet.getInt("treningsoktID");
            }
            Treningsokt treningsokt =  new Treningsokt(treningsoktID, bruker, date, time, varighet);
            statement.close();
            return treningsokt;
        } catch (Exception exc){
            //System.out.println(exc);
            if (statement != null){
                statement.close();
            }
            return null;
        }
    }

    public static Treningsokt getTreningsokt(Connection connection, Integer treningsoktID, ArrayList<Treningsokt> treningsokts, Bruker bruker, ArrayList<Bruker> brukere) throws SQLException{
        Statement statement = null;
        try{
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(String.format("SELECT * FROM Treningsokt WHERE treningsoktID = '%s' and brukernavn=%s", treningsoktID, bruker.getBrukernavn()));
            Treningsokt treningsokt;
            try{
                treningsokt = treningsokts.stream().filter(x -> treningsoktID.equals(x.getTreningsoktID())).findFirst().get();
            } catch (Exception exc){
                resultSet.next();
                int tID = resultSet.getInt("treningsoktID");
                int varighet = resultSet.getInt("varighet");
                Date dato = resultSet.getDate("dato");
                Time tidspunkt = resultSet.getTime("tidspunkt");
                String brukernavn = resultSet.getString("brukernavn");
                if (! bruker.getBrukernavn().equals(brukernavn)){
                    throw new Exception("Denne treningsøkten tilhører en annen bruker!");
                }
                treningsokt = new Treningsokt(
                        tID,
                        bruker,
                        dato,
                        tidspunkt,
                        varighet
                );
                treningsokts.add(treningsokt);
            }
            resultSet.close();
            statement.close();
            return treningsokt;
        } catch (Exception exc){
            if (statement != null){
                statement.close();
            }
            //System.out.println(exc);
            return null;
        }
    }

    public static ArrayList<Treningsokt> getAvailable(Connection connection, Bruker bruker, ArrayList<Treningsokt> treningsokts, ArrayList<Bruker> brukere) throws SQLException {
        Statement statement;
        ArrayList<Treningsokt> trenigsoktList = new ArrayList<Treningsokt>();
        try {
            statement = connection.createStatement();
            PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM Treningsokt WHERE brukernavn = ?");
            pstmt.setString(1, bruker.getBrukernavn());
            ResultSet resultSet = pstmt.executeQuery();
            System.out.println("Resultset = " + resultSet);
            while (resultSet.next()){
                System.out.println("Legger til en treningsokt");
                Treningsokt treningsoktElement = new Treningsokt(resultSet.getInt("treningsoktID"), bruker, resultSet.getDate("dato"), resultSet.getTime("tidspunkt"), resultSet.getInt("varighet"));
                trenigsoktList.add(treningsoktElement);
            }
        } catch (Exception exc){
            //System.out.println(exc);
        }
        return trenigsoktList;
    }

}