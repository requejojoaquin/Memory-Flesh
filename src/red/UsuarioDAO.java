package red;

import java.sql.*;

public class UsuarioDAO {

    public static class Usuario implements java.io.Serializable {
        public int    idUsuario;
        public String nombre;
        public String mail;
        public int    idRol;

        // ─── Usuario ─────────────────
        public Usuario(int idUsuario, String nombre, String mail, int idRol) {
            this.idUsuario = idUsuario;
            this.nombre    = nombre;
            this.mail      = mail;
            this.idRol     = idRol;
        }
    }

    public static class Resultado implements java.io.Serializable {
        public final boolean ok;
        public final String  mensaje;
        public final int     id;

        // ─── Resultado ─────────────────
        public Resultado(boolean ok, String mensaje) {
            this(ok, mensaje, 0);
        }

        // ─── Resultado ─────────────────
        public Resultado(boolean ok, String mensaje, int id) {
            this.ok      = ok;
            this.mensaje = mensaje;
            this.id      = id;
        }
    }

    // ─── login ─────────────────
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
            System.err.println("[UsuarioDAO.login] " + e.getMessage());
        }

        return null;
    }

    // ─── buscarUsuariosByName ─────────────────
    public static java.util.List<Usuario> buscarUsuariosByName(String query) {
        java.util.List<Usuario> lista = new java.util.ArrayList<>();
        String sql = "SELECT idUsuario, nombre, mail, idRol FROM usuario WHERE LOWER(nombre) LIKE LOWER(?)"; 

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + query.trim() + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Usuario(
                        rs.getInt("idUsuario"),
                        rs.getString("nombre"),
                        rs.getString("mail"),
                        rs.getInt("idRol")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("[UsuarioDAO.buscarUsuariosByName] " + e.getMessage());
        }
        return lista;
    }

    // ─── registrar ─────────────────
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
            String mensajeError = e.getMessage();
            
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

    // ─── cambiarContrasena ─────────────────
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
                    if (mensaje.toLowerCase().contains("incorrecta") || mensaje.toLowerCase().contains("error")) {
                        return new Resultado(false, mensaje);
                    }
                    return new Resultado(true, mensaje);
                }
            }

        } catch (SQLException e) {
            String mensajeError = e.getMessage();
            
            if (mensajeError.contains("Usuario no encontrado")) {
                return new Resultado(false, "Usuario no encontrado");
            } else if (mensajeError.contains("Contraseña actual incorrecta") || mensajeError.contains("password") || mensajeError.contains("incorrecta")) {
                return new Resultado(false, "Contraseña actual incorrecta");
            } else {
                return new Resultado(false, "Error al cambiar la contraseña");
            }
        }

        return new Resultado(false, "Error desconocido");
    }

    // ─── eliminarCuenta ─────────────────
    public static Resultado eliminarCuenta(int idUsuario, String contrasena) {
        String sql = "{CALL sp_EliminarCuenta(?, ?)}";

        try (Connection con = ConexionDB.getConexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, idUsuario);
            cs.setString(2, contrasena);

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    String mensaje = rs.getString("mensaje");
                    if (mensaje.toLowerCase().contains("incorrecta") || mensaje.toLowerCase().contains("error")) {
                        return new Resultado(false, mensaje);
                    }
                    return new Resultado(true, mensaje);
                }
            }

        } catch (SQLException e) {
            String mensajeError = e.getMessage();
            
            if (mensajeError.contains("Usuario no encontrado")) {
                return new Resultado(false, "Usuario no encontrado");
            } else if (mensajeError.contains("Contraseña incorrecta") || mensajeError.contains("password") || mensajeError.contains("credenciales") || mensajeError.contains("Actual incorrecta")) {
                return new Resultado(false, "Contraseña incorrecta");
            } else {
                return new Resultado(false, "Error al eliminar la cuenta");
            }
        }

        return new Resultado(false, "Error desconocido");
    }

    public static class Memoria implements java.io.Serializable {
        public int    idMemoria;
        public String titulo;
        public String contenido;
        public String descripcion;
        public int    idUsuario;
        public String nombreUsuario;
        public int    idEstado;

        // ─── Memoria ─────────────────
        public Memoria(int idMemoria, String titulo, String contenido,
                       String descripcion, int idUsuario, String nombreUsuario, int idEstado) {
            this.idMemoria     = idMemoria;
            this.titulo        = titulo;
            this.contenido     = contenido;
            this.descripcion   = descripcion;
            this.idUsuario     = idUsuario;
            this.nombreUsuario = nombreUsuario;
            this.idEstado      = idEstado;
        }
    }

    // ─── crearMemoria ─────────────────
    public static Resultado crearMemoria(String titulo, String contenido,
                                         String descripcion, int idUsuario, boolean esPublica) {
        String sql = "{CALL sp_CrearMemoria(?, ?, ?, ?, ?)}";

        try (Connection con = ConexionDB.getConexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setString(1, titulo);
            cs.setString(2, contenido);
            cs.setString(3, descripcion);
            cs.setInt(4, idUsuario);
            cs.setBoolean(5, esPublica);

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    String mensaje   = rs.getString("mensaje");
                    int    idMemoria = rs.getInt("idMemoria");
                    if (idMemoria > 0) {
                        return new Resultado(true, mensaje, idMemoria);
                    } else {
                        return new Resultado(false, mensaje);
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("[UsuarioDAO.crearMemoria] Error SQL: " + e.getMessage());
            System.err.println("[UsuarioDAO.crearMemoria] Código: " + e.getErrorCode());
            e.printStackTrace();
            return new Resultado(false, "Error en la base de datos: " + e.getMessage());
        }

        return new Resultado(false, "Error desconocido");
    }

    // ─── obtenerMemoriasPublicas ─────────────────
    public static java.util.List<Memoria> obtenerMemoriasPublicas() {
        java.util.List<Memoria> lista = new java.util.ArrayList<>();
        String sql =
            "SELECT m.idMemoria, m.titulo, m.contenido, m.descripcion, " +
            "       m.idUsuario, u.nombre AS nombreUsuario, m.idEstado " +
            "FROM memoria m " +
            "JOIN usuario u ON m.idUsuario = u.idUsuario " +
            "WHERE m.idEstado = 1 " +
            "ORDER BY m.fecha_hr DESC";

        try (Connection con = ConexionDB.getConexion();
             java.sql.PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Memoria(
                    rs.getInt("idMemoria"),
                    rs.getString("titulo"),
                    rs.getString("contenido"),
                    rs.getString("descripcion"),
                    rs.getInt("idUsuario"),
                    rs.getString("nombreUsuario"),
                    rs.getInt("idEstado")
                ));
            }

        } catch (SQLException e) {
            System.err.println("[UsuarioDAO.obtenerMemoriasPublicas] " + e.getMessage());
        }

        return lista;
    }

    // ─── obtenerMemoriasDeUsuario ─────────────────
    public static java.util.List<Memoria> obtenerMemoriasDeUsuario(int idUsuario, boolean soloPublicas) {
        java.util.List<Memoria> lista = new java.util.ArrayList<>();
        String sql =
            "SELECT m.idMemoria, m.titulo, m.contenido, m.descripcion, " +
            "       m.idUsuario, u.nombre AS nombreUsuario, m.idEstado " +
            "FROM memoria m " +
            "JOIN usuario u ON m.idUsuario = u.idUsuario " +
            "WHERE m.idUsuario = ? AND m.idEstado = ? " +
            "ORDER BY m.fecha_hr DESC";

        try (Connection con = ConexionDB.getConexion();
             java.sql.PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ps.setInt(2, soloPublicas ? 1 : 2);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Memoria(
                        rs.getInt("idMemoria"),
                        rs.getString("titulo"),
                        rs.getString("contenido"),
                        rs.getString("descripcion"),
                        rs.getInt("idUsuario"),
                        rs.getString("nombreUsuario"),
                        rs.getInt("idEstado")
                    ));
                }
            }

        } catch (SQLException e) {
            System.err.println("[UsuarioDAO.obtenerMemoriasDeUsuario] " + e.getMessage());
        }

        return lista;
    }

    // ─── darDeBajaMemoria ─────────────────
    public static Resultado darDeBajaMemoria(int idMemoria, int idUsuario) {
        String sql = "{CALL sp_DarDeBajaMemoria(?, ?)}";

        try (Connection con = ConexionDB.getConexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, idMemoria);
            cs.setInt(2, idUsuario);

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    return new Resultado(true, rs.getString("mensaje"));
                }
            }

        } catch (SQLException e) {
            String msg = e.getMessage();
            if (msg.contains("no existe")) {
                return new Resultado(false, "La memoria no existe");
            } else if (msg.contains("permisos")) {
                return new Resultado(false, "No tenés permisos para eliminar esta memoria");
            } else {
                return new Resultado(false, "Error al dar de baja la memoria");
            }
        }

        return new Resultado(false, "Error desconocido");
    }
}
