#!/bin/bash
HOST=120.26.103.172
MON_FILE=/mydata/mysql/log/mysql-slow.log   # 指定所要监控的慢日志路径
MON_POINT_FILE=/tmp/mon_mysql_slow.point  # 指定MySQL慢日志的监控点存放的路径
DIFF_MON_FILE=/tmp/mon_mysql_slow.log     # 指定在监控频率内增加的MySQL慢日志信息存放路径
ADMIN_MAIL="luzhidong@rongcapital.cn","liujingjing@rongcapital.cn","huchenglong@rongcapital.cn","kangjian@rongcapital.cn"  # 指定发送给哪个管理员

[ -f $MON_POINT_FILE ] || echo 1 > $MON_POINT_FILE
NEW_POINT=$(awk 'END{print NR}' $MON_FILE)
OLD_POINT=$(<$MON_POINT_FILE)
[[ -z $OLD_POINT ]]&&OLD_POINT=1
SUM_POINT0=$(($NEW_POINT-$OLD_POINT))
SUM_POINT=${SUM_POINT0#-}
sed -n "$OLD_POINT,${NEW_POINT}p" $MON_FILE > $DIFF_MON_FILE
if [[ -s $DIFF_MON_FILE ]];then
    sed -i '1i '$SUM_POINT' Rows!'  $DIFF_MON_FILE
    /usr/local/bin/sendEmail -o message-content-type=html -f 'Slow Log <rkylin2015@yeah.net>' -s smtp.yeah.net -u 'Slow Log on $HOST' -xu rkylin2015@yeah.net -xp 'othnzrexmapxtbdl' -t $ADMIN_MAIL -o message-file=$DIFF_MON_FILE
    echo $NEW_POINT > $MON_POINT_FILE
fi
exit 
