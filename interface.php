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
		socket_recv($sock, $input, 64, MSG_WAITALL);
		echo $input;
		socket_close($sock);
	}
?>