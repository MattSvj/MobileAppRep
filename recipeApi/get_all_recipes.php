<?php
$response = array();

require 'db_connect.php';

$db = new DB_CONNECT();
$con = $db->con;

$result = $con->query("SELECT * FROM recipes");

if ($result) {
    $response["recipes"] = array();

    while ($row = $result->fetch_assoc()) {
        $recipe = array();
        $recipe["id"] = $row["id"];
        $recipe["title"] = $row["title"];
        $recipe["description"] = $row["description"];
        $recipe["ingredients"] = $row["ingredients"];
    
        array_push($response["recipes"], $recipe);
    }
    $response["success"] = 1;
} else {
    $response["success"] = 0;
    $response["message"] = "No recipes found";
}

echo json_encode($response);
?>