<?php
    if($_SERVER['REQUEST_METHOD']=='POST') {
        require('db_config.php');
        mysqli_query($con,'SET NAMES utf8');

        $email = $_POST['email'];
        $pswd = $_POST['pswd'];

        $sql = "UPDATE user SET pswd='$pswd' WHERE email='$email'";

        $result = mysqli_query($con, $sql);

        if ($result) {
            echo 'true';
        } else {
            echo 'false';
            echo mysqli_error($con);
        }
        
        mysqli_close($con);
    }
?>