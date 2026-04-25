import java.sql.*;

public class UsuarioDAO {

    // Clase interna para representar un Usuario
    public static class Usuario {
        public int    idUsuario;
        public String nombre;
        public String mail;
        public int    idRol;

        public Usuario(int idUsuario, String nombre, String mail, int idRol) {
            this.idUsuario = idUsuario;
            this.nombre    = nombre;
            this.mail      = mail;
            this.idRol     = idRol;
        }
    }

    // Clase para resultados de operaciones (registro, cambio contraseña, etc.)
    public static class Resultado {
        public final boolean ok;
        public final String  mensaje;
        public final int     id; // Para CrearCuenta y CrearMemoria

        public Resultado(boolean ok, String mensaje) {
            this(ok, mensaje, 0);
        }

        public Resultado(boolean ok, String mensaje, int id) {
            this.ok      = ok;
            this.mensaje = mensaje;
            this.id      = id;
        }
    }

    // ── INICIAR SESION ────────────────────────────────────────────────────
    // SP: sp_IniciarSesion(mail, contrasena)
    // Retorna: idUsuario, nombre, mail, idRol, mensaje
    public static Usuario login(String mail, String contrasena) {
        String sql = "{CALL sp_IniciarSesion(?, ?)}";

        try (Connection con = ConexionDB.getConexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setString(1, mail.trim());
            cs.setString(2, contrasena);

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                        rs.getInt("idUsuario"),
                        rs.getString("nombre"),
                        rs.getString("mail"),
                        rs.getInt("idRol")
                    );
                }
            }

        } catch (SQLException e) {
            // El SP lanza SIGNAL si las credenciales son incorrectas
            System.err.println("[UsuarioDAO.login] " + e.getMessage());
        }

        return null;
    }

    // ── CREAR CUENTA ──────────────────────────────────────────────────────
    // SP: sp_CrearCuenta(nombre, mail, contrasena)
    // Retorna: mensaje, idUsuario
    public static Resultado registrar(String nombre, String mail, String contrasena) {
        String sql = "{CALL sp_CrearCuenta(?, ?, ?)}";

        try (Connection con = ConexionDB.getConexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setString(1, nombre.trim());
            cs.setString(2, mail.trim());
            cs.setString(3, contrasena);

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    String mensaje = rs.getString("mensaje");
                    int idUsuario = rs.getInt("idUsuario");
                    
                    if (idUsuario > 0) {
                        return new Resultado(true, mensaje, idUsuario);
                    } else {
                        return new Resultado(false, mensaje);
                    }
                }
            }

        } catch (SQLException e) {
            // El SP lanza SIGNAL si el usuario/mail ya existe
            // El mensaje del error está en e.getMessage()
            String mensajeError = e.getMessage();
            
            // Extrae solo el mensaje después de "MESSAGE_TEXT = "
            if (mensajeError.contains("El nombre de usuario ya existe")) {
                return new Resultado(false, "El nombre de usuario ya existe");
            } else if (mensajeError.contains("El correo")) {
                return new Resultado(false, "El correo electrónico ya está registrado");
            } else {
                return new Resultado(false, "Error al crear la cuenta");
            }
        }

        return new Resultado(false, "Error desconocido");
    }

    // ── CAMBIAR CONTRASEÑA ────────────────────────────────────────────────
    // SP: sp_CambiarContrasena(idUsuario, contrasenaActual, contrasenaNueva)
    // Retorna: mensaje
    public static Resultado cambiarContrasena(int idUsuario, String actual, String nueva) {
        String sql = "{CALL sp_CambiarContrasena(?, ?, ?)}";

        try (Connection con = ConexionDB.getConexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, idUsuario);
            cs.setString(2, actual);
            cs.setString(3, nueva);

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    String mensaje = rs.getString("mensaje");
                    return new Resultado(true, mensaje);
                }
            }

        } catch (SQLException e) {
            String mensajeError = e.getMessage();
            
            if (mensajeError.contains("Usuario no encontrado")) {
                return new Resultado(false, "Usuario no encontrado");
            } else if (mensajeError.contains("Contraseña actual incorrecta")) {
                return new Resultado(false, "Contraseña actual incorrecta");
            } else {
                return new Resultado(false, "Error al cambiar la contraseña");
            }
        }

        return new Resultado(false, "Error desconocido");
    }

    // ── ELIMINAR CUENTA ───────────────────────────────────────────────────
    // SP: sp_EliminarCuenta(idUsuario, contrasena)
    // Retorna: mensaje
    public static Resultado eliminarCuenta(int idUsuario, String contrasena) {
        String sql = "{CALL sp_EliminarCuenta(?, ?)}";

        try (Connection con = ConexionDB.getConexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, idUsuario);
            cs.setString(2, contrasena);

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    String mensaje = rs.getString("mensaje");
                    return new Resultado(true, mensaje);
                }
            }

        } catch (SQLException e) {
            String mensajeError = e.getMessage();
            
            if (mensajeError.contains("Usuario no encontrado")) {
                return new Resultado(false, "Usuario no encontrado");
            } else if (mensajeError.contains("Contraseña incorrecta")) {
                return new Resultado(false, "Contraseña incorrecta");
            } else {
                return new Resultado(false, "Error al eliminar la cuenta");
            }
        }

        return new Resultado(false, "Error desconocido");
    }
}