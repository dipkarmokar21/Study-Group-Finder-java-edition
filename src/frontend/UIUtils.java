package frontend;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class UIUtils {
    public static final Color BG_DARK = new Color(30, 30, 46);
    public static final Color BG_CARD = new Color(42, 42, 60);
    public static final Color BG_INPUT = new Color(49, 50, 68);
    public static final Color TEXT_PRIMARY = new Color(205, 214, 244);
    public static final Color TEXT_MUTED = new Color(166, 173, 200);
    public static final Color ACCENT_BLUE = new Color(137, 180, 250);
    public static final Color ACCENT_PINK = new Color(245, 194, 231);
    public static final Color ACCENT_GREEN = new Color(166, 227, 161);
    public static final Color ACCENT_PURPLE = new Color(203, 166, 247);
    public static final Color BORDER_COLOR = new Color(69, 71, 90);

    public static JButton makeButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(BG_DARK); // Dark text on bright accent buttons looks better
        if (bg.equals(BG_INPUT) || bg.equals(BG_CARD) || bg.equals(BG_DARK)) {
            btn.setForeground(TEXT_PRIMARY);
        }
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static JLabel makeLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        return label;
    }

    public static JTextField makeTextField() {
        JTextField tf = new JTextField();
        tf.setMaximumSize(new Dimension(400, 36));
        tf.setPreferredSize(new Dimension(400, 36));
        tf.setBackground(BG_INPUT);
        tf.setForeground(TEXT_PRIMARY);
        tf.setCaretColor(TEXT_PRIMARY);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        return tf;
    }

    public static JPasswordField makePasswordField() {
        JPasswordField pf = new JPasswordField();
        pf.setMaximumSize(new Dimension(400, 36));
        pf.setPreferredSize(new Dimension(400, 36));
        pf.setBackground(BG_INPUT);
        pf.setForeground(TEXT_PRIMARY);
        pf.setCaretColor(TEXT_PRIMARY);
        pf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        pf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pf.setAlignmentX(Component.LEFT_ALIGNMENT);
        return pf;
    }

    public static JTable makeTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setOpaque(true);
        table.setBackground(BG_INPUT);
        table.setForeground(TEXT_PRIMARY);
        table.setSelectionBackground(ACCENT_BLUE);
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(BORDER_COLOR);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(28);
        
        JTableHeader header = table.getTableHeader();
        header.setOpaque(true);
        header.setBackground(BG_CARD);
        header.setForeground(TEXT_PRIMARY);
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        return table;
    }

    public static void showError(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showSuccess(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public static JPanel buildGroupCard(String[] g, int sessionUserId, backend.GroupMemberDAO memberDAO, backend.StudyGroupDAO groupDAO, Runnable reloadCallback, Component parent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(BG_CARD);
        
        JLabel subjectLabel = new JLabel(g[2].toUpperCase());
        subjectLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        subjectLabel.setForeground(ACCENT_PURPLE);
        topPanel.add(subjectLabel);
        
        JLabel titleLabel = new JLabel(g[1]);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_PRIMARY);
        topPanel.add(titleLabel);
        
        card.add(topPanel, BorderLayout.NORTH);
        JPanel midPanel = new JPanel(new GridLayout(3, 1, 0, 5));
        midPanel.setBackground(BG_CARD);
        midPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        JLabel leaderLabel = new JLabel("Leader: " + g[3]);
        leaderLabel.setForeground(TEXT_MUTED);
        leaderLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JLabel scheduleLabel = new JLabel("Schedule: " + g[7] + " (" + g[8] + ")");
        scheduleLabel.setForeground(TEXT_MUTED);
        scheduleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JLabel capacityLabel = new JLabel("Members: " + g[4] + " / " + g[5] + " (" + g[6] + ")");
        capacityLabel.setForeground(TEXT_MUTED);
        capacityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        midPanel.add(leaderLabel);
        midPanel.add(scheduleLabel);
        midPanel.add(capacityLabel);
        
        card.add(midPanel, BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bottomPanel.setBackground(BG_CARD);
        
        int groupId = Integer.parseInt(g[0]);
        String ownerId = g[9];
        boolean isOwner = String.valueOf(sessionUserId).equals(ownerId);
        boolean isMember = memberDAO.isMember(groupId, sessionUserId);
        boolean isFull = "FULL".equals(g[6]) || Integer.parseInt(g[4]) >= Integer.parseInt(g[5]);

        if (isOwner) {
            JButton delBtn = makeButton("Delete", ACCENT_PINK);
            delBtn.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(card, "Delete group '" + g[1] + "' forever?", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (groupDAO.deleteGroup(groupId, sessionUserId)) {
                        showSuccess(parent, "Group deleted successfully.");
                        reloadCallback.run();
                    } else {
                        showError(parent, "Failed to delete group.");
                    }
                }
            });
            bottomPanel.add(delBtn);
            
            bottomPanel.add(Box.createHorizontalStrut(5));

            JButton btn = makeButton("View Members", ACCENT_BLUE);
            btn.addActionListener(e -> {
                List<String[]> members = memberDAO.getMembersForGroup(groupId);
                if (members.isEmpty()) {
                    JOptionPane.showMessageDialog(parent, "No one has joined this group yet.", "Members of " + g[1], JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                String[] cols = {"Name", "Email", "Joined At"};
                DefaultTableModel model = new DefaultTableModel(cols, 0) {
                    public boolean isCellEditable(int r, int c) { return false; }
                };
                for (String[] m : members) model.addRow(m);
                JTable table = makeTable(model);
                JScrollPane scroll = new JScrollPane(table);
                scroll.setPreferredSize(new Dimension(500, 200));
                scroll.getViewport().setBackground(BG_DARK);
                scroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
                JOptionPane.showMessageDialog(parent, scroll, "Members of " + g[1], JOptionPane.PLAIN_MESSAGE);
            });
            bottomPanel.add(btn);
        } else if (isMember) {
            JButton btn = makeButton("Leave Group", ACCENT_PINK);
            btn.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(card, "Leave '" + g[1] + "'?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (memberDAO.leaveGroup(groupId, sessionUserId)) {
                        showSuccess(parent, "You left the group. TRIGGER fired to update status!");
                        reloadCallback.run();
                    } else {
                        showError(parent, "Failed to leave group.");
                    }
                }
            });
            bottomPanel.add(btn);
        } else if (isFull) {
            JButton btn = makeButton("Group Full", BG_INPUT);
            btn.setEnabled(false);
            bottomPanel.add(btn);
        } else {
            JButton btn = makeButton("Join Group", ACCENT_GREEN);
            btn.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(card, "Join '" + g[1] + "'?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    int result = memberDAO.joinGroup(groupId, sessionUserId);
                    if (result == 0) {
                        showSuccess(parent, "Joined successfully! TRIGGER fired to update status.");
                        reloadCallback.run();
                    } else if (result == 1) {
                        showError(parent, "Group is FULL (max members reached).");
                    } else {
                        showError(parent, "Failed to join group.");
                    }
                }
            });
            bottomPanel.add(btn);
        }
        
        card.add(bottomPanel, BorderLayout.SOUTH);
        return card;
    }
}
