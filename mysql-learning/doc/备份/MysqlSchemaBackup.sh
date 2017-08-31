#!/bin/bash

BakDir=/mydatd/mysql/backup
LogDir=/mydatd/mysql/backup/log
Date=`date +%Y%m%d`
##PROCEDURE,EVENT,VIEW's DEFINER for 'rkylinadmin',so backup user is rkylinadmin ##
username=*******
userpwd='*********'
mailto="liujingjing@rkylin.com.cn","luzhidong@rongcapital.cn"


############## <<start to schema backup>> ################
Begin=`date +"%Y-%m-%d %H:%M:%S"`
SchemaBakDir=$BakDir/schemaback
LogFile=$LogDir/schemabackup.log
DumpFile=schema_$Date.tar.gz

echo " " >> $LogFile
echo " " >> $LogFile
echo "--------------------------" >> $LogFile
echo $(date +"%y-%m-%d %H:%M:%S") >>$LogFile
echo "-----------------" >> $LogFile
cd $SchemaBakDir

mkdir $SchemaBakDir/schema_$Date
for DATABASE in `/mydata/mysql/install/bin/mysql -u $username -p$userpwd -e "show databases"|egrep -v "Database|information_schema|mysql|test|performance_schema|sbtest"`
do
  Begin_1=`date +"%Y-%m-%d %H:%M:%S"`
  cd $SchemaBakDir/schema_$Date
  /mydata/mysql/install/bin/mysqldump -u $username -p$userpwd --flush-logs --quick --opt --master-data=2 --single-transaction $DATABASE|gzip -9>$DATABASE.sql.gz
  if [ $? -ne 0 ]; then
    echo "$DATABASE  backup start:$Begin_database failed!!!" >> $LogFile
     /usr/local/bin/sendEmail -f operation_rsjf@rongcapital.cn -s smtp.exmail.qq.com -u "new_master2 Schema Backup failed" -xu operation_rsjf@rongcapital.cn -xp 'j84AM81QNcgTMQXF' -t $mailto  -m "new_master2 123.56.181.104 Schema backup start:$Begin $DATABASE Backup failed"
  else
    Last_1=`date +"%Y-%m-%d %H:%M:%S"`
    echo "$DATABASE  backup start:$Begin_1 finish:$Last_1 $DATABASE.sql.gz success" >> $LogFile
  fi
done
   
   cd $SchemaBakDir
   tar czvf $DumpFile schema_$Date && rm -rf schema_$Date
   
   #############################################################################
   # backup datafile remote 10.165.115.96  71file=/mydata/rsync/opdbbackup/    #
   #############################################################################
   /usr/bin/rsync -zvrtopg /mydatd/mysql/backup/schemaback/$DumpFile --password-file=/etc/rsync.password  rsyncclient@10.165.115.96::71file/schemaback   


   # delete datafile before 35 days
   find /mydatd/mysql/backup/schemaback/ -name "*.tar.gz" -type f -mtime +7 -exec rm {} \; > /dev/null 2>&1
   Last=`date +"%Y-%m-%d %H:%M:%S"`
   if [ $? -ne 0 ]; then
        echo "Schema Backup [Schema_$Date.tar.gz] scp to  remote backup start:$Last_1 failed" >> $DailyLogFile
             /usr/local/bin/sendEmail -f operation_rsjf@rongcapital.cn -s smtp.exmail.qq.com -u "$Date Schema Backup scp to remote backup failed" -xu operation_rsjf@rongcapital.cn -xp 'j84AM81QNcgTMQXF' -t $mailto  -m "new_master2 123.56.181.104 Schema Backup scp to  remote backup start:$Last_1 failed, $Date Table Backup failed"
 else
    
echo "Dump Done" >> $LogFile
echo  "Schema Backup [$DumpFile]  start at $Begin finish at $Last. Schema Backup and scp to remote backup Success" >> $LogFile
/usr/local/bin/sendEmail -f operation_rsjf@rongcapital.cn -s smtp.exmail.qq.com -u "new_master2 $Date Daily Backup successful" -xu operation_rsjf@rongcapital.cn -xp 'j84AM81QNcgTMQXF' -t $mailto  -m "new_master2 123.56.181.104   $Date Schema Backup successful"
echo "--------------------------" >> $LogFile
echo "-----------------" >> $LogFile
fi

