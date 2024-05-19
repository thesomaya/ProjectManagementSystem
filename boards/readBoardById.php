<?php

include "../connection.php";

if(isset($_GET['current_user_id'])) {
    $current_user_id = $_GET['current_user_id'];

    $sql = "SELECT boards.*, boards.name AS board_name, boards.image, users.name AS created_by
            FROM boards
            JOIN users ON boards.created_by = users.user_id
            JOIN members ON members.board_id = boards.board_id
            WHERE members.user_id = :current_user_id";
    $stmt = $con->prepare($sql);
    $stmt->bindParam(':current_user_id', $current_user_id);
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