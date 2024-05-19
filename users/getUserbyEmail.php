<?php
include_once '../connection.php';

if(isset($_GET['email'])) {
    $email = $_GET['email'];

    $sql = "SELECT user_id FROM users WHERE email = :email ";
    $stmt = $con->prepare($sql);
    $stmt->bindParam(':email', $email);
    $stmt->execute();

    $row = $stmt->fetch(PDO::FETCH_ASSOC);
    $user_id = $row ? $row['user_id'] : null;

    if ($user_id !== null) {
        // Construct a JSON object with the user_id
        $response = array("status" => "success", "data" => array("user_id" => $user_id));
    } else {
        $response = array("status" => "error", "message" => "User not found");
    }

    // Encode the response as JSON and echo it
    echo json_encode($response);
    exit;
} else {
    echo json_encode(array("status" => "error", "message" => "Email parameter not provided"));
    exit;
}
?>
