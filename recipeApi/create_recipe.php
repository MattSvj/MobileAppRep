<?php
$response = array();

if (isset($_GET['title']) && isset($_GET['description']) && isset($_GET['ingredients'])) {
    require 'db_connect.php';

    $db = new DB_CONNECT();
    $con = $db->con;

    $title = $_GET['title'];
    $description = $_GET['description'];
    $ingredients = $_GET['ingredients'];

    $result = $con->query(query: "INSERT INTO recipes (title, description, ingredients) VALUES ('$title', '$description', '$ingredients')");

    if ($result) {
        $response["success"] = 1;
        $response["message"] = "Recipe successfully created.";
    } else {
        $response["success"] = 0;
        $response["message"] = "An error occurred.";
    }
} else {
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
}

echo json_encode(value: $response);
?>
