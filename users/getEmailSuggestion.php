<?php
include_once '../connection.php';

if(isset($_GET['partial_email'])) {
    $partial_email = $_GET['partial_email'];

    // Prepare SQL statement to fetch email suggestions
    $sql = "SELECT email FROM users WHERE email LIKE :partial_email LIMIT 10";
    $stmt = $con->prepare($sql);
    $stmt->bindValue(':partial_email', '%' . $partial_email . '%'); // Bind the parameter with wildcards
    $stmt->execute();

    // Fetch email suggestions
    $emailSuggestions = array();
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        $emailSuggestions[] = $row['email'];
    }

    // Return email suggestions as JSON response
    echo json_encode(array("status" => "success", "data" => $emailSuggestions));
    exit; // Terminate script after sending JSON response
} else {
    // Return error message if 'partial_email' parameter is not set
    echo json_encode(array("status" => "error", "message" => "Partial email parameter not provided"));
    exit; // Terminate script after sending JSON response
}

?>
