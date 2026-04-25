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

    // ════════════════════════════════════════════════════════════════════════
    //  MEMORIAS
    // ════════════════════════════════════════════════════════════════════════

    // Clase interna para representar una Memoria
    public static class Memoria {
        public int    idMemoria;
        public String titulo;
        public String contenido;   // path de imagen (puede estar vacío)
        public String descripcion;
        public int    idUsuario;
        public String nombreUsuario;
        public int    idEstado;    // 1=Publico, 2=Privado, 3=Eliminado

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

    // ── CREAR MEMORIA ─────────────────────────────────────────────────────
    // SP: sp_CrearMemoria(titulo, contenido, descripcion, idUsuario, esPublica)
    // Retorna: mensaje, idMemoria
    public static Resultado crearMemoria(String titulo, String contenido,
                                         String descripcion, int idUsuario, boolean esPublica) {
        String sql = "{CALL sp_CrearMemoria(?, ?, ?, ?, ?)}";

        try (Connection con = ConexionDB.getConexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setString(1, titulo);
            cs.setString(2, contenido);   // path de imagen o ""
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

    // ── OBTENER MEMORIAS PÚBLICAS (feed) ──────────────────────────────────
    // Query directa: todas las memorias con estado Publico (idEstado=1),
    // ordenadas por fecha descendente. Incluye nombre del autor.
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

    // ── OBTENER MEMORIAS DE UN USUARIO (perfil) ───────────────────────────
    // Query directa: memorias de un usuario filtradas por estado
    // (1=Publico, 2=Privado). Excluye eliminadas (3).
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

    // ── DAR DE BAJA MEMORIA ───────────────────────────────────────────────
    // SP: sp_DarDeBajaMemoria(idMemoria, idUsuario)
    // Cambia estado a Eliminado (3). El usuario solo puede bajar las suyas;
    // el admin puede bajar cualquiera.
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