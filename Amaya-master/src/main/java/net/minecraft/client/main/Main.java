package net.minecraft.client.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.properties.PropertyMap.Serializer;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.util.Session;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.net.Proxy.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.List;

import static net.minecraft.util.StringUtils.isNullOrEmpty;

public class Main {
    public static JFrame frame;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    public static String currentUsername = " ";

    public Main(String[] p_main_0_) {
        startGame(p_main_0_,"crkuser");
        initializeNetwork();
        initializeUI(p_main_0_);
    }

    private void initializeNetwork() {
        try {
            socket = new Socket("222.187.227.134", 1444);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            showWindowsNotification("连接错误", "无法连接到服务器: " + e.getMessage());
            System.exit(1);
        }
    }

    private void initializeUI(String[] p_main_0) {
        frame = new JFrame("验证系统");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        JPanel loginPanel = createLoginPanel(p_main_0);
        JPanel registerPanel = createRegisterPanel();
        JPanel renewKeyPanel = createRenewKeyPanel();

        mainPanel.add(loginPanel, "login");
        mainPanel.add(registerPanel, "register");
        mainPanel.add(renewKeyPanel, "renewKey");

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private JPanel createLoginPanel(String[] p_main_0) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel userLabel = new JLabel("用户名:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(userLabel, gbc);

        JTextField userField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(userField, gbc);

        JLabel passLabel = new JLabel("密码:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(passLabel, gbc);

        JPasswordField passField = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(passField, gbc);

        JButton loginButton = new JButton("登录");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        loginButton.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            String hwid = getHWID(); // 获取当前设备HWID

            if (username.isEmpty() || password.isEmpty()) {
                showError("用户名和密码不能为空");
                return;
            }

            try {
                out.println("LOGIN:" + username + ":" + password + ":" + hwid); // 添加HWID参数
                String response = in.readLine();

                if (response.startsWith("SUCCESS")) {
                    currentUsername = username;
                    showUserInfo(username);
                    showSuccess("登录成功");
                    startGame(p_main_0,username);
                } else if (response.contains("卡密已过期")) {
                    currentUsername = username;
                    showError(response.substring(6));
                    cardLayout.show(mainPanel, "renewKey");
                    ((JTextField) ((JPanel) mainPanel.getComponent(2)).getComponent(1)).setText(username);
                } else if (response.contains("已绑定其他设备")) {
                    showError(response.substring(6));
                } else {
                    showError(response.substring(6));
                }
            } catch (IOException ex) {
                showError("与服务器通信时出错");
            }
        });
        panel.add(loginButton, gbc);

        JButton registerButton = new JButton("没有账号? 注册");
        gbc.gridx = 0;
        gbc.gridy = 3;
        registerButton.addActionListener(e -> cardLayout.show(mainPanel, "register"));
        panel.add(registerButton, gbc);

        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel userLabel = new JLabel("用户名:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(userLabel, gbc);

        JTextField userField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(userField, gbc);

        JLabel passLabel = new JLabel("密码:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(passLabel, gbc);

        JPasswordField passField = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(passField, gbc);

        JLabel confirmPassLabel = new JLabel("确认密码:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(confirmPassLabel, gbc);

        JPasswordField confirmPassField = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(confirmPassField, gbc);

        JLabel keyLabel = new JLabel("卡密:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(keyLabel, gbc);

        JTextField keyField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(keyField, gbc);

        JButton registerButton = new JButton("注册");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        registerButton.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            String confirmPassword = new String(confirmPassField.getPassword());
            String key = keyField.getText();

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || key.isEmpty()) {
                showError("所有字段都必须填写");
                return;
            }

            if (!password.equals(confirmPassword)) {
                showError("两次输入的密码不一致");
                return;
            }

            try {
                out.println("REGISTER:" + username + ":" + password + ":" + key);
                String response = in.readLine();

                if (response.startsWith("SUCCESS")) {
                    showSuccess(response.substring(8));

                    userField.setText("");
                    passField.setText("");
                    confirmPassField.setText("");
                    keyField.setText("");

                    cardLayout.show(mainPanel, "login");
                } else {
                    showError(response.substring(6));
                }
            } catch (IOException ex) {
                showError("与服务器通信时出错");
            }
        });
        panel.add(registerButton, gbc);

        JButton backButton = new JButton("返回登录");
        gbc.gridx = 0;
        gbc.gridy = 5;
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "login"));
        panel.add(backButton, gbc);

        return panel;
    }

    private JPanel createRenewKeyPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel userLabel = new JLabel("用户名:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(userLabel, gbc);

        JTextField userField = new JTextField(15);
        userField.setEditable(false); // 设置为不可编辑
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(userField, gbc);

        JLabel passLabel = new JLabel("密码:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(passLabel, gbc);

        JPasswordField passField = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(passField, gbc);

        JLabel keyLabel = new JLabel("新卡密:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(keyLabel, gbc);

        JTextField keyField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(keyField, gbc);

        JButton renewButton = new JButton("续费");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        renewButton.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            String key = keyField.getText();

            if (password.isEmpty() || key.isEmpty()) {
                showError("密码和新卡密不能为空");
                return;
            }

            try {
                out.println("RENEW_KEY:" + username + ":" + password + ":" + key);
                String response = in.readLine();

                if (response.startsWith("SUCCESS")) {
                    showSuccess(response.substring(8));
                    cardLayout.show(mainPanel, "login");
                } else {
                    showError(response.substring(6));
                }
            } catch (IOException ex) {
                showError("与服务器通信时出错");
            }
        });
        panel.add(renewButton, gbc);

        JButton backButton = new JButton("返回登录");
        gbc.gridx = 0;
        gbc.gridy = 4;
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "login"));
        panel.add(backButton, gbc);

        userField.setText(currentUsername);
        return panel;
    }

    private void showUserInfo(String username) {
        try {
            out.println("GET_USER_INFO:" + username);
            String response = in.readLine();

            if (response.startsWith("SUCCESS")) {
                String userInfo = response.substring(8);
                String formattedInfo = formatUserInfo(userInfo);
                showTrayNotification("用户信息 - " + username, formattedInfo);
            } else {
                showError(response.substring(6));
            }
        } catch (IOException ex) {
            showError("获取用户信息失败: " + ex.getMessage());
        }
    }

    private String formatUserInfo(String rawInfo) {
        return rawInfo.replace(",", "\n").replace(":", ": ");
    }

    private void showTrayNotification(String title, String message) {
        if (!SystemTray.isSupported()) {
            JOptionPane.showMessageDialog(frame, message, title, JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            SystemTray tray = SystemTray.getSystemTray();
            BufferedImage trayIconImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            TrayIcon trayIcon = new TrayIcon(trayIconImage);
            trayIcon.setImageAutoSize(true);
            tray.add(trayIcon);
            trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);
            new Timer(3000, e -> tray.remove(trayIcon)).start();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, message, title, JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showWindowsNotification(String title, String message) {
        if (SystemTray.isSupported()) {
            try {
                SystemTray tray = SystemTray.getSystemTray();
                BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
                TrayIcon trayIcon = new TrayIcon(image);
                tray.add(trayIcon);
                trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);
                new Timer(3000, e -> tray.remove(trayIcon)).start();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, message, title, JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(frame, message, title, JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(frame, message, "错误", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(frame, message, "成功", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main(args));
    }

    private String getHWID() {
        try {
            // 方案1: 使用组合信息生成HWID
            String osName = System.getProperty("os.name");
            String osArch = System.getProperty("os.arch");
            String userName = System.getProperty("user.name");
            String macAddress = getMacAddress();

            String combined = osName + osArch + userName + macAddress;
            return md5Hash(combined);
        } catch (Exception e) {
            // 方案2: 使用备用方法
            return getAlternativeHWID();
        }
    }
    private String getMacAddress() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface network = networkInterfaces.nextElement();
                byte[] mac = network.getHardwareAddress();
                if (mac != null) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < mac.length; i++) {
                        sb.append(String.format("%02X", mac[i]));
                    }
                    return sb.toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "NO-MAC";
    }

    private String md5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : array) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "HASH-ERROR";
        }
    }

    private String getAlternativeHWID() {
        // 使用更基本但可靠的信息组合
        String hwid = System.getProperty("user.name") +
                System.getProperty("user.home") +
                System.getProperty("java.home");
        return "ALT-" + hwid.hashCode();
    }

    private void startGame(String[] options, String username){
        //frame.dispose();
        // 原来的游戏启动代码
        System.setProperty("java.net.preferIPv4Stack", "true");
        OptionParser optionparser = new OptionParser();
        optionparser.allowsUnrecognizedOptions();
        optionparser.accepts("demo");
        optionparser.accepts("fullscreen");
        optionparser.accepts("checkGlErrors");
        OptionSpec<String> optionspec = optionparser.accepts("server").withRequiredArg();
        OptionSpec<Integer> optionspec1 = optionparser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(Integer.valueOf(25565), new Integer[0]);
        OptionSpec<File> optionspec2 = optionparser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."), new File[0]);
        OptionSpec<File> optionspec3 = optionparser.accepts("assetsDir").withRequiredArg().<File>ofType(File.class);
        OptionSpec<File> optionspec4 = optionparser.accepts("resourcePackDir").withRequiredArg().<File>ofType(File.class);
        OptionSpec<String> optionspec5 = optionparser.accepts("proxyHost").withRequiredArg();
        OptionSpec<Integer> optionspec6 = optionparser.accepts("proxyPort").withRequiredArg().defaultsTo("8080", new String[0]).<Integer>ofType(Integer.class);
        OptionSpec<String> optionspec7 = optionparser.accepts("proxyUser").withRequiredArg();
        OptionSpec<String> optionspec8 = optionparser.accepts("proxyPass").withRequiredArg();
        OptionSpec<String> optionspec9 = optionparser.accepts("username").withRequiredArg().defaultsTo("Guyuemang", new String[0]);
        OptionSpec<String> optionspec10 = optionparser.accepts("uuid").withRequiredArg();
        OptionSpec<String> optionspec11 = optionparser.accepts("accessToken").withRequiredArg().required();
        OptionSpec<String> optionspec12 = optionparser.accepts("version").withRequiredArg().required();
        OptionSpec<Integer> optionspec13 = optionparser.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(Integer.valueOf(854), new Integer[0]);
        OptionSpec<Integer> optionspec14 = optionparser.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(Integer.valueOf(480), new Integer[0]);
        OptionSpec<String> optionspec15 = optionparser.accepts("userProperties").withRequiredArg().defaultsTo("{}", new String[0]);
        OptionSpec<String> optionspec16 = optionparser.accepts("profileProperties").withRequiredArg().defaultsTo("{}", new String[0]);
        OptionSpec<String> optionspec17 = optionparser.accepts("assetIndex").withRequiredArg();
        OptionSpec<String> optionspec18 = optionparser.accepts("userType").withRequiredArg().defaultsTo("legacy", new String[0]);
        OptionSpec<String> optionspec19 = optionparser.nonOptions();
        OptionSet optionset = optionparser.parse(options);
        List<String> list = optionset.valuesOf(optionspec19);

        if (!list.isEmpty())
        {
            System.out.println("Completely ignored arguments: " + list);
        }

        String s = (String)optionset.valueOf(optionspec5);
        Proxy proxy = Proxy.NO_PROXY;

        if (s != null)
        {
            try
            {
                proxy = new Proxy(Type.SOCKS, new InetSocketAddress(s, ((Integer)optionset.valueOf(optionspec6)).intValue()));
            }
            catch (Exception var46)
            {
                ;
            }
        }

        final String s1 = (String)optionset.valueOf(optionspec7);
        final String s2 = (String)optionset.valueOf(optionspec8);

        if (!proxy.equals(Proxy.NO_PROXY) && isNullOrEmpty(s1) && isNullOrEmpty(s2))
        {
            Authenticator.setDefault(new Authenticator()
            {
                protected PasswordAuthentication getPasswordAuthentication()
                {
                    return new PasswordAuthentication(s1, s2.toCharArray());
                }
            });
        }

        int i = ((Integer)optionset.valueOf(optionspec13)).intValue();
        int j = ((Integer)optionset.valueOf(optionspec14)).intValue();
        boolean flag = optionset.has("fullscreen");
        boolean flag1 = optionset.has("checkGlErrors");
        boolean flag2 = optionset.has("demo");
        String s3 = (String)optionset.valueOf(optionspec12);
        Gson gson = (new GsonBuilder()).registerTypeAdapter(PropertyMap.class, new Serializer()).create();
        PropertyMap propertymap = (PropertyMap)gson.fromJson((String)optionset.valueOf(optionspec15), PropertyMap.class);
        PropertyMap propertymap1 = (PropertyMap)gson.fromJson((String)optionset.valueOf(optionspec16), PropertyMap.class);
        File file1 = (File)optionset.valueOf(optionspec2);
        File file2 = optionset.has(optionspec3) ? (File)optionset.valueOf(optionspec3) : new File(file1, "assets/");
        File file3 = optionset.has(optionspec4) ? (File)optionset.valueOf(optionspec4) : new File(file1, "resourcepacks/");
        String s4 = optionset.has(optionspec10) ? (String)optionspec10.value(optionset) : (String)optionspec9.value(optionset);
        String s5 = optionset.has(optionspec17) ? (String)optionspec17.value(optionset) : null;
        String s6 = (String)optionset.valueOf(optionspec);
        Integer integer = (Integer)optionset.valueOf(optionspec1);
        Session session = new Session((String)optionspec9.value(optionset), s4, (String)optionspec11.value(optionset), (String)optionspec18.value(optionset));
        GameConfiguration gameconfiguration = new GameConfiguration(new GameConfiguration.UserInformation(session, propertymap, propertymap1, proxy), new GameConfiguration.DisplayInformation(i, j, flag, flag1), new GameConfiguration.FolderInformation(file1, file3, file2, s5), new GameConfiguration.GameInformation(flag2, s3), new GameConfiguration.ServerInformation(s6, integer.intValue()));
        Runtime.getRuntime().addShutdownHook(new Thread("Client Shutdown Thread")
        {
            public void run()
            {
                Minecraft.stopIntegratedServer();
            }
        });
        Thread.currentThread().setName("Client thread");
        (new Minecraft(gameconfiguration)).run(username);
    }
}
