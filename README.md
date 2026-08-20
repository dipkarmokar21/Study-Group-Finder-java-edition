# 📚 Study Group Finder

A desktop application built with **Java Swing** and **MySQL** that helps university students create, discover, and join study groups. This project demonstrates key database concepts including Stored Procedures, Triggers, Transactions, and relationship mapping (1-to-1, 1-to-Many, Many-to-Many).

---

## ✨ Features

- **User Authentication** — Register and login with email validation
- **Create Study Groups** — Set title, subject, max members, and meeting schedule
- **Browse & Filter Groups** — Filter by subject using a MySQL Stored Procedure
- **Join / Leave Groups** — MySQL Triggers automatically update group status (OPEN/FULL)
- **View Members** — Group owners can see who joined their group
- **Delete Groups** — Owners can delete groups (CASCADE deletes related data)
- **Dashboard** — Stats cards, created groups, and joined groups at a glance
- **About Section** — Developer info with clickable social links
- **Dark Theme UI** — Premium dark mode design throughout the app

---

## 🏗️ Project Structure

```text
Study Group Finder/
├── src/
│   ├── Launcher.java              # Entry point
│   ├── backend/                   # Database layer (DAO classes)
│   │   ├── DBConnection.java      # MySQL connection + auto-init triggers/procedures
│   │   ├── UserDAO.java           # Login, Register, User queries
│   │   ├── StudyGroupDAO.java     # Group CRUD, stats, stored procedure calls
│   │   ├── GroupMemberDAO.java    # Join, Leave, Member listing
│   │   └── ScheduleDAO.java       # Meeting schedule queries
│   └── frontend/                  # UI layer (Swing panels)
│       ├── UIUtils.java           # Shared colors, fonts, components
│       ├── MainApp.java           # Navigation manager (CardLayout)
│       ├── LoginPanel.java        # Login screen
│       ├── RegisterPanel.java     # Registration screen
│       ├── DashboardPanel.java    # Dashboard with stat cards
│       ├── CreateGroupPanel.java  # Group creation form
│       ├── GroupsPanel.java       # Browse/filter groups
│       └── AboutPanel.java        # Developer info
├── lib/
│   └── mysql-connector-j-8.0.33.jar   # JDBC Driver (required)
├── database.sql                    # Full database schema + sample data
├── .gitignore
└── README.md
```

---

## 📖 Key Concepts Explained

### What is JDBC?
**JDBC (Java Database Connectivity)** is a Java API used to connect and execute queries with the database (MySQL, PostgreSQL, etc.).

In this project, JDBC is used for:
- `Connection` → Connecting to the database
- `PreparedStatement` → Executing SQL queries securely (prevents SQL injection)
- `CallableStatement` → Calling Stored Procedures
- `ResultSet` → Reading the results of a query

To use JDBC, a **Driver** is required. For MySQL, we use the `mysql-connector-j-8.0.33.jar` file.

### What is DAO?
**DAO (Data Access Object)** is a design pattern used to separate data access logic from the rest of the application. It keeps the UI code clean and modular.

DAO classes in this project:
- `UserDAO.java`: Handles user login and registration
- `StudyGroupDAO.java`: Handles group creation, deletion, search, and dashboard statistics
- `GroupMemberDAO.java`: Manages users joining and leaving groups, and fetching member lists
- `ScheduleDAO.java`: Manages meeting schedules

### What is a Stored Procedure?
A Stored Procedure is a prepared SQL code that you can save and reuse. In this project, `GetGroupsBySubject()` is a Stored Procedure that filters groups based on the subject selected in the UI.

### What is a Trigger?
A Trigger is a set of SQL instructions that automatically execute in response to certain events on a particular table (INSERT, UPDATE, DELETE).
This project uses 2 Triggers:
- `trg_after_member_insert` → Automatically updates the group status to "FULL" when the maximum member limit is reached.
- `trg_after_member_delete` → Automatically resets the group status to "OPEN" when a member leaves.

---

## 🗄️ Database Schema

```text
users ──────────────< study_groups ──────────────< group_members >────────────── users
(1)          (1:N)        (N)            (N:M)          (N)          (N:M)        (1)
                            │
                            │ (1:1)
                            ▼
                        schedules
```

| Table | Purpose |
|-------|---------|
| `users` | Stores user accounts (PK, Auto Increment, Unique email) |
| `study_groups` | Stores groups (FK → users, Check/Enum for status) |
| `schedules` | Meeting times (1-to-1 with study_groups via Unique FK) |
| `group_members` | Junction table (Composite PK, Many-to-Many) |

---

## 🔧 DBMS Concepts Demonstrated

| Concept | Implementation |
|---------|---------------|
| **Primary Key + Auto Increment** | All tables |
| **Foreign Key** | `study_groups.owner_id → users.user_id` |
| **ON DELETE CASCADE** | Deleting a group removes its schedule and members |
| **Unique Constraint** | `users.email`, `schedules.group_id` |
| **NOT NULL** | All required fields |
| **CURRENT_TIMESTAMP** | `created_at`, `joined_at` |
| **1-to-1 Relationship** | `study_groups ↔ schedules` |
| **1-to-Many Relationship** | `users → study_groups` (owner) |
| **Many-to-Many Relationship** | `users ↔ study_groups` via `group_members` |
| **Stored Procedure** | `GetGroupsBySubject()` for filtered queries |
| **Triggers** | `trg_after_member_insert` / `trg_after_member_delete` |
| **Transaction** | Group creation inserts into 3 tables atomically |

---

## 🚀 Full Setup Guide (Step by Step)

### Step 1: Install XAMPP

1. Go to [https://www.apachefriends.org/download.html](https://www.apachefriends.org/download.html)
2. Download XAMPP for Windows and install it.
3. Open **XAMPP Control Panel**.
4. Click **Start** next to **MySQL**.

### Step 2: Create the Database

1. Open your browser and navigate to **[http://localhost/phpmyadmin](http://localhost/phpmyadmin)**
2. Click **"New"** on the left sidebar to create a database.
3. Name the database `study_group_db` and click **"Create"**.
4. Click the **"Import"** tab at the top.
5. Click **"Choose File"** and select the `database.sql` file from this project repository.
6. Click **"Go"** at the bottom.

### Step 3: Add the MySQL Connector (JDBC Driver)

The `mysql-connector-j-8.0.33.jar` file is already included in the `lib/` folder of this repository.

1. Open the project in **IntelliJ IDEA**.
2. Go to **File → Project Structure** (or press `Ctrl + Alt + Shift + S`).
3. In the left sidebar, click **"Libraries"**.
4. Click the **"+"** button at the top and select **"Java"**.
5. Navigate to `lib/mysql-connector-j-8.0.33.jar` inside the project folder.
6. Click **"OK"**, then **"Apply"** and **"OK"**.

### Step 4: Run the Application

You can run the application directly from IntelliJ by running the `Launcher.java` file, or via the terminal:

```bash
cd "Study Group Finder"

# Compile the source code
javac -cp ".;lib/mysql-connector-j-8.0.33.jar" -d out src/backend/*.java src/frontend/*.java src/Launcher.java

# Run the application
java -cp "out;lib/mysql-connector-j-8.0.33.jar" Launcher
```

### Step 5: Login with Demo Accounts

Use any of the following demo accounts to explore the app:

| Name | Email | Password |
|------|-------|----------|
| Alex Rivera | alex@university.edu | password123 |
| Sarah Chen | sarah@university.edu | password123 |
| David Kim | david@university.edu | password123 |
| Emily Watson | emily@university.edu | password123 |

---

## ❓ Troubleshooting

| Problem | Solution |
|---------|----------|
| `No suitable driver found` | JAR file is not in classpath. Ensure you completed Step 3. |
| `Communications link failure` | XAMPP MySQL is not running. Start it from the XAMPP Control Panel. |
| `Unknown database 'study_group_db'` | You haven't imported the database yet. Follow Step 2. |
| Blank screen on launch | Make sure you're running `Launcher.java`, not `MainApp.java`. |

---

## 🛠️ Tech Stack

- **Language:** Java
- **UI Framework:** Java Swing
- **Database:** MySQL 8.0 (via XAMPP)
- **JDBC Driver:** MySQL Connector/J 8.0.33
- **IDE:** IntelliJ IDEA

---

## 👤 Author

**Dip Karmokar**

- GitHub: [@dipkarmokar21](https://github.com/dipkarmokar21)
- LinkedIn: [dipkarmokar](https://www.linkedin.com/in/dipkarmokar/)
- Website: [nitchat.com](https://nitchat.com)

---

## 📄 License

This project is open source and available for educational purposes.