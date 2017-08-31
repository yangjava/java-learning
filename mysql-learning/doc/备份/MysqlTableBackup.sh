#!/bin/bash

BakDir=/mydatd/mysql/backup/tableback
LogFile=/mydatd/mysql/backup/log/tableback.log
Date=`date +%Y%m%d`
DumpFile=table$Date.tar.gz
username=*******
userpwd='*************'
mailto="liujingjing@rkylin.com.cn","luzhidong@rongcapital.cn"

echo " " >> $LogFile
echo " " >> $LogFile
echo "--------------------------" >> $LogFile
echo $(date +"%y-%m-%d %H:%M:%S") >>$LogFile
echo "-----------------" >> $LogFile

############## <<read backup table name && table mysqldump>> ################

    cat  /mydata/scripts/backup_scripts/BACKUP_TNAME | while read line; 
    do
        table_schema=$(echo $line|awk '{print $1}')
          table_name=$(echo $line|awk '{print $2}')
        mkdir -p $BakDir/table$Date/$table_schema 
      cd $BakDir/table$Date/$table_schema
    
    Begin=`date +"%Y年%m月%d日 %H:%M:%S"`
     /mydata/mysql/install/bin/mysqldump -u $username -p$userpwd --quick --opt --single-transaction  $table_schema $table_name>$table_name.sql

    if [ $? -ne 0 ]; then
       echo "$table_schema $table_name backup start:$Begin failed!!!" >> $LogFile
    else
       echo "$table_schema $table_name  backup start:$Begin finish:$Last_database $DATABASE.sql succ" >> $LogFile
    fi
  done


cd $BakDir
tar -czvf $DumpFile table$Date&& rm -rf table$Date


#############################################################################
# backup datafile remote 10.165.115.96  71file=/mydata/rsync/opdbbackup/    #
#############################################################################
/usr/bin/rsync -zvrtopg /mydatd/mysql/backup/tableback/$DumpFile --password-file=/etc/rsync.password  rsyncclient@10.165.115.96::71file/tableback
Last=`date +"%Y-%m-%d %H:%M:%S"`
 if [ $? -ne 0 ]; then
        echo "Table Backup scp to  remote backup start:$Last_1 failed" >> $LogFile
             /usr/local/bin/sendEmail -f operation_rsjf@rongcapital.cn -s smtp.exmail.qq.com -u "$Date Table Backup scp to remote backup failed" -xu operation_rsjf@rongcapital.cn -xp 'j84AM81QNcgTMQXF' -t $mailto  -m "new_master2 123.56.181.104 Table Backup scp to  remote backup start:$Last failed, $Date Table Backup failed"
 else

echo "Dump Done" >> $LogFile
echo  "Table Backup  start at $Begin finish at $Last. Schema Backup and scp to remote backup Success" >> $LogFile
/usr/local/bin/sendEmail -f operation_rsjf@rongcapital.cn -s smtp.exmail.qq.com -u "new_master2 $Date Table Backup successful" -xu operation_rsjf@rongcapital.cn -xp 'j84AM81QNcgTMQXF' -t $mailto  -m "new_master2 123.56.181.104   $Date Table Backup successful"
echo "--------------------------" >> $LogFile
echo "-----------------" >> $LogFile
fi

# delete datafile before 35 days
find /mydatd/mysql/backup/tableback/ -name "*.tar.gz" -type f -mtime +25 -exec rm {} \; > /dev/null 2>&1

#find $BakDir/daily -mtime +20 -type f|xargs rm -f
