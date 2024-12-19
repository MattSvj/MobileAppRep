<?php
$response = array();

if (isset($_GET['id'])) {
    require 'db_connect.php';

    $db = new DB_CONNECT();
    $con = $db->con;

    $id = $_GET['id'];
    $result = $con->query("DELETE FROM recipes WHERE id = $id");

    if ($result) {
        $response["success"] = 1;
        $response["message"] = "Recipe successfully deleted.";
    } else {
        $response["success"] = 0;
        $response["message"] = "No recipe found.";
    }
} else {
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
}

echo json_encode($response);
?>