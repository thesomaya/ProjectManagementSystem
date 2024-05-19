<?php

include "../connection.php";

try {

    // Prepare and execute the query to fetch boards
    $sql = "SELECT * FROM boards";
    $stmt = $con->query($sql);

    // Fetch all board data
    $data = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // Count the number of boards fetched
    $count = $stmt->rowCount();

    if ($count > 0) {
        // If boards were found, encode them as JSON and send success response
        echo json_encode(array("status" => "success", "data" => $data));
    } else {
        // If no boards were found, send failure response
        echo json_encode(array("status" => "fail", "message" => "No boards found."));
    }
} catch (PDOException $e) {
    // Handle database connection or query errors
    echo json_encode(array("status" => "error", "message" => $e->getMessage()));
} catch (Exception $e) {
    // Handle other unexpected errors
    echo json_encode(array("status" => "error", "message" => $e->getMessage()));
}
