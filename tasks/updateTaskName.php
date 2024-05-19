<?php

include "../connection.php";

if (isset($_GET['task_id']) && isset($_GET['new_name'])) {
    $task_id = $_GET['task_id'];
    $new_name = $_GET['new_name'];

    $sql = "UPDATE tasks SET name = :new_name WHERE task_id = :task_id";
    $stmt = $con->prepare($sql);
    $stmt->bindParam(':task_id', $task_id);
    $stmt->bindParam(':new_name', $new_name);
    $stmt->execute();

    $rowCount = $stmt->rowCount();

    // If rows were affected, send a success response
    if ($rowCount > 0) {
        echo json_encode(array("status" => "success", "message" => "Task updated successfully for the task ID: $task_id"));
    } else {
        echo json_encode(array("status" => "fail", "message" => "Failed to update task for the task ID: $task_id"));
    }
} else {
    // If board_id is not provided in the request, send a failure response
    echo json_encode(array("status" => "fail", "message" => "task_id or/and new_name parameters are missing"));
}
