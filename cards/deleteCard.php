<?php

include "../connection.php";


$sql = "DELETE FROM `cards` WHERE `task_id` = ?";
$stmt = $this->pdo->prepare($sql);
$stmt->execute(array($taskId));

?>