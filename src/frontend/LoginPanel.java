package frontend;

import backend.UserDAO;
import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {
    private MainApp app;
    private UserDAO userDAO = new UserDAO();

    public LoginPanel(MainApp app) {
        this.app = app;
        setName("LOGIN");
        setLayout(new GridBagLayout());
        setBackground(UIUtils.BG_DARK);
        buildUI();
    }

    private void buildUI() {
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(UIUtils.BG_CARD);
        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIUtils.BORDER_COLOR),
            BorderFactory.createEmptyBorder(40, 50, 40, 50)
        ));
        JLabel title = new JLabel("Welcome Back");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(UIUtils.TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.add(title);

        JLabel subtitle = new JLabel("Sign in to your account");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(UIUtils.TEXT_MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.add(subtitle);
        form.add(Box.createVerticalStrut(30));
        JLabel emailLabel = UIUtils.makeLabel("Email Address");
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.add(emailLabel);
        form.add(Box.createVerticalStrut(4));

        JTextField emailFld = UIUtils.makeTextField();
        emailFld.setMaximumSize(new Dimension(320, 36));
        emailFld.setPreferredSize(new Dimension(320, 36));
        emailFld.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.add(emailFld);
        form.add(Box.createVerticalStrut(18));
        JLabel passLabel = UIUtils.makeLabel("Password");
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.add(passLabel);
        form.add(Box.createVerticalStrut(4));

        JPasswordField passFld = UIUtils.makePasswordField();
        passFld.setMaximumSize(new Dimension(320, 36));
        passFld.setPreferredSize(new Dimension(320, 36));
        passFld.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.add(passFld);
        form.add(Box.createVerticalStrut(28));
        JButton loginBtn = UIUtils.makeButton("Login", UIUtils.ACCENT_BLUE);
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(320, 40));
        loginBtn.addActionListener(e -> {
            String email = emailFld.getText().trim();
            String pwd = new String(passFld.getPassword());
            if (email.isEmpty() || pwd.isEmpty()) {
                UIUtils.showError(this, "Please fill in all fields.");
                return;
            }
            String[] u = userDAO.login(email, pwd);
            if (u != null) {
                app.setSession(Integer.parseInt(u[0]), u[1], u[2]);
                emailFld.setText("");
                passFld.setText("");
                app.navigateTo("DASHBOARD");
            } else {
                UIUtils.showError(this, "Invalid email or password.");
            }
        });
        form.add(loginBtn);
        form.add(Box.createVerticalStrut(18));
        JButton regBtn = new JButton("Don't have an account? Create one");
        regBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        regBtn.setForeground(UIUtils.ACCENT_PURPLE);
        regBtn.setBackground(UIUtils.BG_CARD);
        regBtn.setBorderPainted(false);
        regBtn.setFocusPainted(false);
        regBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        regBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        regBtn.addActionListener(e -> app.navigateTo("REGISTER"));
        form.add(regBtn);
        form.add(Box.createVerticalStrut(20));
        JPanel demoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        demoPanel.setBackground(UIUtils.BG_CARD);
        JLabel demoLabel = new JLabel("Demo:");
        demoLabel.setForeground(UIUtils.TEXT_MUTED);
        demoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        demoPanel.add(demoLabel);

        String[] demos = {"alex@university.edu", "sarah@university.edu", "david@university.edu", "emily@university.edu"};
        for (String d : demos) {
            JButton dBtn = new JButton(d.split("@")[0]);
            dBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
            dBtn.setBackground(UIUtils.BG_INPUT);
            dBtn.setForeground(UIUtils.TEXT_MUTED);
            dBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIUtils.BORDER_COLOR),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
            ));
            dBtn.setFocusPainted(false);
            dBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            dBtn.addActionListener(e -> { emailFld.setText(d); passFld.setText("password123"); });
            demoPanel.add(dBtn);
        }
        form.add(demoPanel);

        add(form);
    }
}
