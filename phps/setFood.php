<?php
    if($_SERVER['REQUEST_METHOD']=='POST') {
        require('db_config.php');
        mysqli_query($con,'SET NAMES utf8');

        $foodType = $_POST['foodType'];
        $kcal = $_POST['kcal'];
        $email = $_POST['email'];
        $today = $_POST['today'];
           

        $food_sql = "INSERT INTO food (food_type, kcal, user, time) VALUES ('$foodType', '$kcal', '$email', '$today')";

        if(mysqli_query($con, $food_sql)) {
            echo 'true';
        } else {
            echo mysqli_error($con);
        }

        mysqli_close($con);
    }
?>