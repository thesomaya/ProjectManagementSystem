<?php

include "../connection.php";

if (isset($_GET['task_id'])) {
    $task_id = $_GET['task_id'];

    $sql = "UPDATE cards SET card_order = 0 WHERE task_id = :task_id";
    $stmt = $con->prepare($sql);
    $stmt->bindParam(':task_id', $task_id);
    $stmt->execute();

    $rowCount = $stmt->rowCount();

    // If rows were affected, send a success response
    if ($rowCount > 0) {
        echo json_encode(array("status" => "success", "message" => "Card Order updated successfully for the task ID: $task_id"));
    } else {
        echo json_encode(array("status" => "fail", "message" => "Failed to update Card Order for the task ID: $task_id"));
    }
} else {
    // If board_id is not provided in the request, send a failure response
    echo json_encode(array("status" => "fail", "message" => "task_id or/and card_order parameters are missing"));
}
