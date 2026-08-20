package frontend;

import backend.UserDAO;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RegisterPanel extends JPanel {
    private MainApp app;
    private UserDAO userDAO = new UserDAO();

    public RegisterPanel(MainApp app) {
        this.app = app;
        setName("REGISTER");
        setLayout(new GridBagLayout());
        setBackground(UIUtils.BG_DARK);
        buildUI();
    }

    private void buildUI() {
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(UIUtils.BG_CARD);
        form.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel title = new JLabel("Create an Account");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(UIUtils.TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.add(title);
        form.add(Box.createVerticalStrut(25));

        JTextField nameFld = UIUtils.makeTextField();
        form.add(UIUtils.makeLabel("Full Name"));
        form.add(nameFld);
        form.add(Box.createVerticalStrut(15));

        JTextField emailFld = UIUtils.makeTextField();
        form.add(UIUtils.makeLabel("Email Address"));
        form.add(emailFld);
        form.add(Box.createVerticalStrut(15));

        JPasswordField passFld = UIUtils.makePasswordField();
        form.add(UIUtils.makeLabel("Password (min 6 chars)"));
        form.add(passFld);
        form.add(Box.createVerticalStrut(15));

        JPasswordField confirmFld = UIUtils.makePasswordField();
        form.add(UIUtils.makeLabel("Confirm Password"));
        form.add(confirmFld);
        form.add(Box.createVerticalStrut(25));

        JButton regBtn = UIUtils.makeButton("Register", UIUtils.ACCENT_GREEN);
        regBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        regBtn.addActionListener(e -> {
            String name = nameFld.getText().trim();
            String email = emailFld.getText().trim();
            String pwd = new String(passFld.getPassword());
            String pwd2 = new String(confirmFld.getPassword());

            if (name.isEmpty() || email.isEmpty() || pwd.isEmpty() || pwd2.isEmpty()) {
                UIUtils.showError(this, "All fields are required."); return;
            }
            if (pwd.length() < 6) {
                UIUtils.showError(this, "Password must be at least 6 characters."); return;
            }
            if (!pwd.equals(pwd2)) {
                UIUtils.showError(this, "Passwords do not match."); return;
            }

            String[] result = userDAO.register(name, email, pwd);
            if (result != null) {
                UIUtils.showSuccess(this, "Registration successful!");
                String[] u = userDAO.login(email, pwd);
                if (u != null) {
                    app.setSession(Integer.parseInt(u[0]), u[1], u[2]);
                    nameFld.setText(""); emailFld.setText(""); passFld.setText(""); confirmFld.setText("");
                    app.navigateTo("DASHBOARD");
                }
            } else {
                UIUtils.showError(this, "Email is already registered or an error occurred.");
            }
        });
        form.add(regBtn);
        form.add(Box.createVerticalStrut(15));

        JButton loginBtn = new JButton("Already have an account? Login");
        loginBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        loginBtn.setForeground(UIUtils.ACCENT_PURPLE);
        loginBtn.setBackground(UIUtils.BG_CARD);
        loginBtn.setBorderPainted(false);
        loginBtn.setFocusPainted(false);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.addActionListener(e -> app.navigateTo("LOGIN"));
        form.add(loginBtn);

        add(form);
    }
}
