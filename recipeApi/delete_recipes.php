<?php
$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    require 'db_connect.php';
    $db = new DB_CONNECT();
    $con = $db->con;

    // Получаем JSON тело запроса
    $input = file_get_contents("php://input");
    $data = json_decode($input, true);

    if (isset($data['ids']) && is_array($data['ids'])) {
        $ids = $data['ids'];
        $idList = implode(',', array_map('intval', $ids));

        $query = "DELETE FROM recipes WHERE id IN ($idList)";
        if ($con->query($query)) {
            $response['success'] = 1;
            $response['message'] = "Записи успешно удалены.";
        } else {
            $response['success'] = 0;
            $response['message'] = "Ошибка при удалении записей.";
        }
    } else {
        $response['success'] = 0;
        $response['message'] = "Неверные данные.";
    }
} else {
    $response['success'] = 0;
    $response['message'] = "Неправильный метод запроса.";
}

echo json_encode($response);
?>
