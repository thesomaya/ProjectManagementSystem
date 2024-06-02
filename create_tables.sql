-- Create users table
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    mobile VARCHAR(20),
    image VARCHAR(255),
    password VARCHAR(255)
);

-- Create boards table
CREATE TABLE IF NOT EXISTS boards (
    board_id INT AUTO_INCREMENT PRIMARY KEY,
    created_by INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    image VARCHAR(255),
    document_id JSON,
    FOREIGN KEY (created_by) REFERENCES users(user_id)
);

-- Create tasks table
CREATE TABLE IF NOT EXISTS tasks (
    task_id INT AUTO_INCREMENT PRIMARY KEY,
    board_id INT,
    name VARCHAR(255) NOT NULL,
    created_by INT,
    FOREIGN KEY (board_id) REFERENCES boards(board_id)
);

-- Create cards table
CREATE TABLE IF NOT EXISTS cards (
    card_id INT AUTO_INCREMENT PRIMARY KEY,
    card_order INT,
    task_id INT,
    name VARCHAR(255) NOT NULL,
    created_by INT,
    member_id INT,
    due_date VARCHAR(255),
    due_time VARCHAR(255),
    label VARCHAR(255),
    document VARCHAR(255),
    FOREIGN KEY (task_id) REFERENCES tasks(task_id),
    FOREIGN KEY (created_by) REFERENCES users(user_id),
    FOREIGN KEY (member_id) REFERENCES users(user_id)
);
-- Create members table
-- Create members table
CREATE TABLE IF NOT EXISTS members (
    member_id INT AUTO_INCREMENT PRIMARY KEY,
    board_id INT,
    user_id INT,
    FOREIGN KEY (board_id) REFERENCES boards(board_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);



