#!/bin/bash
#######################################################################################
## CONTENT : MONITOR MYSQL                                                           ##
## VERSION : 1.0   Completed at 2015-05-08                                           ##
## SUPPORT : Mysql   Linux                                                           ##
## SHELL   : bash  ksh                                                               ##
## Created : Liujingjing                                                             ##
## Email   : liujingjing@rkylin.com.cn                                               ##
#######################################################################################
#system
IP="120.26.104.245"
user=rkylinadmin
pwd='Rkylin_web!1'
#file
enddate=`date +%Y%m%d%H%M`
CHECK_LOG_FILE=/mydata/monitor/file/${IP}_${enddate}.out
CHECK_LOG_FILE1=/mydata/monitor/file/${IP}_${enddate}_1.out
sql_result=0
#mail
RESULT=/mydata/monitor/file/dbcheck.html
subject=database_check_$IP
mailto="liujingjing@rkylin.com.cn","huchenglong@rkylin.com.cn"
echo > $CHECK_LOG_FILE
#############################################################################################
##mysql function status invoke
#############################################################################################
function get_variables_item()
{
sql_script="select variable_value from information_schema.$1 where variable_name = '$2'"
sql_result=`/mydata/mysql/install/bin/mysql -u $3 -p$4 -e "${sql_script}" -N`
}
#############################################################################################
##get_system check database normal
#############################################################################################
get_system(){
echo " ">>$CHECK_LOG_FILE
echo " ">>$CHECK_LOG_FILE
echo " ">>$CHECK_LOG_FILE
echo "###########################################">>$CHECK_LOG_FILE
echo "###              SYSTEM CHECK              ">>$CHECK_LOG_FILE
echo "###########################################">>$CHECK_LOG_FILE
echo "Disk Check:">>$CHECK_LOG_FILE
df -k >>$CHECK_LOG_FILE
echo " ">>$CHECK_LOG_FILE
echo "Memory Check:">>$CHECK_LOG_FILE
/usr/bin/vmstat>>$CHECK_LOG_FILE

echo "<br>服务器磁盘与内存情况请看附件<br>">>$CHECK_LOG_FILE1

db_check
}
#############################################################################################
##get_process check database normal
#############################################################################################
get_process(){
echo " ">>$CHECK_LOG_FILE
echo " ">>$CHECK_LOG_FILE
echo " ">>$CHECK_LOG_FILE
echo "###########################################">>$CHECK_LOG_FILE
echo "###         MYSQL PROCESS CHECK            ">>$CHECK_LOG_FILE
echo "###########################################">>$CHECK_LOG_FILE
echo "<br>MYSQL进程检查:<br>">>$CHECK_LOG_FILE1
ps -ef|grep mysql |grep -v grep >/dev/null 2>&1
if  [ $? -eq 0  ];then
  echo "OK!">>$CHECK_LOG_FILE
  echo "<br>OK!<br>">>$CHECK_LOG_FILE1
else 
  echo "Warning!!! Mysql Instance is down!">>$CHECK_LOG_FILE
  echo "<br>Warning!!! Mysql Instance is down!<br>">>$CHECK_LOG_FILE1
fi
}
#############################################################################################
##get_listener check listener port normal
#############################################################################################
get_listener(){
echo " ">>$CHECK_LOG_FILE
echo " ">>$CHECK_LOG_FILE
echo " ">>$CHECK_LOG_FILE
echo "###########################################">>$CHECK_LOG_FILE
echo "###        MYSQL LISTENER PORT CHECK       ">>$CHECK_LOG_FILE
echo "###########################################">>$CHECK_LOG_FILE
echo "<br>MYSQL监听检查:<br>">>$CHECK_LOG_FILE1
PORT=`netstat -na|grep "LISTEN"|grep "3316"|awk -F[:" "]+ '{print $5}'`
if [ "$PORT" == "3316" ];then
  echo "OK! PORT=3316.">>$CHECK_LOG_FILE
  echo "<br>OK! PORT=3316.<br>">>$CHECK_LOG_FILE1
else
  echo "Warning!!! MYSQL Listener is not running normal!">>$CHECK_LOG_FILE
  echo "<br>Warning!!! MYSQL Listener is not running normal!<br>">>$CHECK_LOG_FILE1
fi
}
#############################################################################################
##get_qps_tps check QPS and TPS
#############################################################################################
get_qps_tps(){
echo " ">>$CHECK_LOG_FILE
echo " ">>$CHECK_LOG_FILE
echo " ">>$CHECK_LOG_FILE
echo "###########################################">>$CHECK_LOG_FILE
echo "###         MYSQL QPS TPS CHECK            ">>$CHECK_LOG_FILE
echo "###########################################">>$CHECK_LOG_FILE
get_variables_item global_status Com_insert $user $pwd
Com_insert1=$sql_result
get_variables_item global_status Com_delete $user $pwd
Com_delete1=$sql_result
get_variables_item global_status Com_update $user $pwd
Com_update1=$sql_result
get_variables_item global_status Com_select $user $pwd
Com_select1=$sql_result
get_variables_item global_status Com_repalce $user $pwd
Com_repalce1=$sql_result
get_variables_item global_status Com_commit $user $pwd
Com_commit1=$sql_result
sleep 5
get_variables_item global_status Com_insert $user $pwd
Com_insert2=$sql_result
get_variables_item global_status Com_delete $user $pwd
Com_delete2=$sql_result
get_variables_item global_status Com_update $user $pwd
Com_update2=$sql_result
get_variables_item global_status Com_select $user $pwd
Com_select2=$sql_result
get_variables_item global_status Com_repalce $user $pwd
Com_repalce2=$sql_result
get_variables_item global_status Com_commit $user $pwd
Com_commit2=$sql_result
insert_ps=$(printf "%5d" `echo "scale=4;($Com_insert2-$Com_insert1)/5"|bc`)
delete_ps=$(printf "%5d" `echo "scale=4;($Com_delete2-$Com_delete1)/5"|bc`)
update_ps=$(printf "%5d" `echo "scale=4;($Com_update2-$Com_update1)/5"|bc`)
select_ps=$(printf "%5d" `echo "scale=4;($Com_select2-$Com_select1)/5"|bc`)
replace_ps=$(printf "%5d" `echo "scale=4;($Com_repalce2-$Com_repalce1)/5"|bc`)
qps=$(printf "%5d" `echo "scale=4;$insert_ps+$delete_ps+$update_ps+$select_ps"|bc`)
tps=$(printf "%5d" `echo "scale=4;($Com_commit2-$Com_commit1)/5"|bc`)
echo "ips: $insert_ps dps: $delete_ps ups: $update_ps sps: $select_ps rps: $replace_ps">>$CHECK_LOG_FILE
echo "<br>ips: $insert_ps dps: $delete_ps ups: $update_ps sps: $select_ps rps: $replace_ps<br>">>$CHECK_LOG_FILE1
echo "qps: $qps tps: $tps">>$CHECK_LOG_FILE
echo "<br>qps: $qps tps: $tps<br>">>$CHECK_LOG_FILE1
}
#############################################################################################
##get_dbinfo check database infomation
#############################################################################################
get_dbinfo(){
echo " ">>$CHECK_LOG_FILE
echo " ">>$CHECK_LOG_FILE
echo " ">>$CHECK_LOG_FILE
echo "###########################################">>$CHECK_LOG_FILE
echo "###    MYSQL DATABESE INFOMATION CHECK     ">>$CHECK_LOG_FILE
echo "###########################################">>$CHECK_LOG_FILE
echo "Mysql Size Check(MB):">>$CHECK_LOG_FILE
/mydata/mysql/install/bin/mysql -u $user -p$pwd -N -e "
select table_schema,round(sum(data_length+index_length)/1024/1024,4) as Size
  from information_schema.tables 
 group by table_schema
">>$CHECK_LOG_FILE
echo "<br>MYSQL数据大小检查(MB):<br>">>$CHECK_LOG_FILE1
/mydata/mysql/install/bin/mysql -u $user -p$pwd -N -e "
select CONCAT('<br>',info.table_schema,':',info.Size,'<br>')
  from ( 
select table_schema,round(sum(data_length+index_length)/1024/1024,4) as Size
  from information_schema.tables 
 group by table_schema) as info
">>$CHECK_LOG_FILE1
}
#############################################################################################
##get_cpu check database cup rate
#############################################################################################
get_cpu(){
echo " ">>$CHECK_LOG_FILE
echo " ">>$CHECK_LOG_FILE
echo " ">>$CHECK_LOG_FILE
echo "###########################################">>$CHECK_LOG_FILE
echo "###         MYSQL CPU RATE CHECK           ">>$CHECK_LOG_FILE
echo "###########################################">>$CHECK_LOG_FILE
echo "<br>MYSQL使用CPU情况检查:<br>">>$CHECK_LOG_FILE1
ps aux | grep 'mysqld' | grep -v grep | cut -d " " -f8 | cut -d . -f1 >/dev/null 2>&1
cpu_rate=$?
echo "CPU Rate is: $cpu_rate%.">>$CHECK_LOG_FILE
echo "<br>CPU Rate is: $cpu_rate%.<br>">>$CHECK_LOG_FILE1
}
#############################################################################################
##get_connect check database connect
#############################################################################################
get_connect(){
echo " ">>$CHECK_LOG_FILE
echo " ">>$CHECK_LOG_FILE
echo " ">>$CHECK_LOG_FILE
echo "###########################################">>$CHECK_LOG_FILE
echo "###           MYSQL CONNECT CHECK          ">>$CHECK_LOG_FILE
echo "###########################################">>$CHECK_LOG_FILE
echo "Mysql Connect Count:">>$CHECK_LOG_FILE
/mydata/mysql/install/bin/mysql -u $user -p$pwd -N -e "
select substring_index(host,':',1) as IP ,count(user) as cnt
  from information_schema.processlist
 where user != 'system user'
 group by substring_index(host,':',1)
">>$CHECK_LOG_FILE
echo "<br>MYSQL数据连接总数检查:<br>">>$CHECK_LOG_FILE1
/mydata/mysql/install/bin/mysql -u $user -p$pwd -N -e "
select CONCAT('<br>',info.IP,':',info.cnt,'<br>')
  from (
select substring_index(host,':',1) as IP ,count(user) as cnt
  from information_schema.processlist
 where user != 'system user'
 group by substring_index(host,':',1)) as info
">>$CHECK_LOG_FILE1

echo "<br>MYSQL数据连接汇总检查:<br>">>$CHECK_LOG_FILE1
echo " ">>$CHECK_LOG_FILE
get_variables_item global_status connections $user $pwd
connections=$sql_result
get_variables_item global_status threads_created $user $pwd
threads_created=$sql_result
thread_cache_hit=$(printf "%.2f" `echo "scale=4;($connections-$threads_created)/$connections*100"|bc`)
echo "Thread Cache Hit: $thread_cache_hit%. It Should More Than 90%">>$CHECK_LOG_FILE
echo "<br>Thread Cache Hit: $thread_cache_hit%. It Should More Than 90%<br>">>$CHECK_LOG_FILE1
echo " ">>$CHECK_LOG_FILE

get_variables_item global_status max_used_connections $user $pwd
max_used_connect=$sql_result
get_variables_item global_variables max_connections $user $pwd
max_connect=$sql_result
max_conn_hit=$(printf "%.2f" `echo "scale=4;$max_used_connect/$max_connect*100"|bc`)
echo "Max_used_connections Rate: $max_conn_hit%. It Should More Than 10%">>$CHECK_LOG_FILE
echo "<br>Max_used_connections Rate: $max_conn_hit%. It Should More Than 10%<br>">>$CHECK_LOG_FILE1
echo " ">>$CHECK_LOG_FILE

get_variables_item global_status threads_connected $user $pwd
threads_connect=$sql_result
echo "Threads Connected: $threads_connect.">>$CHECK_LOG_FILE
echo "<br>Threads Connected: $threads_connect.<br>">>$CHECK_LOG_FILE1
echo " ">>$CHECK_LOG_FILE

get_variables_item global_status threads_running $user $pwd
threads_run=$sql_result
echo "Threads Active: $threads_run.">>$CHECK_LOG_FILE
echo "<br>Threads Active: $threads_run.<br>">>$CHECK_LOG_FILE1
echo " ">>$CHECK_LOG_FILE

get_variables_item global_status open_files $user $pwd
open_file=$sql_result
get_variables_item global_variables open_files_limit $user $pwd
open_file_limit=$sql_result
open_files_usage_hit=$(printf "%.2f" `echo "scale=4;$open_file/$open_file_limit*100"|bc`)
echo "Open File Usage Rate: $open_files_usage_hit%. It should less than 75%">>$CHECK_LOG_FILE
echo "<br>Open File Usage Rate: $open_files_usage_hit%.  It should less than 75%<br>">>$CHECK_LOG_FILE1
echo " ">>$CHECK_LOG_FILE

get_variables_item global_status opened_tables $user $pwd
opened_tables=$sql_result
echo "Opened Tables: $opened_tables. 如果太大，需要执行FLUSH TABLES或者增大table_open_cache系统变量。">>$CHECK_LOG_FILE
echo "<br>Opened Tables: $opened_tables. 如果太大，需要执行FLUSH TABLES或者增大table_open_cache系统变量。<br>">>$CHECK_LOG_FILE1
}
#############################################################################################
##get_key check database key
#############################################################################################
get_key(){
echo " ">>$CHECK_LOG_FILE
echo " ">>$CHECK_LOG_FILE
echo " ">>$CHECK_LOG_FILE
echo "###########################################">>$CHECK_LOG_FILE
echo "###         MYSQL CACHE KEY CHECK          ">>$CHECK_LOG_FILE
echo "###########################################">>$CHECK_LOG_FILE
echo "<br>Key Buffer命中率:<br>">>$CHECK_LOG_FILE1
get_variables_item global_status Key_reads $user $pwd
key_read=$sql_result
get_variables_item global_status Key_read_requests $user $pwd
key_read_request=$sql_result
key_buffer_read_hit=$(printf "%.2f" `echo "scale=4;(1-$key_read/$key_read_request)*100"|bc`)
echo "Key Buffer Hit: $key_buffer_read_hit%. It Should More Than 99%">>$CHECK_LOG_FILE
echo "<br>Key Buffer Hit: $key_buffer_read_hit%. It Should More Than 99%<br>">>$CHECK_LOG_FILE1
}
#############################################################################################
##get_tmp check database TmpTable hite rate
#############################################################################################
get_tmp(){
echo " ">>$CHECK_LOG_FILE
echo " ">>$CHECK_LOG_FILE
echo " ">>$CHECK_LOG_FILE
echo "###########################################">>$CHECK_LOG_FILE
echo "###   MYSQL CACHE TMPTABLE CHECK          ">>$CHECK_LOG_FILE
echo "###########################################">>$CHECK_LOG_FILE
echo "<br>MYSQL的临时空间汇总检查:<br>">>$CHECK_LOG_FILE1
get_variables_item global_status created_tmp_disk_tables $user $pwd
created_tmp_disk_tables=$sql_result
get_variables_item global_status created_tmp_tables $user $pwd
created_tmp_tables=$sql_result
created_tmp_disk_tables_rate=$(printf "%.2f" `echo "scale=4;$created_tmp_disk_tables/$created_tmp_tables*100"|bc`)
echo "Created Tmp Disk Tables Hit: $created_tmp_disk_tables_rate%. It should less than 10%">>$CHECK_LOG_FILE
echo "<br>Created Tmp Disk Tables Hit: $created_tmp_disk_tables_rate%. It should less than 10%<br>">>$CHECK_LOG_FILE1
echo " ">>$CHECK_LOG_FILE

get_variables_item global_status Handler_read_rnd_next $user $pwd
handler_read_rnd_next=$sql_result
get_variables_item global_status Com_select $user $pwd
com_select=$sql_result
table_scan_rate=$(printf "%.2f" `echo "scale=4;$handler_read_rnd_next/$com_select"|bc`)
echo "Table scan rate: $table_scan_rate. It Should Less 4000">>$CHECK_LOG_FILE
echo "<br>Table scan rate: $table_scan_rate. It Should Less 4000<br>">>$CHECK_LOG_FILE1
}
#############################################################################################
##get_cache check database cache hite rate
#############################################################################################
get_cache(){
echo " ">>$CHECK_LOG_FILE
echo " ">>$CHECK_LOG_FILE
echo " ">>$CHECK_LOG_FILE
echo "###########################################">>$CHECK_LOG_FILE
echo "###   MYSQL QCACHE HITE RATE CHECK          ">>$CHECK_LOG_FILE
echo "###########################################">>$CHECK_LOG_FILE
echo "<br>MYSQL的Qcache值汇总检查:<br>">>$CHECK_LOG_FILE1
get_variables_item global_status Qcache_hits $user $pwd
qcache_hit=$sql_result
get_variables_item global_status qcache_inserts $user $pwd
qcache_inserts=$sql_result
qcache_hit=$(printf "%.2f" `echo "scale=4;$qcache_hit/($qcache_hit+$qcache_inserts)*100"|bc`)
echo "Query Cache Hit: $qcache_hit%. It should more than 75%">>$CHECK_LOG_FILE
echo "<br>Query Cache Hit: $qcache_hit%. It should more than 75%<br>">>$CHECK_LOG_FILE1
}
#############################################################################################
##get_innodb check database innodb
#############################################################################################
get_innodb(){
echo " ">>$CHECK_LOG_FILE
echo " ">>$CHECK_LOG_FILE
echo " ">>$CHECK_LOG_FILE
echo "###########################################">>$CHECK_LOG_FILE
echo "###     MYSQL INNODB CHECK       ">>$CHECK_LOG_FILE
echo "###########################################">>$CHECK_LOG_FILE
echo "<br>InnoDB Buffer Pool 命中率检查:<br>">>$CHECK_LOG_FILE1
get_variables_item global_variables innodb_buffer_pool_size $user $pwd
innodb_buffer_pool_size=$sql_result
get_variables_item global_status innodb_buffer_pool_reads $user $pwd
innodb_buffer_pool_reads=$sql_result
get_variables_item global_status innodb_buffer_pool_read_requests $user $pwd
innodb_buffer_pool_read_requests=$sql_result
get_variables_item global_status Innodb_buffer_pool_pages_data $user $pwd
Innodb_buffer_pool_pages_data=$sql_result
get_variables_item global_status Innodb_buffer_pool_pages_dirty $user $pwd
Innodb_buffer_pool_pages_dirty=$sql_result
get_variables_item global_status Innodb_buffer_pool_pages_total $user $pwd
Innodb_buffer_pool_pages_total=$sql_result
innodb_buffer_pool_size=$(printf "%.2f" `echo "scale=4;$innodb_buffer_pool_size/1024/1024"|bc`)
inn_buffer_pool_pages_hit=$(printf "%.2f" `echo "scale=4;(1-$innodb_buffer_pool_reads/$innodb_buffer_pool_read_requests)*100"|bc`)
inn_buffer_pool_dirty_hit=$(printf "%.2f" `echo "scale=4;($Innodb_buffer_pool_pages_dirty/$Innodb_buffer_pool_pages_data)*100"|bc`)
inn_buffer_pool_use_hit=$(printf "%.2f" `echo "scale=4;($Innodb_buffer_pool_pages_data/$Innodb_buffer_pool_pages_total)*100"|bc`)
ibdata1=`du -sh /mydata/mysql/data/ibdata1|cut -f1`
echo "ibdata1 is $ibdata1.">>$CHECK_LOG_FILE
echo "<br>ibdata1 is $ibdata1.<br>">>$CHECK_LOG_FILE1
echo "Innodb Buffer Pool Size: $innodb_buffer_pool_size MB.">>$CHECK_LOG_FILE
echo "<br>Innodb Buffer Pool Size: $innodb_buffer_pool_size MB.<br>">>$CHECK_LOG_FILE1
echo "Innodb Buffer Pool Hit: $inn_buffer_pool_pages_hit%. It should more than 90%">>$CHECK_LOG_FILE
echo "<br>Innodb Buffer Pool Hit: $inn_buffer_pool_pages_hit%. It should more than 90%<br>">>$CHECK_LOG_FILE1
echo "Innodb Buffer Pool Dirty Hit: $inn_buffer_pool_dirty_hit%.">>$CHECK_LOG_FILE
echo "<br>Innodb Buffer Dirty Hit: $inn_buffer_pool_dirty_hit%.<br>">>$CHECK_LOG_FILE1
echo "Innodb Buffer Pool Use Hit: $inn_buffer_pool_use_hit%.">>$CHECK_LOG_FILE
echo "<br>Innodb Buffer Pool Use Hit: $inn_buffer_pool_use_hit%.<br>">>$CHECK_LOG_FILE1
get_variables_item global_status Innodb_data_read $user $pwd
Innodb_data_read1=$sql_result
get_variables_item global_status Innodb_data_written $user $pwd
Innodb_data_written1=$sql_result
get_variables_item global_status Innodb_data_reads $user $pwd
Innodb_data_reads1=$sql_result
get_variables_item global_status Innodb_data_writes $user $pwd
Innodb_data_writes1=$sql_result
get_variables_item global_status Innodb_log_writes $user $pwd
Innodb_log_writes1=$sql_result
get_variables_item global_status Innodb_log_write_requests $user $pwd
Innodb_log_write_requests1=$sql_result
get_variables_item global_status Innodb_os_log_fsyncs $user $pwd
Innodb_os_log_fsyncs1=$sql_result
sleep 5
get_variables_item global_status Innodb_data_read $user $pwd
Innodb_data_read2=$sql_result
get_variables_item global_status Innodb_data_written $user $pwd
Innodb_data_written2=$sql_result
get_variables_item global_status Innodb_data_reads $user $pwd
Innodb_data_reads2=$sql_result
get_variables_item global_status Innodb_data_writes $user $pwd
Innodb_data_writes2=$sql_result
get_variables_item global_status Innodb_log_writes $user $pwd
Innodb_log_writes2=$sql_result
get_variables_item global_status Innodb_log_write_requests $user $pwd
Innodb_log_write_requests2=$sql_result
get_variables_item global_status Innodb_os_log_fsyncs $user $pwd
Innodb_os_log_fsyncs2=$sql_result
innodb_data_rps=$(printf "%5d" `echo "scale=4;($Innodb_data_read2-$Innodb_data_read1)/5"|bc`)
innodb_data_wps=$(printf "%5d" `echo "scale=4;($Innodb_data_written2-$Innodb_data_written1)/5"|bc`)
innodb_data_rps_from_file=$(printf "%5d" `echo "scale=4;($Innodb_data_reads2-$Innodb_data_reads1)/5"|bc`)
innodb_data_wps_from_file=$(printf "%5d" `echo "scale=4;($Innodb_data_writes2-$Innodb_data_writes1)/5"|bc`)
innodb_log_wps=$(printf "%5d" `echo "scale=4;($Innodb_log_writes2-$Innodb_log_writes1)/5"|bc`)
innodb_log_qps=$(printf "%5d" `echo "scale=4;($Innodb_log_write_requests2-$Innodb_log_write_requests1)/5"|bc`)
innodb_log_fsyncps=$(printf "%5d" `echo "scale=4;($Innodb_os_log_fsyncs2-$Innodb_os_log_fsyncs1)/5"|bc`)
echo "InnoDB每秒读量是 $innodb_data_rps B.">>$CHECK_LOG_FILE
echo "<br>InnoDB每秒读量是 $innodb_data_rps B.<br>">>$CHECK_LOG_FILE1
echo "InnoDB每秒写量是 $innodb_data_wps B.">>$CHECK_LOG_FILE
echo "<br>InnoDB每秒写量是 $innodb_data_wps B.<br>">>$CHECK_LOG_FILE1
echo "InnoDB每秒从文件中读取次数是 $innodb_data_rps_from_file.">>$CHECK_LOG_FILE
echo "<br>InnoDB每秒从文件中读取次数是 $innodb_data_rps_from_file.<br>">>$CHECK_LOG_FILE1
echo "InnoDB每秒从文件中写入次数是 $innodb_data_wps_from_file.">>$CHECK_LOG_FILE
echo "<br>InnoDB每秒从文件中写入次数是 $innodb_data_wps_from_file.<br>">>$CHECK_LOG_FILE1
echo "InnoDB每秒日志物理写次数是 $innodb_log_wps.">>$CHECK_LOG_FILE
echo "<br>InnoDB每秒日志物理写次数是 $innodb_log_wps.<br>">>$CHECK_LOG_FILE1
echo "InnoDB每秒日志写请求次数是 $innodb_log_qps.">>$CHECK_LOG_FILE
echo "<br>InnoDB每秒日志写请求次数是 $innodb_log_qps.<br>">>$CHECK_LOG_FILE1
echo "InnoDB每秒向日志文件完成的fsync()写数量是 $innodb_log_fsyncps.">>$CHECK_LOG_FILE
echo "<br>InnoDB每秒向日志文件完成的fsync()写数量是 $innodb_log_fsyncps.<br>">>$CHECK_LOG_FILE1
}
#############################################################################################
##get_slave check slave
#############################################################################################
get_slave(){
echo " ">>$CHECK_LOG_FILE
echo " ">>$CHECK_LOG_FILE
echo " ">>$CHECK_LOG_FILE
echo "###########################################">>$CHECK_LOG_FILE
echo "###             MYSQL SLAVE CHECK          ">>$CHECK_LOG_FILE
echo "###########################################">>$CHECK_LOG_FILE
echo "<br>MYSQL的Slave值汇总检查:<br>">>$CHECK_LOG_FILE1
array=($(/mydata/mysql/install/bin/mysql -u 'rkylinadmin' -p'Rkylin_web!1' -e "show slave status\G"|grep "Running"| grep -i io |awk '{print $2}'))
if [ "${array[0]}" = "Yes" ] || [ "${array[1]}" = "Yes" ]; then 
  echo "MYSQL Slave IO Process is OK!">>$CHECK_LOG_FILE
  echo "<br>MYSQL Slave IO Process is OK!<br>">>$CHECK_LOG_FILE1
else 
  echo "Warning!!!MYSQL Slave IO Process is not running.">>$CHECK_LOG_FILE
  echo "<br>Warning!!!MYSQL Slave IO Process is not running.<br>">>$CHECK_LOG_FILE1
fi

array=($(/mydata/mysql/install/bin/mysql -u 'rkylinadmin' -p'Rkylin_web!1' -e "show slave status\G"|grep "Running"| grep -i sql |awk '{print $2}'))
if [ "${array[0]}" = "Yes" ] || [ "${array[1]}" = "Yes" ]; then 
  echo "MYSQL Slave Sql Process is OK!">>$CHECK_LOG_FILE
  echo "<br>MYSQL Slave Sql Process is OK!<br>">>$CHECK_LOG_FILE1
else 
  echo "Warning!!!MYSQL Slave Sql Process is not running.">>$CHECK_LOG_FILE
  echo "<br>Warning!!!MYSQL Slave Sql Process is not running.<br>">>$CHECK_LOG_FILE1
fi

Slave_IO_Running=($(/mydata/mysql/install/bin/mysql -u 'rkylinadmin' -p'Rkylin_web!1' -e "show slave status\G"|grep "Running"| grep Slave_IO_Running |awk '{print $2}'))
Slave_SQL_Running=($(/mydata/mysql/install/bin/mysql -u 'rkylinadmin' -p'Rkylin_web!1' -e "show slave status\G"|grep "Running"| grep Slave_SQL_Running |awk '{print $2}'))
Seconds_Behind_Master=($(/mydata/mysql/install/bin/mysql -u 'rkylinadmin' -p'Rkylin_web!1' -e "show slave status\G"|grep "Behind"|awk '{print $2}'))
if [ "${Slave_IO_Running[0]}" = "Yes"  -a  "${Slave_SQL_Running[0]}" = "Yes" ]; then 
  echo "MYSQL Slave is OK!">>$CHECK_LOG_FILE
  echo "<br>MYSQL Slave is OK!<br>">>$CHECK_LOG_FILE1
  echo "Slave behind Master is: $Seconds_Behind_Master seconds. It should less than 40">>$CHECK_LOG_FILE
  echo "<br>Slave behind Master is: $Seconds_Behind_Master seconds. It should less than 40<br>">>$CHECK_LOG_FILE1
else 
  echo "Warning!!!MYSQL Slave is not running.">>$CHECK_LOG_FILE
  echo "<br>Warning!!!MYSQL Slave is not running.<br>">>$CHECK_LOG_FILE1
fi
echo "<br> <br>">>$CHECK_LOG_FILE1
}
#############################################################################################
##db_check check database all process
#############################################################################################
db_check(){
get_process
get_listener
get_qps_tps
get_dbinfo
get_cpu
get_connect
get_key
get_tmp
get_cache
get_innodb
get_slave 
}
echo "#######################################################################################">>$CHECK_LOG_FILE
echo "##                            MYSQL DB HEALTH CHECK                                  ##">>$CHECK_LOG_FILE
echo "##                                                                                   ##">>$CHECK_LOG_FILE
echo "## VERSION : 1.0   Completed at 2015-05-08      First Edition   order by Liujingjing ##">>$CHECK_LOG_FILE
echo "## SHELL   : bash                                                                    ##">>$CHECK_LOG_FILE
echo "## Created : Liujingjing                                                             ##">>$CHECK_LOG_FILE
echo "## Email   : liujingjing@rkylin.com.cn                                               ##">>$CHECK_LOG_FILE
echo "#######################################################################################">>$CHECK_LOG_FILE
echo " ">>$CHECK_LOG_FILE

date=`date +%Y-%m-%d`
echo " ">>$CHECK_LOG_FILE
cat /mydata/mysql/log/mysql-error.log|grep $date >> $CHECK_LOG_FILE
cat /mydata/mysql/log/mysql-error.log|grep $date >> $CHECK_LOG_FILE1

get_system


find /mydata/monitor/file/ -mtime +15 -type f|xargs rm -f
#############################################################################################
##check $check_log_file warnning have warning to email
#############################################################################################
#mail content
cat > $RESULT << EOF
  <html>
  <body>
  <head>
  <style type="text/css">
  <!--
  body {font-size:10pt;color=#3333FF;FONT-FAMILY:Microsoft YaHei;}
  td   {font-size:11pt;color=#3333FF;FONT-FAMILY:Microsoft YaHei;height:20pt;nowrap}
  -->
  </style>
  </head>
  mysql database ${IP} ${SID} checking has alarm,please see the appendix <p>
  --------------------
  <br>
  Warmth Warning: this email sended automatically by Operation & Maintenance  Dep, if has the question reply<p>
  Operation & Maintenance Dep<br>
  $enddate
  </body>
  </html>
EOF

#mail sucess
/usr/bin/mutt -s $subject -e 'set content_type="text/html"' -e 'set realname="Operation&Maintenance Dep"' -e 'set from=rkylin2015@yeah.net' $mailto -a $CHECK_LOG_FILE -i $CHECK_LOG_FILE1< $RESULT

