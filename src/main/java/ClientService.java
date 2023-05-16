import tables.Client;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientService {
    private final Connection connection;
    private PreparedStatement preparedStatement;

    public ClientService() {
        try {
            connection = getConnection();
            preparedStatement = null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Connection getConnection() throws SQLException {
        return Database.getConnection();
    }

    public Long create(String name) {
        String insert = "INSERT INTO client (name) VALUES (?)";

        try {
            preparedStatement = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, name);
            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            return generatedKeys.next() ? generatedKeys.getLong(1) : null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getById(long id) {
        String select = "SELECT id, name FROM client WHERE id = ?";

        try {
            preparedStatement = connection.prepareStatement(select);
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next() ? resultSet.getString("name") : null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setName(Long id, String name) {
        String update = "UPDATE client SET name = ? WHERE id = ?";

        try {
            preparedStatement = connection.prepareStatement(update);
            preparedStatement.setString(1, name);
            preparedStatement.setLong(2, id);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteById(long id) {
        String deleteProjectWorkerDependency = "DELETE FROM project_worker WHERE project_id IN (SELECT id FROM project WHERE client_id = ?)";
        String deleteProjectDependency = "DELETE FROM project WHERE client_id = ?";
        String deleteClient = "DELETE FROM client WHERE id = ?";

        try {
            preparedStatement = connection.prepareStatement(deleteProjectWorkerDependency);
            preparedStatement.setLong(1, id);
            preparedStatement.execute();

            preparedStatement = connection.prepareStatement(deleteProjectDependency);
            preparedStatement.setLong(1, id);
            preparedStatement.execute();

            preparedStatement = connection.prepareStatement(deleteClient);
            preparedStatement.setLong(1, id);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }




    public List<Client> listAll() {
        String select = "SELECT id, name FROM client ORDER BY id";
        List<Client> clients = new ArrayList<>();

        try {
            preparedStatement = connection.prepareStatement(select);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                clients.add(new Client(resultSet.getLong("id"), resultSet.getString("name")));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return clients;
    }
}
