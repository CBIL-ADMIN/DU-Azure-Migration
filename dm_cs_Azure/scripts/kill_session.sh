#!/bin/sh

ps -ef | grep MigTool|grep '^Jaganat'|awk -F" " '{print $2}'>session.txt
#ps -ef | grep MigTool.ex |awk -F" " '{print $3}'>session.txt

#cat session.txt | sed 's/ /,/g' >session_final.txt

while read line; do
  #echo $line
  sess_id=$(echo $line|awk -F, '{print}')
  echo $sess_id
  kill -9 $sess_id
done < session.txt

