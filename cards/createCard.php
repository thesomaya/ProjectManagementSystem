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
$task_id = isset($request_data["task_id"]) ? trim($request_data["task_id"]) : null;
$name = isset($request_data["name"]) ? trim($request_data["name"]) : null;
$created_by = isset($request_data["created_by"]) ? trim($request_data["created_by"]) : null;
$member_id = isset($request_data["member_id"]) ? trim($request_data["member_id"]) : null;
$due_date = isset($request_data["due_date"]) ? trim($request_data["due_date"]) : null;
$status = isset($request_data["status"]) ? trim($request_data["status"]) : null;

error_log("Extracted parameters: task_id=$task_id, name=$name, created_by=$created_by, member_id=$member_id, due_date=$due_date, 
status=$status");

//$user_id = uniqid();

$stmt = $con->prepare("
INSERT INTO `cards` (`task_id`, `name`, `created_by`, `member_id` , `due_date` , `status`) 
VALUES (?, ?, ?, ?, ?, ?)
");
$result = $stmt->execute(array($task_id, $name, $created_by, $member_id, $due_date, $status));

if ($result) {
    $cardId = $con->lastInsertId();
    echo json_encode(array("status" => "success", "card_id" => $cardId));
} else {
    $error = $stmt->errorInfo();
    error_log("SQL error: " . $error[2]);
    echo json_encode(array("status" => "failed", "error" => "Failed to insert data into database"));
}

// Set Content-Type header
header('Content-Type: application/json');
?>
