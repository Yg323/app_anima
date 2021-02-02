<?php
    define('HOST', 'localhost');
    define('USER', 'anima');
    define('PASS', 'CSanima12@!');
    define('DB', 'anima');

    $con = mysqli_connect(HOST, USER, PASS, DB) or die('DB에 연결할 수 없습니다.');
?>