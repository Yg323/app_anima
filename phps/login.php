<?php
    if($_SERVER['REQUEST_METHOD']=='POST') {
        require('db_config.php');
        mysqli_query($con,'SET NAMES utf8');

        $email = $_POST['email'];
        $pswd = $_POST['pswd'];

        $sql = "SELECT * FROM user WHERE email = '$email' AND pswd = '$pswd'";

        $res = mysqli_query($con, $sql);

        $result = array();
        $result['success'] = 'false';
        if ($res) {
            $result['success'] = 'true';
            while($row = mysqli_fetch_array($res)){
                $result['email']=$row['email'];
                $result['pswd']=$row['pswd'];
                $result['name']=$row['name'];
                $result['petname']=$row['pet_name'];
                $result['specie']=$row['pet_specie'];
                $result['sex']=$row['pet_sex'];
                $result['age']=$row['pet_age'];
                $result['weight']=$row['pet_weight'];
                $result['profile']=$row['profile'];
            }
            echo json_encode($result, JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
        } else {
            echo mysqli_error($con);
        }
        
        mysqli_close($con);
    }
?>