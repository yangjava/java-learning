#!/bin/bash
#set -x
SQL='/mydata/mysql/install/bin/mysql'
connect='-u ***** -p'******' -P3316'
mailto="liujingjing@rkylin.com.cn","kangjian@rkylin.com.cn","luzhidong@rongcapital.cn"


curr_time=`date "+%Y-%m-%d %H:%M:%S"`
last_time=`date -d '10 minutes ago' "+%Y-%m-%d %H:%M:%S"`
curr_timestamp=`date +%s`
last_timestamp=`date -d '10 minutes ago' +%s`

echo "##############   BEGIN CHECK AT $curr_time     #####################"

LOCAL_IP=`/sbin/ifconfig|grep inet |sed -n '2p'|cut -d : -f 2|cut -d ' ' -f -2`
HOST_NAME=$LOCAL_IP


#######获取当前时间############
current_time=`date +"%Y%m%d%H%M%S"`
event_date=`date +"%Y%m%d"`
event_time1=${event_date}"230000"
event_time2=${event_date}"150000"


###################  1.MYSQL RUNNING STATUS ######################
PORT=`netstat -anplt|grep mysql|grep 'LISTEN'|awk -F: '{print $2}'|awk '{print $1}'`
PS=` ps -ef|grep mysql |grep -v grep`
if [ "$PORT -eq 3316" -a $? -eq 0 ]; then
      echo "MySQL is OK!"
else
      worktime=`date +"%H%M%S"`
      weekday=`date  +"%w"`
     if [ $worktime -ge 090000 ] && [ $worktime -le 180000 ] && [  $weekday -ge 1 ] && [ $weekday -le 5 ];then
      /usr/local/bin/sendEmail -f operation_rsjf@rongcapital.cn -s smtp.exmail.qq.com -u "$HOST_NAME WARNING EMAIL" -xu operation_rsjf@rongcapital.cn -xp 'j84AM81QNcgTMQXF' -t $mailto  -m "ERROR:HOST:$HOST_NAME MYSQL IS DOWN!"
     else
      $JAVA -jar $jar $mobile "ERROR:主机为:$HOST_NAME 的MYSQL 服务器已宕掉!请检查修复!"
     fi
fi



#################  2.DEV STATUS ############################
mydata=`df -Th|grep mydata|awk '{print $6}'|cut -d "%" -f1`
xvdb1=`df -Th|grep xvdb1|awk '{print $6}'|cut -d "%" -f1`
if [ $mydata -ge 90 ]; then
/usr/local/bin/sendEmail -f operation_rsjf@rongcapital.cn -s smtp.exmail.qq.com -u "$HOST_NAME WARNING EMAIL" -xu operation_rsjf@rongcapital.cn -xp 'j84AM81QNcgTMQXF' -t $mailto  -m "ERROR:HOST:$HOST_NAME /mydata used is $mydata"%""
else
echo "/mydata used is OK!"
fi


################# 3.CHECK PROCESS NUMS#######################
process_num=`$SQL $connect -e"show processlist;"|grep -v Id|wc -l`
if [ $process_num -le 400 ];then
echo "Mysql processlist is OK!"
else
/usr/local/bin/sendEmail -f operation_rsjf@rongcapital.cn -s smtp.exmail.qq.com -u "$HOST_NAME WARNING EMAIL" -xu operation_rsjf@rongcapital.cn -xp 'j84AM81QNcgTMQXF' -t $mailto  -m "ERROR:HOST:$HOST_NAME process numbers is $process_num!"
fi

############ 4.CHECK DEADLOCK ###########
start_deadlock=`$SQL $connect -e "show engine innodb status\G"|grep -n  "LATEST DETECTED DEADLOCK"|cut -d : -f1`
end_deadlock=`$SQL $connect -e "show engine innodb status\G"|grep -n  "ROLL BACK TRANSACTION"|cut -d : -f1`
deadlock_time=`$SQL $connect -e"show engine innodb status\G"|grep -A2  "LATEST DETECTED DEADLOCK"|egrep -v "LATEST|---"|cut -d ' ' -f1,2`
deadlock_timestamp=`date -d "$deadlock_time" +%s`

if [ $start_deadlock -gt 0 -a $end_deadlock -gt $start_deadlock ];then
 if [ $deadlock_timestamp -gt $last_timestamp -a $deadlock_timestamp -le $curr_timestamp  ];then
/usr/local/bin/sendEmail -f operation_rsjf@rongcapital.cn -s smtp.exmail.qq.com -u "$HOST_NAME WARNING EMAIL" -xu operation_rsjf@rongcapital.cn -xp 'j84AM81QNcgTMQXF' -t $mailto  -m "ERROR:HOST:$HOST_NAME MYSQL HAVE DEADLOCK"
else
  echo "deadlock is old!"
fi
else
echo "NO DEADLOCK!"
fi

############# 5.CHECK LOCK WAITS###################

waitlock=`$SQL $connect -e"SELECT b.*  From information_schema.INNODB_TRX a,information_schema.PROCESSLIST b where a.trx_mysql_thread_id=b.ID and a.trx_state='RUNNING' ;"|grep -v 'COMMAND'`
waitlock_time=`echo $waitlock|awk '{print $6}'`
process_ID=`echo $waitlock|awk '{print $1}'`
echo $waitlock;
if [ "$waitlock_time" != "" ];then
if [ $waitlock_time -lt 300 ];then
  echo "进程ID 为  $process_ID 执行时间 $waitlock_time"
else
/usr/local/bin/sendEmail -f operation_rsjf@rongcapital.cn -s smtp.exmail.qq.com -u "$HOST_NAME WARNING EMAIL" -xu operation_rsjf@rongcapital.cn -xp 'j84AM81QNcgTMQXF' -t $mailto  -m "ERROR:HOST:$HOST_NAME process id is $process_ID running for $waitlock_time "
fi
else
 echo "NO WAITED LOCK!"
fi




################ 6.CHECK PROCESSLIST COUNT ##############
$SQL $connect -e "select user,count(*) as t_count from INFORMATION_SCHEMA.PROCESSLIST group by user having t_count>=150 order by t_count desc;"|grep -v 't_count'|while read schema_text
do
schema=`echo $schema_text|awk '{print $1}'`
schema_count=`echo $schema_text|awk '{print $2}'`
/usr/local/bin/sendEmail -f operation_rsjf@rongcapital.cn -s smtp.exmail.qq.com -u "$HOST_NAME WARNING EMAIL" -xu operation_rsjf@rongcapital.cn -xp 'j84AM81QNcgTMQXF' -t $mailto  -m "ERROR:HOST:$HOST_NAME  $schema process number is  $schema_count"
done


$SQL $connect -e "select DB,TIME,count(*) as t_count from information_schema.processlist where COMMAND='Sleep' group by DB,TIME having t_count>150 order by t_count desc;"|grep -v 't_count'|while read schema_text1
do
schema1=`echo $schema_text1|awk '{print $1}'`
schema_time1=`echo $schem_text|awk '{print $2}'`
schema_count1=`echo $schema_text1|awk '{print $3}'`
/usr/local/bin/sendEmail -f operation_rsjf@rongcapital.cn -s smtp.exmail.qq.com -u "$HOST_NAME WARNING EMAIL" -xu operation_rsjf@rongcapital.cn -xp 'j84AM81QNcgTMQXF' -t $mailto  -m "ERROR:HOST:$HOST_NAME $schema1 process number is $schema_count1 Sleep time $schema_time1"
done

end_time=`date +"%Y-%m-%d %H:%M:%S"`
echo "###########     END CHECK at $end_time!          #################### "
