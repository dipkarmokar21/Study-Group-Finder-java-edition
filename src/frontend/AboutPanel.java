package frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

public class AboutPanel extends JPanel {
    private MainApp app;

    public AboutPanel(MainApp app) {
        this.app = app;
        setName("ABOUT");
        setLayout(new BorderLayout());
        setBackground(UIUtils.BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        buildUI();
    }

    private void buildUI() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UIUtils.BG_DARK);
        JLabel titleLabel = new JLabel("About");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(UIUtils.TEXT_PRIMARY);
        topBar.add(titleLabel, BorderLayout.WEST);

        JButton backBtn = UIUtils.makeButton("Back to Dashboard", UIUtils.BG_INPUT);
        backBtn.addActionListener(e -> app.navigateTo("DASHBOARD"));
        topBar.add(backBtn, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(UIUtils.BG_DARK);
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(UIUtils.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIUtils.BORDER_COLOR),
            BorderFactory.createEmptyBorder(40, 60, 40, 60)
        ));
        JLabel appName = new JLabel("Study Group Finder");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 32));
        appName.setForeground(UIUtils.ACCENT_BLUE);
        appName.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(appName);
        card.add(Box.createVerticalStrut(6));
        JLabel version = new JLabel("Version 1.0.0");
        version.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        version.setForeground(UIUtils.TEXT_MUTED);
        version.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(version);
        card.add(Box.createVerticalStrut(30));
        JSeparator sep = new JSeparator();
        sep.setForeground(UIUtils.BORDER_COLOR);
        sep.setBackground(UIUtils.BG_CARD);
        sep.setMaximumSize(new Dimension(300, 1));
        card.add(sep);
        card.add(Box.createVerticalStrut(30));
        JLabel creator = new JLabel("Created by: Dip Karmokar");
        creator.setFont(new Font("Segoe UI", Font.BOLD, 18));
        creator.setForeground(UIUtils.TEXT_PRIMARY);
        creator.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(creator);
        card.add(Box.createVerticalStrut(30));
        JPanel linksPanel = new JPanel();
        linksPanel.setLayout(new BoxLayout(linksPanel, BoxLayout.Y_AXIS));
        linksPanel.setBackground(UIUtils.BG_CARD);
        linksPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        linksPanel.add(createLinkButton("[G]  GitHub - dipkarmokar21", "https://github.com/dipkarmokar21", UIUtils.ACCENT_GREEN));
        linksPanel.add(Box.createVerticalStrut(10));
        linksPanel.add(createLinkButton("[in]  LinkedIn - dipkarmokar", "https://www.linkedin.com/in/dipkarmokar/", UIUtils.ACCENT_BLUE));
        linksPanel.add(Box.createVerticalStrut(10));
        linksPanel.add(createLinkButton("[W]  Website - nitchat.com", "https://nitchat.com", UIUtils.ACCENT_PURPLE));

        card.add(linksPanel);

        centerWrapper.add(card);
        add(centerWrapper, BorderLayout.CENTER);
    }

    private JButton createLinkButton(String text, String url, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(color);
        btn.setBackground(UIUtils.BG_INPUT);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIUtils.BORDER_COLOR),
            BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(320, 42));
        btn.setPreferredSize(new Dimension(320, 42));

        btn.addActionListener(e -> {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (Exception ex) {
                UIUtils.showError(AboutPanel.this, "Could not open link: " + url);
            }
        });

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(UIUtils.BORDER_COLOR);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(UIUtils.BG_INPUT);
            }
        });

        return btn;
    }
}
