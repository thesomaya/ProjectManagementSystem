<?php

include '../connection.php';

$createdByString = $_POST['created_by'];
$createdBy = (int)$createdByString;
$name = filterRequest("name");
$image = $_POST['image'];

// Check if the name already exists in the database for the user
$stmt_check = $con->prepare("
    SELECT COUNT(*) AS count 
    FROM boards b
    JOIN members m ON b.board_id = m.board_id
    WHERE b.name = ? AND m.user_id = ?
");
$stmt_check->execute(array($name, $createdBy));
$result_check = $stmt_check->fetch(PDO::FETCH_ASSOC);

if ($result_check['count'] > 0) {
    // If the name already exists for the user, show an error message
    echo json_encode(array("status" => "failed", "error" => "Board name already exists for this user"));
    exit;
}

// Handle the image upload if provided
$completePath = null;
if (!empty($image)) {
    $path = 'upload/' . date('d-m-Y') . '-' . time() . '-' . rand(1000, 100000) . '.jpg';
    if (file_put_contents($path, base64_decode($image))) {
        $completePath = "http://192.168.1.143/myproject/boards/" . $path;
    } else {
        echo json_encode(array("status" => "failed", "error" => "Failed to upload image"));
        exit;
    }
}

// Insert the new board into the boards table
$sql = "INSERT INTO boards (created_by, name, image) VALUES (?, ?, ?)";
$stmt_insert_board = $con->prepare($sql);
$result_insert_board = $stmt_insert_board->execute(array($createdBy, $name, $completePath));

if (!$result_insert_board) {
    // If insertion into the boards table fails, return error
    $error = $stmt_insert_board->errorInfo();
    error_log("SQL error: " . $error[2]);
    echo json_encode(array("status" => "failed", "error" => "Failed to insert data into database"));
    exit;
}

// Get the inserted board_id
$board_id = $con->lastInsertId();

// Insert the board_id and created_by into the members table
$stmt_insert_member = $con->prepare("
    INSERT INTO members (board_id, user_id) 
    VALUES (?, ?)
");
$result_insert_member = $stmt_insert_member->execute(array($board_id, $createdBy));

if ($result_insert_member) {
    echo json_encode(array("status" => "success", "board_id" => $board_id));
} else {
    // If insertion into the members table fails, return error
    $error = $stmt_insert_member->errorInfo();
    error_log("SQL error: " . $error[2]);
    echo json_encode(array("status" => "failed", "error" => "Failed to insert data into members table"));
}

?>
