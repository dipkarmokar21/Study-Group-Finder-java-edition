package frontend;

import backend.StudyGroupDAO;
import backend.GroupMemberDAO;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DashboardPanel extends JPanel {
    private MainApp app;
    private StudyGroupDAO groupDAO = new StudyGroupDAO();
    private GroupMemberDAO memberDAO = new GroupMemberDAO();

    public DashboardPanel(MainApp app) {
        this.app = app;
        setName("DASHBOARD");
        setLayout(new BorderLayout(0, 0));
        setBackground(UIUtils.BG_DARK);
        buildUI();
    }

    private void buildUI() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(35, 35, 52));
        topBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UIUtils.BORDER_COLOR),
            BorderFactory.createEmptyBorder(16, 24, 16, 24)
        ));

        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
        welcomePanel.setBackground(new Color(35, 35, 52));

        JLabel welcomeLabel = new JLabel("Welcome back, " + app.sessionUserName);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        welcomeLabel.setForeground(UIUtils.TEXT_PRIMARY);

        JLabel welcomeSub = new JLabel("Here's an overview of your study groups");
        welcomeSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        welcomeSub.setForeground(UIUtils.TEXT_MUTED);

        welcomePanel.add(welcomeLabel);
        welcomePanel.add(Box.createVerticalStrut(2));
        welcomePanel.add(welcomeSub);
        topBar.add(welcomePanel, BorderLayout.WEST);

        JPanel navButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        navButtons.setBackground(new Color(35, 35, 52));

        JButton createBtn = UIUtils.makeButton("+ Create Group", UIUtils.ACCENT_BLUE);
        createBtn.addActionListener(e -> app.navigateTo("CREATE_GROUP"));

        JButton searchBtn = UIUtils.makeButton("Groups", UIUtils.ACCENT_PURPLE);
        searchBtn.addActionListener(e -> app.navigateTo("GROUPS"));

        JButton aboutBtn = UIUtils.makeButton("About", UIUtils.BG_INPUT);
        aboutBtn.addActionListener(e -> app.navigateTo("ABOUT"));

        JButton logoutBtn = UIUtils.makeButton("Logout", UIUtils.ACCENT_PINK);
        logoutBtn.addActionListener(e -> {
            app.clearSession();
            app.navigateTo("LOGIN");
        });

        navButtons.add(createBtn);
        navButtons.add(searchBtn);
        navButtons.add(aboutBtn);
        navButtons.add(logoutBtn);
        topBar.add(navButtons, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(UIUtils.BG_DARK);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        int totalGroups = groupDAO.getTotalGroupCount();
        int myCreated = groupDAO.getMyCreatedCount(app.sessionUserId);
        int myJoined = groupDAO.getMyJoinedCount(app.sessionUserId);

        JPanel statsRow = new JPanel(new GridLayout(1, 3, 16, 0));
        statsRow.setBackground(UIUtils.BG_DARK);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        statsRow.add(makeStatCard("Total Groups", String.valueOf(totalGroups), UIUtils.ACCENT_BLUE, "All available study groups"));
        statsRow.add(makeStatCard("Created by Me", String.valueOf(myCreated), UIUtils.ACCENT_GREEN, "Groups you own and manage"));
        statsRow.add(makeStatCard("Joined", String.valueOf(myJoined), UIUtils.ACCENT_PURPLE, "Groups you participate in"));
        centerPanel.add(statsRow);
        centerPanel.add(Box.createVerticalStrut(24));

        Runnable reloadDashboard = () -> app.navigateTo("DASHBOARD");

        centerPanel.add(makeSectionHeader("My Created Groups", UIUtils.ACCENT_GREEN));
        centerPanel.add(Box.createVerticalStrut(10));

        JPanel createdCards = new JPanel(new GridLayout(0, 2, 14, 14));
        createdCards.setBackground(UIUtils.BG_DARK);

        List<String[]> myGroups = groupDAO.getMyCreatedGroups(app.sessionUserId);
        if (myGroups.isEmpty()) {
            createdCards.add(makeEmptyCard("You haven't created any groups yet. Click '+ Create Group' to get started!"));
        } else {
            for (String[] g : myGroups) {
                createdCards.add(UIUtils.buildGroupCard(g, app.sessionUserId, memberDAO, groupDAO, reloadDashboard, this));
            }
        }

        JPanel createdWrapper = new JPanel(new BorderLayout());
        createdWrapper.setBackground(UIUtils.BG_DARK);
        createdWrapper.add(createdCards, BorderLayout.NORTH);
        JScrollPane createdScroll = new JScrollPane(createdWrapper);
        createdScroll.setPreferredSize(new Dimension(0, 220));
        createdScroll.getViewport().setBackground(UIUtils.BG_DARK);
        createdScroll.setBorder(null);
        createdScroll.getVerticalScrollBar().setUnitIncrement(16);
        centerPanel.add(createdScroll);
        centerPanel.add(Box.createVerticalStrut(24));

        centerPanel.add(makeSectionHeader("Groups I've Joined", UIUtils.ACCENT_PURPLE));
        centerPanel.add(Box.createVerticalStrut(10));

        JPanel joinedCards = new JPanel(new GridLayout(0, 2, 14, 14));
        joinedCards.setBackground(UIUtils.BG_DARK);

        List<String[]> joinedGroups = groupDAO.getMyJoinedGroups(app.sessionUserId);
        if (joinedGroups.isEmpty()) {
            joinedCards.add(makeEmptyCard("You haven't joined any groups yet. Explore the Groups tab to find one!"));
        } else {
            for (String[] g : joinedGroups) {
                joinedCards.add(UIUtils.buildGroupCard(g, app.sessionUserId, memberDAO, groupDAO, reloadDashboard, this));
            }
        }

        JPanel joinedWrapper = new JPanel(new BorderLayout());
        joinedWrapper.setBackground(UIUtils.BG_DARK);
        joinedWrapper.add(joinedCards, BorderLayout.NORTH);
        JScrollPane joinedScroll = new JScrollPane(joinedWrapper);
        joinedScroll.setPreferredSize(new Dimension(0, 220));
        joinedScroll.getViewport().setBackground(UIUtils.BG_DARK);
        joinedScroll.setBorder(null);
        joinedScroll.getVerticalScrollBar().setUnitIncrement(16);
        centerPanel.add(joinedScroll);

        JScrollPane mainScroll = new JScrollPane(centerPanel);
        mainScroll.setBorder(null);
        mainScroll.getViewport().setBackground(UIUtils.BG_DARK);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(mainScroll, BorderLayout.CENTER);
    }

    private JPanel makeSectionHeader(String title, Color accent) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIUtils.BG_DARK);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel label = new JLabel(title);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(accent);
        header.add(label, BorderLayout.WEST);

        JSeparator sep = new JSeparator();
        sep.setForeground(UIUtils.BORDER_COLOR);
        sep.setBackground(UIUtils.BG_DARK);
        JPanel sepWrapper = new JPanel(new BorderLayout());
        sepWrapper.setBackground(UIUtils.BG_DARK);
        sepWrapper.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 0));
        sepWrapper.add(sep);
        header.add(sepWrapper, BorderLayout.CENTER);

        return header;
    }

    private JPanel makeStatCard(String title, String value, Color accent, String subtitle) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(UIUtils.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIUtils.BORDER_COLOR),
            BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));

        JPanel accentBar = new JPanel();
        accentBar.setBackground(accent);
        accentBar.setMaximumSize(new Dimension(40, 4));
        accentBar.setPreferredSize(new Dimension(40, 4));
        accentBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(accentBar);
        card.add(Box.createVerticalStrut(12));

        JLabel vLabel = new JLabel(value);
        vLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        vLabel.setForeground(accent);
        vLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(vLabel);
        card.add(Box.createVerticalStrut(4));

        JLabel tLabel = new JLabel(title);
        tLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tLabel.setForeground(UIUtils.TEXT_PRIMARY);
        tLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(tLabel);
        card.add(Box.createVerticalStrut(2));

        JLabel sLabel = new JLabel(subtitle);
        sLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sLabel.setForeground(UIUtils.TEXT_MUTED);
        sLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(sLabel);

        return card;
    }

    private JPanel makeEmptyCard(String message) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UIUtils.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIUtils.BORDER_COLOR),
            BorderFactory.createEmptyBorder(30, 20, 30, 20)
        ));

        JLabel label = new JLabel(message);
        label.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        label.setForeground(UIUtils.TEXT_MUTED);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(label, BorderLayout.CENTER);

        return card;
    }
}
