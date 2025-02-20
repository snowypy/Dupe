package codes.snowy.dupeJS.economy;

import codes.snowy.dupeJS.DupeJS;
import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DupeyEconomy extends AbstractEconomy {

    private Connection dbConnection;

    private DupeyEconomy() {
        connectDatabase();
        createTable();
    }

    private void connectDatabase() {
        try {
            dbConnection = DriverManager.getConnection("jdbc:sqlite:Databases/economy.db");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTable() {
        try (PreparedStatement statement = dbConnection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS balances (player_name TEXT PRIMARY KEY, balance LONG)")) {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "DupeyEconomy";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 0;
    }

    @Override
    public String format(double amount) {
        return ((long) amount) + " " + (((long) amount) == 1 ? this.currencyNameSingular() : this.currencyNamePlural());
    }

    @Override
    public String currencyNamePlural() {
        return "Dollars";
    }

    @Override
    public String currencyNameSingular() {
        return "Dollar";
    }

    @Override
    public boolean hasAccount(String playerName) {
        return hasAccountByName(playerName);
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return hasAccountByName(playerName);
    }

    @Override
    public double getBalance(String playerName) {
        return getByName(playerName);
    }

    @Override
    public double getBalance(String playerName, String world) {
        return getByName(playerName);
    }

    @Override
    public boolean has(String playerName, double amount) {
        return hasByName(playerName, amount);
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return hasByName(playerName, amount);
    }

    private boolean hasAccountByName(String playerName) {
        try (PreparedStatement statement = dbConnection.prepareStatement("SELECT * FROM balances WHERE player_name = ?")) {
            statement.setString(1, playerName);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private long getByName(String playerName) {
        try (PreparedStatement statement = dbConnection.prepareStatement("SELECT balance FROM balances WHERE player_name = ?")) {
            statement.setString(1, playerName);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next() ? resultSet.getLong("balance") : 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private boolean hasByName(String playerName, double amount) {
        return getByName(playerName) >= amount;
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        return withdrawPlayer(playerName, null, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        if (amount < 0)
            return new EconomyResponse(0, getBalance(playerName), EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds");

        if (!has(playerName, amount)) {
            return new EconomyResponse(0, getBalance(playerName), EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
        }

        long newBalance = getByName(playerName) - (long) amount;
        updateBalance(playerName, newBalance);

        return new EconomyResponse(amount, getByName(playerName), EconomyResponse.ResponseType.SUCCESS, "");
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        return depositPlayer(playerName, null, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        if (amount < 0)
            return new EconomyResponse(0, getBalance(playerName), EconomyResponse.ResponseType.FAILURE, "Cannot deposit negative funds");

        long newBalance = getByName(playerName) + (long) amount;
        updateBalance(playerName, newBalance);

        return new EconomyResponse(amount, getByName(playerName), EconomyResponse.ResponseType.SUCCESS, "");
    }

    private void updateBalance(String playerName, long newBalance) {
        try (PreparedStatement statement = dbConnection.prepareStatement("REPLACE INTO balances (player_name, balance) VALUES (?, ?)")) {
            statement.setString(1, playerName);
            statement.setLong(2, newBalance);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return new EconomyResponse(0, getBalance(player), EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public List<String> getBanks() {
        return new ArrayList<>();
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return false;
    }

    public static void register() {
        Bukkit.getServicesManager().register(Economy.class, new DupeyEconomy(), DupeJS.getInstance(), ServicePriority.Normal);
    }
}
