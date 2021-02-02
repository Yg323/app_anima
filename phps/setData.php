<?php
    if($_SERVER['REQUEST_METHOD']=='POST') {
        require('db_config.php');
        mysqli_query($con,'SET NAMES utf8');

        $date = $_POST['date'];
        $waterCnt = $_POST['waterCnt'];
        $stepCnt = $_POST['stepCnt'];
        $tempValue = $_POST['tempValue'];
        $email = $_POST['email'];
    

        $water_sql = "INSERT INTO water (time, cnt_water, user) VALUES ('$date', '$waterCnt', '$email')";
        $step_sql = "INSERT INTO step (time, cnt_step, user) VALUES ('$date', '$stepCnt', '$email')";
        $temp_sql = "INSERT INTO temp (time, val_temp, user) VALUES ('$date', '$tempValue', '$email')";

        if(mysqli_query($con, $water_sql) && mysqli_query($con, $step_sql) && mysqli_query($con, $temp_sql)) {
            echo 'true';
        } else {
            echo mysqli_error($con);
        }

        mysqli_close($con);
    }
?>