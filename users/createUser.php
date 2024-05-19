<?php

include "../connection.php";
$json_data = file_get_contents('php://input');
$request_data = json_decode($json_data, true);
$name = isset($request_data["name"]) ? trim($request_data["name"]) : null;
$email = isset($request_data["email"]) ? trim($request_data["email"]) : null;
$password = isset($request_data["password"]) ? trim($request_data["password"]) : null;

// Hash the password
$hashedPassword = password_hash($password, PASSWORD_DEFAULT);

$stmt = $con->prepare("
INSERT INTO `users` (`name`, `email`, `password`) 
VALUES (?, ?, ?)
");

$result = $stmt->execute(array($name, $email, $hashedPassword));

if ($result) {
    $userId = $con->lastInsertId();
    echo json_encode(array("status" => "success", "user_id" => $userId));
} else {
    $error = $stmt->errorInfo();
    error_log("SQL error: " . $error[2]);
    echo json_encode(array("status" => "failed", "error" => "Failed to insert data into database"));
}

?>
