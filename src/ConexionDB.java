import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
    private static final String HOST     = "localhost";
    private static final String PUERTO   = "3306";
    private static final String BASE     = "memoryflesh";
    private static final String USUARIO  = "root";
    private static final String PASSWORD = "joaco2122007."; // <-- CAMBIÁ ESTO

    private static final String URL =
        "jdbc:mysql://" + HOST + ":" + PUERTO + "/" + BASE
        + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    // Constructor privado para evitar instancias
    private ConexionDB() {}

    /**
     * Devuelve una NUEVA conexión cada vez (mejor para transacciones)
     * IMPORTANTE: Cerrar la conexión con try-with-resources
     */
    public static Connection getConexion() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            System.out.println("[ConexionDB] Nueva conexión establecida con " + BASE);
            return con;
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                "Driver MySQL no encontrado. Agregá mysql-connector-j al classpath.", e
            );
        }
    }

    // Método de prueba
    public static void main(String[] args) {
        try (Connection con = ConexionDB.getConexion()) {
            System.out.println("¡Conexión exitosa! Auto-commit: " + con.getAutoCommit());
        } catch (SQLException e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }
    }
}