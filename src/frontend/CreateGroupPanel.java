package frontend;

import backend.StudyGroupDAO;
import javax.swing.*;
import java.awt.*;

public class CreateGroupPanel extends JPanel {
    private MainApp app;
    private StudyGroupDAO groupDAO = new StudyGroupDAO();

    public CreateGroupPanel(MainApp app) {
        this.app = app;
        setName("CREATE_GROUP");
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
        JLabel createTitle = new JLabel("Create New Study Group");
        createTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        createTitle.setForeground(UIUtils.TEXT_PRIMARY);
        JLabel createSub = new JLabel("Inserts into 3 tables (study_groups, schedules, group_members) via Transaction");
        createSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        createSub.setForeground(UIUtils.TEXT_MUTED);
        titlePanel.add(createTitle);
        titlePanel.add(createSub);
        topBar.add(titlePanel, BorderLayout.WEST);

        JButton backBtn = UIUtils.makeButton("Back to Dashboard", UIUtils.BG_INPUT);
        backBtn.addActionListener(e -> app.navigateTo("DASHBOARD"));
        topBar.add(backBtn, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(UIUtils.BG_DARK);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(UIUtils.BG_CARD);
        form.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JTextField titleFld = UIUtils.makeTextField();
        form.add(UIUtils.makeLabel("Group Title (e.g. AI Study Group)"));
        form.add(titleFld);
        form.add(Box.createVerticalStrut(15));

        JTextField subjFld = UIUtils.makeTextField();
        form.add(UIUtils.makeLabel("Subject (e.g. Computer Science)"));
        form.add(subjFld);
        form.add(Box.createVerticalStrut(15));

        SpinnerNumberModel maxModel = new SpinnerNumberModel(5, 2, 50, 1);
        JSpinner maxSpin = new JSpinner(maxModel);
        maxSpin.setMaximumSize(new Dimension(400, 36));
        maxSpin.setPreferredSize(new Dimension(400, 36));
        maxSpin.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        maxSpin.setAlignmentX(Component.LEFT_ALIGNMENT);
        JComponent editor = maxSpin.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
            tf.setBackground(UIUtils.BG_INPUT);
            tf.setForeground(UIUtils.TEXT_PRIMARY);
            tf.setCaretColor(UIUtils.TEXT_PRIMARY);
            tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            tf.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        }
        maxSpin.setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_COLOR));
        form.add(UIUtils.makeLabel("Max Members"));
        form.add(maxSpin);
        form.add(Box.createVerticalStrut(15));

        JTextField dayFld = UIUtils.makeTextField();
        form.add(UIUtils.makeLabel("Meeting Day (e.g. Monday)"));
        form.add(dayFld);
        form.add(Box.createVerticalStrut(15));

        JTextField timeFld = UIUtils.makeTextField();
        form.add(UIUtils.makeLabel("Meeting Time (e.g. 10:00 AM)"));
        form.add(timeFld);
        form.add(Box.createVerticalStrut(25));

        JButton saveBtn = UIUtils.makeButton("Create Group & Schedule", UIUtils.ACCENT_GREEN);
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.addActionListener(e -> {
            String t = titleFld.getText().trim();
            String s = subjFld.getText().trim();
            String d = dayFld.getText().trim();
            String tm = timeFld.getText().trim();
            int m = (Integer) maxSpin.getValue();

            if (t.isEmpty() || s.isEmpty() || d.isEmpty() || tm.isEmpty()) {
                UIUtils.showError(this, "Please fill in all required fields."); return;
            }

            boolean success = groupDAO.createGroupWithSchedule(app.sessionUserId, t, s, m, d, tm);
            if (success) {
                UIUtils.showSuccess(this, "Group created successfully!\nTransaction committed (3 tables inserted).");
                app.navigateTo("DASHBOARD");
            } else {
                UIUtils.showError(this, "Failed to create group.");
            }
        });
        form.add(saveBtn);

        centerPanel.add(form);
        add(centerPanel, BorderLayout.CENTER);
    }
}
