<?php
include_once '../connection.php';

if ($_SERVER['CONTENT_TYPE'] === 'application/json') {
    $json_data = file_get_contents('php://input');
    $request_data = json_decode($json_data, true);
} else {
    $request_data = $_POST; 
}

if(isset($request_data['user_id'])) {
    $user_id = (int)$request_data['user_id'];

    $sql = "SELECT * FROM users WHERE user_id = :user_id";
    $stmt = $con->prepare($sql);
    $stmt->bindParam(':user_id', $user_id);
    $stmt->execute();
    $user_data = $stmt->fetch(PDO::FETCH_ASSOC);

    if ($user_data) {
        if(isset($request_data['name'])) {
            $name = trim($request_data['name']);
            $sql = "UPDATE users SET name = :name WHERE user_id = :user_id";
            $stmt = $con->prepare($sql);
            $stmt->bindParam(':name', $name);
            $stmt->bindParam(':user_id', $user_id);
            $stmt->execute();
        }

        if(isset($request_data['email'])) {
            $email = trim($request_data['email']);
            $sql = "UPDATE users SET email = :email WHERE user_id = :user_id";
            $stmt = $con->prepare($sql);
            $stmt->bindParam(':email', $email);
            $stmt->bindParam(':user_id', $user_id);
            $stmt->execute();
        }

        if(isset($request_data['mobile'])) {
            $mobile = trim($request_data['mobile']);
            $sql = "UPDATE users SET mobile = :mobile WHERE user_id = :user_id";
            $stmt = $con->prepare($sql);
            $stmt->bindParam(':mobile', $mobile);
            $stmt->bindParam(':user_id', $user_id);
            $stmt->execute();
        }
        $response = array(
            "status" => "success",
            "message" => "User updated"
          );
        } 
      } else {
        $response = array(
          "status" => "error",
          "message" => "User ID parameter not provided"
        );
}

echo json_encode($response);
exit;
?>
