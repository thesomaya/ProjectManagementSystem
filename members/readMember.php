<?php

include "../connection.php";

// Check if board_id is provided in the request
if(isset($_GET['board_id'])) {
    $board_id = $_GET['board_id'];

    // Prepare the SQL query to fetch tasks for a specific board_id
    $sql = "SELECT m.member_id, m.user_id, u.name AS user_name
    FROM members m 
    JOIN users u ON m.user_id = u.user_id 
    WHERE m.board_id = :board_id";
    $stmt = $con->prepare($sql);
    $stmt->bindParam(':board_id', $board_id);
    $stmt->execute();

    // Fetch all members and their associated user details for the provided board_id
    $data = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // Count the number of members fetched
    $count = $stmt->rowCount();

    // Check if any members were found
    if ($count > 0) {
        // If members were found, send response
        echo json_encode(array("status" => "success", "data" => $data));
    } else {
        // If no members were found, send a failure response
        echo json_encode(array("status" => "fail", "message" => "No tasks found for the provided board_id"));
    }
} else {
    // If board_id is not provided in the request, send a failure response
    echo json_encode(array("status" => "fail", "message" => "board_id parameter is missing"));
}

?>
