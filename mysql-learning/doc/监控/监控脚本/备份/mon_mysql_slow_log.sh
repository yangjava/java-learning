#!/bin/bash
#
# 本脚本用来在指定频率内监控 MySQL 慢日志的变化，并在发生增长时及时报警
# Written by kyo
# Mail : hcl0224@126.com
# Date : 2015-07-25
#
 
 
MON_FILE="$2"   # 指定所要监控的慢日志路径
SEC=1          # 指定所要监控的频率，即间隔多久去查看一次
MON_POINT_FILE=/tmp/mon_mysql_slow.point  # 指定MySQL慢日志的监控点存放的路径
DIFF_MON_FILE=/tmp/mon_mysql_slow.log     # 指定在监控频率内增加的MySQL慢日志信息存放路径
ADMIN_MAIL=hcl0224@126.com                # 指定发送给哪个管理员
 
function USAGE {
    echo -e "\033[31m脚本名称: \033[37m"
    echo "    $0"
    echo -e "\033[31m语法结构: \033[37m"
    echo "    $0 {start|stop|restart} MySQL慢日志文件路径"
    echo -e "\033[31m使用范例: \033[37m"
    echo "    $0 start /usr/local/mysql/log/mysql_slow.log"
    echo "    $0 stop"
    echo "    $0 restart /usr/local/mysql/log/mysql_slow.log"
    echo -e "\033[31m注意事项: \033[37m"
    echo "    1. 除了stop操作,start和restart操作时,\$2 参数不能为空"
    echo "    2. \$2 参数指定的文件必须存在"
    exit 2
}
 
function start {
    echo "MySQL慢日志监控进程已经启动,监控文件为 $MON_FILE ,监控频率为 ${SEC}d一次."
    while :
    do
        [ -f $MON_POINT_FILE ] || echo 1 > $MON_POINT_FILE
        NEW_POINT=$(awk 'END{print NR}' $MON_FILE)
        OLD_POINT=$(<$MON_POINT_FILE)
        [[ -z $OLD_POINT ]]&&OLD_POINT=1
        SUM_POINT0=$(($NEW_POINT-$OLD_POINT))
        SUM_POINT=${SUM_POINT0#-}
        sed -n "$OLD_POINT,${NEW_POINT}p" $MON_FILE > $DIFF_MON_FILE
        if [[ -s $DIFF_MON_FILE ]];then
            sed -i '1i 本次新增慢日志 '$SUM_POINT' 条'  $DIFF_MON_FILE
            mutt -s "[警告] 服务器 $(hostname) 产生 MySQL 慢日志 $SUM_POINT 条" $ADMIN_MAIL < $DIFF_MON_FILE
            > $DIFF_MON_FILE
            echo $NEW_POINT > $MON_POINT_FILE
        fi
        sleep ${SEC}d
    done
}
 
function stop {
        if [[ -n `ps -ef|awk '$0~"mon_mysql_slow_log.sh"{print $2}'` ]];
            then
                for PID in `ps -ef|awk '$0~"mon_mysql_slow_log.sh"{print $2}'`;
                       do
                           [[ $PID != $$ ]] && kill -9 $PID >& /dev/null
                       done
            else
                echo '目前暂无MySQL慢日志监控进程'
                exit 0
        fi
        echo 'MySQL慢日志监控进程已经停止运行'
}
 
function restart {
        stop
        start &
}
 
if [[ $1 == stop ]]
    then
        :
    else
        [[ $2 < 3 ]] && USAGE
        [[ ! -f $2 ]] && USAGE
fi
 
case $1 in
    start)
        start &
        ;;
    stop)
        stop
        ;;
    restart)
        MON_FILE=$2
        restart
        ;;
    *)
        USAGE
        ;;
esac