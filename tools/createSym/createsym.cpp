#include <iostream>
#include <fstream>
#include <cstdlib>
#include <set>

/*
process output from nm with external symbols, 
produce a refines symbol for a new namespace suitable for use with objcopy

simple implementation for experimental purposes

createsym test defines.sym redefines.sym
*/

std::set<std::string> symbols;

int process(const std::string& ns,const std::string& in, std::string& out) {
    out = "";
    std::string oldsym;
    std::string newsym;
    if(in.length()==0) return -1;

    size_t pos = in.find("_ZN");
    if( pos == std::string::npos) return -2;

    if (in[pos-2] =='U') return -3;

    pos = in.find("_ZN");
    oldsym = in.substr(pos,in.length()-pos);

    // duplicate symbol
    if (symbols.find(oldsym)!=symbols.end()) return -4;

    symbols.insert(oldsym);

    // now work on the newsym
    newsym = oldsym;

    // add namespace at start
    // _ZN6clouds10lut_cutoffE _ZN6myclds6clouds10lut_cutoffE
    newsym = newsym.replace(0,3, "_ZN" + std::to_string(ns.length()) + ns);

    // now search for compression , this is S_, or S<num>_ e.g. S0_
    // replace S_ = S0_ , S<num>_ with S<num+1>_ e.g. S1_ to S2_
    pos = newsym.find('S',0);
    while(pos != std::string::npos) {
        pos++;
        if(newsym[pos] =='_') {
            newsym.replace(pos,1,"0_");
            pos++;
        } else if(std::isdigit(newsym[pos])) {
            int i = 0;
            std::string tmpstr = newsym.substr(pos,newsym.length()-pos);
            size_t end;
            i=std::stoi(tmpstr,&end);
            newsym.replace(pos,end+1,std::to_string(i+1)+"_");
            pos += end;
        }
        pos = newsym.find('S',pos);
    }

    out = oldsym + " " + newsym;
    // std::cout << out << std::endl;
    return 0;
}

int main(int argc, char** argv) {

    if (argc != 4) {
        std::cerr << "createsym namespace defines.sym redefines.sym " << argc << std::endl;
        return -1;
    }

    std::string nspace  = argv[1];
    std::string inname  = argv[2];
    std::string outname = argv[3];

    std::cout << "processing namespace: " << nspace << " in: " << inname << " out: " << outname << std::endl;


    std::fstream infile;
    infile.open(inname, std::ios::in);
    if (!infile.is_open()) {
        std::cerr << "error opening input file: " << inname << std::endl;
        return -1;
    }

    std::fstream outfile;
    outfile.open(outname, std::fstream::out | std::fstream::trunc);
    if (!outfile.is_open()) {
        std::cerr << "error opening output file: " << outname << std::endl;
        return -1;
    }


    std::string iline = "";
    std::string oline = "";
    int lcount = 0;
    int limit = -1;


    while (getline(infile, iline) && (limit<0 || lcount < limit)  ) {
        lcount++;
        if (process(nspace, iline, oline)>=0) {
            outfile << oline << std::endl;
        }
    }
    outfile.close();
    return 0;
}


