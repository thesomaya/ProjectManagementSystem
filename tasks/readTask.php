<?php

include "../connection.php";

// Check if board_id is provided in the request
if(isset($_GET['board_id'])) {
    $board_id = $_GET['board_id'];

    // Prepare the SQL query to fetch tasks for a specific board_id
    $sql = "SELECT * , tasks.name AS task_name, users.name AS user_name
    FROM tasks
    JOIN users ON tasks.created_by = users.user_id
    WHERE board_id = :board_id";
    $stmt = $con->prepare($sql);
    $stmt->bindParam(':board_id', $board_id);
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
        echo json_encode(array("status" => "fail", "message" => "No tasks found for the provided board_id"));
    }
} else {
    // If board_id is not provided in the request, send a failure response
    echo json_encode(array("status" => "fail", "message" => "board_id parameter is missing"));
}

?>
