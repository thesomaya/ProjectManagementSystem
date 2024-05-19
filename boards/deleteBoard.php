<?php

include "../connection.php";


$sql = "DELETE FROM `boards` WHERE `board_id` = ?";
$stmt = $this->pdo->prepare($sql);
$stmt->execute(array($boardId));

?>