<?php

  $con = mysqli_connect("localhost","root","test","mappractice");
  
  $query = "select * from marker";
  
  $result = mysqli_query($con, $query);
  
  while($rs = mysqli_fetch_assoc($result)){
  
    $arrRows[] = $rs;

  }
  echo json_encode($arrRows);
?>