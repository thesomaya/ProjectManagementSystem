<?php

include "../connection.php";

$json_data = file_get_contents('php://input');

$request_data = json_decode($json_data, true);

$email = isset($request_data["email"]) ? trim($request_data["email"]) : null;
$password = isset($request_data["password"]) ? trim($request_data["password"]) : null;


$stmt = $con->prepare("SELECT * FROM users WHERE email=?");
$stmt->execute(array($email));

$user = $stmt->fetch(PDO::FETCH_ASSOC);

if ($user) {
    if (password_verify($password, $user['password'])) {
        echo json_encode(array("status" => "success", "user_id" => $user['user_id']));
    } else {
        echo json_encode(array("status" => "fail", "error" => "Incorrect password"));
    }
} else {
    echo json_encode(array("status" => "fail", "error" => "User not found"));
}

?>
