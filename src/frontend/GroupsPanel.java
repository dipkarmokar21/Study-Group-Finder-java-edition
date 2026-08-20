package frontend;

import backend.StudyGroupDAO;
import backend.GroupMemberDAO;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GroupsPanel extends JPanel {
    private MainApp app;
    private StudyGroupDAO groupDAO = new StudyGroupDAO();
    private GroupMemberDAO memberDAO = new GroupMemberDAO();

    public GroupsPanel(MainApp app) {
        this.app = app;
        setName("GROUPS");
        setLayout(new BorderLayout(0, 10));
        setBackground(UIUtils.BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        buildUI();
    }

    private void buildUI() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UIUtils.BG_DARK);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(UIUtils.BG_DARK);
        JLabel searchTitle = new JLabel("Study Groups");
        searchTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        searchTitle.setForeground(UIUtils.TEXT_PRIMARY);
        JLabel searchSub = new JLabel("Filter using Stored Procedure, Join/Leave groups (Triggers fire)");
        searchSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchSub.setForeground(UIUtils.TEXT_MUTED);
        titlePanel.add(searchTitle);
        titlePanel.add(searchSub);
        topBar.add(titlePanel, BorderLayout.WEST);

        JButton backBtn = UIUtils.makeButton("Back to Dashboard", UIUtils.BG_INPUT);
        backBtn.addActionListener(e -> app.navigateTo("DASHBOARD"));
        topBar.add(backBtn, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.setBackground(UIUtils.BG_DARK);

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterBar.setBackground(UIUtils.BG_CARD);
        filterBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIUtils.BORDER_COLOR),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        JLabel filterLabel = new JLabel("Filter by Subject (Stored Procedure):");
        filterLabel.setForeground(UIUtils.TEXT_PRIMARY);
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JComboBox<String> subjectFilter = new JComboBox<>();
        subjectFilter.setBackground(UIUtils.BG_INPUT);
        subjectFilter.setForeground(UIUtils.TEXT_PRIMARY);
        subjectFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subjectFilter.addItem("ALL");
        List<String> subjects = groupDAO.getAllSubjects();
        for (String s : subjects) subjectFilter.addItem(s);

        filterBar.add(filterLabel);
        filterBar.add(subjectFilter);
        centerPanel.add(filterBar, BorderLayout.NORTH);

        JPanel cardsContainer = new JPanel(new GridLayout(0, 2, 15, 15));
        cardsContainer.setBackground(UIUtils.BG_DARK);

        JPanel cardsWrapper = new JPanel(new BorderLayout());
        cardsWrapper.setBackground(UIUtils.BG_DARK);
        cardsWrapper.add(cardsContainer, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(cardsWrapper);
        scrollPane.getViewport().setBackground(UIUtils.BG_DARK);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        Runnable loadData = () -> {
            cardsContainer.removeAll();
            String subject = (String) subjectFilter.getSelectedItem();
            List<String[]> groups = groupDAO.getGroupsBySubject(subject);
            
            if (groups.isEmpty()) {
                JLabel emptyLabel = new JLabel("No study groups found for this subject.");
                emptyLabel.setForeground(UIUtils.TEXT_MUTED);
                cardsContainer.add(emptyLabel);
            } else {
                for (String[] g : groups) {
                    Runnable reloadCb = () -> subjectFilter.setSelectedIndex(subjectFilter.getSelectedIndex());
                    cardsContainer.add(UIUtils.buildGroupCard(g, app.sessionUserId, memberDAO, groupDAO, reloadCb, this));
                }
            }
            cardsContainer.revalidate();
            cardsContainer.repaint();
        };

        loadData.run();
        subjectFilter.addActionListener(e -> loadData.run());

        add(centerPanel, BorderLayout.CENTER);
    }
}
