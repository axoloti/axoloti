/_ZN/ {
    sym=$3;
    nsym=$3;
    if(match(sym,"_ZN")) {
        # symbol is within a namespace 
        sub("_ZN", sprintf("_ZN%d%s", length(nspace), nspace) , nsym); 
        print sprintf("%s %s",sym, nsym);
    }
} 
