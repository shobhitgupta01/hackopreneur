<!doctype html>
<head>
</head>
<body>
  <?php
  $to='shwetanshu.rohatgi@gmail.com';
  $subject='Blood donor';
  $body='you have got a blood donor';
  $header='From: Blood Donation App <some@gmail.com>';
  if(mail($to,$subject,$body,$header))
  {
    echo 'Thanks for Donating Blood we will give you further details of blood seeker';
  }
  else
  {
    echo 'There was an error recording you details. Please try again.';
  }

  ?>
</body>  
</html>
