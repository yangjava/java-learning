#!/bin/bash

BakDir=/mydatd/mysql/backup
LogDir=/mydatd/mysql/backup/log
BinDir=/mydata/mysql/binlog/3316
BinFile=/mydata/mysql/binlog/3316/binlog.index

username=******
userpwd='*********'
Date=`date +%Y%m%d`
mailto="liujingjing@rkylin.com.cn","luzhidong@rongcapital.cn"

start_binlog=`awk '{print $1}' $BakDir/postion`
start_pos=`awk '{print $2}' $BakDir/postion`

Begin=`date +"%Y-%m-%d %H:%M:%S"`
############## <<start to daily backup>> ################
 DailyBakDir=$BakDir/dailyback
 DailyLogFile=$LogDir/dailybackup.log
 DailyDumpFile=daily$Date.sql.gz

echo " " >> $DailyLogFile
echo " " >> $DailyLogFile
echo "--------------------------" >> $DailyLogFile
echo $(date +"%y-%m-%d %H:%M:%S") >>$DailyLogFile
echo "-----------------" >> $DailyLogFile
cd $DailyBakDir

   #####--1.1 get now binlog--######
     stop_binlog=`mysql -u root -p'iNMdzrww8A5Jm^Sm' -e "show master status;"|tail -n 1 |awk '{print $1}'`
     stop_pos=`mysql -u root -p'iNMdzrww8A5Jm^Sm' -e "show master status;"|tail -n 1 |awk '{print $2}'`
    if [ "$start_binlog" == "$stop_binlog" ]; then
       /mydata/mysql/install/bin/mysqlbinlog --no-defaults -v -v -v --start-position=$start_pos --stop-position=$stop_pos $BinDir/$start_binlog >>daily$Date.sql
    else
       start_line=`awk "/$start_binlog/{print NR}" $BinFile`
       stop_line=`wc -l $BinFile |awk '{print $1}'`
         for i in `seq $start_line $stop_line`
      do
          binlog1=`sed -n "$i"p  $BinFile`
          binlog=${binlog1:26}
          case "$binlog" in
          "$start_binlog")
           /mydata/mysql/install/bin/mysqlbinlog --no-defaults -v -v -v --start-position=$start_pos $BinDir/$binlog >>daily$Date.sql
                  ;;
          "$stop_binlog")
           /mydata/mysql/install/bin/mysqlbinlog --no-defaults -v -v -v --stop-position=$stop_pos $BinDir/$binlog >>daily$Date.sql
                  ;;
           *)
          /mydata/mysql/install/bin/mysqlbinlog --no-defaults -v -v -v $BinDir/$binlog >>daily$Date.sql
      esac
      done
   fi
   
   if [ $? -ne 0 ]; then
       echo "all_database backup start:$Begin failed" >> $DailyLogFile
       /usr/local/bin/sendEmail -f operation_rsjf@rongcapital.cn -s smtp.exmail.qq.com -u "new_master2 Backup failed" -xu operation_rsjf@rongcapital.cn -xp 'j84AM81QNcgTMQXF' -t $mailto  -m "new_master2 123.56.181.104 daily backup start:$Begin Backup failed"
       exit 1
    else
        Last_1=`date +"%Y-%m-%d %H:%M:%S"`
        echo  "Daily Backup [$DailyDumpFile] start at $Begin finish at $Last_1. Daily Backup Success" >> $DailyLogFile

         gzip -9 daily$Date.sql
         /bin/sleep 2
         rm -rf /mydatd/mysql/backup/dailyback/daily$Date.sql
         Last=`date +"%Y-%m-%d %H:%M:%S"`
       #############################################################################
       # backup datafile remote 10.165.115.96  71file=/mydata/rsync/opdbbackup/    #
       #############################################################################
           /usr/bin/rsync -zvrtopg /mydatd/mysql/backup/dailyback/$DailyDumpFile --password-file=/etc/rsync.password  rsyncclient@10.165.115.96::71file/dailyback

            if [ $? -ne 0 ]; then
              echo "Daily Backup [$DailyDumpFile] scp to  remote backup start:$Last_1 failed" >> $DailyLogFile
             /usr/local/bin/sendEmail -f operation_rsjf@rongcapital.cn -s smtp.exmail.qq.com -u "$Date Daily Backup scp to remote backup failed" -xu operation_rsjf@rongcapital.cn -xp 'j84AM81QNcgTMQXF' -t $mailto  -m "new_master2 123.56.181.104 Daily Backup scp to  remote backup start:$Last_1 failed, $Date Daily Backup failed"
     else 
         echo "Dump Done Backup  Start_binlog $start_binlog Start_postion $start_pos    Last_binlog $stop_binlog  Last_postion $stop_pos" >> $DailyLogFile
         echo  "Daily Backup [$DailyDumpFile]  start at $Begin finish at $Last. Daily Backup and scp to remote backup Success" >> $DailyLogFile
            /usr/local/bin/sendEmail -f operation_rsjf@rongcapital.cn -s smtp.exmail.qq.com -u "new_master2 $Date Daily Backup successful" -xu operation_rsjf@rongcapital.cn -xp 'j84AM81QNcgTMQXF' -t $mailto  -m "new_master2 123.56.181.104   $Date Daily Backup successful"
         echo "--------------------------" >> $DailyLogFile
         echo "-----------------" >> $DailyLogFile
       fi
   fi

