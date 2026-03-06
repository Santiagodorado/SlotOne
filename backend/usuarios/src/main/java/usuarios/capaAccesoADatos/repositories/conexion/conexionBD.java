package usuarios.capaAccesoADatos.repositories.conexion;

import java.sql.*;

public class conexionBD {

    private Connection connection;

    public conexionBD() {
    }

    public void conectar() {
        try {
            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection("jdbc:h2:mem:testusuariosdb", "sa", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void desconectar() {
        try {
            if (connection != null)
                connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}