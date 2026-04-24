import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class PantallaPerfil extends JFrame {

    private static final Color BG_COLOR      = new Color(0x1E1B4B);
    private static final Color ACCENT_COLOR  = new Color(0x9999FF);
    private static final Color BUTTON_COLOR  = new Color(0x6157E8);
    private static final Color TEXT_MAIN     = new Color(0xF8FAFC);
    private static final Color TEXT_DIM      = new Color(0xD9D9D9);
    private static final Color DIVIDER_COLOR = new Color(0x9999FF);
    private static final Color CARD_COLOR    = new Color(0x27226E);
    private static final Color CARD_BORDER   = new Color(0x9999FF);
    private static final Color MENU_BG       = new Color(0x2E2A6E);
    private static final Color MENU_ELIMINAR = new Color(0x2D1314);
    private static final Color MENU_BTN      = new Color(0x6157E8);

    // TODO: REEMPLAZAR CON DATOS REALES DE LA BDD (Usuario + Memoria)
    private String nombreUsuario = "Usuario";
    private int cantPublicaciones = 0;
    private boolean viendoPublicas = true;

    private JPanel menuDesplegable;
    private JPanel gridPanel;

    public PantallaPerfil(JFrame parent) {
        setTitle("Perfil - Memory Flesh");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_COLOR);
        root.add(buildTop(parent), BorderLayout.NORTH);
        root.add(buildGrid(), BorderLayout.CENTER);

        setContentPane(root);
        setVisible(true);
    }

    // ─── TOP ─────────────────────────────────────────────────────────────────

    private JPanel buildTop(JFrame parent) {
        JLayeredPane layered = new JLayeredPane();
        layered.setPreferredSize(new Dimension(1920, 420));
        layered.setBackground(BG_COLOR);
        layered.setOpaque(true);

        JPanel top = new JPanel(null);
        top.setBackground(BG_COLOR);
        top.setBounds(0, 0, 1920, 420);

        // ── Botón Atrás ──
        JLabel btnAtras = new JLabel("✕   Atrás");
        btnAtras.setForeground(TEXT_MAIN);
        btnAtras.setFont(new Font("SansSerif", Font.BOLD, 20));
        btnAtras.setBounds(32, 32, 140, 36);
        btnAtras.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAtras.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                dispose();
                parent.setVisible(true);
            }
            @Override public void mouseEntered(MouseEvent e) { btnAtras.setForeground(ACCENT_COLOR); }
            @Override public void mouseExited(MouseEvent e)  { btnAtras.setForeground(TEXT_MAIN); }
        });
        top.add(btnAtras);

        // ── Avatar círculo ──
        int avatarSize = 160;
        int avatarX = (1920 - avatarSize) / 2;
        JPanel avatar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ACCENT_COLOR);
                g2.setStroke(new BasicStroke(6f));
                g2.drawOval(4, 4, getWidth() - 8, getHeight() - 8);
                // cabeza
                g2.setStroke(new BasicStroke(5f));
                int cx = getWidth() / 2;
                g2.drawOval(cx - 28, 26, 56, 56);
                // cuerpo
                g2.drawRoundRect(cx - 40, 94, 80, 46, 36, 36);
                g2.dispose();
            }
        };
        avatar.setBounds(avatarX, 50, avatarSize, avatarSize);
        avatar.setOpaque(false);
        top.add(avatar);

        // ── Nombre ──
        // TODO: REEMPLAZAR nombreUsuario CON BDD (Usuario.nomUsuario)
        JLabel lblNombre = new JLabel(nombreUsuario, SwingConstants.CENTER);
        lblNombre.setForeground(TEXT_MAIN);
        lblNombre.setFont(new Font("SansSerif", Font.BOLD, 30));
        lblNombre.setBounds(760, 222, 400, 40);
        top.add(lblNombre);

        // ── Cantidad publicaciones ──
        // TODO: REEMPLAZAR cantPublicaciones CON COUNT DE BDD
        JLabel lblCantidad = new JLabel(String.valueOf(cantPublicaciones), SwingConstants.CENTER);
        lblCantidad.setForeground(TEXT_MAIN);
        lblCantidad.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblCantidad.setBounds(760, 268, 400, 30);
        top.add(lblCantidad);

        JLabel lblPubs = new JLabel("Publicaciones", SwingConstants.CENTER);
        lblPubs.setForeground(TEXT_DIM);
        lblPubs.setFont(new Font("SansSerif", Font.PLAIN, 16));
        lblPubs.setBounds(760, 298, 400, 24);
        top.add(lblPubs);

        // ── Botones ojo ──
        int eyeCX = 1920 / 2;
        int eyeY  = 338;

        JPanel btnPublicas = eyeButton(false); // ojo sin tachar = públicas
        JPanel btnPrivadas = eyeButton(true);  // ojo tachado   = privadas

        btnPublicas.setBounds(eyeCX - 90, eyeY, 72, 54);
        btnPrivadas.setBounds(eyeCX + 18,  eyeY, 72, 54);

        updateEyeStyle(btnPublicas, true);   // empieza seleccionado
        updateEyeStyle(btnPrivadas, false);

        btnPublicas.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnPrivadas.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnPublicas.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                viendoPublicas = true;
                updateEyeStyle(btnPublicas, true);
                updateEyeStyle(btnPrivadas, false);
                // TODO: FILTRAR POR MEMORIAS PUBLICAS EN BDD
                refreshGrid(true);
            }
        });

        btnPrivadas.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                viendoPublicas = false;
                updateEyeStyle(btnPrivadas, true);
                updateEyeStyle(btnPublicas, false);
                // TODO: FILTRAR POR MEMORIAS PRIVADAS EN BDD
                refreshGrid(false);
            }
        });

        top.add(btnPublicas);
        top.add(btnPrivadas);

        // ── Línea divisoria ──
        JPanel divider = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(DIVIDER_COLOR);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        divider.setBounds(0, 408, 1920, 5);
        top.add(divider);

        // ── 3 puntos ──
        JLabel tresPuntos = new JLabel("• • •");
        tresPuntos.setForeground(TEXT_MAIN);
        tresPuntos.setFont(new Font("SansSerif", Font.BOLD, 22));
        tresPuntos.setBounds(1840, 32, 70, 30);
        tresPuntos.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tresPuntos.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                menuDesplegable.setVisible(!menuDesplegable.isVisible());
                layered.repaint();
            }
        });
        top.add(tresPuntos);

        // ── Menú desplegable ──
        menuDesplegable = buildMenuDesplegable();
        menuDesplegable.setBounds(1680, 68, 220, 120);
        menuDesplegable.setVisible(false);

        layered.add(top, JLayeredPane.DEFAULT_LAYER);
        layered.add(menuDesplegable, JLayeredPane.POPUP_LAYER);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_COLOR);
        wrapper.add(layered, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel eyeButton(boolean tachado) {
        JPanel btn = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean sel = Boolean.TRUE.equals(getClientProperty("selected"));
                if (sel) {
                    g2.setColor(new Color(0x2E2A6E));
                    g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 12, 12));
                    g2.setColor(ACCENT_COLOR);
                    g2.setStroke(new BasicStroke(2f));
                    g2.draw(new RoundRectangle2D.Double(1, 1, getWidth()-2, getHeight()-2, 12, 12));
                }
                int cx = getWidth() / 2, cy = getHeight() / 2;
                g2.setColor(ACCENT_COLOR);
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawArc(cx - 18, cy - 10, 36, 20, 0,  180);
                g2.drawArc(cx - 18, cy - 10, 36, 20, 0, -180);
                g2.fillOval(cx - 6, cy - 6, 12, 12);
                if (tachado) {
                    g2.setColor(BG_COLOR);
                    g2.setStroke(new BasicStroke(5f));
                    g2.drawLine(cx - 16, cy + 14, cx + 16, cy - 14);
                    g2.setColor(ACCENT_COLOR);
                    g2.setStroke(new BasicStroke(2.5f));
                    g2.drawLine(cx - 16, cy + 14, cx + 16, cy - 14);
                }
                g2.dispose();
            }
        };
        btn.setOpaque(false);
        return btn;
    }

    private void updateEyeStyle(JPanel btn, boolean selected) {
        btn.putClientProperty("selected", selected);
        btn.repaint();
    }

    private JPanel buildMenuDesplegable() {
        JPanel menu = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(MENU_BG);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
            }
        };
        menu.setOpaque(false);
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JButton btnEliminar = menuButton("Eliminar publicación", MENU_ELIMINAR);
        JButton btnAjustes  = menuButton("Ajustes cuenta",       MENU_BTN);

        // TODO: CONECTAR CON BDD
        btnEliminar.addActionListener(e -> { });
        btnAjustes.addActionListener(e -> { });

        menu.add(btnEliminar);
        menu.add(Box.createVerticalStrut(8));
        menu.add(btnAjustes);

        return menu;
    }

    private JButton menuButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? bg.brighter() : bg);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setForeground(TEXT_MAIN);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setMaximumSize(new Dimension(196, 44));
        btn.setPreferredSize(new Dimension(196, 44));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ─── GRID ────────────────────────────────────────────────────────────────

    private JPanel buildGrid() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_COLOR);

        gridPanel = new JPanel(new GridLayout(0, 2, 20, 20));
        gridPanel.setBackground(BG_COLOR);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(28, 60, 28, 60));

        // TODO: REEMPLAZAR CON MEMORIAS REALES DE LA BDD
        // TODO: FIN DATOS DE EJEMPLO

        JScrollPane scroll = new JScrollPane(gridPanel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG_COLOR);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildPublicacionCard(String titulo, String descripcion) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_COLOR);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 14, 14));
                g2.setColor(CARD_BORDER);
                g2.setStroke(new BasicStroke(2f));
                g2.draw(new RoundRectangle2D.Double(1, 1, getWidth()-2, getHeight()-2, 14, 14));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        card.setPreferredSize(new Dimension(0, 220));

        // TODO: REEMPLAZAR CON IMAGEN REAL DE LA BDD
        JPanel img = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(new Color(0x3D3580));
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g.setColor(ACCENT_COLOR);
                g.setFont(new Font("SansSerif", Font.PLAIN, 12));
                FontMetrics fm = g.getFontMetrics();
                String t = "[ imagen ]";
                g.drawString(t, (getWidth()-fm.stringWidth(t))/2, getHeight()/2);
            }
        };
        img.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        img.setPreferredSize(new Dimension(0, 130));
        img.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(img);
        card.add(Box.createVerticalStrut(10));

        JLabel lblT = new JLabel(titulo);
        lblT.setForeground(TEXT_MAIN);
        lblT.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblT.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblT);
        card.add(Box.createVerticalStrut(4));

        String descCorta = descripcion.length() > 60 ? descripcion.substring(0, 60) + "..." : descripcion;
        JLabel lblD = new JLabel(descCorta);
        lblD.setForeground(TEXT_DIM);
        lblD.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblD.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblD);

        return card;
    }

    private void refreshGrid(boolean publicas) {
        gridPanel.removeAll();
        // TODO: CARGAR MEMORIAS DE LA BDD FILTRADAS POR publicas/privadas
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PantallaPerfil(null));
    }
}