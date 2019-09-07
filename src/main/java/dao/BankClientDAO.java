package dao;

import com.sun.deploy.util.SessionState;
import model.BankClient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BankClientDAO {

    private Connection connection;

    public BankClientDAO(Connection connection) {
        this.connection = connection;
    }

    public List<BankClient> getAllBankClient() throws SQLException {
        List<BankClient> list = new ArrayList<>();
        Statement stmt = connection.createStatement();
        stmt.execute("select * from bank_client");
        ResultSet result = stmt.getResultSet();

        result.last();
        long endOfList = result.getRow();

        result.first();

        for (int i = 1; i <= endOfList; i++) {
            list.add(new BankClient(
                    result.getLong("id"),
                    result.getString("name"),
                    result.getString("password"),
                    result.getLong("money")));

            result.next();
        }

        result.close();
        stmt.close();

        return list;
    }

    public boolean validateClient(String name, String password) throws SQLException {
        List<BankClient> list = getAllBankClient();

        for (int i = 0; i < list.size(); i++) {
            if (name.equals(list.get(i).getName()) & password.equals(list.get(i).getPassword())) {
                return true;
            }
        }

        return false;
    }

    public void updateClientsMoney(String name, String password, Long transactValue) throws SQLException {
        String query = "SELECT `money`, `id` FROM bank_client WHERE `name` = ? AND `password` = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, name);
        stmt.setString(2, password);

        ResultSet result = stmt.executeQuery();
        result.next();

        long resultMoney = (long) result.getInt("money") + transactValue;
        int id = result.getInt("id");

        stmt = connection.prepareStatement("update `bank_client` set money = ? where `id` = ?");
        stmt.setLong(1, resultMoney);
        stmt.setInt(2, id);
        stmt.executeUpdate();
//        stmt.executeUpdate("update `bank_client` set money = " + resultMoney + " where `id` = " + result.getInt("id"));

        result.close();
        stmt.close();
    }

    public BankClient getClientById(long id) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("select * from bank_client where `id` = '" + id + "'");

        ResultSet result = stmt.getResultSet();
        result.next();

        BankClient client = new BankClient(
                result.getLong("id"),
                result.getString("name"),
                result.getString("password"),
                result.getLong("money"));

        result.close();
        stmt.close();

        return client;
    }

    public boolean isClientHasSum(String name, Long expectedSum) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("select `money`, `id` from `bank_client` where `name` = '" + name + "'");

        ResultSet result = stmt.getResultSet();
        result.next();
        long resultMoney = (long)result.getInt("money");

        result.close();
        stmt.close();

        return resultMoney >= Math.abs(expectedSum);
    }

    public long getClientIdByName(String name) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("select * from bank_client where name='" + name + "'");
        ResultSet result = stmt.getResultSet();
        result.next();
        Long id = result.getLong(1);
        result.close();
        stmt.close();
        return id;
    }

    public BankClient getClientByName(String name) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("select * from bank_client where `name` = '" + name + "'");

        ResultSet result = stmt.getResultSet();
        result.next();

        BankClient client = new BankClient(
                result.getLong("id"),
                result.getString("name"),
                result.getString("password"),
                result.getLong("money"));

        stmt.close();

        return client;
    }

    public void addClient(BankClient client) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("INSERT INTO bank_client (`name`, `password`, `money`) VALUES ('" +
                client.getName() + "', '" +
                client.getPassword() + "', '" +
                client.getMoney() + "')");

        stmt.close();
    }

    public void createTable() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("create table if not exists bank_client (id bigint auto_increment, name varchar(256), password varchar(256), money bigint, primary key (id))");
        stmt.close();
    }

    public void dropTable() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("DROP TABLE IF EXISTS bank_client");
        stmt.close();
    }
}
