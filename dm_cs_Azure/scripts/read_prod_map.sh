awk -F, 'BEGIN{OFS=","}{
	if($9=="PCBT"){
		pcbt++;
		print $1,$2,"=",$3,pcbt,$10,$11,$12,$13,$14,$15,$16,$17,$18;
		print $4,$5,$7,$8,pcbt,$10,$11,$12,$13,$14,$15,$16,$17,$18;
	}
	else if($9=="PC")
	{
		pcbt++
		print $1,$2,"=",$3,pcbt,$10,$11,$12,$13,$14,$15,$16,$17,$18;
	}
	else if($9=="BT")
	{
		pcbt++;
		print $4,$5,$7,$8,pcbt,$10,$11,$12,$13,$14,$15,$16,$17,$18;
	}
	else if($9=="MBT")
	{
		print $4,$5,$7,$8,$6,$10,$11,$12,$13,$14,$15,$16,$17,$18;
	}
}' config/PRODUCT_MAP.txt
