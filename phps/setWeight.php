<?php
    if($_SERVER['REQUEST_METHOD']=='POST') {
        require('db_config.php');
        mysqli_query($con,'SET NAMES utf8');

        $email = $_POST['email'];
        $weight = $_POST['weight'];

        $sql = "UPDATE user SET pet_weight='$weight' WHERE email='$email'";

        $res = mysqli_query($con, $sql);

        if ($res) {
            echo 'true';
        } else {
            echo 'false';
            echo mysqli_error($con);
        }

        mysqli_close($con);
    }
?>