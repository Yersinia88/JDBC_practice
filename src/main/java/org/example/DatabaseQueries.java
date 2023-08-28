package org.example;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class DatabaseQueries {
    public static final int NUMBER_OF_CONTINENTS = 7;
    private final Connection conn;
    private final PreparedStatement pstmListOfCitiesOfACountry;
    private final PreparedStatement pstmListOfCountriesAndCapitalsAccordingToContinents;
    private final PreparedStatement pstmListOfCountriesAccordingToLanguage;
    private final PreparedStatement pstmListOfCountriesAccordingToLanguageAndPopulation;
    private final PreparedStatement pstmListOfContientAndGNPPerCapita;


    public DatabaseQueries(Connection conn) throws DatabaseException {
        this.conn = conn;
        try {
            pstmListOfCitiesOfACountry = conn.prepareStatement("" +
                    "SELECT city.Name, city.Population\n" +
                    "FROM `city`\n" +
                    "LEFT JOIN country ON city.CountryCode = country.Code\n" +
                    "WHERE country.Name = ?" +
                    "ORDER BY city.Population DESC; ");

            pstmListOfCountriesAndCapitalsAccordingToContinents = conn.prepareStatement("" +
                    "SELECT country.Name, city.Name\n" +
                    "FROM `country`\n" +
                    "LEFT JOIN city ON country.Code = city.CountryCode\n" +
                    "WHERE (country.Continent LIKE ? " +
                    "OR country.Continent LIKE ? " +
                    "OR country.Continent LIKE ? " +
                    "OR country.Continent LIKE ? " +
                    "OR country.Continent LIKE ? " +
                    "OR country.Continent LIKE ? " +
                    "OR country.Continent LIKE ?) " +
                    "AND city.ID = country.Capital " +
                    "ORDER BY country.Continent, country.Name;");

            pstmListOfCountriesAccordingToLanguage = conn.prepareStatement("" +
                    "SELECT country.Name, (country.Population*(countrylanguage.Percentage/100)), " +
                    "countrylanguage.IsOfficial\n" +
                    "FROM country\n" +
                    "LEFT JOIN countrylanguage ON country.Code = countrylanguage.CountryCode\n" +
                    "WHERE countrylanguage.Language = ?\n" +
                    "ORDER BY isOfficial, (country.Population*(countrylanguage.Percentage/100)) DESC;");

            pstmListOfCountriesAccordingToLanguageAndPopulation = conn.prepareStatement("" +
                    " SELECT countrylanguage.Language, COUNT(country.Code), SUM(country.Population *(countrylanguage.Percentage/100))\n" +
                    "    FROM country\n" +
                    "    LEFT JOIN countrylanguage ON country.Code = countrylanguage.CountryCode\n" +
                    "    GROUP By countrylanguage.Language\n" +
                    "    HAVING SUM(country.Population *(countrylanguage.Percentage/100)) > ?\n" +
                    "    ORDER BY SUM(country.Population *(countrylanguage.Percentage/100)) DESC;");

            pstmListOfContientAndGNPPerCapita = conn.prepareStatement("" +
                    "SELECT Continent, SUM(GNP/Population*1000000)\n" +
                    "    FROM country\n" +
                    "    WHERE Continent <> \"Antarctica\"\n" +
                    "    GROUP BY Continent\n" +
                    "    ORDER BY  SUM(GNP/Population*1000000) DESC;");

        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public void listOfCitiesOfACountry(String countryName) throws DatabaseException {
        try {
            pstmListOfCitiesOfACountry.setString(1, countryName);
            ResultSet result = pstmListOfCitiesOfACountry.executeQuery();

            System.out.println();
            System.out.printf("%-30.30s  %-30.30s%n", "Cities in " + countryName, "Population");
            while (result.next()) {
                String name = result.getString(1);
                int population = result.getInt(2);
                System.out.printf("%-30.30s  %d%n", name, population);
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public void listOfCountriesAndCapitalsAccordingToContinents(String continents) throws DatabaseException {
        String[] continentsArray = continents.split(",");

        for (int i = 0; i < continentsArray.length; i++) {
            if (continentsArray[i].contains("*")) {
                StringBuilder builder = new StringBuilder();
                continentsArray[i] = continentsArray[i].replace("*", "").trim();
                builder.append("%").append(continentsArray[i]).append("%");
                continentsArray[i] = builder.toString();
            } else {
                StringBuilder builder = new StringBuilder();
                continentsArray[i] = continentsArray[i].trim();
                builder.append("%").append(continentsArray[i]).append("%");
            }
        }

        try {

            int i = 1;
            int j = 0;
            while (i <= continentsArray.length && j < continentsArray.length) {
                pstmListOfCountriesAndCapitalsAccordingToContinents.setString(i, continentsArray[j]);
                i++;
                j++;
            }

            if (continentsArray.length != 7) {
                for (int k = continentsArray.length; k <= NUMBER_OF_CONTINENTS; k++) {
                    pstmListOfCountriesAndCapitalsAccordingToContinents.setString(k, null);
                }
            }

            ResultSet result = pstmListOfCountriesAndCapitalsAccordingToContinents.executeQuery();

            System.out.println();
            System.out.printf("%-30.30s  %-30.30s%n", "Country name", "Capital name");
            while (result.next()) {
                String countryName = result.getString(1);
                String capitalName = result.getString(2);
                System.out.printf("%-30.30s  %-30.30s%n", countryName, capitalName);
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public void listOfCities(String cities) throws DatabaseException {
        String[] citiesArray = cities.split(",");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < citiesArray.length; i++) {
            if (citiesArray[i].contains("*")) {
                if (i != 0) {
                    citiesArray[i] = citiesArray[i].replace("*", "").trim();
                    builder.append("OR city.Name LIKE ").append("\"%").append(citiesArray[i]).append("%\"").append(" ");
                    citiesArray[i] = builder.toString();
                } else {
                    citiesArray[i] = citiesArray[i].replace("*", "").trim();
                    builder.append("city.Name LIKE ").append("\"%").append(citiesArray[i]).append("%\"").append(" ");
                    citiesArray[i] = builder.toString();
                }

            } else {
                if (i != 0) {
                    citiesArray[i] = citiesArray[i].trim();
                    builder.append("OR city.Name LIKE ").append("\"%").append(citiesArray[i]).append("%\"").append(" ");
                } else {
                    citiesArray[i] = citiesArray[i].trim();
                    builder.append("city.Name LIKE ").append("\"%").append(citiesArray[i]).append("%\"").append(" ");
                }
            }
        }


        try (Statement statement = conn.createStatement()) {
            String sql = "SELECT city.Name , \n" +
                    "(city.Population/country.population)*100, \n" +
                    "        country.Name, \n" +
                    "        CASE WHEN country.Capital = city.ID THEN \"yes\" ELSE \"no\"\n" +
                    "        END AS IsCapital\n" +
                    "FROM `city`\n" +
                    "LEFT JOIN country ON city.CountryCode = country.Code\n" +
                    "WHERE (" + builder + ")\n" +
                    "GROUP BY city.Name;";

            ResultSet result = statement.executeQuery(sql);

            System.out.println();
            System.out.printf("%-30.30s  %-30.30s %-30.30s %-30.30s%n", "City name", "Population distribution", "Country name", "Is capital?");
            while (result.next()) {
                String cityName = result.getString(1);
                Double distribution = result.getDouble(2);
                String countryName = result.getString(3);
                String isCapital = result.getString(4);
                System.out.printf("%-30.30s  %-30.2f %-30.30s %-30.30s%n", cityName, distribution, countryName, isCapital);
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public void listOfCountriesAccordingToLanguage(String language) throws DatabaseException {
        try {
            pstmListOfCountriesAccordingToLanguage.setString(1, language);
            ResultSet result = pstmListOfCountriesAccordingToLanguage.executeQuery();

            System.out.println();
            System.out.printf("%-30.30s  %-30.30s %-30.30s%n", "Country name", "Speakers of the language", "isOfficial");
            while (result.next()) {
                String name = result.getString(1);
                Double population = result.getDouble(2);
                String isOfficial = result.getString(3);
                System.out.printf("%-30.30s  %-30.2f %-30.30s%n", name, population, isOfficial);
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }


    public void listOfCountriesAccordingToLanguageAndPopulation(int number) throws DatabaseException {
        try {
            pstmListOfCountriesAccordingToLanguageAndPopulation.setInt(1, number);
            System.out.println(pstmListOfCountriesAccordingToLanguageAndPopulation);
            ResultSet result = pstmListOfCountriesAccordingToLanguageAndPopulation.executeQuery();

            System.out.println();
            System.out.printf("%-30.30s  %-30.30s %-30.30s%n", "Language", "Number of countries", "Number of people");
            while (result.next()) {
                String languageName = result.getString(1);
                Integer numberOfCountries = result.getInt(2);
                Integer numberOfPeople = result.getInt(3);
                System.out.printf("%-30.30s  %-30d %-30.30s%n", languageName, numberOfCountries, numberOfPeople);
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public void listOfContinentAndGNPPerCapita() throws DatabaseException {
        List<Double> list = new ArrayList<>();

        try {
            ResultSet result = pstmListOfContientAndGNPPerCapita.executeQuery();

            System.out.println();
            System.out.printf("%-30.30s   %-30.30s%n", "Continent", "GNP/capita");
            while (result.next()) {
                list.add(result.getDouble(2));
                String continentName = result.getString(1);
                Double gnpPerCapita = result.getDouble(2);
                System.out.printf("%-30.30s  %-30.2f%n", continentName, gnpPerCapita);
            }

            double number = 0;
            for (Double aDouble : list) {
                number += aDouble;
            }
            System.out.println();
            System.out.println("World average of the GNP/capita: " + String.format("%.2f", number / list.size()));
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }
}
