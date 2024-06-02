<?php

include "../connection.php";

if (isset($_GET['task_id']) && isset($_GET['card_id'])) {
    $task_id = $_GET['task_id'];
    $card_id = $_GET['card_id'];

    $sql_get_last_card_order_for_task = "SELECT MAX(card_order) AS max_card_order FROM cards WHERE task_id = :task_id ";
    $stmt = $con->prepare($sql_get_last_card_order_for_task);
    $stmt->bindParam(':task_id', $task_id, PDO::PARAM_INT);
    $stmt->execute();
    $max_card_order = $stmt->fetchColumn();
    $card_order = $max_card_order + 1;

    $sql = "UPDATE cards SET card_order = $card_order WHERE task_id = :task_id AND card_id = :card_id";
    $stmt = $con->prepare($sql);
    $stmt->bindParam(':task_id', $task_id);
    $stmt->bindParam(':card_id', $card_id);
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
    echo json_encode(array("status" => "fail", "message" => "task_id or/and card_id parameters are missing"));
}
