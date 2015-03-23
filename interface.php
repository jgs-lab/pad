<?php
	//the host that is being connected to
	$host = "localhost";
	//the port being read
	$port = 7474;

	//create the socket
	$sock = socket_create(AF_INET, SOCK_STREAM, SOL_TCP);
	if(socket_connect($sock, $host, $port))
	{
		$input = "";
		socket_recv($sock, $input, 32, MSG_WAITALL);
		echo json_encode($input);
		socket_close($sock);
	}else{

		echo json_encode("Unable to connect to socket");
		socket_close($sock);
	}
?>