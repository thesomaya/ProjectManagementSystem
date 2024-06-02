<?php

include "../connection.php";

if (isset($_GET['card_id'])) {
    $card_id = $_GET['card_id'];

    $sql = "DELETE FROM cards WHERE card_id = :card_id";
    $stmt = $con->prepare($sql);
    $stmt->bindParam(':card_id', $card_id);
    $stmt->execute();

    // Check if any rows were affected
    $rowCount = $stmt->rowCount();

    // If rows were affected, send a success response
    if ($rowCount > 0) {
        echo json_encode(array("status" => "success", "message" => "Card deleted successfully for the card ID: $card_id"));
    } else {
        echo json_encode(array("status" => "fail", "message" => "Failed to delete card for the card ID: $card_id"));
    }
} else {
    // If task_id is not provided in the request, send a failure response
    echo json_encode(array("status" => "fail", "message" => "card_id parameter is missing"));
}