/[0-9]* / {
    sym=$3;
    nsym=$3;
    if(match(sym,"_ZN")) {
        # symbol is within a namespace 
        sub("_ZN", sprintf("_ZN%d%s", length(nspace), nspace) , nsym); 
    }
    else if(match(sym,"_Z")) {
        # symbol not in a namespace
        # this needs works, we need to get _ZN(len)namespace(len)nameE
        sub("_Z", sprintf("_ZN%d%s", length(nspace), nspace) , nsym); 
    }
    print sprintf("%s %s",sym, nsym);
} 
