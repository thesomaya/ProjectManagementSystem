<?php

include '../connection.php';

$userIdString = $_POST['user_id'];
$userId = (int)$userIdString;

$image = $_POST['image'];

if (!empty($_POST['image'])) {
    $path = 'upload/' . date('d-m-Y') . '-' . time() . '-' . rand(1000, 100000) . '.jpg';
    if (file_put_contents($path, base64_decode($_POST['image']))) {
        $completePath = "http://192.168.1.143/myproject/users/" . $path;
        $sql = "UPDATE users SET image = '" . $completePath . "' WHERE user_id = '" . $userId . "'";
        $stmt = $con->prepare($sql);
        $stmt->execute();
        echo 'success';

    }
}
?>
