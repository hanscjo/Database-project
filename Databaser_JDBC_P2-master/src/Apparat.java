import java.sql.*;
import java.util.ArrayList;


public class Apparat {

    private String apparatNavn = null;
    private String beskrivelse = null;

    private Apparat(String apparatNavn, String beskrivelse){
        this.apparatNavn = apparatNavn;
        this.beskrivelse = beskrivelse;
    }


    public String getApparatNavn(){
        return this.apparatNavn;
    }

    public String getBeskrivelse(){
        return this.beskrivelse;
    }

    public String toString(){
        return "Apparatnavn: " + this.apparatNavn + ", Beskrivelse: " + this.beskrivelse;
    }

    public static Apparat registerApparat(Connection connection, String apparatNavn, String beskrivelse, ArrayList<Apparat> apparater) throws SQLException {
        Statement statement = null;
        try{
            statement = connection.createStatement();
            Apparat apparat = getApparat(connection, apparatNavn, apparater);
            if (apparat == null){
                PreparedStatement pstmt = connection.prepareStatement("INSERT INTO Apparat(apparatNavn, beskrivelse) VALUES (?,?)");
                pstmt.setString(1, apparatNavn);
                pstmt.setString(2, beskrivelse);

                pstmt.executeUpdate();
                apparat =  new Apparat(apparatNavn, beskrivelse);
                apparater.add(apparat);
                statement.close();
                return apparat;
            } else{
                statement.close();
                return null;
                //throw new Exception("Dette apparatet finnes allerede");
            }
        }catch (Exception exc){
            //System.out.println(exc);
            if (statement != null){
                statement.close();
            }
            return null;
        }
    }

    public static Apparat getApparat(Connection connection, String apparatNavn, ArrayList<Apparat> apparater) throws SQLException{
        Statement statement = null;
        try{
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(String.format("SELECT * FROM Apparat WHERE apparatNavn = '%s'", apparatNavn));
            Apparat apparat;
            try{
                apparat = apparater.stream().filter(x -> apparatNavn.equals(x.getApparatNavn())).findFirst().get();
            } catch (Exception exc){
                resultSet.next();
                String aprtNavn = resultSet.getString("apparatNavn");
                String apparatBeskrivelse = resultSet.getString("beskrivelse");

                apparat = new Apparat(
                        aprtNavn,
                        apparatBeskrivelse
                );
                apparater.add(apparat);
            }
            resultSet.close();
            statement.close();
            return apparat;
        } catch (Exception exc){
            if (statement != null){
                statement.close();
            }
            //System.out.println(exc);
            return null;
        }
    }


}
