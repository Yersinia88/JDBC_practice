package org.example;

import org.example.dao.CityDao;
import org.example.dao.CityDaoImpl;
import org.example.dao.CountryLanguageDao;
import org.example.dao.CountryLanguageDaoImpl;
import org.example.dto.City;
import org.example.dto.CountryLanguage;

import java.util.Scanner;

public class Main {
    public static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
        try (DatabaseConnection conn = new DatabaseConnection("world", "root", "")) {
            DatabaseQueries databaseQueries = new DatabaseQueries(conn.getConn());
            CityDao cityDao = new CityDaoImpl(conn.getConn());
            CountryLanguageDao countryLanguageDao = new CountryLanguageDaoImpl(conn.getConn());
            menu(databaseQueries, cityDao, countryLanguageDao);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void menu(DatabaseQueries databaseQueries, CityDao cityDao, CountryLanguageDao countryLanguageDao) throws DatabaseException {
        int number;
        do {
            menuPontok();
            System.out.println("Kérem írjon be egy számot: ");
            number = SCANNER.nextInt();
            SCANNER.nextLine();

            if (number == 0) {
                SCANNER.close();
                System.out.println("A program most kilép. Viszlát!");
            } else {
                switch (number) {
                    case 1 -> {
                        System.out.println("Kérek egy ország nevet: ");
                        String countryName = SCANNER.nextLine();
                        databaseQueries.listOfCitiesOfACountry(countryName);
                    }
                    case 2 -> {
                        System.out.println("Kérek egy vagy több kontinens nevet. A kontinens megadható dzsókeresen, azaz *-ot követően a keresett szövegrészlet beírása. Több kontinens esetén vessző legyen azok között!");
                        String continents = SCANNER.nextLine();
                        databaseQueries.listOfCountriesAndCapitalsAccordingToContinents(continents);
                    }
                    case 3 -> {
                        System.out.println("Kérek egy vagy több várps nevet.  A város megadható dzsókeresen, a városokat vesszőve lelválasztva kell beírni!");
                        String countries = SCANNER.nextLine();
                        databaseQueries.listOfCities(countries);
                    }
                    case 4 -> {
                        System.out.println("Kérek egy nyelv nevet: ");
                        String language = SCANNER.nextLine();
                        databaseQueries.listOfCountriesAccordingToLanguage(language);
                    }
                    case 5 -> {
                        System.out.println("Kérek egy számot, az adott lélekszámot meghaladó beszélővel rendelkező nyelvek listázásáho: ");
                        int numberOfLimit = SCANNER.nextInt();
                        SCANNER.nextLine();
                        databaseQueries.listOfCountriesAccordingToLanguageAndPopulation(numberOfLimit);
                    }
                    case 6 -> databaseQueries.listOfContinentAndGNPPerCapita();

                    case 7 -> {
                        System.out.println("Írja be a város nevét: ");
                        String cityName = SCANNER.nextLine();
                        System.out.println("Írja be az ország kódot: ");
                        String countryCode = SCANNER.nextLine();
                        System.out.println("Írja be a közigazgatási terület nevét: ");
                        String district = SCANNER.nextLine();
                        System.out.println("Írja be a lélekszámot:");
                        int population = SCANNER.nextInt();
                        SCANNER.nextLine();
                        cityDao.addCity(new City(cityName, countryCode, district, population));
                    }
                    case 8 -> {
                        System.out.println("Írja be a város azonosítóját:");
                        int id = SCANNER.nextInt();
                        SCANNER.nextLine();
                        cityDao.queryCityData(id);
                        System.out.println("Valóban ezt az adatot szeretné módosítani? Írjon be a válaszának megfelelően egy karaktert: Igen - Y, Nem - N");
                        String userResponse = SCANNER.nextLine();
                        if (userResponse.equals("Y") || userResponse.equals("y")) {
                            System.out.println("Írja be a város azonosítóját:");
                            int id2 = SCANNER.nextInt();
                            SCANNER.nextLine();
                            System.out.println("Írja be a közigazgatási terület nevét: ");
                            String district = SCANNER.nextLine();
                            System.out.println("Írja be a lélekszámot:");
                            int population = SCANNER.nextInt();
                            SCANNER.nextLine();
                            cityDao.updateCity(new City(id2, district, population));
                        } else {
                            menuPontok();
                        }

                    }
                    case 9 -> {
                        System.out.println("Írja be a város azonosítóját:");
                        int id = SCANNER.nextInt();
                        SCANNER.nextLine();
                        cityDao.queryCityData(id);
                        System.out.println("Írjon be a válaszának megfelelően egy karaktert: Igen - Y, Nem - N");
                        String userResponse = SCANNER.nextLine();
                        if (userResponse.equals("Y") || userResponse.equals("y")) {
                            System.out.println("Írja be a város azonosítóját:");
                            int id2 = SCANNER.nextInt();
                            cityDao.deleteCity(id2);
                        } else {
                            menuPontok();
                        }
                    }
                    case 10 -> {
                        System.out.println("Írja be az ország kódját: ");
                        String countryCode = SCANNER.nextLine();
                        System.out.println("Írja be a nyelvet: ");
                        String language = SCANNER.nextLine();
                        System.out.println("Hivatalos jelleg, igen -T, nem - F: ");
                        String isOfficial = SCANNER.nextLine();
                        System.out.println("Írja be a beszélői számarányt:");
                        double percentage = SCANNER.nextDouble();
                        SCANNER.nextLine();
                        countryLanguageDao.addCountryLanguage(new CountryLanguage(countryCode, language, isOfficial, percentage));
                    }
                    case 11 -> {
                        System.out.println("\"Írja be az ország kódját:");
                        String countryCode = SCANNER.nextLine();
                        System.out.println("\"Írja be a beszélt nyelvet:");
                        String language = SCANNER.nextLine();
                        countryLanguageDao.queryCityData(new CountryLanguage(countryCode, language));
                        System.out.println("Valóban ezt az adatot szeretné módosítani? Írjon be a válaszának megfelelően egy karaktert: Igen - Y, Nem - N");
                        String userResponse = SCANNER.nextLine();
                        if (userResponse.equals("Y") || userResponse.equals("y")) {
                            System.out.println("\"Írja be az ország kódját:");
                            String countryCode2 = SCANNER.nextLine();
                            System.out.println("\"Írja be a beszélt nyelvet:");
                            String language2 = SCANNER.nextLine();
                            System.out.println("Hivatalos jelleg, igen -T, nem - F: ");
                            String isOfficial = SCANNER.nextLine();
                            System.out.println("Írja be a beszélői számarányt:");
                            double percentage = SCANNER.nextDouble();
                            SCANNER.nextLine();
                            countryLanguageDao.updateCountryLanguage(new CountryLanguage(countryCode2, language2, isOfficial, percentage));
                        } else {
                            menuPontok();
                        }

                    }
                    case 12 -> {
                        System.out.println("\"Írja be az ország kódját:");
                        String countryCode = SCANNER.nextLine();
                        System.out.println("\"Írja be a beszélt nyelvet:");
                        String language = SCANNER.nextLine();
                        countryLanguageDao.queryCityData(new CountryLanguage(countryCode, language));
                        System.out.println("Valóban ezt az adatot szeretné módosítani? Írjon be a válaszának megfelelően egy karaktert: Igen - Y, Nem - N");
                        String userResponse = SCANNER.nextLine();
                        if (userResponse.equals("Y") || userResponse.equals("y")) {
                            System.out.println("\"Írja be az ország kódját:");
                            String countryCode2 = SCANNER.nextLine();
                            System.out.println("\"Írja be a beszélt nyelvet:");
                            String language2 = SCANNER.nextLine();
                            countryLanguageDao.deleteCountryLanguage(new CountryLanguage(countryCode2, language2));
                        } else {
                            menuPontok();
                        }
                    }

                }
            }
        } while (number != 0);

    }

    private static void menuPontok() {
        System.out.println();
        System.out.println("Kérem válassszon az alábbi menüpontok közül:");
        System.out.println("0.  kilépés");
        System.out.println("1.  Listázza ki a választott ország (adatbázisban szereplő) városait. A lista a következőket tartalmazza majd: a városok nevét annak lélekszámával, az utóbbi szerinti csökkenő sorrendben.");
        System.out.println("2.  Listázza ki a választott kontinens(ek) országait és azok fővárosát.");
        System.out.println("3.  Listázza ki a választott város vagy városokat. A lista a következőket tartalmazza majd: a város neve, az ország nevével, a lakosságának részarányával a országéhoz képest, valamint annak jelzésével, hogy a város főváros-e.");
        System.out.println("4.  Listázza ki a választott nyelvet beszélő országokat azok nevével, a nyelvet beszélők számával, valamint annak jelzésével, hogy a nyelv ott hivatalos-e.");
        System.out.println("5.  Listázza ki az adott számot meghaladó beszélővel rendelkező nyelveket, a lista tartalmazza majd a nyelv nevét, az országok számát, valamint a beszélők számát.");
        System.out.println("6.  A kontinensek listája az átlagos egy főre eső GNP adattal. A listán nem szerepel a lényegében lakatlan (ill. nagyon alacsony teljes GNP-vel rendelkező) Antarktisz. A lista utánszerepel világátlag is.");
        System.out.println("7.  Város beillesztése. Írja be a beillesztení kívánt városnevet: ");
        System.out.println("8.  Város módosítása (ID alapján): lélekszám és közigazgatási terület neve.");
        System.out.println("9.  Város törlése (ID alapján).");
        System.out.println("10. Beszélt nyelv létrehozása egy országban.");
        System.out.println("11. Beszélt nyelv módosítása egy országban (nyelv és országkód alapján): beszélői számarány és hivatalos jelleg");
        System.out.println("12. Beszélt nyelv törlése egy országban (nyelv és országkód alapján)");
    }
}