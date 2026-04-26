package red;

import java.io.*;
import java.net.*;
import java.util.List;

public class Cliente {
    private static final String HOST = "localhost";
    private static final int PORT = 12345;
    private static Socket socket;
    private static ObjectOutputStream out;
    private static ObjectInputStream in;

    // ─── conectar ─────────────────
    public static void conectar() {
        try {
            if (socket == null || socket.isClosed()) {
                socket = new Socket(HOST, PORT);
                out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                in = new ObjectInputStream(socket.getInputStream());
                System.out.println("Conectado al servidor " + HOST + ":" + PORT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ─── desconectar ─────────────────
    public static void desconectar() {
        try {
            if (socket != null && !socket.isClosed()) {
                out.writeObject("EXIT");
                out.flush();
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ─── login ─────────────────
    public static UsuarioDAO.Usuario login(String mail, String pass) {
        try {
            conectar();
            out.writeObject("LOGIN");
            out.writeObject(mail);
            out.writeObject(pass);
            out.flush();
            return (UsuarioDAO.Usuario) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ─── registrar ─────────────────
    public static UsuarioDAO.Resultado registrar(String nom, String m, String p) {
        try {
            conectar();
            out.writeObject("REGISTRAR");
            out.writeObject(nom);
            out.writeObject(m);
            out.writeObject(p);
            out.flush();
            return (UsuarioDAO.Resultado) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return new UsuarioDAO.Resultado(false, "Error de red");
        }
    }

    // ─── obtenerMemoriasPublicas ─────────────────
    @SuppressWarnings("unchecked")
    public static List<UsuarioDAO.Memoria> obtenerMemoriasPublicas() {
        try {
            conectar();
            out.writeObject("GET_MEMORIAS_PUBLICAS");
            out.flush();
            return (List<UsuarioDAO.Memoria>) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

    // ─── obtenerMemoriasDeUsuario ─────────────────
    @SuppressWarnings("unchecked")
    public static List<UsuarioDAO.Memoria> obtenerMemoriasDeUsuario(int idU, boolean soloP) {
        try {
            conectar();
            out.writeObject("GET_MEMORIAS_USUARIO");
            out.writeObject(idU);
            out.writeObject(soloP);
            out.flush();
            return (List<UsuarioDAO.Memoria>) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

    // ─── crearMemoria ─────────────────
    public static UsuarioDAO.Resultado crearMemoria(String tit, String cont, String desc, int idUser, boolean esPub) {
        try {
            conectar();
            out.writeObject("CREAR_MEMORIA");
            out.writeObject(tit);
            out.writeObject(cont);
            out.writeObject(desc);
            out.writeObject(idUser);
            out.writeObject(esPub);
            out.flush();
            return (UsuarioDAO.Resultado) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return new UsuarioDAO.Resultado(false, "Error de red");
        }
    }

    // ─── darDeBajaMemoria ─────────────────
    public static UsuarioDAO.Resultado darDeBajaMemoria(int idM, int idU) {
        try {
            conectar();
            out.writeObject("ELIMINAR_MEMORIA");
            out.writeObject(idM);
            out.writeObject(idU);
            out.flush();
            return (UsuarioDAO.Resultado) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return new UsuarioDAO.Resultado(false, "Error de red");
        }
    }

    // ─── cambiarContrasena ─────────────────
    public static UsuarioDAO.Resultado cambiarContrasena(int idU, String act, String nue) {
        try {
            conectar();
            out.writeObject("CAMBIAR_PASS");
            out.writeObject(idU);
            out.writeObject(act);
            out.writeObject(nue);
            out.flush();
            return (UsuarioDAO.Resultado) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return new UsuarioDAO.Resultado(false, "Error de red");
        }
    }

    // ─── eliminarCuenta ─────────────────
    public static UsuarioDAO.Resultado eliminarCuenta(int idU, String pass) {
        try {
            conectar();
            out.writeObject("ELIMINAR_CUENTA");
            out.writeObject(idU);
            out.writeObject(pass);
            out.flush();
            return (UsuarioDAO.Resultado) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return new UsuarioDAO.Resultado(false, "Error de red");
        }
    }

    // ─── buscarUsuarios ─────────────────
    @SuppressWarnings("unchecked")
    public static List<UsuarioDAO.Usuario> buscarUsuarios(String query) {
        try {
            conectar();
            out.writeObject("SEARCH_USERS");
            out.writeObject(query);
            out.flush();
            return (List<UsuarioDAO.Usuario>) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

    // ─── subirImagen ─────────────────
    public static void subirImagen(File file, String remoteName) {
        try {
            conectar();
            out.writeObject("SUBIR_IMAGEN");
            out.writeObject(remoteName);
            out.writeObject(file.length());
            out.flush();

            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[4096];
                int read;
                while ((read = fis.read(buffer)) != -1) {
                    socket.getOutputStream().write(buffer, 0, read);
                }
                socket.getOutputStream().flush();
            }
            in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ─── descargarImagen ─────────────────
    public static File descargarImagen(String remoteName) {
        try {
            conectar();
            out.writeObject("DESCARGAR_IMAGEN");
            out.writeObject(remoteName);
            out.flush();

            boolean exists = (boolean) in.readObject();
            if (!exists) return null;

            long size = (long) in.readObject();
            File tempDir = new File("uploads_cache");
            if (!tempDir.exists()) tempDir.mkdirs();
            File localFile = new File(tempDir, remoteName);
            
            try (FileOutputStream fos = new FileOutputStream(localFile)) {
                byte[] buffer = new byte[4096];
                long remaining = size;
                int read;
                while (remaining > 0 && (read = socket.getInputStream().read(buffer, 0, (int)Math.min(buffer.length, remaining))) != -1) {
                    fos.write(buffer, 0, read);
                    remaining -= read;
                }
            }
            return localFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
