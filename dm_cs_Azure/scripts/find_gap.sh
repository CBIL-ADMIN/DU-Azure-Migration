x=1
y=`expr $1+1`
while [[ "$x" -lt "$y" ]]; do
    
    ./bt_unmatch.sh ${x} ${2} &
    sleep 1
    ((x++))
    
done
