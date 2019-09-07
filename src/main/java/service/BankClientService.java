package service;

import dao.BankClientDAO;
import exception.DBException;
import model.BankClient;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class BankClientService {

    public BankClientService() {
    }

    public BankClient getClientById(long id) throws DBException {
        try {
            return getBankClientDAO().getClientById(id);
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public BankClient getClientByName(String name) throws DBException {
        try {
            return getBankClientDAO().getClientByName(name);
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public List<BankClient> getAllClient() throws DBException {
        try {
            return getBankClientDAO().getAllBankClient();
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public boolean deleteClient(String name) throws DBException {
        BankClientDAO bankClientDAO = getBankClientDAO();
        try {
            BankClient client = bankClientDAO.getClientByName(name);
            getMysqlConnection().createStatement().execute("DELETE FROM bank_client WHERE (`id` = '" + client.getId() + "')");

            return true;
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public boolean addClient(BankClient client) throws DBException {
        try {
            BankClientDAO dao = getBankClientDAO();

            if (dao.validateClient(client.getName(), client.getPassword())) {
                return false;
            } else if (getAllClient().contains(client)){
                return false;
            } else {
                dao.addClient(client);
                return true;
            }
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public boolean sendMoneyToClient(BankClient sender, String name, Long value) throws DBException, SQLException {
        BankClientDAO bankClientDAO = getBankClientDAO();
        BankClient recipient = bankClientDAO.getClientByName(name);

        try {
            if (bankClientDAO.isClientHasSum(sender.getName(), value)) {
                bankClientDAO.updateClientsMoney(sender.getName(), sender.getPassword(), -Math.abs(value));
                bankClientDAO.updateClientsMoney(name, recipient.getPassword(), Math.abs(value));
            } else {
                return false;
            }
        } catch (SQLException e) {
            throw new DBException(e);
        }

        return true;
    }

    public void cleanUp() throws DBException {
        BankClientDAO dao = getBankClientDAO();
        try {
            dao.dropTable();
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public void createTable() throws DBException{
        BankClientDAO dao = getBankClientDAO();
        try {
            dao.createTable();
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    private static Connection getMysqlConnection() {
        try {
            DriverManager.registerDriver((Driver) Class.forName("com.mysql.cj.jdbc.Driver").newInstance());

            StringBuilder url = new StringBuilder();

            url.
                    append("jdbc:mysql://").        //db type
                    append("localhost:").           //host name
                    append("3306/").                //port
                    append("db_example?").          //db name
                    append("user=root&").          //login
                    append("password=1234").       //password
                    append("&serverTimezone=Europe/Moscow");

            System.out.println("URL: " + url + "\n");

            Connection connection = DriverManager.getConnection(url.toString());
            return connection;
        } catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }

    private static BankClientDAO getBankClientDAO() {
        return new BankClientDAO(getMysqlConnection());
    }
}
