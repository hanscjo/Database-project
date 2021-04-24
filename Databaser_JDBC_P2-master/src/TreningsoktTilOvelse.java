import java.sql.*;
import java.util.ArrayList;

public class TreningsoktTilOvelse {

    private Treningsokt treningsokt = null;
    private Ovelse ovelse = null;
    private Integer kilo = null;
    private Integer sett = null;

    public TreningsoktTilOvelse(Treningsokt treningsokt, Ovelse ovelse, Integer kilo, Integer sett){
        this.treningsokt = treningsokt;
        this.ovelse = ovelse;
        this.kilo = kilo;
        this.sett = sett;
    }

    public Treningsokt getTreningsokt(){
        return this.treningsokt;
    }

    public Ovelse getOvelse(){
        return this.ovelse;
    }

    public String toString(){
        return "TreningsøktID: " + this.treningsokt.getTreningsoktID() + ", Øvelsesnavn: " + this.ovelse.getOvelseNavn();
    }


    public static TreningsoktTilOvelse registerTreningsOktTilOvelse(Connection connection, Treningsokt treningsokt, Ovelse ovelse, Integer kilo, Integer sett, ArrayList<TreningsoktTilOvelse> treningsoktTilOvelses, ArrayList<Treningsokt> treningsokts, ArrayList<Ovelse> ovelser) throws SQLException{
        Statement statement = null;
        try{
            statement = connection.createStatement();
            TreningsoktTilOvelse treningsoktTilOvelse = getTreningsoktTilOvelse(connection, treningsokt, ovelse, treningsoktTilOvelses, treningsokts, ovelser);
            if (treningsoktTilOvelse == null){
                PreparedStatement pstmt = connection.prepareStatement("INSERT INTO TreningsoktTilOvelse(treningsoktID, ovelseNavn, Kilo, Sett) VALUES (?,?,?,?)");
                pstmt.setInt(1, treningsokt.getTreningsoktID());
                pstmt.setString(2, ovelse.getOvelseNavn());
                pstmt.setInt(3, kilo);
                pstmt.setInt(4, sett);

                pstmt.executeUpdate();
                treningsoktTilOvelse =  new TreningsoktTilOvelse(treningsokt, ovelse, kilo, sett);
                treningsoktTilOvelses.add(treningsoktTilOvelse);
                statement.close();
                return treningsoktTilOvelse;
            } else{
                statement.close();
                return null;
                //throw new Exception("Denne treningsoktTilOvelse finnes allerede");
            }
        }catch (Exception exc){
            System.out.println(exc);
            if (statement != null){
                statement.close();
            }
            return null;
        }
    }

    public static TreningsoktTilOvelse getTreningsoktTilOvelse(Connection connection, Treningsokt treningsokt, Ovelse ovelse, ArrayList<TreningsoktTilOvelse> treningsoktTilOvelseliste,ArrayList<Treningsokt> treningsokts, ArrayList<Ovelse> ovelser) throws SQLException{
        Statement statement = null;
        try{
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(String.format("SELECT * FROM TreningsoktTilOvelse WHERE treningsoktID = '%s' AND ovelseNavn = '%s'", treningsokt.getTreningsoktID(), ovelse.getOvelseNavn()));

            TreningsoktTilOvelse treningsoktTilOvelse;
            try{
                //Stoppet her, orker ikke sette meg inn i lambdaexpressions i dette øyeblikk. Må kanskje ha egne felt til treningsoktID og ovelseNavn siden IntelliJ gir advarsel når jeg refererer fra et objekt
                final Treningsokt current_treningsokt = treningsokt;
                final Ovelse current_ovelse = ovelse;

                treningsoktTilOvelse = treningsoktTilOvelseliste.stream().filter(x ->
                                (current_treningsokt.getTreningsoktID() == (x.getTreningsokt().getTreningsoktID()) &&
                                current_ovelse.getOvelseNavn().equals(x.getOvelse().getOvelseNavn())
                                )).findFirst().get();

            } catch (Exception exc){
                resultSet.next();
                treningsoktTilOvelse = new TreningsoktTilOvelse(treningsokt, ovelse, resultSet.getInt("kilo"), resultSet.getInt("sett"));

            }
            resultSet.close();
            statement.close();

            return treningsoktTilOvelse;

        } catch (Exception exc){
            if (statement != null){
                statement.close();
            }
            System.out.println(exc);
            return null;
        }
    }
}
