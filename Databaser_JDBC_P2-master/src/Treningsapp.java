
import javax.swing.plaf.nimbus.State;
import java.security.KeyStore;
import java.sql.*;
import java.sql.Date;
import java.util.*;


public class Treningsapp {

    public static void main(String[] args) throws SQLException {
        Scanner brukernavn_input = new Scanner(System.in);

        System.out.println("Brukernavn");
        String database_brukernavn = brukernavn_input.nextLine();
        System.out.println("Passord");
        String passord = brukernavn_input.nextLine();

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        ArrayList<Bruker> brukere = new ArrayList<Bruker>();
        ArrayList<Treningsokt> treningsokts = new ArrayList<Treningsokt>();
        ArrayList<Apparat> apparater = new ArrayList<Apparat>();
        ArrayList<Ovelse> ovelser = new ArrayList<Ovelse>();
        ArrayList<OvelseGruppe> ovelseGrupper = new ArrayList<OvelseGruppe>();//
        ArrayList<OvelsesgruppeTilOvelse> ovelsesgruppeTilOvelseList = new ArrayList<>();
        ArrayList<Notat> notater = new ArrayList<>();
        ArrayList<TreningsoktTilOvelse> treningsoktTilOvelseList = new ArrayList<TreningsoktTilOvelse>();
        try {
            String username = database_brukernavn;
            //Markus: 1234qwer, Sander:, Torstein: Torstein123 , HC:
            String password = passord;


            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/treningsdagbok?useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", username, password);

            //Setup
            //brukere = Bruker.getBrukere(statement);

            //To insert values
            //myStatement.executeUpdate("INSERT INTO BRUKER VALUES ('Navn', 20)");

            //To Query values
            //resultSet = statement.executeQuery("select * from Bruker");

            Scanner scanner = new Scanner(System.in);
            while (true) {
                String input = "";
                statement = connection.createStatement();
                System.out.println("Hva ønsker du å gjøre?");
                System.out.println("1 = Registrere bruker");
                System.out.println("2 = Velge en bruker");
                System.out.println("3 = Finne en bruker");
                System.out.println("4 = Legg til øvelse");
                System.out.println("5 = Legg til øvelse i øvelsesgruppe");
                System.out.println("6 = Legg til apparat");
                System.out.println("7 = Se oversikt over ovelsesgrupper");
                System.out.println("8 = Lag ny øvelsesgruppe");
                System.out.println("9 = Se resultater fra øvelser");
                System.out.println("Exit = Exit the program");
                input = scanner.nextLine();


                // Registere Bruker
                if (input.equals("1")){
                    System.out.print("Skriv inn brukernavn: ");
                    String brukernavn = scanner.nextLine();
                    System.out.print("Skriv inn alder: ");
                    Integer alder = scanner.nextInt();
                    Bruker bruker = Bruker.createBruker(connection, brukernavn, alder, brukere);
                    if (bruker != null){
                        brukere.add(bruker);
                        System.out.println("Brukeren er laget");
                    } else{
                        System.out.println("Noe gikk galt ved opprettesle av brukeren.");
                    }
                    scanner.nextLine(); // Prevents automatic new-line/enter
                }
                //Velge en bruker
                else if (input.equals("2")){
                    System.out.println("Skriv inn et brukernavn fra listen: ");
                    for (Bruker bruker : Bruker.getBrukere(connection)) {
                        System.out.println(bruker);
                    }

                    input = scanner.nextLine();
                    Bruker bruker = Bruker.getBruker(connection, input, brukere);
                    if (bruker != null){
                        brukere.add(bruker);
                        brukerOppforsel(connection, bruker, brukere, treningsokts, treningsoktTilOvelseList, apparater, ovelser, notater,scanner);
                    } else{
                        System.out.println("Den brukeren eksisterer ikke");
                    }
                }

                // Finne bruker
                else if (input.equals("3")){
                    System.out.print("Hvilken?: ");
                    input = scanner.nextLine();
                    Bruker bruker = Bruker.getBruker(connection, input, brukere);
                    if (bruker != null){
                        brukere.add(bruker);
                        System.out.println(bruker);
                    }
                }

                else if(input.equals("4")){
                    addOvelse(scanner, connection, apparater, ovelser);
                }

                else if(input.equals("5")){
                    //Bruker bevisst lokal scanner
                    Scanner sc = new Scanner(System.in);
                    System.out.println("Navn på øvelsesgruppe: ");
                    String gruppeNavn = sc.next();
                    System.out.println();
                    OvelseGruppe gruppe = OvelseGruppe.getOvelseGruppe(connection, gruppeNavn,ovelseGrupper);
                    if(gruppe == null){
                        System.out.println("Øvelsesgruppen finnes ikke. Prøv igjen senere.");
                    } else {
                        System.out.println("Navn på øvelse: ");
                        String ovelseNavn = sc.next();
                        Ovelse ovelse = Ovelse.getOvelse(connection, ovelseNavn, ovelser,apparater);
                        if(ovelse == null){
                            System.out.println("Øvelsen " + ovelseNavn + " finnes ikke!");
                            return;
                        }

                        OvelsesgruppeTilOvelse result = OvelsesgruppeTilOvelse.registererOvelsesgruppeTilOvelse(connection, gruppe, ovelse, ovelsesgruppeTilOvelseList, ovelseGrupper, ovelser);
                        //System.out.println(result.toString());
                        //OvelsesgruppeTilOvelse result = OvelsesgruppeTilOvelse.getOvelsesgruppeTilOvelse(connection, gruppe, ovelse, ovelsesgruppeTilOvelseList, ovelseGrupper,ovelser);

                        try {
                            System.out.println(result.toString());
                            ovelsesgruppeTilOvelseList.add(result);
                        } catch(Exception e){
                            System.out.println("Noe gikk galt! Kanskje finnes denne assosiasjonen allerede?? ");
                        }

                    }



                }

                //Legge til apparat
                else if(input.equals("6")){
                    addApparat(scanner, connection,apparater);
                }

                else if(input.equals("7")){
                    //Oversikt over hvilke ovelser som er i hvilke ovelsesgrupper
                    getOvelserInGruppe(connection);
                }

                else if(input.equals("8")){
                    //Legge til øvelsesgrupper
                    Scanner sc = new Scanner(System.in);
                    System.out.print("Navn:");
                    String navn = sc.next();
                    System.out.println();

                    OvelseGruppe result = OvelseGruppe.registerOvelseGruppe(connection, navn, ovelseGrupper);
                    if(result == null){
                        System.out.println("Noe gikk galt. Prøv igjen senere.");
                    } else {
                        ovelseGrupper.add(result);
                        System.out.println("Suksess! " + navn + " lagt til!");
                    }
                }

                else if(input.equals("9")){ //Må ta inn start og slutt dato
                    System.out.println("Ta inn navn på øvelsen: ");
                    String ovelseNavn = scanner.nextLine();

                    System.out.println("Ta inn fra dato: 'år,måned,dag' ");
                    String dato = scanner.nextLine();
                    String[] datoTabell = dato.split(",");
                    Date fromDate = new Date(Integer.parseInt(datoTabell[0]), Integer.parseInt(datoTabell[1]), Integer.parseInt(datoTabell[2]));

                    System.out.println("Ta inn til dato: 'år,måned,dag' ");
                    dato = scanner.nextLine();
                    datoTabell = dato.split(",");
                    Date toDate = new Date(Integer.parseInt(datoTabell[0]), Integer.parseInt(datoTabell[1]), Integer.parseInt(datoTabell[2]));

                    getResultsLog(connection, ovelseNavn, fromDate, toDate);
                }

                else if (input.equals("Exit")){
                    System.out.println("Goodbye, and welcome back");
                    break;
                }
                statement.close();
            }

        } catch (Exception exc){
            exc.printStackTrace();
        } finally {
            if (resultSet != null){
                resultSet.close();
            }
            if (statement != null) {
                statement.close();

            }
            if (connection != null){
                connection.close();
            }
        }

    }

    protected static void brukerOppforsel(Connection connection, Bruker bruker, ArrayList<Bruker> brukere, ArrayList<Treningsokt> treningsokts, ArrayList<TreningsoktTilOvelse> treningsoktTilOvelseList, ArrayList<Apparat> apparater, ArrayList<Ovelse> ovelser, ArrayList<Notat> notater, Scanner scanner) throws SQLException{
        System.out.println("Hva vil du gjøre "+bruker.getBrukernavn()+"?");
        while (true){
            System.out.println("1 = Registrere treningsokt");
            System.out.println("2 = Velg en treningsøkt");
            System.out.println("3 = Se oversikt over alle treningsøkter");
            System.out.println("4 = Se oversikt over et antall treningsøkter med tilhørende notater");
            System.out.println("back = Return to create/choose user");

            String input = scanner.nextLine();
            //Registrere treningsøkt:
            if (input.equals("1")){
                try{
                    // Dato
                    System.out.println("Dato maa skrives på formen 'år,måned,dag'");
                    System.out.print("Dato: ");
                    String dato = scanner.nextLine();
                    System.out.println("Skriv inn: "+dato);
                    String[] datoTabell = dato.split(",");
                    Date date = new Date(Integer.parseInt(datoTabell[0])-1900, Integer.parseInt(datoTabell[1])-1, Integer.parseInt(datoTabell[2])+1);
                    // Tidspunkt
                    System.out.println("Tidspunkt maa skrives på formen 'time,minutt,sekund'");
                    System.out.print("Tidspunkt: ");
                    String tid = scanner.nextLine();
                    String[] tidTabell = dato.split(",");
                    //TODO: Fix
                    Time time = new Time(Integer.parseInt(tidTabell[0]),Integer.parseInt(tidTabell[1]),Integer.parseInt(tidTabell[2]));
                    // Varighet
                    System.out.print("Varighet (timer): ");
                    Integer varighet = scanner.nextInt();
                    try{
                        Treningsokt treningsokt = Treningsokt.registerNewTreningsokt(connection, bruker, date, time, varighet, treningsokts, brukere);
                        if (treningsokt != null){
                            System.out.println("Det ble laget en treningokt med ID: "+treningsokt);
                        } else System.out.println("Det gikk noe galt ved opprettelsen av trenignsokten");
                    } catch (Exception exc){
                        System.out.println(exc);
                        System.out.println("Noe gikk galt");
                    }
                } catch (Exception exc){
                    System.out.println("Noe gikk galt ved input av verdier. "+exc);
                }
            }
            // Velg og fortsett med en treningsøkt
            if (input.equals("2")){
                System.out.println("Hvilken treningsøkt vil du bruke?");
                int treningsoktID = scanner.nextInt();
                System.out.println("treningsoktID = " + treningsoktID);
                ArrayList<Treningsokt> availableTreningsoktList = Treningsokt.getAvailable(connection, bruker, treningsokts, brukere);
                System.out.println("Liste: " + availableTreningsoktList);
                Treningsokt treningsokt = availableTreningsoktList.stream().filter(x -> x.getTreningsoktID() == treningsoktID).findFirst().get();
                System.out.println("Treningsokt: " + treningsokt);
                treningsoktOppforsel(connection, bruker, brukere, treningsokt, treningsokts, ovelser, apparater, treningsoktTilOvelseList, notater, scanner);
            }

            //Se en oversikt over alle de tilgjengelige treningsøktene
            if (input.equals("3")){
                System.out.println("Treningsøkter:");
                ArrayList<Treningsokt> availableTreningsoktList = Treningsokt.getAvailable(connection, bruker, treningsokts, brukere);
                availableTreningsoktList.stream().forEach(x -> System.out.println("Treningsøkt, ID: "+x));
            }

            //Se et antall treningsøkter med tilhørende notater
            if (input.equals("4")){
                System.out.println("Hvor mange av dine siste treningsøkter vil du se på?");
                int treningsoktAntall = scanner.nextInt();
                ArrayList<Treningsokt> notatTreningsokt = new ArrayList<Treningsokt>();
                ArrayList<Treningsokt> availableTreningsoktList = Treningsokt.getAvailable(connection, bruker, treningsokts, brukere);
                availableTreningsoktList.stream().forEach(x -> notatTreningsokt.add(x));
                System.out.println("Tilgjengelige treningsokter: " + notatTreningsokt.size());
                treningsoktAntall = notatTreningsokt.size() -  treningsoktAntall;

                if (treningsoktAntall < 0) {
                    treningsoktAntall = 0;
                }

                while (treningsoktAntall > 0) {
                    availableTreningsoktList.remove(0);
                    treningsoktAntall = treningsoktAntall - 1;
                }
                System.out.println("Indekser: " + availableTreningsoktList);

                //ArrayList<Notat> spesiellNotatList = new ArrayList<Notat>();

                for (Treningsokt treningsokt : availableTreningsoktList) {
                    System.out.println("Treningsøkt " + treningsokt.getTreningsoktID() + ", dato " + treningsokt.getDato());
                    Statement statement = null;
                    try{
                        statement = connection.createStatement();
                        PreparedStatement pstmt1 = connection.prepareStatement("SELECT * FROM Notat WHERE treningsoktID = ?");
                        pstmt1.setInt(1, treningsokt.getTreningsoktID());
                        ResultSet resultSet = pstmt1.executeQuery();

                        while(resultSet.next()){
                            Notat notat = new Notat(resultSet.getInt("Id"), treningsokt, resultSet.getInt("Personlig_form"), resultSet.getInt("Prestasjon"), resultSet.getString("Treningsformaal"), resultSet.getString("Opplevelse_av_trening"));
                            System.out.println("Notat " + notat.getNotatID() + ", personlig form " + notat.getPersonligForm() + ", prestasjon " + notat.getPrestasjon() + ", treningsformaal " + notat.getFormal() + ", opplevelse " + notat.getOpplevelse());
                        }

                        resultSet.close();
                        statement.close();

                    }catch (Exception exc){
                        if (statement != null){
                            statement.close();
                        }
                        System.out.println(exc);
                    }
                }


            }

            // Tilbake til orginal logikk
            else if (input.equals("back")){
                System.out.println("Forlater brukeroppførselen og går tilbake til vanlig oppførsel");
                break;
            }
        }
    }


    protected static void treningsoktOppforsel(Connection connection, Bruker bruker, ArrayList<Bruker> brukere, Treningsokt treningsokt,ArrayList<Treningsokt> treningsokts, ArrayList<Ovelse> ovelser, ArrayList<Apparat> apparater, ArrayList<TreningsoktTilOvelse> treningsoktTilOvelseList, ArrayList<Notat> notater ,Scanner scanner) throws SQLException {
        String input;
        System.out.println("Hva vil du gjøre med trenignsøkt "+treningsokt.getTreningsoktID());
        while (true){
            System.out.println("1 = Legg til en øvelse");
            System.out.println("2 = Legg til notat");
            System.out.println("back = Gå tilbake til valg for bruker");
            input = scanner.nextLine();
            if (input.equals("1")){
                System.out.println("Hvilken øvelse? ");
                String ovelseNavn = scanner.nextLine();
                Ovelse ovelse = Ovelse.getOvelse(connection, ovelseNavn, ovelser, apparater);
                if (ovelse != null){
                    Integer kilo = null;
                    Integer sett = null;
                    if (ovelse.getHarApparat()){
                        System.out.println("Hvor mange kilo? ");
                        kilo = scanner.nextInt();
                        System.out.println("Hvor mange sett? ");
                        sett = scanner.nextInt();
                    }
                    TreningsoktTilOvelse.registerTreningsOktTilOvelse(connection, treningsokt, ovelse, kilo, sett, treningsoktTilOvelseList, treningsokts, ovelser);
                } else{
                    System.out.println("Øvelsen eksisterer ikke. Hva vil du gjøre?");
                    while (true){
                        System.out.println("1 = Registrer en ny øvelse som du skal bruke");
                        System.out.println("2 = Avbrytt");
                        input = scanner.nextLine();
                        if (input.equals("1")){
                            ovelse = addOvelse(scanner, connection, apparater, ovelser);
                            if (ovelse == null){
                                System.out.println("Noe gikk feil ved opprettelse av ovelsen. Vi avslutter oppgaven");
                                break;
                            } else{
                                Integer kilo = null;
                                Integer sett = null;
                                if (ovelse.getHarApparat()){
                                    System.out.println("Hvor mange kilo?");
                                    kilo = scanner.nextInt();
                                    System.out.println("Hvor mange sett?");
                                    sett = scanner.nextInt();
                                }
                                TreningsoktTilOvelse.registerTreningsOktTilOvelse(connection, treningsokt, ovelse, kilo, sett, treningsoktTilOvelseList, treningsokts, ovelser);
                                break;
                            }
                        }
                    }
                }
            }
            else if(input.equals("2")){
                addNotat(scanner, connection, notater, treningsokt, treningsokts, brukere);
            }
            else if (input.equals("back")) return;
        }
    }

    protected static Ovelse addOvelse(Scanner scanner, Connection connection, ArrayList<Apparat> apparater, ArrayList<Ovelse> ovelser) throws SQLException{
        System.out.println("Hva er øvelsens navn?");
        String name = scanner.nextLine();
        System.out.println("Apparat(La denne være tom hvis øvelsen ikke inneholder et apparat):");
        Scanner sc = new Scanner(System.in); // Måtte lage ny scanner. Hvorfor? Fordi den globale scanneren sluttet å fungere når den ble kalt mange ganger
        String apparatNavn = sc.nextLine();
        Apparat apparat = null;
        if(!apparatNavn.equals("")){
            apparat = Apparat.getApparat(connection, apparatNavn, apparater);
            if(apparat == null){
                System.out.println("Apparatet eksister ikke! Hva vil du gjøre?");
                while (true){
                    System.out.println("1 = Fortsett å lage øvelsen uten apparet");
                    System.out.println("2 = Registrer et apparat og fortsett med dette");
                    System.out.println("3 = Avbryt registrering av øvelsen");
                    String input = sc.nextLine();
                    if (input.equals("1")){
                        break;
                    } else if (input.equals("2")){
                        addApparat(scanner, connection, apparater);
                        break;
                    } else if(input.equals("3")){
                        return null;
                    } else System.out.println("Ingen av valgene er registrert. Prøv igjen");
                }
            }
        }
        System.out.println("Skriv en passende beskrvelse til øvelsen:");
        String beskrivelse = sc.nextLine();

        Ovelse ovelse = Ovelse.getOvelse(connection, name, ovelser, apparater);
        if(ovelse != null){
            System.out.println("Denne øvelsen eksisterer allerede.");
            return ovelse;
        } else {
            ovelse = Ovelse.registerOvelse(connection,name, apparat != null, apparat, beskrivelse, ovelser, apparater);
            ovelser.add(ovelse);
            System.out.println("Suksess! Øvelsen " + name + " er blitt lagt til!");
            return ovelse;
        }
    }


    protected static void addNotat(Scanner scanner, Connection connection, ArrayList<Notat> notater, Treningsokt treningsokt, ArrayList<Treningsokt> treningsoktList, ArrayList<Bruker> brukere) throws SQLException{
        Notat notat = Notat.alreadyNotat(connection, treningsokt, notater);
        if (notat != null){
            System.out.println("Trenignsøkten har allerede et notat");
            return;
        } else {
            System.out.println("Notat finnes ikke, lager nytt!");
            Integer id = null;
            Integer personlig_form = null;
            Integer prestasjon = null;
            String treningsformaal = "";
            String opplevelse = "";
            System.out.println("Personlig_form: ");
            personlig_form = scanner.nextInt();
            System.out.println("Prestasjon: ");
            prestasjon = scanner.nextInt();
            System.out.println("Treningsformål: ");
            scanner.nextLine();
            treningsformaal = scanner.nextLine();
            System.out.println("Opplevelse av trening: ");
            opplevelse = scanner.nextLine();
            if (treningsokt == null) {
                System.out.println("Valgt treningsøkt finnes ikke");
            } else {
                try {
                    System.out.println("register new notat");
                    notater.add(Notat.registerNewNotat(connection, treningsoktList, notater, treningsokt, personlig_form, prestasjon, treningsformaal, opplevelse));
                } catch (Exception e) {
                    System.out.println("Noe gikk galt. Prøv igjen senere ;)");
                }
            }
        }
    }

    protected static void addApparat(Scanner scanner, Connection connection, ArrayList<Apparat> apparater) throws SQLException {
        String apparatNavn = "";
        String beskrivelse = "";
        Scanner sc = new Scanner(System.in);

        while(apparatNavn.equals("") || beskrivelse.equals("")){
            System.out.println("NB: tom streng aksepteres ikke");
            System.out.println("Apparatets navn: ");
            apparatNavn = scanner.nextLine();
            System.out.println("Beskrivelse til apparatet: ");
            beskrivelse = scanner.nextLine();
        }
        Apparat apparat = Apparat.getApparat(connection, apparatNavn, apparater);
        if(apparat != null){
            System.out.println("Apparat finnes allerede");
        } else {
            apparater.add(Apparat.registerApparat(connection, apparatNavn, beskrivelse, apparater));
            System.out.println("Suksess! appartet " + apparatNavn + " er blitt lagt til!");
        }
    }

    protected static void getOvelserInGruppe(Connection connection) throws SQLException {
        Statement statement = null;
        ResultSet resultSet = null;
        try{
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM OvelseGruppe"); //Henter inn tabellen

            Map<String, ArrayList<String> > map = new HashMap<>(); //Oppretter en dicionary slik det skal bli bedre å printe

            while (resultSet.next()){
                String ovelseGruppeNavn = resultSet.getString("ovelseGruppeNavn"); //fetcher navn-attributtene fra tabellen
                PreparedStatement prepStatement = connection.prepareStatement("SELECT * FROM OvelsesgruppeTilOvelse WHERE ovelseGruppeNavn=?");
                prepStatement.setString(1, ovelseGruppeNavn);
                ResultSet resultSet1 = prepStatement.executeQuery();
                ArrayList<String> ovelser = new ArrayList<>(); //Arraylist som hører til i dictionaryen
                while (resultSet1.next()){
                    ovelser.add(resultSet1.getString("ovelseNavn"));                    //Dersom dictionaryen ikke inneholder gruppen, legg til ovelsen til listen og lag en ny entry i dictionaryen
                }
                map.put(ovelseGruppeNavn,ovelser);
            }
            System.out.println("Øvelsegruppe    Øvelser"); //Printer
            for (String key : map.keySet()){
                System.out.println(key + ": " + map.get(key));
            }
            System.out.println(); //Estetisk mellomrom

            resultSet.close();
            statement.close();

        }catch (Exception exc){
            if (statement != null){
                statement.close();
            }
            System.out.println(exc);
        }


    }

    protected static void getResultsLog(Connection connection, String ovelseNavn, Date fromDate, Date toDate) throws SQLException {
        Statement statement = null;
        ResultSet resultSet = null;

        try{
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM TreningsoktTilOvelse INNER JOIN Treningsokt ON TreningsoktTilOvelse.treningsoktID = Treningsokt.treningsoktID WHERE TreningsoktTilOvelse.ovelseNavn = '" + ovelseNavn + "' AND Treningsokt.dato BETWEEN '" + fromDate + "' AND '" + toDate + "' ");


            Integer kilo = null;
            Integer sett = null;
            Date datolapp = null;
            String navn = null;

            System.out.println(); //Estetisk println
            System.out.println("Dette er resultatene for " + ovelseNavn + " mellom " + fromDate + " og " + toDate + ":");

            while (resultSet.next()){
                sett = resultSet.getInt("Sett");
                kilo = resultSet.getInt("Kilo");
                datolapp = resultSet.getDate("dato");
                navn = resultSet.getString("brukernavn");

                System.out.print(navn + " " + datolapp + " tok: ");
                System.out.print(kilo + " kg x ");
                System.out.print(sett + " sett" + "\n");
            }
            System.out.println(); //Estetisk println

            resultSet.close();
            statement.close();

        }catch (Exception exc){
            if (statement != null){
                statement.close();
            }
            System.out.println(exc);
        }


    }




}
