<?php
    if($_SERVER['REQUEST_METHOD']=='POST') {
        require('db_config.php');
        mysqli_query($con,'SET NAMES utf8');

        $email = $_POST['email'];
        $today = $_POST['today'];
        
        $sql = "SELECT food_type, kcal FROM food WHERE user = '$email' AND time = '$today'" ;

        $res = mysqli_query($con, $sql);

        $result = array();
        $result['success'] = 'false';
        $result['null'] ='false';
        $result['data'] = null;
        if ($res) {
            $result['success'] = 'true';
            foreach($res as $i){
                $result['data'][] = $i;
            }
            if ($result['data']==null){
                $result['null'] ='true';
            }
            echo json_encode($result, JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
        } else {
            echo mysqli_error($con);
        }

        mysqli_close($con);
    }
?>