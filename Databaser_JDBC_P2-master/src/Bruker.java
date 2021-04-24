import java.sql.*;

import java.util.ArrayList;


public class Bruker {

    private String brukernavn = null;
    private Integer alder = null;

    public Bruker(String brukernavn, Integer alder){
        this.brukernavn = brukernavn;
        this.alder = alder;
    }


    public String getBrukernavn(){
        return this.brukernavn;
    }

    public Integer getAlder(){
        return this.alder;
    }

    public String toString(){
        return this.brukernavn + ", " + this.alder;
    }

    public static Bruker createBruker(Connection connection, String brukernavn, Integer alder, ArrayList<Bruker> brukere) throws SQLException {
        Statement statement = null;
        try{
            statement = connection.createStatement();
            if (brukernavn.length() > 0 && (alder > 0 && alder != null)){
                Bruker bruker = Bruker.getBruker(connection, brukernavn, brukere);
                //What is return when there is no user
                if (bruker == null){
                    statement.executeUpdate(String.format("INSERT INTO BRUKER VALUES ('%s', %s)", brukernavn, alder));
                    bruker = new Bruker(brukernavn, alder);
                    statement.close();
                    return bruker;
                } else{
                    throw new Exception("Brukeren eksisterer allerede");
                }
            } throw new Exception("Noe galt med innputen");
        } catch (Exception exc){
            //System.out.println(exc);
            if (statement != null){
                statement.close();
            }
            return null;
        }
    }

    public static Bruker getBruker(Connection connection, String brukernavn, ArrayList<Bruker> brukere) throws SQLException{
        Statement statement = null;
        ResultSet resultSet = null;
        try{
            statement = connection.createStatement();
            resultSet = statement.executeQuery(String.format("SELECT * FROM Bruker WHERE brukernavn = '%s'", brukernavn));
            Bruker bruker = null;
            try{
                bruker = brukere.stream().filter(x -> brukernavn.equals(x.getBrukernavn())).findFirst().get();
            } catch (Exception exc){
                resultSet.next();
                bruker = new Bruker(resultSet.getString("brukernavn"), resultSet.getInt("alder"));
            }
            resultSet.close();
            statement.close();
            return bruker;
        } catch (Exception exc){
            //System.out.println(exc);
            //System.out.println(brukernavn + " does not exist");
            if (resultSet != null){
                resultSet.close();
            }
            if (statement != null){
                statement.close();
            }
            return null;
        }
    }


    public static ArrayList<Bruker> getBrukere(Connection connection)  throws SQLException{
        Statement statement = null;
        ResultSet resultSet = null;
        try{
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM Bruker");
            ArrayList<Bruker> brukere = new ArrayList<Bruker>();
            while (resultSet.next()){
                Bruker bruker = new Bruker(resultSet.getString("brukernavn"), resultSet.getInt("alder"));
                brukere.add(bruker);
            }
            resultSet.close();
            statement.close();
            return brukere;
        } catch (Exception exc){
            //System.out.println("Something went wrong extractiong users");
            if (resultSet != null){
                resultSet.close();
            }
            if (statement != null){
                statement.close();
            }
            return new ArrayList<Bruker>();
        }
    }

}
