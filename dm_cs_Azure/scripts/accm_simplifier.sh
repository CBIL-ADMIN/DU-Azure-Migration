x=1
y=`expr $1+1`
while [[ "$x" -lt "$y" ]]; do
     awk -F, 'BEGIN{OFS=","}{if($3!=0){print $1,$3,$4}if($6!=0){print $1,$6,$7}if($9!=0){print $1,$9,$10}if($12!=0){print $1,$12,$13}if($15!=0){print $1,$15,$16}if($18!=0){print $1,$18,$19}if($21!=0){print $1,$21,$22}if($24!=0){print $1,$24,$25}if($27!=0){print $1,$27,$28}if($30!=0){print $1,$30,$31}}' ../output/batch_${x}/Accumulator.csv > ../output/batch_${x}/Accumulator.csv_
     ((x++))
done
