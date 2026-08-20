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

```
Study Group Finder/
├── src/
│   ├── Launcher.java              # Entry point
│   ├── backend/                    # Database layer (DAO classes)
│   │   ├── DBConnection.java      # MySQL connection + auto-init triggers/procedures
│   │   ├── UserDAO.java           # Login, Register, User queries
│   │   ├── StudyGroupDAO.java     # Group CRUD, stats, stored procedure calls
│   │   ├── GroupMemberDAO.java    # Join, Leave, Member listing
│   │   └── ScheduleDAO.java      # Meeting schedule queries
│   └── frontend/                   # UI layer (Swing panels)
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

### JDBC কী?

**JDBC (Java Database Connectivity)** হলো Java এর একটা API যেটা দিয়ে Java প্রোগ্রাম থেকে MySQL, PostgreSQL ইত্যাদি ডেটাবেসে কানেক্ট করে SQL query চালানো যায়।

এই প্রজেক্টে আমরা JDBC ব্যবহার করে:
- `Connection` → ডেটাবেসে কানেক্ট হওয়া
- `PreparedStatement` → SQL query চালানো (safe from SQL injection)
- `CallableStatement` → Stored Procedure কল করা
- `ResultSet` → Query এর রেজাল্ট পড়া

JDBC ব্যবহার করতে হলে একটা **Driver** লাগে। MySQL এর জন্য সেটা হলো `mysql-connector-j-8.0.33.jar` ফাইলটি।

### DAO কী?

**DAO (Data Access Object)** হলো একটা ডিজাইন প্যাটার্ন। এর মানে হলো ডেটাবেসের সাথে কথা বলার সব কোড আলাদা ক্লাসে রাখা। এতে কোড পরিষ্কার থাকে এবং UI কোড আর ডেটাবেস কোড মিশে যায় না।

এই প্রজেক্টে আমাদের DAO ক্লাসগুলো হলো:

| DAO Class | কী করে |
|-----------|--------|
| `UserDAO.java` | ইউজার লগিন, রেজিস্ট্রেশন |
| `StudyGroupDAO.java` | গ্রুপ তৈরি, ডিলিট, সার্চ, স্ট্যাটিস্টিকস |
| `GroupMemberDAO.java` | গ্রুপে জয়েন, লিভ, মেম্বার লিস্ট |
| `ScheduleDAO.java` | মিটিং শিডিউল |

### Stored Procedure কী?

Stored Procedure হলো ডেটাবেসের ভেতরে সেভ করা একটা SQL ফাংশন। Java থেকে শুধু কল করলেই কাজ হয়ে যায়। এই প্রজেক্টে `GetGroupsBySubject()` নামে একটা Stored Procedure আছে যেটা সাবজেক্ট অনুযায়ী গ্রুপ ফিল্টার করে।

### Trigger কী?

Trigger হলো ডেটাবেসের একটা অটোমেটিক অ্যাকশন যেটা কোনো INSERT/DELETE/UPDATE হলে নিজে থেকে চলে। এই প্রজেক্টে ২টা Trigger আছে:
- `trg_after_member_insert` → কেউ জয়েন করলে মেম্বার সংখ্যা চেক করে, ম্যাক্সে পৌঁছলে স্ট্যাটাস "FULL" করে দেয়
- `trg_after_member_delete` → কেউ লিভ করলে স্ট্যাটাস আবার "OPEN" করে দেয়

---

## 🗄️ Database Schema

```
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
2. Download XAMPP for Windows
3. Install it (default path: `C:\xampp`)
4. Open **XAMPP Control Panel**
5. Click **Start** next to **MySQL**
6. MySQL should now be running on `localhost:3306`

### Step 2: Create the Database

1. Open your browser and go to **[http://localhost/phpmyadmin](http://localhost/phpmyadmin)**
2. Click **"New"** on the left sidebar
3. Type database name: `study_group_db`
4. Click **"Create"**
5. Click the **"Import"** tab at the top
6. Click **"Choose File"** and select the `database.sql` file from this project
7. Click **"Go"** at the bottom
8. You should see 4 tables created: `users`, `study_groups`, `schedules`, `group_members`

### Step 3: Download MySQL Connector JAR (JDBC Driver)

This is the bridge between Java and MySQL. Without it, Java cannot talk to MySQL.

1. Go to [https://dev.mysql.com/downloads/connector/j/](https://dev.mysql.com/downloads/connector/j/)
2. Select **"Platform Independent"**
3. Download the **ZIP** file
4. Extract it — you will find a file named `mysql-connector-j-8.x.x.jar`
5. Copy this `.jar` file into the `lib/` folder of this project

> **Note:** This project already includes the JAR file in the `lib/` folder. If you already have it, skip this step.

### Step 4: Add the JAR to IntelliJ IDEA

1. Open the project in **IntelliJ IDEA**
2. Go to **File → Project Structure** (or press `Ctrl + Alt + Shift + S`)
3. In the left sidebar, click **"Libraries"**
4. Click the **"+"** button at the top
5. Select **"Java"**
6. Navigate to `lib/mysql-connector-j-8.0.33.jar` inside the project folder
7. Click **"OK"**
8. Click **"Apply"** → **"OK"**

Now IntelliJ knows how to use the MySQL JDBC driver.

### Step 5: Run the Application

**Option A: From IntelliJ**
1. Open `src/Launcher.java`
2. Right-click → **"Run Launcher.main()"**

**Option B: From Terminal**
```bash
cd "Study Group Finder"

# Compile
javac -cp ".;lib/mysql-connector-j-8.0.33.jar" -d out src/backend/*.java src/frontend/*.java src/Launcher.java

# Run
java -cp "out;lib/mysql-connector-j-8.0.33.jar" Launcher
```

### Step 6: Login

Use any demo account:

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
| `No suitable driver found` | JAR file is not in classpath. Follow Step 4 again. |
| `Communications link failure` | XAMPP MySQL is not running. Start it from XAMPP Control Panel. |
| `Unknown database 'study_group_db'` | You haven't created the database yet. Follow Step 2. |
| `Access denied for user 'root'` | XAMPP default is root with no password. Check `DBConnection.java`. |
| Blank screen on launch | Make sure you're running `Launcher.java`, not `MainApp.java`. |

---

## 🛠️ Tech Stack

- **Language:** Java
- **UI Framework:** Java Swing
- **Database:** MySQL 8.0 (via XAMPP)
- **JDBC Driver:** MySQL Connector/J 8.0.33
- **IDE:** IntelliJ IDEA
- **Design:** Custom dark theme with Catppuccin-inspired colors

---

## 👤 Author

**Dip Karmokar**

- GitHub: [@dipkarmokar21](https://github.com/dipkarmokar21)
- LinkedIn: [dipkarmokar](https://www.linkedin.com/in/dipkarmokar/)
- Website: [nitchat.com](https://nitchat.com)

---

## 📄 License

This project is open source and available for educational purposes.
#   S t u d y - G r o u p - F i n d e r - j a v a - e d i t i o n  
 