package org.example.dao;

import org.example.DatabaseException;
import org.example.dto.CountryLanguage;

import java.sql.*;

public class CountryLanguageDaoImpl implements CountryLanguageDao {
    private final Connection conn;
    private final PreparedStatement queryPstmt;
    private final PreparedStatement addLanguagePstmt;
    private final PreparedStatement updateLanguagePstmt;
    private final PreparedStatement deleteLanguagePstmt;


    public CountryLanguageDaoImpl(Connection conn) throws DatabaseException {
        this.conn = conn;
        try {
            try (Statement statement = conn.createStatement()) {
                String sql = "ALTER TABLE city DROP FOREIGN KEY IF EXISTS countryLanguage_ibfk_1;";
                statement.executeUpdate(sql);
            }
            queryPstmt = conn.prepareStatement("" +
                    "SELECT * FROM countrylanguage\n" +
                    "WHERE CountryCode = ? AND  Language = ?");

            addLanguagePstmt = conn.prepareStatement("" +
                    "INSERT INTO countrylanguage (CountryCode, Language, IsOfficial, Percentage)\n" +
                    "VALUES (?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);

            updateLanguagePstmt = conn.prepareStatement("" +
                    "UPDATE countrylanguage\n" +
                    "SET IsOfficial = ?, Percentage = ?\n" +
                    "WHERE CountryCode = ? AND  Language = ?;", Statement.KEEP_CURRENT_RESULT);

            deleteLanguagePstmt = conn.prepareStatement("" +
                    "DELETE FROM countrylanguage\n" +
                    "WHERE CountryCode = ? AND  Language = ?;");

        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public int addCountryLanguage(CountryLanguage countryLanguage) throws DatabaseException {
        if (countryLanguage.getCountryCode() == null || countryLanguage.getCountryCode().isEmpty()) {
            throw new IllegalArgumentException("A város kódjának megadása kötelező.");
        }
        if (countryLanguage.getLanguage() == null || countryLanguage.getLanguage().isEmpty()) {
            throw new IllegalArgumentException("A nyelv megadása kötelező.");
        }
        if (countryLanguage.getIsOfficial() == null || countryLanguage.getIsOfficial().isEmpty()) {
            throw new IllegalArgumentException("A hivatalos jelleg megadása kötelező.");
        }
        if (countryLanguage.getPercentage() == null || countryLanguage.getPercentage().toString().isEmpty()) {
            throw new IllegalArgumentException("A nyelvet beszélők számarányának arányának a megadása kötelező.");
        }

        try {
            addLanguagePstmt.setString(1, countryLanguage.getCountryCode());
            addLanguagePstmt.setString(2, countryLanguage.getLanguage());
            addLanguagePstmt.setString(3, countryLanguage.getIsOfficial());
            addLanguagePstmt.setDouble(4, countryLanguage.getPercentage());

            int result = addLanguagePstmt.executeUpdate();

            if (result != 1) {
                throw new DatabaseException("Nem sikerült létrehozni a rekordot" + countryLanguage);
            } else {
                System.out.println("A hozzáadás sikeres volt!");
                return result;
            }

        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }


    @Override
    public void updateCountryLanguage(CountryLanguage countryLanguage) throws DatabaseException {

        if (countryLanguage.getCountryCode() == null || countryLanguage.getCountryCode().isEmpty()) {
            throw new IllegalArgumentException("A hivatalos jelleg megadása kötelező.");
        }

        if (countryLanguage.getLanguage() == null || countryLanguage.getLanguage().isEmpty()) {
            throw new DatabaseException("A nyelvet beszélők számarányának megadása köteléző.");
        }


        try {

            updateLanguagePstmt.setString(1, countryLanguage.getIsOfficial());
            updateLanguagePstmt.setDouble(2, countryLanguage.getPercentage());
            updateLanguagePstmt.setString(3, countryLanguage.getCountryCode());
            updateLanguagePstmt.setString(4, countryLanguage.getLanguage());

            int isUpdated = updateLanguagePstmt.executeUpdate();
            System.out.println(isUpdated);

            if (isUpdated == 0) {
                throw new DatabaseException("Nincs ilyen országkód és nyelv. Nem sikerült a rekord módosítása.");
            } else {
                System.out.println("A módosítás megtörtént!");
            }

        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public void deleteCountryLanguage(CountryLanguage countryLanguage) throws DatabaseException {
        if (countryLanguage.getCountryCode() == null || countryLanguage.getCountryCode().isEmpty()) {
            throw new IllegalArgumentException("A hivatalos jelleg megadása kötelező.");
        }

        if (countryLanguage.getLanguage() == null || countryLanguage.getLanguage().isEmpty()) {
            throw new DatabaseException("A nyelvet beszélők számarányának megadása köteléző.");
        }
        try {
            deleteLanguagePstmt.setString(1, countryLanguage.getCountryCode());
            deleteLanguagePstmt.setString(2, countryLanguage.getLanguage());

            int isDeleted = deleteLanguagePstmt.executeUpdate();

            if (isDeleted == 0) {
                System.out.println("Nincs ilyen országkód és nyelv párosítás, nem tudjuk törölni a rekordot.");
            } else {
                System.out.println("A törlés megtörtént!");
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public void queryCityData(CountryLanguage countryLanguage) throws DatabaseException {
        if (countryLanguage.getCountryCode() == null || countryLanguage.getCountryCode().isEmpty()) {
            throw new IllegalArgumentException("A hivatalos jelleg megadása kötelező.");
        }

        if (countryLanguage.getLanguage() == null || countryLanguage.getLanguage().isEmpty()) {
            throw new DatabaseException("A nyelvet beszélők számarányának megadása köteléző.");
        }

        try {
            queryPstmt.setString(1, countryLanguage.getCountryCode());
            queryPstmt.setString(2, countryLanguage.getLanguage());
            ResultSet result = queryPstmt.executeQuery();
            while (result.next()) {
                String countryCode = result.getString(1);
                String language = result.getString(2);
                Object isOfficial = result.getObject(3);
                Double percentage = result.getDouble(4);

                System.out.print("Valóban ezt a rekordot szerentné módosítani? ");
                System.out.printf("%s, %s, %s, %.2f%n", countryCode, language, isOfficial, percentage);
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }
}
