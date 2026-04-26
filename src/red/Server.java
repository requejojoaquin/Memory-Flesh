package red;

import java.io.*;
import java.net.*;
import java.util.List;

public class Server {
    private static final int PORT = 12345;

    // ─── main ─────────────────
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor de Memory Flesh iniciado en el puerto " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket socket;

        // ─── ClientHandler ─────────────────
        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        // ─── run ─────────────────
        @Override
        public void run() {
            try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                out.flush();
                while (true) {
                    String command = (String) in.readObject();
                    System.out.println("Comando recibido: " + command);

                    switch (command) {
                        case "LOGIN":
                            String mail = (String) in.readObject();
                            String pass = (String) in.readObject();
                            out.writeObject(UsuarioDAO.login(mail, pass));
                            break;

                        case "REGISTRAR":
                            String nom = (String) in.readObject();
                            String m = (String) in.readObject();
                            String p = (String) in.readObject();
                            out.writeObject(UsuarioDAO.registrar(nom, m, p));
                            break;

                        case "GET_MEMORIAS_PUBLICAS":
                            out.writeObject(UsuarioDAO.obtenerMemoriasPublicas());
                            break;

                        case "GET_MEMORIAS_USUARIO":
                            int idU = (int) in.readObject();
                            boolean soloP = (boolean) in.readObject();
                            out.writeObject(UsuarioDAO.obtenerMemoriasDeUsuario(idU, soloP));
                            break;

                        case "CREAR_MEMORIA":
                            String tit = (String) in.readObject();
                            String cont = (String) in.readObject();
                            String desc = (String) in.readObject();
                            int idUser = (int) in.readObject();
                            boolean esPub = (boolean) in.readObject();
                            out.writeObject(UsuarioDAO.crearMemoria(tit, cont, desc, idUser, esPub));
                            break;

                        case "ELIMINAR_MEMORIA":
                            int idM = (int) in.readObject();
                            int idUdel = (int) in.readObject();
                            out.writeObject(UsuarioDAO.darDeBajaMemoria(idM, idUdel));
                            break;

                        case "CAMBIAR_PASS":
                            int idUcp = (int) in.readObject();
                            String act = (String) in.readObject();
                            String nue = (String) in.readObject();
                            out.writeObject(UsuarioDAO.cambiarContrasena(idUcp, act, nue));
                            break;

                        case "ELIMINAR_CUENTA":
                            int idUec = (int) in.readObject();
                            String passEc = (String) in.readObject();
                            out.writeObject(UsuarioDAO.eliminarCuenta(idUec, passEc));
                            break;

                        case "SEARCH_USERS":
                            String query = (String) in.readObject();
                            out.writeObject(UsuarioDAO.buscarUsuariosByName(query));
                            break;

                        case "SUBIR_IMAGEN":
                            String fileName = (String) in.readObject();
                            long fileSize = (long) in.readObject();
                            byte[] buffer = new byte[4096];
                            File uploadDir = new File("uploads");
                            if (!uploadDir.exists()) uploadDir.mkdirs();
                            File file = new File(uploadDir, fileName);
                            try (FileOutputStream fos = new FileOutputStream(file)) {
                                long remaining = fileSize;
                                int read;
                                while (remaining > 0 && (read = socket.getInputStream().read(buffer, 0, (int)Math.min(buffer.length, remaining))) != -1) {
                                    fos.write(buffer, 0, read);
                                    remaining -= read;
                                }
                            }
                            out.writeObject(true);
                            break;

                        case "DESCARGAR_IMAGEN":
                            String fileToDown = (String) in.readObject();
                            File f = new File("uploads", fileToDown);
                            if (f.exists()) {
                                out.writeObject(true);
                                out.writeObject(f.length());
                                try (FileInputStream fis = new FileInputStream(f)) {
                                    byte[] buf = new byte[4096];
                                    int r;
                                    while ((r = fis.read(buf)) != -1) {
                                        socket.getOutputStream().write(buf, 0, r);
                                    }
                                    socket.getOutputStream().flush();
                                }
                            } else {
                                out.writeObject(false);
                            }
                            break;

                        case "EXIT":
                            return;
                    }
                    out.flush();
                }

            } catch (EOFException e) {
                System.out.println("Cliente desconectado.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
