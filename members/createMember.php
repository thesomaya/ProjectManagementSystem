<?php

include "../connection.php";

// Read JSON data from request body
$json_data = file_get_contents('php://input');

// Check if the request has a JSON content type header
if (empty($_SERVER['CONTENT_TYPE']) || $_SERVER['CONTENT_TYPE'] !== 'application/json') {
    error_log("Request does not have a JSON Content-Type header");
    echo json_encode(array("status" => "failed", "error" => "Invalid request format"));
    exit;
}

error_log("Received JSON data: " . $json_data);

$request_data = json_decode($json_data, true);
if ($request_data === null) {
    // JSON parsing failed
    error_log("Failed to parse JSON data: " . json_last_error_msg());
    echo json_encode(array("status" => "failed", "error" => "Failed to parse JSON data"));
    exit;
}

// Extract parameters from JSON data (assuming filterRequest is for basic validation)
$board_id = isset($request_data["board_id"]) ? trim($request_data["board_id"]) : null;
$user_id = isset($request_data["user_id"]) ? trim($request_data["user_id"]) : null;

error_log("Extracted parameters: board_id=$board_id, user_id=$user_id");

// Check if the combination of board_id and user_id already exists
$stmt_check = $con->prepare("SELECT COUNT(*) AS count FROM members WHERE board_id = ? AND user_id = ?
");
$stmt_check->execute(array($board_id, $user_id));
$result_check = $stmt_check->fetch(PDO::FETCH_ASSOC);

if ($result_check['count'] > 0) {
    // If the combination already exists, show a failure message
    echo json_encode(array("status" => "failed", "error" => "This member is already exist"));
    exit;
}

// If the combination does not exist, proceed with inserting the new record
$stmt_insert = $con->prepare("
    INSERT INTO `members` (`board_id`, `user_id`) 
    VALUES (?, ?)
");
$result_insert = $stmt_insert->execute(array($board_id, $user_id));

if ($result_insert) {
    $memberId = $con->lastInsertId();
    echo json_encode(array("status" => "success", "member_id" => $memberId));
} else {
    $error = $stmt_insert->errorInfo();
    error_log("SQL error: " . $error[2]);
    echo json_encode(array("status" => "failed", "error" => "Failed to insert data into database"));
}

?>
