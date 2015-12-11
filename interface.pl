#!c:\Perl64\bin\perl.exe
use warnings;
use strict;
use Socket;
use CGI qw(:standard);
use JSON;
use Data::Dumper; #for debugging. 

my $json; #for holding data object
my $time = $ARGV[0];
my $name = $ARGV[1];

# initialize host and port
my $host = 'localhost';
my $port = 7474;
my $server = "localhost";  # Host IP running the server

my $timestamp = time(); #unix timestamp
my @returnArray = (); # thereturn array for the data

$SIG{ALRM} = sub { 
	#$timestamp = time();
	#$json->{"root"} = \@returnArray;
	#my $json_text = to_json($json);
	#print $json_text;
	open (MYFILE, '>>video'.$name.'.csv'); 
	for my $item (@returnArray){
		print MYFILE "Vid $name,$item";
	}
	close(MYFILE);
	print("done");
	die "timeout"; 
};

# create the socket, connect to the port
socket(SOCKET,PF_INET,SOCK_STREAM,(getprotobyname('tcp'))[2])
   or die "Can't create a socket $!\n";
connect( SOCKET, pack_sockaddr_in($port, inet_aton($server)))
   or die "$timestamp Can't connect to port $port! \n";

my $line;
eval {
	alarm($time);
	while (1) {
			$line = <SOCKET>;
			$timestamp = time();
			push(@returnArray, $timestamp .", ". $line);
	        sleep(.1);
	}
	alarm(0);
};

close SOCKET or die "close: $!";