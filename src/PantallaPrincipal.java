import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;

public class PantallaPrincipal extends JFrame {

    private static final Color BG_COLOR      = new Color(0x1E1B4B);
    private static final Color CARD_COLOR    = new Color(0x2E2A6E);
    private static final Color CARD_BORDER   = new Color(0x9999FF);
    private static final Color HEADER_COLOR  = new Color(0x4941BA);
    private static final Color BUTTON_COLOR  = new Color(0x6157E8);
    private static final Color TEXT_MAIN     = new Color(0xF8FAFC);
    private static final Color TEXT_DESC     = new Color(0xFFFFFF);
    private static final Color DIVIDER_COLOR = new Color(0x6157E8);
    private static final Color NOTIF_COLOR   = new Color(0x4941BA);
    private static final Color PRIV_PUBLICO_COLOR = new Color(0x7C73EB);
    private static final Color PRIV_PRIVADO_COLOR = new Color(0x16143A);

    private JPanel centerContainer;
    private JLayeredPane layeredPane;
    private JPanel notifDrawer;
    private boolean notifOpen = false;
    private int idUsuarioActual;
    private String nombreUsuario;
    private int idRolUsuario;
    private UsuarioDAO.Usuario usuarioActual;
    private java.util.List<UsuarioDAO.Memoria> memorias = new java.util.ArrayList<>();
    public static java.util.List<String> notificacionesGlobal = new java.util.ArrayList<>();
    public static void agregarNotificacion(String texto) {
        notificacionesGlobal.add(0, texto);
    }

    private JPanel imgBox;
    private BufferedImage imagenSeleccionada = null;
    private String rutaImagenSeleccionada = null;

    public PantallaPrincipal(UsuarioDAO.Usuario usuario) {
        this.idUsuarioActual = usuario.idUsuario;
        this.nombreUsuario   = usuario.nombre;
        this.idRolUsuario    = usuario.idRol;
        this.usuarioActual   = usuario;
        this.memorias        = UsuarioDAO.obtenerMemoriasPublicas();

        setTitle("Inicio - Memory Flesh");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_COLOR);
        root.add(buildHeader(), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(BG_COLOR);
        body.add(buildSidebar(), BorderLayout.WEST);

        centerContainer = new JPanel(new CardLayout());
        centerContainer.setBackground(BG_COLOR);
        centerContainer.add(buildFeed(),      "feed");
        centerContainer.add(buildCrearPost(), "crear");

        body.add(centerContainer, BorderLayout.CENTER);
        root.add(body, BorderLayout.CENTER);

        root.setBounds(0, 0, 1920, 1080);
        layeredPane.add(root, JLayeredPane.DEFAULT_LAYER);

        notifDrawer = buildNotifDrawer();
        notifDrawer.setBounds(320, 64, 360, 1016);
        notifDrawer.setVisible(false);
        layeredPane.add(notifDrawer, JLayeredPane.PALETTE_LAYER);

        setContentPane(layeredPane);
        setVisible(true);

        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                root.setBounds(0, 0, getWidth(), getHeight());
                notifDrawer.setBounds(320, 64, 360, getHeight() - 64);
            }
        });
    }

    // ─── HEADER ──────────────────────────────────────────────────────────────

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_COLOR);
        header.setPreferredSize(new Dimension(0, 64));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, DIVIDER_COLOR));

        JLabel inicio = new JLabel("  Inicio");
        inicio.setForeground(new Color(0xFFFFFF));
        inicio.setFont(new Font("SansSerif", Font.PLAIN, 22));
        inicio.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));
        header.add(inicio, BorderLayout.WEST);

        JLabel brand = new JLabel("MEMORY FLESH", SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 0));
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x + 2, y + 2);
                g2.setColor(TEXT_MAIN);
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        brand.setForeground(TEXT_MAIN);
        brand.setFont(new Font("SansSerif", Font.BOLD, 28));
        header.add(brand, BorderLayout.CENTER);

        JPanel searchBox = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x3D3580));
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
            }
        };
        searchBox.setOpaque(false);
        searchBox.setPreferredSize(new Dimension(240, 38));

        JLabel lupaIcon = new JLabel("🔍");
        lupaIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        lupaIcon.setForeground(Color.WHITE);

        JLabel searchLabel = new JLabel("Buscar personas");
        searchLabel.setForeground(new Color(0xB0AAF0));
        searchLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        searchLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        searchBox.add(lupaIcon);
        searchBox.add(searchLabel);

        JPanel searchWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 13));
        searchWrapper.setOpaque(false);
        searchWrapper.setPreferredSize(new Dimension(300, 64));
        searchWrapper.add(searchBox);
        header.add(searchWrapper, BorderLayout.EAST);

        return header;
    }

    // ─── SIDEBAR ─────────────────────────────────────────────────────────────

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(BG_COLOR);
        sidebar.setPreferredSize(new Dimension(300, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 2, DIVIDER_COLOR),
            BorderFactory.createEmptyBorder(80, 36, 50, 36)
        ));

        JButton btnInicio = sidebarButton("🏠", "Inicio");
        btnInicio.addActionListener(e -> { closeNotifDrawer(); showPanel("feed"); });

        JButton btnNotif = sidebarButton("🔔", "Notificaciones");
        btnNotif.addActionListener(e -> toggleNotifDrawer());

        JButton btnPerfil = sidebarButton("👤", nombreUsuario);
        btnPerfil.addActionListener(e -> {
            setVisible(false);
            new PantallaPerfil(PantallaPrincipal.this, usuarioActual);
        });

        JButton btnMas = sidebarButton("•••", "Más");
        btnMas.addActionListener(e -> {
            setVisible(false);
            new Pantallaajustes(PantallaPrincipal.this, usuarioActual);
        });

        JButton btnPublicar = createPublicarButton();
        btnPublicar.addActionListener(e -> showPanel("crear"));

        sidebar.add(btnInicio);
        sidebar.add(Box.createVerticalStrut(48));
        sidebar.add(btnNotif);
        sidebar.add(Box.createVerticalStrut(48));
        sidebar.add(btnPerfil);
        sidebar.add(Box.createVerticalStrut(48));
        sidebar.add(btnMas);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(btnPublicar);

        return sidebar;
    }

    private JButton sidebarButton(String icon, String label) {
        JButton btn = new JButton(icon + "   " + label);
        btn.setForeground(TEXT_MAIN);
        btn.setFont(new Font("SansSerif", Font.BOLD, 22));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setForeground(new Color(0x9999FF)); }
            @Override public void mouseExited(MouseEvent e)  { btn.setForeground(TEXT_MAIN); }
        });
        return btn;
    }

    private JButton createPublicarButton() {
        JButton btn = new JButton("Publicar") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? BUTTON_COLOR.brighter() : BUTTON_COLOR);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 18));
        btn.setPreferredSize(new Dimension(220, 52));
        btn.setMaximumSize(new Dimension(220, 52));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ─── FEED ────────────────────────────────────────────────────────────────

    private JPanel buildFeed() {
        JPanel feed = new JPanel();
        feed.setBackground(BG_COLOR);
        feed.setLayout(new BoxLayout(feed, BoxLayout.Y_AXIS));
        feed.setBorder(BorderFactory.createEmptyBorder(32, 0, 32, 0));

        for (UsuarioDAO.Memoria mem : memorias) {
            feed.add(buildMemoryCard(mem));
            feed.add(Box.createVerticalStrut(24));
        }

        feed.add(Box.createVerticalGlue());

        JScrollPane scroll = new JScrollPane(feed);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG_COLOR);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(BG_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;

        JPanel left  = new JPanel(); left.setOpaque(false);
        JPanel right = new JPanel(); right.setOpaque(false);

        gbc.weightx = 0.25; wrapper.add(left, gbc);
        gbc.weightx = 0.50; wrapper.add(scroll, gbc);
        gbc.weightx = 0.25; wrapper.add(right, gbc);

        return wrapper;
    }

    private JPanel buildMemoryCard(UsuarioDAO.Memoria mem) {
        // Card con fondo redondeado
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_COLOR);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(CARD_BORDER);
                g2.setStroke(new BasicStroke(2f));
                g2.draw(new RoundRectangle2D.Double(1, 1, getWidth()-2, getHeight()-2, 16, 16));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.setMaximumSize(new Dimension(520, Integer.MAX_VALUE));
        card.setMinimumSize(new Dimension(520, 50));

        // ── Autor ──
        JLabel lblAutor = new JLabel("@" + mem.nombreUsuario);
        lblAutor.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblAutor.setForeground(new Color(0x9999FF));
        lblAutor.setFont(new Font("SansSerif", Font.PLAIN, 12));
        card.add(lblAutor);
        card.add(Box.createVerticalStrut(4));

        // ── Título ──
        JLabel lblTitulo = new JLabel(mem.titulo);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblTitulo.setForeground(TEXT_MAIN);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 17));
        card.add(lblTitulo);
        card.add(Box.createVerticalStrut(10));

        // ── Imagen: cuadrada, escalada sin distorsión ──
        BufferedImage imgCargada = null;
        if (mem.contenido != null && !mem.contenido.isEmpty()) {
            try {
                File f = new File("uploads", mem.contenido);
                if (f.exists()) imgCargada = ImageIO.read(f);
            } catch (Exception ignored) {}
        }
        final BufferedImage imgFinal = imgCargada;

        // Tamaño fijo cuadrado para la imagen
        final int IMG_SIZE = 460;

        JPanel imgPanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                if (imgFinal != null) {
                    // Escala manteniendo proporción, centra en el cuadrado
                    int iw = imgFinal.getWidth();
                    int ih = imgFinal.getHeight();
                    double scale = Math.min((double) getWidth() / iw, (double) getHeight() / ih);
                    int nw = (int)(iw * scale);
                    int nh = (int)(ih * scale);
                    int ox = (getWidth() - nw) / 2;
                    int oy = (getHeight() - nh) / 2;
                    g2.setColor(new Color(0x1A1740));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2.drawImage(imgFinal, ox, oy, nw, nh, null);
                } else {
                    g2.setColor(new Color(0x3D3580));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2.setColor(new Color(0x9999FF));
                    g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
                    FontMetrics fm = g2.getFontMetrics();
                    String msg = "[ Imagen de la memoria ]";
                    g2.drawString(msg, (getWidth() - fm.stringWidth(msg)) / 2, getHeight() / 2);
                }
                g2.dispose();
            }
        };
        imgPanel.setPreferredSize(new Dimension(IMG_SIZE, IMG_SIZE));
        imgPanel.setMaximumSize(new Dimension(IMG_SIZE, IMG_SIZE));
        imgPanel.setMinimumSize(new Dimension(IMG_SIZE, IMG_SIZE));
        imgPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(imgPanel);
        card.add(Box.createVerticalStrut(10));

        // ── Descripción ──
        String descText = (mem.descripcion != null && !mem.descripcion.isEmpty()) ? mem.descripcion : "";
        if (!descText.isEmpty()) {
            JTextArea lblDesc = new JTextArea(descText);
            lblDesc.setForeground(TEXT_DESC);
            lblDesc.setFont(new Font("SansSerif", Font.PLAIN, 13));
            lblDesc.setOpaque(false);
            lblDesc.setEditable(false);
            lblDesc.setLineWrap(true);
            lblDesc.setWrapStyleWord(true);
            lblDesc.setMaximumSize(new Dimension(IMG_SIZE, 60));
            lblDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(lblDesc);
        }

        return card;
    }

    // ─── NOTIFICACIONES ──────────────────────────────────────────────────────

    private JPanel buildNotifDrawer() {
        JPanel drawer = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(0x252260));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(DIVIDER_COLOR);
                g2.fillRect(getWidth() - 2, 0, 2, getHeight());
                g2.dispose();
            }
        };
        drawer.setOpaque(false);
        drawer.setLayout(new BoxLayout(drawer, BoxLayout.Y_AXIS));
        drawer.setBorder(BorderFactory.createEmptyBorder(28, 20, 28, 20));

        JLabel title = new JLabel("Notificaciones", SwingConstants.CENTER);
        title.setForeground(TEXT_MAIN);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        drawer.add(title);
        drawer.add(Box.createVerticalStrut(20));

        for (String n : notificacionesGlobal) {
            drawer.add(buildNotifItem(n));
            drawer.add(Box.createVerticalStrut(10));
        }

        return drawer;
    }

    private JPanel buildNotifItem(String texto) {
        JPanel item = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(NOTIF_COLOR);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
            }
        };
        item.setOpaque(false);
        item.setLayout(new FlowLayout(FlowLayout.LEFT, 14, 8));
        item.setMaximumSize(new Dimension(320, 48));

        JLabel lbl = new JLabel(texto);
        lbl.setForeground(TEXT_MAIN);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 14));
        item.add(lbl);
        return item;
    }

    // ─── CREAR POST ──────────────────────────────────────────────────────────

    private JPanel buildCrearPost() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(BG_COLOR);

        JPanel panel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x2E2A6E));
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
                g2.setColor(CARD_BORDER);
                g2.setStroke(new BasicStroke(2f));
                g2.draw(new RoundRectangle2D.Double(1, 1, getWidth()-2, getHeight()-2, 20, 20));
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(780, 640));
        panel.setLayout(null);

        JLabel back = new JLabel("✕   Atrás");
        back.setForeground(TEXT_MAIN);
        back.setFont(new Font("SansSerif", Font.BOLD, 18));
        back.setBounds(20, 20, 120, 30);
        back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        back.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { showPanel("feed"); }
            @Override public void mouseEntered(MouseEvent e) { back.setForeground(new Color(0x9999FF)); }
            @Override public void mouseExited(MouseEvent e)  { back.setForeground(TEXT_MAIN); }
        });
        panel.add(back);

        JTextField titulo = new JTextField("Agregar Título...");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        titulo.setBounds(160, 16, 590, 46);
        titulo.setBackground(CARD_COLOR);
        titulo.setForeground(new Color(0x9999FF));
        titulo.setCaretColor(TEXT_MAIN);
        titulo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 2, true),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        titulo.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (titulo.getText().equals("Agregar Título...")) {
                    titulo.setText("");
                    titulo.setForeground(TEXT_MAIN);
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (titulo.getText().isEmpty()) {
                    titulo.setText("Agregar Título...");
                    titulo.setForeground(new Color(0x9999FF));
                }
            }
        });
        titulo.addKeyListener(new KeyAdapter() {
            @Override public void keyTyped(KeyEvent e) {
                if (titulo.getText().length() >= 38) e.consume();
            }
        });
        panel.add(titulo);

        imgBox = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                if (imagenSeleccionada != null) {
                    int iw = imagenSeleccionada.getWidth();
                    int ih = imagenSeleccionada.getHeight();
                    double scale = Math.min((double) getWidth() / iw, (double) getHeight() / ih);
                    int nw = (int)(iw * scale);
                    int nh = (int)(ih * scale);
                    int ox = (getWidth() - nw) / 2;
                    int oy = (getHeight() - nh) / 2;
                    g2.setColor(new Color(0x1E1B4B));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.drawImage(imagenSeleccionada, ox, oy, nw, nh, null);
                } else {
                    g2.setColor(new Color(0x3D3580));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                    g2.setColor(CARD_BORDER);
                    g2.setFont(new Font("SansSerif", Font.BOLD, 72));
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString("+", (getWidth() - fm.stringWidth("+")) / 2, getHeight() / 2 + 28);
                    g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
                    fm = g2.getFontMetrics();
                    String hint = "Hacé clic para subir una imagen";
                    g2.drawString(hint, (getWidth() - fm.stringWidth(hint)) / 2, getHeight() / 2 + 56);
                }
                g2.dispose();
            }
        };
        imgBox.setBounds(160, 78, 440, 320);
        imgBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        imgBox.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { abrirSelectorImagen(); }
        });
        panel.add(imgBox);

        JTextArea desc = new JTextArea("Agregar descripción...");
        desc.setFont(new Font("SansSerif", Font.PLAIN, 14));
        desc.setBounds(160, 412, 440, 90);
        desc.setBackground(CARD_COLOR);
        desc.setForeground(new Color(0x9999FF));
        desc.setCaretColor(TEXT_MAIN);
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        desc.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 2),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        desc.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (desc.getText().equals("Agregar descripción...")) {
                    desc.setText("");
                    desc.setForeground(TEXT_MAIN);
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (desc.getText().isEmpty()) {
                    desc.setText("Agregar descripción...");
                    desc.setForeground(new Color(0x9999FF));
                }
            }
        });

        JLabel charCount = new JLabel("0/110");
        charCount.setForeground(new Color(0x9999FF));
        charCount.setFont(new Font("SansSerif", Font.PLAIN, 12));
        charCount.setBounds(545, 500, 60, 20);

        desc.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void update() {
                String texto = desc.getText();
                if (texto.equals("Agregar descripción...")) { charCount.setText("0/110"); return; }
                if (texto.length() > 110) desc.setText(texto.substring(0, 110));
                charCount.setText(Math.min(texto.length(), 110) + "/110");
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { update(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { update(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
        });

        panel.add(desc);
        panel.add(charCount);

        JButton privacidad = new JButton("🔒 Público") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        privacidad.setOpaque(false);
        privacidad.setContentAreaFilled(false);
        privacidad.setBorderPainted(false);
        privacidad.setFocusPainted(false);
        privacidad.setForeground(Color.WHITE);
        privacidad.setBackground(PRIV_PUBLICO_COLOR);
        privacidad.setFont(new Font("SansSerif", Font.BOLD, 14));
        privacidad.setBounds(614, 382, 140, 48);
        privacidad.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        privacidad.addActionListener(e -> {
            if (privacidad.getText().equals("🔒 Público")) {
                privacidad.setText("🔒 Privado");
                privacidad.setBackground(PRIV_PRIVADO_COLOR);
            } else {
                privacidad.setText("🔒 Público");
                privacidad.setBackground(PRIV_PUBLICO_COLOR);
            }
        });
        panel.add(privacidad);

        JButton publicar = new JButton("Publicar") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? BUTTON_COLOR.brighter() : BUTTON_COLOR);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        publicar.setOpaque(false);
        publicar.setContentAreaFilled(false);
        publicar.setBorderPainted(false);
        publicar.setFocusPainted(false);
        publicar.setForeground(Color.WHITE);
        publicar.setFont(new Font("SansSerif", Font.BOLD, 15));
        publicar.setBounds(614, 320, 140, 48);
        publicar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        publicar.addActionListener(e -> {
            String t = titulo.getText().trim();
            String d = desc.getText().trim();
            if (t.isEmpty() || t.equals("Agregar Título...")) return;
            if (d.equals("Agregar descripción...")) d = "";

            boolean esPublica = privacidad.getText().contains("Público");
            String nombreArchivoFinal = "";

            if (imagenSeleccionada != null && rutaImagenSeleccionada != null) {
                try {
                    Path carpetaUploads = Paths.get("uploads");
                    if (!Files.exists(carpetaUploads)) Files.createDirectories(carpetaUploads);
                    String extension = "";
                    int i = rutaImagenSeleccionada.lastIndexOf('.');
                    if (i > 0) extension = rutaImagenSeleccionada.substring(i);
                    nombreArchivoFinal = "img_" + System.currentTimeMillis() + extension;
                    Path destino = carpetaUploads.resolve(nombreArchivoFinal);
                    Files.copy(Paths.get(rutaImagenSeleccionada), destino, StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error al procesar la imagen: " + ex.getMessage());
                    return;
                }
            }

            UsuarioDAO.Resultado res = UsuarioDAO.crearMemoria(t, nombreArchivoFinal, d, idUsuarioActual, esPublica);
            if (!res.ok) {
                JOptionPane.showMessageDialog(PantallaPrincipal.this,
                    "Error al publicar: " + res.mensaje, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            memorias = UsuarioDAO.obtenerMemoriasPublicas();
            centerContainer.remove(0);
            centerContainer.add(buildFeed(), "feed", 0);
            showPanel("feed");

            agregarNotificacion("Memoria publicada con éxito ✓");

            titulo.setText("Agregar Título...");
            titulo.setForeground(new Color(0x9999FF));
            desc.setText("Agregar descripción...");
            desc.setForeground(new Color(0x9999FF));
            imagenSeleccionada = null;
            rutaImagenSeleccionada = null;
        });
        panel.add(publicar);

        wrapper.add(panel);
        return wrapper;
    }

    private void abrirSelectorImagen() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Seleccioná una imagen");
        chooser.setFileFilter(new FileNameExtensionFilter("Imágenes (jpg, png, gif, bmp)", "jpg", "jpeg", "png", "gif", "bmp"));
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                imagenSeleccionada = ImageIO.read(file);
                rutaImagenSeleccionada = file.getAbsolutePath();
                imgBox.repaint();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "No se pudo cargar la imagen.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ─── UTILS ───────────────────────────────────────────────────────────────

    private void toggleNotifDrawer() {
        notifOpen = !notifOpen;
        if (notifOpen) {
            // Reconstruir el drawer para mostrar notificaciones actualizadas
            layeredPane.remove(notifDrawer);
            notifDrawer = buildNotifDrawer();
            notifDrawer.setBounds(320, 64, 360, layeredPane.getHeight() - 64);
            layeredPane.add(notifDrawer, JLayeredPane.PALETTE_LAYER);
        }
        notifDrawer.setVisible(notifOpen);
        layeredPane.repaint();
    }

    private void closeNotifDrawer() {
        notifOpen = false;
        notifDrawer.setVisible(false);
        layeredPane.repaint();
    }

    private void showPanel(String name) {
        ((CardLayout) centerContainer.getLayout()).show(centerContainer, name);
    }
}