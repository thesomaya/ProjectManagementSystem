<?php

include "../connection.php";


// Check if board_id is provided in the request
if(isset($_GET['task_id'])) {
    $task_id = $_GET['task_id'];

    // Prepare the SQL query to fetch tasks for a specific board_id
    $sql = "SELECT cards.*, users.name AS user_name
            FROM cards 
            LEFT JOIN users ON cards.member_id = users.user_id 
            WHERE task_id = :task_id ORDER BY `card_order`";
    $stmt = $con->prepare($sql);
    $stmt->bindParam(':task_id', $task_id);
    $stmt->execute();

    // Fetch all tasks associated with the board_id
    $data = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // Count the number of tasks fetched
    $count = $stmt->rowCount();

    // Check if any tasks were found
    if ($count > 0) {
        // If tasks were found, encode them as JSON and send response
        echo json_encode(array("status" => "success", "data" => $data));
    } else {
        // If no tasks were found, send a failure response
        echo json_encode(array("status" => "fail", "message" => "No cards found for the provided task_id"));
    }
} else {
    // If board_id is not provided in the request, send a failure response
    echo json_encode(array("status" => "fail", "message" => "task_id parameter is missing"));
}

?>
