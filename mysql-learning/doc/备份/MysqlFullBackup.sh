#/bin/bash

BakDir=/mydatd/mysql/backup
LogDir=/mydatd/mysql/backup/log

username=********
userpwd='********'
Date=`date +%Y%m%d`
mailto="liujingjing@rkylin.com.cn","luzhidong@rongcapital.cn"


echo " " >> $FullLogFile
echo "--------------------------" >> $FullLogFile
echo $(date +"%y-%m-%d %H:%M:%S") >>$FullLogFile
echo "-----------------" >> $FullLogFile

Begin=`date +"%Y-%m-%d %H:%M:%S"`
############## <<start to full backup>> ################

FullBakDir=$BakDir/fullback
FullFullLogFile=$LogDir/fullbackup.log
FullDumpFile=full$Date.sql.gz

/mydata/mysql/install/bin/mysqldump -u $username -p$userpwd --flush-logs --quick --opt --master-data=2 --all-databases --single-transaction |gzip -9> $FullBakDir/$FullDumpFile

   if [ $? -ne 0 ]; then
       echo "all_database backup start:$Begin failed" >> $FullLogFile
       /usr/local/bin/sendEmail -f operation_rsjf@rongcapital.cn -s smtp.exmail.qq.com -u "new architecture master2 Backup failed" -xu operation_rsjf@rongcapital.cn -xp 'j84AM81QNcgTMQXF' -t $mailto  -m "new architecture master2 123.56.181.104 full backup start:$Begin Backup failed"
       exit 1
    else
        Last_1=`date +"%Y-%m-%d %H:%M:%S"`
        echo  "Full Backup [$FullDumpFile] start at $Begin finish at $Last_1. Full Backup Success" >> $FullLogFile
        #get binlog postion of fullback file
         zcat $FullBakDir/$FullDumpFile|head -n 22|tail -n 1|awk -F'=|,|;' '{print $2,$4}'|awk -F"'" '{print $2,$3}'>/mydatd/mysql/backup/postion
        # backup datafile remote 182.92.202.145
         /usr/bin/rsync -zvrtopg /mydatd/mysql/backup/fullback/$FullDumpFile --password-file=/etc/rsync.password  rsyncclient@10.165.115.96::71file/fullback

        # delete datafile before 35 days
         find /mydatd/mysql/backup/fullback/ -name "*.sql.gz" -type f -mtime +8 -exec rm {} \; > /dev/null 2>&1
        #find $FullBakDir/daily -mtime +20 -type f|xargs rm -f
         find /mydatd/mysql/backup/dailyback/ -name "*.sql.gz" -type f -mtime +7 -exec rm {} \; > /dev/null 2>&1
           if [ $? -ne 0 ]; then
               echo "fullbackup $FullDumpFile scp to  remote backup start:$Last_1 failed" >> $FullLogFile
               /usr/local/bin/sendEmail -f operation_rsjf@rongcapital.cn -s smtp.exmail.qq.com -u "fullbackup $FullDumpFile scp to remote backup failed" -xu operation_rsjf@rongcapital.cn -xp 'j84AM81QNcgTMQXF' -t $mailto  -m "new architecture master2 123.56.181.104 fullbackup $FullDumpFile scp to  remote backup start:$Last_1 failed"
               exit 1
           else
            Last=`date +"%Y-%m-%d %H:%M:%S"`
            echo "Dump Done" >> $FullLogFile
            echo  "Full Backup [$FullDumpFile] start at $Begin finish at $Last. Full Backup and scp to remote backup Success" >> $FullLogFile
            echo "--------------------------" >> $FullLogFile
            echo "-----------------" >> $FullLogFile
            /usr/local/bin/sendEmail -f operation_rsjf@rongcapital.cn -s smtp.exmail.qq.com -u "new architecture master2 Full Backup successful" -xu operation_rsjf@rongcapital.cn -xp 'j84AM81QNcgTMQXF' -t $mailto  -m "new architecture master2 123.56.181.104 full backup start:$Begin finish at $Last. Full Backup successful"
         fi
    fi
