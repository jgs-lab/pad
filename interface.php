<?php
	//$str = "This is just a test.";
	//echo json_encode($str);
	//the host that is being connected to
	$host = "localhost";
	//the port being read
	$port = 7474;

	//set_time_limit(2); 
	//create the socket
	$sock = socket_create(AF_INET, SOCK_STREAM, 0);
	socket_connect($sock, $host, $port);
	$input = socket_read($sock, 1024);
	echo $input;
	socket_close($sock);
?>