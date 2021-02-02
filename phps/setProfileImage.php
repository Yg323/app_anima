<?php
    if($_SERVER['REQUEST_METHOD']=='POST') {
        require('db_config.php');
        mysqli_query($con,'SET NAMES utf8');

        $email = $_POST['email'];
        $file = $_FILES['image'];

        $sql = "SELECT profile FROM user where email = '$email'";

        $res = mysqli_query($con, $sql);

        if ($res) {
            while ($row=mysqli_fetch_array($res)) {
                $pastfile = $row[0];
            }
        }
        unlink("./$pastfile");

        $srcName = $file['name'];
        $tmpName = $file['tmp_name'];

        $dstName = "profileImage/".$email."_".$srcName;
        $result=move_uploaded_file($tmpName, $dstName);

        $sql = "UPDATE user SET profile='$dstName' WHERE email='$email'";

        $res = mysqli_query($con, $sql);

        $result = array();

        if ($res) {
            $sql = "SELECT profile FROM user where email = '$email'";
            $res = mysqli_query($con, $sql);
            $result = array();
            if ($res) {
                while ($row=mysqli_fetch_array($res)) {
                    $result['profile']=$row['profile'];
                }
                echo json_encode($result, JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
            } else {
                echo mysqli_error($con);
            }
        } else {
            echo mysqli_error($con);
        }

        mysqli_close($con);
    }
?>