Backup tool for twitter statuses. It's written by scala.

Required:
 JDK 6
 manen 2.0.9 or later

How to use:
$ git clone git://github.com/yuroyoro/TwitBackupper.git
$ mvn scala:run -DaddArgs="-i|<your twitter id>"

usage: Twitter backuuper
 -count <arg>                  count per file.max 200.default 200.
 -f,--file_name <file name>    output filename.
 -i,--id <id or screen name>   ID or screen name of the user for backup
                               statuses.
 -interval <arg>               interval for fetching one request.
 -m,--max_id <max id>          backup only statuses with an ID less than
                               the specified ID.
 -s,--since_id <since id>      backup only statuses with an ID greater
                               than the specified ID.
 -t,--type <file format>       ouput file format.xml or csv

