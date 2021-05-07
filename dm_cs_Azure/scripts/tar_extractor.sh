x=1
y=`expr $1+1`
while [[ "$x" -lt "$y" ]]; do
     mkdir ../${2}/batch_${x}; tar -C ../${2}/batch_${x} -xvf ../${2}/${2}_${x}.tar.gz --strip-components 1
     ((x++))
done
