<?php
    if($_SERVER['REQUEST_METHOD']=='POST') {
        require('db_config.php');
        mysqli_query($con,'SET NAMES utf8');

        $email = $_POST['email'];

        $sql = "SELECT * FROM user WHERE email='$email'";

        $res = mysqli_query($con, $sql);

        $cnt = mysqli_num_rows($res);

        if ($res) {
            if ($cnt >= 1) {
                echo 'false';
            } else {
                echo 'true';
            }
        } else {
            echo mysqli_error($con);
        }

        mysqli_close($con);
    }
?>