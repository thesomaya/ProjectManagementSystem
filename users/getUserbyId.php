<?php
include_once '../connection.php';

if(isset($_GET['user_id'])) {
    $user_id = $_GET['user_id'];

    $sql = "SELECT * FROM users WHERE user_id = :user_id";
    $stmt = $con->prepare($sql);
    $stmt->bindParam(':user_id', $user_id);
    $stmt->execute();

    $user_data = $stmt->fetch(PDO::FETCH_ASSOC);

    if ($user_data) {
        // Construct a JSON object with the user data
        $response = array("status" => "success", "data" => $user_data);
    } else {
        $response = array("status" => "error", "message" => "User not found");
    }

    // Encode the response as JSON and echo it
    echo json_encode($response);
    exit;
} else {
    echo json_encode(array("status" => "error", "message" => "User ID parameter not provided"));
    exit;
}
?>
