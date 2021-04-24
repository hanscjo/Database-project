import java.sql.*;
import java.util.ArrayList;

public class OvelsesgruppeTilOvelse {

    private OvelseGruppe ovelseGruppe = null;
    private Ovelse ovelse = null;

    public OvelsesgruppeTilOvelse(OvelseGruppe ovelseGruppe, Ovelse ovelse){
        this.ovelseGruppe = ovelseGruppe;
        this.ovelse = ovelse;
    }

    public OvelseGruppe getOvelseGruppe(){
        return ovelseGruppe;
    }

    public Ovelse getOvelse(){ return ovelse; }

    public String toString(){
        return "Ovelsegruppenavn :" + ovelseGruppe.getOvelseGruppeNavn() + ", Øvelsesnavn: " + ovelse.getOvelseNavn();
    }

    public static OvelsesgruppeTilOvelse registererOvelsesgruppeTilOvelse(Connection connection, OvelseGruppe ovelseGruppe, Ovelse ovelse, ArrayList<OvelsesgruppeTilOvelse> ovelsesgruppeTilOvelselist, ArrayList<OvelseGruppe> ovelsegrupper, ArrayList<Ovelse> ovelser) throws SQLException{
        Statement statement = null;
        try{
            statement = connection.createStatement();
            OvelsesgruppeTilOvelse ovelsesgruppeTilOvelse = null;//getOvelsesgruppeTilOvelse(connection, ovelseGruppe, ovelse, ovelsesgruppeTilOvelselist, ovelsegrupper, ovelser);
            if (ovelsesgruppeTilOvelse == null){
                PreparedStatement pstmt = connection.prepareStatement("INSERT INTO OvelsesgruppeTilOvelse(ovelseGruppeNavn, ovelseNavn) VALUES (?,?)");
                System.out.println("Gruppenavn: " + ovelseGruppe.getOvelseGruppeNavn());
                System.out.println("Ovelsenavn: " + ovelse.getOvelseNavn());
                pstmt.setString(1, ovelseGruppe.getOvelseGruppeNavn());
                pstmt.setString(2, ovelse.getOvelseNavn());

                pstmt.executeUpdate();
                ovelsesgruppeTilOvelse =  new OvelsesgruppeTilOvelse(ovelseGruppe, ovelse);
                statement.close();
                return ovelsesgruppeTilOvelse;
            } else{
                statement.close();
                return null;
                //throw new Exception("Denne ovelsesgruppeTilOvelse finnes allerede");
            }
        }catch (Exception exc){
            System.out.println(exc);
            if (statement != null){
                statement.close();
            }
            return null;
        }

    }

    public static OvelsesgruppeTilOvelse getOvelsesgruppeTilOvelse(Connection connection, OvelseGruppe ovelseGruppe, Ovelse ovelse, ArrayList<OvelsesgruppeTilOvelse> ovelsesgruppeTilOvelselist, ArrayList<OvelseGruppe> ovelsegrupper,ArrayList<Ovelse> ovelser) throws SQLException {
        Statement statement = null;
        try{
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(String.format("SELECT * FROM OvelsesgruppeTilOvelse WHERE ovelseGruppeNavn = '%s' AND ovelseNavn = '%s'", ovelseGruppe.getOvelseGruppeNavn(), ovelse.getOvelseNavn()));

            OvelsesgruppeTilOvelse ovelsesgruppeTilOvelse;
            try{
                final OvelseGruppe current_ovelseGruppe = ovelseGruppe;
                final Ovelse current_ovelse = ovelse;

                ovelsesgruppeTilOvelse = ovelsesgruppeTilOvelselist.stream().filter(x ->
                        (current_ovelseGruppe.getOvelseGruppeNavn().equals(x.getOvelseGruppe().getOvelseGruppeNavn()) &&
                                current_ovelse.getOvelseNavn().equals(x.getOvelse().getOvelseNavn())
                        )).findFirst().get();

            } catch (Exception exc){
                resultSet.next();
                ovelsesgruppeTilOvelse = new OvelsesgruppeTilOvelse(ovelseGruppe, ovelse);

            }
            resultSet.close();
            statement.close();

            return ovelsesgruppeTilOvelse;

        } catch (Exception exc){
            if (statement != null){
                statement.close();
            }
            System.out.println(exc);
            return null;
        }

    }


}
