import java.sql.*;
import java.util.ArrayList;

public class Ovelse {

    private String ovelseNavn = null;
    private Boolean harApparat = null;
    private Apparat apparat = null;
    private String beskrivelse = null;

    public Ovelse(String ovelseNavn, Boolean harApparat, Apparat apparat, String beskrivelse){
        this.ovelseNavn = ovelseNavn;
        this.harApparat = harApparat;
        this.apparat = apparat;
        this.beskrivelse = beskrivelse;
    }


    public String getOvelseNavn(){
        return this.ovelseNavn;
    }

    public Boolean getHarApparat() {
        return this.harApparat;
    }

    public Apparat getApparat() {
        return this.apparat;
    }

    public String getBeskrivelse(){
        return this.beskrivelse;
    }


    public String toString(){
        return "Øvelsesnavn: " + this.ovelseNavn + ", Har apparat: " + this.harApparat + ", Apparatnavn: " + this.apparat.getApparatNavn() + ", Beskrivelse: " + this.beskrivelse;
    }

    public static Ovelse registerOvelse(Connection connection, String ovelseNavn, Boolean harApparat, Apparat apparat, String beskrivelse, ArrayList<Ovelse> ovelser, ArrayList<Apparat> apparater) throws SQLException {
        Statement statement = null;
        try{
            statement = connection.createStatement();
            Ovelse ovelse = getOvelse(connection, ovelseNavn, ovelser, apparater);
            if (ovelse == null){
                //Variabler i SQL-query er konsekvente med databasen!
                PreparedStatement pstmt = connection.prepareStatement("INSERT INTO Ovelse(ovelseNavn, harApparat, apparatNavn, Bekrivelse) VALUES (?,?,?,?)");
                pstmt.setString(1, ovelseNavn);
                pstmt.setBoolean(2, harApparat);

                if(!harApparat) {
                    pstmt.setNull(3, java.sql.Types.VARCHAR);
                }
                else {
                    pstmt.setString(3, apparat.getApparatNavn());

                }

                pstmt.setString(4, beskrivelse);
                pstmt.executeUpdate();

                ovelse =  new Ovelse(ovelseNavn, harApparat, apparat, beskrivelse);
                ovelser.add(ovelse);
                statement.close();
                return ovelse;
            } else{
                statement.close();
                return null;
                //throw new Exception("Denne øvelsen finnes allerede");
            }
        }catch (Exception exc){
            //System.out.println(exc);
            if (statement != null){
                statement.close();
            }
            return null;
        }
    }

    public static Ovelse getOvelse(Connection connection, String ovelseNavn, ArrayList<Ovelse> ovelser, ArrayList<Apparat> apparater) throws SQLException{
        Statement statement = null;
        try{
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(String.format("SELECT * FROM Ovelse WHERE ovelseNavn = '%s'", ovelseNavn));
            Ovelse ovelse;
            try{
                ovelse = ovelser.stream().filter(x -> ovelseNavn.equals(x.getOvelseNavn())).findFirst().get();
            } catch (Exception exc){
                resultSet.next();
                String ovlNavn = resultSet.getString("ovelseNavn");
                Boolean harApparat = resultSet.getBoolean("harApparat");
                String apparatNavn = resultSet.getString("apparatNavn");
                Apparat apparat = Apparat.getApparat(connection, apparatNavn, apparater);
                String Beskrivelse = resultSet.getString("Bekrivelse");

                ovelse = new Ovelse(
                        ovlNavn,
                        harApparat,
                        apparat,
                        Beskrivelse
                );
            }
            resultSet.close();
            statement.close();
            return ovelse;
        } catch (Exception exc){
            if (statement != null){
                statement.close();
            }
            //System.out.println(exc);
            return null;
        }
    }


}
