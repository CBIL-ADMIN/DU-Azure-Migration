awk -F"|" -v todir=$2 'function basename(file){
        sub(".*/", "", file)
        return file
    }BEGIN{
OFS="|"
}{
        if($1 ~ /[0-9]+/)
        {
                l_msi=length($1) #12
                m_msi=""
                for(i=1;i<=l_msi;i++)  # 1->12
                {
                        if(length(10-substr($1,i,1))>1)
                        {
                                n_dig=substr($1,i,1)
                        }
                        else
                        {
                                n_dig=10-substr($1,i,1)
                        }
                        m_msi=m_msi n_dig
                }
                print $1"|"m_msi >> todir"/mask_nos.lst"
                $1=m_msi
                print $0 >> todir"/"basename(FILENAME)
        }
        else
        {
                print $0 >> todir"/"basename(FILENAME)
        }

}' $1/*_be_*
