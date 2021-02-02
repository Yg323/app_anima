<?php
    if($_SERVER['REQUEST_METHOD']=='POST') {
        require('db_config.php');
        mysqli_query($con,'SET NAMES utf8');

        $email = $_POST['email'];
        
        $sql = "SELECT profile FROM user WHERE email='$email'";

        $res = mysqli_query($con, $sql);

        if ($res) {
            while ($row=mysqli_fetch_array($res)) {
                $pic=$row[0];
            }
            unlink("./$pic");
        }

        $sql = "UPDATE user SET profile=NULL WHERE email='$email'";

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