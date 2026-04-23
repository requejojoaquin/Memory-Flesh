import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;

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
    private String usuarioActual = null;

    public PantallaPrincipal() {
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
        centerContainer.add(buildFeed(), "feed");
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

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_COLOR);
        header.setPreferredSize(new Dimension(0, 64));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, DIVIDER_COLOR));

        JLabel title = new JLabel("Inicio");
        title.setForeground(TEXT_MAIN);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(0, 340, 0, 0));
        header.add(title, BorderLayout.WEST);

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
        searchBox.setPreferredSize(new Dimension(280, 40));

        JLabel lupaIcon = new JLabel("🔍");
        lupaIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        lupaIcon.setForeground(Color.WHITE);

        JLabel searchLabel = new JLabel("Buscar personas");
        searchLabel.setForeground(new Color(0xB0AAF0));
        searchLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));

        searchBox.add(lupaIcon);
        searchBox.add(searchLabel);

        JPanel searchWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 12));
        searchWrapper.setOpaque(false);
        searchWrapper.setPreferredSize(new Dimension(320, 64));
        searchWrapper.add(searchBox);
        header.add(searchWrapper, BorderLayout.EAST);

        return header;
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(BG_COLOR);
        sidebar.setPreferredSize(new Dimension(320, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 2, DIVIDER_COLOR),
            BorderFactory.createEmptyBorder(80, 36, 50, 36)
        ));

        JButton btnInicio = sidebarButton("🏠", "Inicio");
        btnInicio.addActionListener(e -> {
            closeNotifDrawer();
            showPanel("feed");
        });

        JButton btnNotif = sidebarButton("🔔", "Notificaciones");
        btnNotif.addActionListener(e -> toggleNotifDrawer());

        JButton btnPerfil = sidebarButton("👤", usuarioActual != null ? usuarioActual : "Usuario");
        JButton btnMas    = sidebarButton("•••", "Más");

        JButton btnPublicar = createPublicarButton();

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
        btn.setPreferredSize(new Dimension(240, 52));
        btn.setMaximumSize(new Dimension(240, 52));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> showPanel("crear"));
        return btn;
    }

    private JPanel buildFeed() {
        JPanel feed = new JPanel();
        feed.setBackground(BG_COLOR);
        feed.setLayout(new BoxLayout(feed, BoxLayout.Y_AXIS));
        feed.setBorder(BorderFactory.createEmptyBorder(32, 0, 32, 0));

        feed.add(buildMemoryCard("Gran Día", "Descripción de ejemplo del recuerdo..."));
        feed.add(Box.createVerticalStrut(24));
        feed.add(buildMemoryCard("Otro recuerdo", "Más texto de ejemplo..."));
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

        // --- AQUÍ ESTÁ EL CAMBIO DE ANCHO ---
        // Antes era 0.28 | 0.44 | 0.08
        // Ahora es 0.35 | 0.30 | 0.03. La columna central (scroll) es más angosta.
        gbc.weightx = 0.35; wrapper.add(left, gbc);
        gbc.weightx = 0.30; wrapper.add(scroll, gbc);
        gbc.weightx = 0.03; wrapper.add(right, gbc);

        return wrapper;
    }

    private JPanel buildMemoryCard(String titulo, String descripcion) {
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
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 380));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setForeground(TEXT_MAIN);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        card.add(lblTitulo);
        card.add(Box.createVerticalStrut(10));

        JPanel imgPlaceholder = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(0x3D3580));
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(new Color(0x9999FF));
                g.setFont(new Font("SansSerif", Font.PLAIN, 13));
                FontMetrics fm = g.getFontMetrics();
                String msg = "[ Imagen de la memoria ]";
                g.drawString(msg, (getWidth() - fm.stringWidth(msg)) / 2, getHeight() / 2);
            }
        };
        imgPlaceholder.setPreferredSize(new Dimension(0, 260));
        imgPlaceholder.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));
        card.add(imgPlaceholder);
        card.add(Box.createVerticalStrut(10));

        JTextArea lblDesc = new JTextArea(descripcion);
        lblDesc.setForeground(TEXT_DESC);
        lblDesc.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblDesc.setOpaque(false);
        lblDesc.setEditable(false);
        lblDesc.setLineWrap(true);
        lblDesc.setWrapStyleWord(true);
        card.add(lblDesc);

        return card;
    }

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

        JLabel title = new JLabel("Notificaciones");
        title.setForeground(TEXT_MAIN);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        drawer.add(title);
        drawer.add(Box.createVerticalStrut(20));

        String[] notifs = {"Notificación 1", "Notificación 2"};
        for (String n : notifs) {
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

    private void toggleNotifDrawer() {
        notifOpen = !notifOpen;
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

    private JPanel buildCrearPost() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(BG_COLOR);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(0x2E2A6E));
        panel.setPreferredSize(new Dimension(700, 600));
        panel.setLayout(null);

        JLabel back = new JLabel("✕   Atrás");
        back.setForeground(TEXT_MAIN);
        back.setFont(new Font("SansSerif", Font.BOLD, 20));
        back.setBounds(20, 20, 120, 30);
        back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        back.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                showPanel("feed");
            }
        });
        panel.add(back);

        JTextField titulo = new JTextField("Agregar Título...");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 14));
        titulo.setBounds(180, 20, 480, 50);
        titulo.setBackground(CARD_COLOR);
        titulo.setForeground(TEXT_MAIN);
        titulo.setBorder(BorderFactory.createLineBorder(CARD_BORDER, 4));
        panel.add(titulo);

        JPanel imgBox = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(0x3D3580));
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(CARD_BORDER);
                g.setFont(new Font("SansSerif", Font.BOLD, 80));
                g.drawString("+", getWidth()/2 - 20, getHeight()/2 + 30);
            }
        };
        imgBox.setBounds(180, 90, 350, 300);
        imgBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        imgBox.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                JFileChooser chooser = new JFileChooser();
                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    System.out.println("Imagen: " + file.getAbsolutePath());
                }
            }
        });
        panel.add(imgBox);

        JTextArea desc = new JTextArea("Agregar descripción...");
        desc.setFont(new Font("SansSerif", Font.BOLD, 12));
        desc.setBounds(180, 410, 350, 80);
        desc.setBackground(CARD_COLOR);
        desc.setForeground(TEXT_MAIN);
        desc.setBorder(BorderFactory.createLineBorder(CARD_BORDER, 4));
        panel.add(desc);

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
        publicar.setForeground(Color.WHITE);
        publicar.setBounds(540, 350, 140, 50);
        publicar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panel.add(publicar);

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
        privacidad.setForeground(Color.WHITE);
        privacidad.setBackground(PRIV_PUBLICO_COLOR);
        privacidad.setBounds(540, 410, 140, 50);
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

        wrapper.add(panel);
        return wrapper;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PantallaPrincipal::new);
    }
}