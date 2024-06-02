<?php

include "../connection.php";

if (
    isset($_POST['card_id']) && isset($_POST['name']) && isset($_POST['member_id']) 
    && isset($_POST['due_date']) && isset($_POST['due_time']) && isset($_POST['label'])
    && isset($_POST['document']) // Ensure document parameter is received
) {
    $card_id = $_POST['card_id'];
    $name = $_POST['name'];
    $member_id = $_POST['member_id'];
    $due_date = $_POST['due_date'];
    $due_time = $_POST['due_time'];
    $label = $_POST['label'];
    $documentBase64 = $_POST['document']; // The document is received as a base64-encoded string

    // Decode the base64 string to get the file content
    $documentContent = base64_decode($documentBase64);

    // Generate a unique filename
    $filename = 'upload/' . date('d-m-Y') . '-' . time() . '-' . rand(1000, 100000) . '.pdf';

    // Save the file locally
    if (file_put_contents($filename, $documentContent)) {
        // File saved successfully, proceed with database update

        // Provide the complete path to the uploaded document
        $completePath = "http://192.168.1.143/myproject/cards/" . $filename;

        // Update the card in the database with the complete path
        $sql = "UPDATE cards SET 
            name = :name,
            member_id = :member_id,
            due_date = :due_date,
            due_time = :due_time,
            label = :label,
            document = :document
            WHERE card_id = :card_id";

        $stmt = $con->prepare($sql);

        $stmt->bindParam(':card_id', $card_id);
        $stmt->bindParam(':name', $name);
        $stmt->bindParam(':member_id', $member_id);
        $stmt->bindParam(':due_date', $due_date);
        $stmt->bindParam(':due_time', $due_time);
        $stmt->bindParam(':label', $label);
        $stmt->bindParam(':document', $completePath);

        if ($stmt->execute()) {
            $rowCount = $stmt->rowCount();
            if ($rowCount > 0) {
                echo json_encode(array("status" => "success", "message" => "Card updated successfully for the card ID: $card_id", "document_path" => $completePath));
            } else {
                echo json_encode(array("status" => "fail", "message" => "No changes were made to the card for the card ID: $card_id"));
            }
        } else {
            echo json_encode(array("status" => "fail", "message" => "Failed to update Card for the card ID: $card_id"));
        }
    } else {
        echo json_encode(array("status" => "failed", "error" => "Failed to save document"));
    }
} else {
    echo json_encode(array("status" => "fail", "message" => "Parameters are missing or file not uploaded."));
}

?>
