<?php

include "../connection.php";

if (isset($_GET['task_id'])) {
    $task_id = $_GET['task_id'];

    // Delete the card(s) with the specified task_id
    $sqlDeleteCard = "DELETE FROM cards WHERE task_id = :task_id";
    $stmtDeleteCard = $con->prepare($sqlDeleteCard);
    $stmtDeleteCard->bindParam(':task_id', $task_id);
    $stmtDeleteCard->execute();

    // Check if any rows were affected
    $rowCount = $stmtDeleteCard->rowCount();

    // If rows were affected, send a success response
    if ($rowCount > 0) {
        echo json_encode(array("status" => "success", "message" => "Card(s) deleted successfully for the task ID: $task_id"));
    } else {
        echo json_encode(array("status" => "fail", "message" => "Failed to delete cards for the task ID: $task_id"));
    }

    // Delete task with the specified task_id
    $sqlDeleteTask = "DELETE FROM tasks WHERE task_id = :task_id";
    $stmtDeleteTask  = $con->prepare($sqlDeleteTask);
    $stmtDeleteTask->bindParam(':task_id', $task_id);
    $stmtDeleteTask->execute();

    // Check if any rows were affected
    $rowCount = $stmtDeleteTask->rowCount();

    // If rows were affected, send a success response
    if ($rowCount > 0) {
        echo json_encode(array("status" => "success", "message" => "Task deleted successfully for the task ID: $task_id"));
    } else {
        echo json_encode(array("status" => "fail", "message" => "Failed to delete task for the task ID: $task_id"));
    }
} else {
    // If task_id is not provided in the request, send a failure response
    echo json_encode(array("status" => "fail", "message" => "task_id parameter is missing"));
}