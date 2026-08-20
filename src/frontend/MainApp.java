package frontend;
import javax.swing.*;
import java.awt.*;

public class MainApp extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    public int sessionUserId = 0;
    public String sessionUserName = null;
    public String sessionUserEmail = null;
    
    public MainApp() {
        setTitle("Study Group Finder");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(UIUtils.BG_DARK);
        getContentPane().setBackground(UIUtils.BG_DARK);
        mainPanel.add(new LoginPanel(this), "LOGIN");
        mainPanel.add(new RegisterPanel(this), "REGISTER");

        add(mainPanel);
    }
    
    public void navigateTo(String screenName) {
        if (screenName.equals("DASHBOARD")) {
            removePanel("DASHBOARD");
            mainPanel.add(new DashboardPanel(this), "DASHBOARD");
        } else if (screenName.equals("CREATE_GROUP")) {
            removePanel("CREATE_GROUP");
            mainPanel.add(new CreateGroupPanel(this), "CREATE_GROUP");
        } else if (screenName.equals("GROUPS")) {
            removePanel("GROUPS");
            mainPanel.add(new GroupsPanel(this), "GROUPS");
        } else if (screenName.equals("ABOUT")) {
            removePanel("ABOUT");
            mainPanel.add(new AboutPanel(this), "ABOUT");
        }
        cardLayout.show(mainPanel, screenName);
    }
    
    private void removePanel(String name) {
        for (Component c : mainPanel.getComponents()) {
            if (name.equals(c.getName())) {
                mainPanel.remove(c);
                break;
            }
        }
    }
    
    public void setSession(int id, String name, String email) {
        this.sessionUserId = id;
        this.sessionUserName = name;
        this.sessionUserEmail = email;
    }
    
    public void clearSession() {
        this.sessionUserId = 0;
        this.sessionUserName = null;
        this.sessionUserEmail = null;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.put("Panel.background", UIUtils.BG_DARK);
            UIManager.put("OptionPane.background", new Color(50, 52, 68));
            UIManager.put("OptionPane.messageForeground", UIUtils.TEXT_PRIMARY);
            UIManager.put("Label.foreground", UIUtils.TEXT_PRIMARY);
            UIManager.put("ScrollPane.background", UIUtils.BG_DARK);
            UIManager.put("Viewport.background", UIUtils.BG_DARK);
        } catch (Exception e) { }
        SwingUtilities.invokeLater(() -> new MainApp().setVisible(true));
    }
}
