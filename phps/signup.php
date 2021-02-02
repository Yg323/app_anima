<?php
    if($_SERVER['REQUEST_METHOD']=='POST') {
        require('db_config.php');
        mysqli_query($con,'SET NAMES utf8');

        $email = $_POST['email'];
        $pswd = $_POST['pswd'];
        $name = $_POST['name'];
        $petname = $_POST['petname'];
        $specie = $_POST['specie'];
        $sex = $_POST['sex'];
        $age = $_POST['age'];

        $sql = "INSERT INTO user (email, pswd, name, pet_name, pet_specie, pet_age, pet_sex) VALUES ('$email', '$pswd', '$name', '$petname', '$specie', '$age', '$sex')";

        if(mysqli_query($con, $sql)) {
            echo 'true';
        } else {
            echo mysqli_error($con);
        }

        mysqli_close($con);
    }
?>