<?php
    if($_SERVER['REQUEST_METHOD']=='POST') {
        require('db_config.php');
        mysqli_query($con,'SET NAMES utf8');

        $email = $_POST['email'];
        $name = $_POST['name'];
        $petname = $_POST['petname'];
        $specie = $_POST['specie'];
        $sex = $_POST['sex'];
        $age = $_POST['age'];

        $sql = "UPDATE user SET name='$name', pet_name='$petname', pet_specie='$specie', pet_sex='$sex', pet_age='$age' WHERE email='$email'";

        if(mysqli_query($con, $sql)) {
            echo 'true';
        } else {
            echo mysqli_error($con);
        }

        mysqli_close($con);
    }
?>