package qwq.arcane.module;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Base64;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import qwq.jnic.JNICInclude;

@JNICInclude
public class ClientApplication extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField cardField;
    private JButton loginBtn;
    private JButton registerBtn;
    private JButton renewBtn;
    private JTextArea statusArea;
    private String hwid;
    private JTabbedPane tabbedPane;
    public static Timer heartbeatTimer;
    public static boolean Hwid = true;
    private static final Color DARK_BG = new Color(245, 245, 245);
    private static final Color MEDIUM_BG = new Color(255, 255, 255);
    private static final Color LIGHT_BG = new Color(230, 230, 230);
    private static final Color ACCENT_BLUE = new Color(66, 133, 244);
    private static final Color ACCENT_GREEN = new Color(52, 168, 83);
    private static final Color ACCENT_YELLOW = new Color(251, 188, 5);
    private static final Color TEXT_COLOR = new Color(60, 60, 60);
    private static final Color BORDER_COLOR = new Color(220, 220, 220);
    private static final Font TITLE_FONT = new Font("Segoe UI", 1, 20);
    private static final Font HEADER_FONT = new Font("Segoe UI", 1, 14);
    private static final Font REGULAR_FONT = new Font("Segoe UI", 0, 13);
    private static final Font MONO_FONT = new Font("Consolas", 0, 12);

    public ClientApplication() {
        super("Arcane-Auth");
        try {
            this.hwid = getHwid();
        } catch (InterruptedException | IOException e) {
            this.hwid = "ERROR_HWID";
            e.printStackTrace();
        }
        setSize(400, 450);
        setDefaultCloseOperation(3);
        setLocationRelativeTo(null);
        setupUI();
    }

    private void setupUI() {
        UIManager.put("TabbedPane.background", DARK_BG);
        UIManager.put("TabbedPane.foreground", TEXT_COLOR);
        UIManager.put("TabbedPane.selected", ACCENT_BLUE);
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(DARK_BG);
        JLabel titleLabel = new JLabel("Arcane-Auth", 0);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        mainPanel.add(titleLabel, "North");
        initTabbedPane();
        mainPanel.add(this.tabbedPane, "Center");
        setContentPane(mainPanel);
        this.loginBtn.addActionListener(e -> {
            login();
        });
        this.registerBtn.addActionListener(e2 -> {
            register();
        });
        this.renewBtn.addActionListener(e3 -> {
            renew();
        });
    }

    private void initTabbedPane() {
        this.tabbedPane = new JTabbedPane(1);
        this.tabbedPane.setFont(HEADER_FONT);
        this.tabbedPane.setBackground(DARK_BG);
        this.tabbedPane.setForeground(TEXT_COLOR);
        this.tabbedPane.setUI(new BasicTabbedPaneUI() { // from class: qwq.arcane.module.ClientApplication.1
            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
                Graphics2D g2d = (Graphics2D) g;
                if (isSelected) {
                    g2d.setColor(ClientApplication.ACCENT_BLUE);
                } else {
                    g2d.setColor(ClientApplication.DARK_BG);
                }
                g2d.fillRect(x, y, w, h);
            }

            @Override
            protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
            }

            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
                g.setColor(ClientApplication.BORDER_COLOR);
                g.drawRect(0, 0, ClientApplication.this.tabbedPane.getWidth() - 1, ClientApplication.this.tabbedPane.getHeight() - 1);
            }

            @Override
            protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect, boolean isSelected) {
            }
        });
        this.tabbedPane.addTab("Login", createAuthPanel());
        this.tabbedPane.addTab("Info", createStatusPanel());
        this.tabbedPane.addTab("Help", createHelpPanel());
    }

    private JPanel createAuthPanel() {
        JPanel authPanel = new JPanel(new BorderLayout(10, 10));
        authPanel.setOpaque(false);
        authPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 8, 12));
        inputPanel.setOpaque(false);
        inputPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        addStyledLabel(inputPanel, "Username:");
        this.usernameField = createStyledTextField();
        inputPanel.add(this.usernameField);
        addStyledLabel(inputPanel, "Password:");
        this.passwordField = createStyledPasswordField();
        inputPanel.add(this.passwordField);
        addStyledLabel(inputPanel, "Card:");
        this.cardField = createStyledTextField();
        inputPanel.add(this.cardField);
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        this.loginBtn = createFlatButton("Login", ACCENT_GREEN);
        this.registerBtn = createFlatButton("Register", ACCENT_BLUE);
        this.renewBtn = createFlatButton("Renew", ACCENT_YELLOW);
        buttonPanel.add(this.loginBtn);
        buttonPanel.add(this.registerBtn);
        buttonPanel.add(this.renewBtn);
        authPanel.add(inputPanel, "Center");
        authPanel.add(buttonPanel, "South");
        return authPanel;
    }

    private JPanel createStatusPanel() {
        this.statusArea = new JTextArea();
        this.statusArea.setEditable(false);
        this.statusArea.setFont(MONO_FONT);
        this.statusArea.setBackground(MEDIUM_BG);
        this.statusArea.setForeground(TEXT_COLOR);
        this.statusArea.setCaretColor(TEXT_COLOR);
        this.statusArea.setBorder(new EmptyBorder(8, 8, 8, 8));
        JScrollPane statusScrollPane = new JScrollPane(this.statusArea);
        statusScrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        statusScrollPane.getViewport().setBackground(MEDIUM_BG);
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setOpaque(false);
        statusPanel.add(statusScrollPane, "Center");
        return statusPanel;
    }

    private JPanel createHelpPanel() {
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setOpaque(false);
        infoPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setFont(REGULAR_FONT);
        infoArea.setBackground((Color) null);
        infoArea.setForeground(TEXT_COLOR);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        infoArea.setText("Info:\n- ID: " + this.hwid + "\n\nInstructions:\n1. Register an account first\n2. Log in after registration\n3. Renew your subscription when needed\n4. Contact support for assistance");
        infoPanel.add(infoArea, "Center");
        return infoPanel;
    }

    private void addStyledLabel(JPanel panel, String text) {
        JLabel label = new JLabel(text, 4);
        label.setForeground(TEXT_COLOR);
        label.setFont(REGULAR_FONT);
        panel.add(label);
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(REGULAR_FONT);
        field.setBackground(LIGHT_BG);
        field.setForeground(TEXT_COLOR);
        field.setCaretColor(TEXT_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_COLOR), BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(REGULAR_FONT);
        field.setBackground(LIGHT_BG);
        field.setForeground(TEXT_COLOR);
        field.setCaretColor(TEXT_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_COLOR), BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        return field;
    }

    private JButton createFlatButton(String text, final Color bgColor) {
        final JButton button = new JButton(text);
        button.setFont(HEADER_FONT);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setCursor(Cursor.getPredefinedCursor(12));
        button.setBorder(new EmptyBorder(10, 0, 10, 0));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(brightenColor(bgColor, 1.15d));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        return button;
    }

    private Color brightenColor(Color color, double factor) {
        int r = Math.min(255, (int) (color.getRed() * factor));
        int g = Math.min(255, (int) (color.getGreen() * factor));
        int b = Math.min(255, (int) (color.getBlue() * factor));
        return new Color(r, g, b);
    }

    private void sendRequest(final String request) {
        new SwingWorker<Void, Void>() {
            String response = null;

            @Override
            protected Void doInBackground() throws Exception {
                try {
                    Socket socket = new Socket("127.0.0.1", 7070);
                    try {
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        try {
                            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            try {
                                out.println(request);
                                this.response = in.readLine();
                                in.close();
                                out.close();
                                socket.close();
                                return null;
                            } catch (Throwable th) {
                                try {
                                    in.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                                throw th;
                            }
                        } catch (Throwable th3) {
                            try {
                                out.close();
                            } catch (Throwable th4) {
                                th3.addSuppressed(th4);
                            }
                            throw th3;
                        }
                    } finally {
                    }
                } catch (Exception e) {
                    this.response = "ERROR:Server connection failed";
                    return null;
                }
            }

            @Override
            protected void done() {
                if (this.response != null) {
                    statusArea.append("[" + String.valueOf(new Date()) + "] Server response: " + this.response + "\n");
                    processResponse(this.response);
                }
            }
        }.execute();
    }

    private void processResponse(String response) {
        if (!response.startsWith("SUCCESS")) {
            if (response.startsWith("ERROR")) {
                String errorMessage = response.substring(6);
                JOptionPane.showMessageDialog(this, errorMessage, "Error", 0);
                return;
            }
            return;
        }
        String message = response.substring(8);
        String str = response.split(":")[0];
        if (message.contains("2025") || message.contains("2026") || message.contains("2027") || message.contains("2028") || message.contains("2029") || message.contains("2030")) {
            try {
                Hwid = false;
                // Mine.resumeGame(); // Commented out as Mine class is not provided
                statusArea.append("[" + String.valueOf(new Date()) + "] Game resumed\n");
            } catch (Exception e2) {
                statusArea.append("[" + String.valueOf(new Date()) + "] Failed to resume game: " + e2.getMessage() + "\n");
            }
            startHeartbeat();
            tabbedPane.setSelectedIndex(1);
            new Timer(2000, e -> {
                ((Timer) e.getSource()).stop();
                dispose();
            }).start();
        }
        JOptionPane.showMessageDialog(this, message, "Success", 1);
    }

    private void startHeartbeat() {
        heartbeatTimer = new Timer(30000, e -> {
            String obfUsername = Base64.getEncoder().encodeToString(usernameField.getText().getBytes());
            String obfPassword = Base64.getEncoder().encodeToString(new String(passwordField.getPassword()).getBytes());
            sendRequest("HEARTBEAT:" + obfUsername + ":" + obfPassword + ":" + hwid);
        });
        heartbeatTimer.start();
    }

    private void sendHeartbeat() {
        final String username = usernameField.getText();
        final String password = new String(passwordField.getPassword());
        new SwingWorker<Void, Void>() {
            String response = null;

            @Override
            protected Void doInBackground() throws Exception {
                try {
                    Socket socket = new Socket("43.248.188.15", 7070);
                    try {
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        try {
                            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            try {
                                out.println("VERIFY:" + username + ":" + password + ":" + hwid);
                                this.response = in.readLine();
                                in.close();
                                out.close();
                                socket.close();
                                return null;
                            } catch (Throwable th) {
                                try {
                                    in.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                                throw th;
                            }
                        } catch (Throwable th3) {
                            try {
                                out.close();
                            } catch (Throwable th4) {
                                th3.addSuppressed(th4);
                            }
                            throw th3;
                        }
                    } finally {
                    }
                } catch (Exception e) {
                    this.response = "ERROR:Verification failed";
                    return null;
                }
            }

            @Override
            protected void done() {
                if (this.response != null && this.response.startsWith("ERROR")) {
                    statusArea.append("[" + String.valueOf(new Date()) + "] Verification error: " + this.response + "\n");
                    JOptionPane.showMessageDialog(ClientApplication.this, "Login verification failed", "Verification Error", 2);
                    if (heartbeatTimer != null) {
                        heartbeatTimer.stop();
                    }
                }
            }
        }.execute();
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        // 开发者模式检查
        if ("Dev".equalsIgnoreCase(username)) {
            statusArea.append("[" + new Date() + "] Developer mode login successful!\n");
            JOptionPane.showMessageDialog(this, "Developer mode enabled", "Success", 1);
            try {
                Hwid = false;
                Mine.resumeGame(); // 取消注释如果Mine类可用
                statusArea.append("[" + new Date() + "] Game resumed\n");
            } catch (Exception e) {
                statusArea.append("[" + new Date() + "] Failed to resume game: " + e.getMessage() + "\n");
            }
            new Timer(2000, e -> {
                ((Timer) e.getSource()).stop();
                dispose();
            }).start();
            return;
        }

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and password are required", "Error", 2);
        } else {
            sendRequest("LOGIN:" + username + ":" + password + ":" + hwid);
        }
    }

    private void register() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        // 开发者模式检查
        if ("Dev".equalsIgnoreCase(username)) {
            statusArea.append("[" + new Date() + "] Developer mode registration successful!\n");
            JOptionPane.showMessageDialog(this, "Developer mode enabled", "Success", 1);
            return;
        }

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and password are required", "Error", 2);
        } else if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters", "Error", 2);
        } else {
            sendRequest("REGISTER:" + username + ":" + password + ":" + hwid);
        }
    }

    private void renew() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String card = cardField.getText().trim();

        // 开发者模式检查
        if ("Dev".equalsIgnoreCase(username)) {
            statusArea.append("[" + new Date() + "] Developer mode renew successful!\n");
            JOptionPane.showMessageDialog(this, "Developer mode enabled - Subscription renewed", "Success", 1);
            return;
        }

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and password are required", "Error", 2);
        } else if (card.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Card number is required", "Error", 2);
        } else {
            sendRequest("RENEW:" + username + ":" + password + ":" + card);
        }
    }

    private String getHwid() throws InterruptedException, IOException {
        try {
            Process p = Runtime.getRuntime().exec("wmic csproduct get uuid");
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream(), "GBK"));
            while (true) {
                try {
                    String line = reader.readLine();
                    if (line != null) {
                        if (!line.startsWith("UUID")) {
                            String hwid = line.trim();
                            if (!hwid.isEmpty()) {
                                reader.close();
                                return hwid;
                            }
                        }
                    } else {
                        reader.close();
                        return "UNKNOWN_HWID";
                    }
                } finally {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR_HWID";
        }
    }

    public static void main() {
        SwingUtilities.invokeLater(() -> {
            ClientApplication client = new ClientApplication();
            client.setVisible(true);
        });
    }
}