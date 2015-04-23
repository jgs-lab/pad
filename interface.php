<?php
	//the host that is being connected to
	$host = "localhost";
	//the port being read
	$port = 7474;
	//create the socket
	$action = $_POST["action"];
	if($action == "false")
	{
		$sock = socket_create(AF_INET, SOCK_STREAM, SOL_TCP);
		if(socket_connect($sock, $host, $port))
		{
			$input = "";
			socket_recv($sock, $input, 33, MSG_WAITALL);
			echo json_encode($input);
			socket_close($sock);
		}else{
			echo json_encode(socket_strerror(socket_last_error($sock)));
			socket_close($sock);
		}
	}else
	{
		header('Content-Type: text/csv; charset=utf-8');
		header('Content-Disposition: attachment; filename=data.csv');
		$output = fopen('php://output', 'w');
		$stuff = json_decode($action);
		$vidNum = 0;
		foreach ($stuff as $key => $value) {
			$i = array("video" . $vidNum); //video counter
			fputcsv($output, $i); //adds the video title
			fputcsv($output, $value); // adds the  data
			$vidNum++; // increments to the next video.
		}
	}
?>