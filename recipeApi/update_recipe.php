<?php
$response = array();

if (isset($_GET['id']) && isset($_GET['title']) && isset($_GET['description']) && isset($_GET['ingredients'])) {
    require 'db_connect.php';

    $db = new DB_CONNECT();
    $con = $db->con;

    $id = $_GET['id'];
    $title = $_GET['title'];
    $description = $_GET['description'];
    $ingredients = $_GET['ingredients'];

    // Обновление записи с учетом переданного ID
    $result = $con->query("UPDATE recipes SET title = '$title', description = '$description', ingredients = '$ingredients' WHERE id = '$id'");

    if ($result) {
        $response["success"] = 1;
        $response["message"] = "Recipe successfully updated.";
    } else {
        $response["success"] = 0;
        $response["message"] = "No changes made.";
    }
} else {
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
}

echo json_encode($response);
?>
