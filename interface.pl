use warnings;
use strict;
use Socket;

# initialize host and port
my $host = shift || 'localhost';
my $port = shift || 7474;
my $server = "localhost";  # Host IP running the server

my $timestamp = localtime();

$SIG{ALRM} = sub { 
	print "done";
	die "timeout"; 
};

# create the socket, connect to the port
socket(SOCKET,PF_INET,SOCK_STREAM,(getprotobyname('tcp'))[2])
   or die "Can't create a socket $!\n";
connect( SOCKET, pack_sockaddr_in($port, inet_aton($server)))
   or die "$timestamp Can't connect to port $port! \n";

my $line;
eval {
	alarm(3);
	while (1) {
			$line = <SOCKET>;
			$timestamp = localtime();
			print "$timestamp ";
	        print "$line\n";
	        sleep(.1);
	}
	alarm(0);
};

close SOCKET or die "close: $!";