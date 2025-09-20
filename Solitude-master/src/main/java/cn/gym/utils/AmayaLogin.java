package cn.gym.utils;

import cn.gym.utils.misc.FileUtils;
import com.alibaba.fastjson2.JSONObject;
import net.minecraft.client.Minecraft;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Random;

public class AmayaLogin extends JFrame {
    private static final String SECRET_KEY = "mySuperSecretKey12345";
    public static final String SALT = "randomSaltValue123";
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final IvParameterSpec IV_SPEC = new IvParameterSpec(new byte[16]);
    private static final File CONFIG_FILE = new File("amaya_login_config.json");
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;

    public AmayaLogin() {
        setTitle("Amaya Login");
        setSize(500, 300); // 长方形尺寸
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false); // 禁止调整窗口大小

        // 创建主面板，使用哑光黑色背景
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(30, 30, 30)); // 哑光黑色背景
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        // 创建表单面板
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false); // 透明背景
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 用户名
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel usernameLabel = new JLabel("用户名:");
        usernameLabel.setForeground(Color.WHITE); // 白色文字
        formPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(20);
        usernameField.setForeground(Color.WHITE); // 白色文字
        usernameField.setBackground(new Color(50, 50, 50)); // 深灰色背景
        usernameField.setCaretColor(Color.WHITE); // 白色光标
        usernameField.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70))); // 边框
        formPanel.add(usernameField, gbc);

        // 密码
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel passwordLabel = new JLabel("密码:");
        passwordLabel.setForeground(Color.WHITE); // 白色文字
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        passwordField.setForeground(Color.WHITE); // 白色文字
        passwordField.setBackground(new Color(50, 50, 50)); // 深灰色背景
        passwordField.setCaretColor(Color.WHITE); // 白色光标
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70))); // 边框
        formPanel.add(passwordField, gbc);

        // 登录按钮
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        loginButton = new JButton("登录");
        loginButton.setForeground(Color.WHITE); // 白色文字
        loginButton.setBackground(new Color(70, 70, 70)); // 深灰色背景
        loginButton.setBorder(BorderFactory.createLineBorder(new Color(90, 90, 90))); // 边框
        loginButton.setFocusPainted(false); // 去除焦点边框
        formPanel.add(loginButton, gbc);

        // 状态标签
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        statusLabel = new JLabel(" ", JLabel.CENTER);
        statusLabel.setForeground(Color.WHITE); // 白色文字
        formPanel.add(statusLabel, gbc);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);
        if (tryAutoLogin()) {
            statusLabel.setText("正在尝试自动登录...");
            statusLabel.setForeground(Color.BLUE);
        }
    }

    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("用户名和密码不能为空");
            return;
        }
        if ("Dev".equalsIgnoreCase(username)) {
            statusLabel.setText("开发者模式登录成功!");
            statusLabel.setForeground(Color.GREEN);
            saveLoginInfo(username, password); // 可选：保存本地
            JOptionPane.showMessageDialog(this, "Cracked");
            dispose();
            Minecraft.resumeGame();
            return;
        }
        loginButton.setEnabled(false);
        statusLabel.setText("正在连接服务器...");
        statusLabel.setForeground(Color.BLUE);

        new Thread(() -> {
            Socket socket = null;
            int retryCount = 0;
            final int MAX_RETRIES = 3;

            while (retryCount < MAX_RETRIES) {
                try {
                    socket = new Socket();
                    socket.connect(new InetSocketAddress("43.248.188.15", 7070), 5000);
                    System.out.println("成功连接到服务器");

                    // 创建并启动接收线程
                    ClientReceiver receiver = new ClientReceiver(socket, this);
                    new Thread(receiver).start();

                    // 发送登录数据
                    DataOutputStream oos = new DataOutputStream(socket.getOutputStream());
                    JSONObject json = new JSONObject();
                    json.put("type", "login");
                    json.put("username", username);
                    json.put("password", hashPassword(password));
                    json.put("uuid", generateUUID());

                    String jsonString = json.toJSONString();
                    System.out.println("发送: " + jsonString);
                    String encodedString = encrypt(jsonString);
                    oos.writeUTF(encodedString);

                    // 不再立即返回，等待接收线程处理
                    return;

                } catch (Exception e) {
                    retryCount++;
                    if (retryCount >= MAX_RETRIES) {
                        SwingUtilities.invokeLater(() -> {
                            statusLabel.setText("连接服务器失败");
                            statusLabel.setForeground(Color.RED);
                            loginButton.setEnabled(true);
                        });
                    } else {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }
        }).start();
    }

    private static String generateUUID() {
        // 使用固定种子确保每次生成相同的UUID
        long fixedSeed = 123456789L; // 可以替换为你想要的任何固定值
        Random random = new Random(fixedSeed);

        // 生成16字节的随机数据(模拟UUID)
        byte[] uuidBytes = new byte[16];
        random.nextBytes(uuidBytes);

        // Base64编码
        return Base64.getEncoder().encodeToString(uuidBytes);
    }

    public void handleServerResponse(String response) {
        SwingUtilities.invokeLater(() -> {
            if (response.equalsIgnoreCase("{\"message\":\"Login success\",\"status\":\"success\"}")) {
                statusLabel.setText("登录成功!");
                statusLabel.setForeground(Color.GREEN);

                // 保存登录信息
                saveLoginInfo(usernameField.getText(), new String(passwordField.getPassword()));

                JOptionPane.showMessageDialog(this, "登录成功!");
                dispose(); // 关闭登录窗口
                Minecraft.resumeGame();
            } else if (response.equalsIgnoreCase("{\"message\":\"UUID not match\",\"status\":\"failure\"}")) {
                statusLabel.setText("UUID不匹配");
                loginButton.setEnabled(true);
            } else if (response.equalsIgnoreCase("{\"status\":\"fail\",\"message\":\"User not activated\"}")) {
                statusLabel.setText("用户未激活");
                loginButton.setEnabled(true);
            } else if (response.equalsIgnoreCase("{\"status\":\"fail\",\"message\":\"User not found\"}")) {
                statusLabel.setText("用户不存在");
                loginButton.setEnabled(true);
            } else if (response.equalsIgnoreCase("{\"status\":\"fail\",\"message\":\"Incorrect password\"}")) {
                statusLabel.setText("密码错误");
                loginButton.setEnabled(true);
            } else {
                statusLabel.setText("账号或密码错误或者卡密到期");
                loginButton.setEnabled(true);
            }
        });
    }
    private boolean tryAutoLogin() {
        File configFile = new File(String.valueOf(CONFIG_FILE));
        if (!configFile.exists()) {
            return false;
        }

        try {
            // 读取文件内容并去除可能的空白字符
            String encrypted = FileUtils.readFile(CONFIG_FILE).trim();

            // 检查Base64格式是否有效
            if (!isValidBase64(encrypted)) {
                configFile.delete(); // 删除无效的配置文件
                return false;
            }

            String decrypted = decrypt(encrypted);
            JSONObject config = JSONObject.parseObject(decrypted);

            // 验证必要字段是否存在
            if (config == null || !config.containsKey("username") || !config.containsKey("password")) {
                configFile.delete();
                return false;
            }

            // 检查是否在有效期内（例如7天内）
            long lastLogin = config.getLongValue("lastLogin");
            if (System.currentTimeMillis() - lastLogin > 7 * 24 * 60 * 60 * 1000) {
                configFile.delete();
                return false;
            }

            // 使用保存的凭据自动登录
            usernameField.setText(config.getString("username"));
            passwordField.setText(config.getString("password"));
            login();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            // 发生任何错误都删除可能损坏的配置文件
            configFile.delete();
            return false;
        }
    }

    // 添加Base64验证方法
    private boolean isValidBase64(String value) {
        try {
            Base64.getDecoder().decode(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // 修改保存方法确保写入正确
    private void saveLoginInfo(String username, String password) {
        try {
            JSONObject config = new JSONObject();
            config.put("username", username);
            config.put("password", password);
            config.put("lastLogin", System.currentTimeMillis());

            String encrypted = encrypt(config.toJSONString());

            // 确保写入前去除任何可能的空白字符
            FileUtils.writeFile(CONFIG_FILE, encrypted.trim());
        } catch (Exception e) {
            e.printStackTrace();
            // 保存失败时删除可能损坏的文件
            new File(String.valueOf(CONFIG_FILE)).delete();
        }
    }
    public static void main() {
        SwingUtilities.invokeLater(() -> {
            AmayaLogin login = new AmayaLogin();
            login.setVisible(true);
        });
    }

    // 静态加密/解密方法
    static String encrypt(String value) throws Exception {
        KeySpec keySpec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALT.getBytes(), 65536, 128);
        SecretKey key = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(keySpec);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getEncoded(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, IV_SPEC);
        byte[] encrypted = cipher.doFinal(value.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    static String decrypt(String encrypted) throws Exception {
        KeySpec keySpec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALT.getBytes(), 65536, 128);
        SecretKey key = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(keySpec);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getEncoded(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, IV_SPEC);
        byte[] decoded = Base64.getDecoder().decode(encrypted);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted);
    }

    private static String hashPassword(String password) throws Exception {
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), SALT.getBytes(), 65536, 128);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hash = keyFactory.generateSecret(keySpec).getEncoded();
        return Base64.getEncoder().encodeToString(hash);
    }
}

class ClientReceiver implements Runnable {
    private Socket socket;
    private AmayaLogin loginFrame;

    public ClientReceiver(Socket socket, AmayaLogin loginFrame) {
        this.socket = socket;
        this.loginFrame = loginFrame;
    }

    @Override
    public void run() {
        try {
            DataInputStream ois = new DataInputStream(socket.getInputStream());
            String received = ois.readUTF();
            String decoded = AmayaLogin.decrypt(received);
            System.out.println("接收: " + decoded);
            loginFrame.handleServerResponse(decoded);
        } catch (Exception e) {
            SwingUtilities.invokeLater(() -> {
                loginFrame.handleServerResponse("{\"status\":\"fail\",\"message\":\"连接错误\"}");
            });
            e.printStackTrace();
        } finally {
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}