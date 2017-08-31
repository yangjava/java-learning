#!/bin/bash
#set -x
SQL='/mydata/mysql/install/bin/mysql'
CONN='-u queryuser -p'Query!1' -P3316'
JAVA='/usr/local/java/jdk1.7.0_67/bin/java'
jar='/mydata/sendSMS/SendSMS.jar'
mobile='13683376883,18611176353,13426136816,18612791964'
mailto="liujingjing@rkylin.com.cn","kangjian@rongcapital.cn","luzhidong@rongcapital.cn"
#mobile='13683376883'



############# 2. get account_date and result ##############
current_time=`date +"%H"`
account_date=`date -d"+ 1 day" +"%Y-%m-%d"`
current_date=`date +"%Y-%m-%d "`
begin_time=$current_date"00:59:58"
begin_time1=$current_date"06:59:58"
begin_time2=$current_date"15:19:58"
result1=`$SQL $CONN -e "use account; select PARAMETER_VALUE,UPDATED_TIME from PARAMETER_INFO where PARAMETER_ID=1;"|grep -v 'PARAMETER_VALUE'`
result2=`$SQL $CONN -e "use account; select PARAMETER_VALUE,UPDATED_TIME from PARAMETER_INFO where PARAMETER_ID=2;"|grep -v 'PARAMETER_VALUE'`
PARAMETER_VALUE1=`echo $result1|awk '{print $1}'`
UPDATE_TIME1=`echo $result1|awk '{print $2}'`
PARAMETER_VALUE2=`echo $result2|awk '{print $1}'`
UPDATE_TIME2=`echo $result2|awk '{print $2}'`


########### 3. compare account_date and result ##############
if [ $current_time -eq 23 ];then
   if [ $PARAMETER_VALUE1 = $account_date  -a $UPDATE_TIME1 = $current_date  -a  $PARAMETER_VALUE2 = 0  -a $UPDATE_TIME2 = $current_date ] ;then
    echo "$current_date 23:00 日切成功!"
   else
 $JAVA -jar $jar $mobile "ERROR:主机为:生产主库 $current_date 23点日切失败! 请检查修复!"
   fi

else if [ $current_time -eq 01 ];then
    ERR_TEXT=`$SQL $CONN -e "use interest;SELECT * FROM LOG_APP WHERE JOB_NAME='PRO_INTEREST_BORROW' AND BEGIN_TIME>='$begin_time';"|grep -v 'ERR_CODE'`
    ERR_CODE=`echo $ERR_TEXT|awk '{print $4}'`
    BEGIN_TIME=`echo $ERR_TEXT|awk '{print $6,$7}'`
    END_TIME=`echo $ERR_TEXT|awk '{print $8,$9}'`
    time=$[$(date -d "$END_TIME" +%s)-$(date -d "$BEGIN_TIME" +%s)]

      if [ "$ERR_CODE" != "" ];then
          if [ $ERR_CODE -eq 0 ];then
               echo "$current_date PRO_INTEREST_BORROW 01:00 执行成功!"
             if [ $time -le 1800 ];then
                 echo "PRO_INTEREST_BORROW running quickly!"
             else
                 echo "ERROR:主机为:生产主库 PRO_INTEREST_BORROW 凌晨1点 running too long for $time seconds! 请检查修复!"
         /usr/local/bin/sendEmail -f operation_rsjf@rongcapital.cn -s smtp.exmail.qq.com -u "$HOST_NAME WARNING EMAIL" -xu operation_rsjf@rongcapital.cn -xp 'j84AM81QNcgTMQXF' -t $mailto  -m "WARNING: PRO_INTEREST_BORROW $begin_time running too long for $time seconds! 请检查修复!"
             fi
          else
            $JAVA -jar $jar $mobile "ERROR:主机为:生产主库 $current_date PRO_INTEREST_BORROW 凌晨 1 点执行失败! 请检查修复!"
          fi
       else
          $JAVA -jar $jar $mobile "ERROR:主机为:生产主库 $current_date PRO_INTEREST_BORROW 凌晨 1 点执行失败! 请检查修复!"
       fi

else if [ $current_time -eq 02 ];then
    ERR_TEXT=`$SQL $CONN -e "use account;SELECT * FROM LOG_APP WHERE JOB_NAME='PRO_ACCOUNT_INTEREST' AND BEGIN_TIME>='$begin_time';"|grep -v 'ERR_CODE'`
    ERR_CODE=`echo $ERR_TEXT|awk '{print $4}'`
    BEGIN_TIME=`echo $ERR_TEXT|awk '{print $6,$7}'`
    END_TIME=`echo $ERR_TEXT|awk '{print $8,$9}'`
    time=$[$(date -d "$END_TIME" +%s)-$(date -d "$BEGIN_TIME" +%s)]

      if [ "$ERR_CODE" != "" ];then
          if [ $ERR_CODE -eq 0 ];then
               echo "$current_date 01:00 日切成功!"
             if [ $time -le 1800 ];then
                 echo "PRO_ACCOUNT_INTEREST running quickly!"
             else
                 echo "ERROR:主机为:生产主库 PRO_ACCOUNT_INTEREST 凌晨2点 running too long for $time seconds! 请检查修复!"
         /usr/local/bin/sendEmail -f operation_rsjf@rongcapital.cn -s smtp.exmail.qq.com -u "$HOST_NAME WARNING EMAIL" -xu operation_rsjf@rongcapital.cn -xp 'j84AM81QNcgTMQXF' -t $mailto  -m "WARNING: PRO_ACCOUNT_INTEREST $begin_time running too long for $time seconds! 请检查修复!"
            #   $JAVA -jar $jar $mobile "ERROR:主机为:生产主库 PRO_ACCOUNT_INTEREST running too long for $time seconds! 请检查修复!"
             fi
          else
            $JAVA -jar $jar $mobile "ERROR:主机为:生产主库 $current_date 凌晨 1 点日切失败! 请检查修复!"
          fi
       else
          $JAVA -jar $jar $mobile "ERROR:主机为:生产主库 $current_date 凌晨 1 点日切失败! 请检查修复!"
       fi


else if [ $current_time -eq 15 ];then
    ERR_TEXT=`$SQL $CONN -e "use interest;SELECT * FROM LOG_APP WHERE JOB_NAME='PRO_CREDIT_ENTRY' AND BEGIN_TIME>='$begin_time2';"|grep -v 'ERR_CODE'`
    ERR_CODE=`echo $ERR_TEXT|awk '{print $4}'`
    BEGIN_TIME=`echo $ERR_TEXT|awk '{print $6,$7}'`
    END_TIME=`echo $ERR_TEXT|awk '{print $8,$9}'`
    time=$[$(date -d "$END_TIME" +%s)-$(date -d "$BEGIN_TIME" +%s)]

      if [ "$ERR_CODE" != "" ];then
          if [ $ERR_CODE -eq 0 ];then
               echo "$current_date 15:20 日切成功!"
             if [ $time -le 1800 ];then
                 echo "PRO_CREDIT_ENTRY running quickly!"
             else
                $JAVA -jar $jar $mobile "ERROR:主机为:生产主库 PRO_CREDIT_ENTRY running too long for $time seconds! 请检查修复!"
             fi
          else
            $JAVA -jar $jar $mobile "ERROR:主机为:生产主库 PRO_CREDIT_ENTRY $current_date 下午15点20 执行失败! 请检查修复!"
          fi
       else
          $JAVA -jar $jar $mobile "ERROR:主机为:生产主库 PRO_CREDIT_ENTRY $current_date 下午15点20 执行失败! 请检查修复!"
       fi



else if [ $current_time -eq 07 ];then
    ERR_TEXT=`$SQL $CONN -e "use interest;SELECT * FROM LOG_APP WHERE JOB_NAME='PRO_INTEREST_REPAYMENT' AND BEGIN_TIME>='$begin_time1';"|grep -v 'ERR_CODE'`
    ERR_CODE=`echo $ERR_TEXT|awk '{print $4}'`
    BEGIN_TIME=`echo $ERR_TEXT|awk '{print $6,$7}'`
    END_TIME=`echo $ERR_TEXT|awk '{print $8,$9}'`
    time=$[$(date -d "$END_TIME" +%s)-$(date -d "$BEGIN_TIME" +%s)]

       if [ "$ERR_CODE" != "" ];then
           if [ $ERR_CODE -eq 0 ];then
                 echo "$current_date 07:00 PRO_INTEREST_REPAYMENT 日切成功!"
              if [ $time -le 1800 ];then
                  echo "PRO_INTEREST_REPAYMENT running quickly!"
              else
                  $JAVA -jar $jar $mobile "ERROR:主机为:生产主库 PRO_INTEREST_REPAYMENT running too long for $time seconds! 请检查修复!"
              fi
          
          else
               $JAVA -jar $jar $mobile "ERROR:主机为:生产主库 PRO_INTEREST_REPAYMENT $current_date 早上 7 点计息失败! 请检查修复!"
           fi
       else
            $JAVA -jar $jar $mobile "ERROR:主机为:生产主库 PRO_INTEREST_REPAYMENT $current_date 早上 7 点计息失败! 请检查修复!"
       fi
    
   ERR_TEXT=`$SQL $CONN -e "use interest;SELECT * FROM LOG_APP WHERE JOB_NAME='PRO_INTEREST_BEAR' AND BEGIN_TIME>='$begin_time1';"|grep -v 'ERR_CODE'`
    ERR_CODE=`echo $ERR_TEXT|awk '{print $4}'`
    BEGIN_TIME=`echo $ERR_TEXT|awk '{print $6,$7}'`
    END_TIME=`echo $ERR_TEXT|awk '{print $8,$9}'`
    time=$[$(date -d "$END_TIME" +%s)-$(date -d "$BEGIN_TIME" +%s)]

       if [ "$ERR_CODE" != "" ];then
           if [ $ERR_CODE -eq 0 ];then
                 echo "$current_date 07:30 PRO_INTEREST_BEAR 日切成功!"
              if [ $time -le 1800 ];then
                  echo "PRO_INTEREST_BEAR running quickly!"
              else
                  $JAVA -jar $jar $mobile "ERROR:主机为:生产主库 PRO_INTEREST_BEAR running too long for $time seconds! 请检查修复!"
              fi

          else
               $JAVA -jar $jar $mobile "ERROR:主机为:生产主库 PRO_INTEREST_BEAR $current_date 早上 7 点计息失败! 请检查修复!"
           fi
       else
            $JAVA -jar $jar $mobile "ERROR:主机为:生产主库 PRO_INTEREST_BEAR $current_date 早上 7 点计息失败! 请检查修复!"
     fi

   fi
 fi
fi
fi
fi
