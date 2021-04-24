import java.sql.*;
import java.util.ArrayList;

public class OvelseGruppe {

    private String ovelseGruppeNavn = null;

    public OvelseGruppe(String ovelseGruppeNavn){
        this.ovelseGruppeNavn = ovelseGruppeNavn;
    }

    public String getOvelseGruppeNavn(){
        return this.ovelseGruppeNavn;
    }


    public String toString(){
        return "Ovelsegruppenavn: " + this.ovelseGruppeNavn;
    }


    public static OvelseGruppe registerOvelseGruppe(Connection connection, String ovelseGruppeNavn, ArrayList<OvelseGruppe> ovelseGrupper) throws SQLException {
        Statement statement = null;
        try{
            statement = connection.createStatement();
            OvelseGruppe ovelseGruppe = getOvelseGruppe(connection, ovelseGruppeNavn, ovelseGrupper);
            if (ovelseGruppe == null){
                PreparedStatement pstmt = connection.prepareStatement("INSERT INTO OvelseGruppe(ovelseGruppeNavn) VALUES (?)");
                pstmt.setString(1, ovelseGruppeNavn);

                pstmt.executeUpdate();
                ovelseGruppe =  new OvelseGruppe(ovelseGruppeNavn);
                statement.close();
                return ovelseGruppe;
            } else{
                statement.close();
                return null;
                //throw new Exception("Denne ovelsesgruppen finnes allerede");
            }
        }catch (Exception exc){
            System.out.println(exc);
            if (statement != null){
                statement.close();
            }
            return null;
        }
    }

    public static OvelseGruppe getOvelseGruppe(Connection connection, String ovelseGruppeNavn, ArrayList<OvelseGruppe> ovelseGrupper) throws SQLException{
        Statement statement = null;
        try{
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(String.format("SELECT * FROM OvelseGruppe WHERE ovelseGruppeNavn = '%s'", ovelseGruppeNavn));
            OvelseGruppe ovelseGruppe;
            try{
                ovelseGruppe = ovelseGrupper.stream().filter(x -> ovelseGruppeNavn.equals(x.getOvelseGruppeNavn())).findFirst().get();
            } catch (Exception exc){
                resultSet.next();
                String ovlGrpNavn = resultSet.getString("ovelseGruppeNavn");

                ovelseGruppe = new OvelseGruppe(
                        ovlGrpNavn
                );
            }
            resultSet.close();
            statement.close();
            return ovelseGruppe;
        } catch (Exception exc){
            if (statement != null){
                statement.close();
            }
            System.out.println(exc);
            return null;
        }
    }



}
