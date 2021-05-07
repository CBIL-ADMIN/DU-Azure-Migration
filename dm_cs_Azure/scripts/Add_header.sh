printf '%s\n' '0r !head -n 1 ../config/header_files/${1}' x | ex ../input/${1}
