#include "logging.h"
#include "test4.h"

int i=66;

/*
Ctest4::Ctest4(){
  log_printf("Ctest4 ctor\n");
}
Ctest4::~Ctest4() {
  log_printf("Ctest4 dtor\n");
}
*/

void Ctest4::increment() {
  i++;
}

void Ctest4::print() {
  log_printf("Ctest4 val=%d\n",i);
}

Ctest4 LIB_EXPORT(ctest4);
Ctest4 *LIB_EXPORT(ctest4p) = &LIB_EXPORT(ctest4);

extern "C" {
  Ctest4 * LIB_EXPORT(ctest4factory)(void) {
    return &LIB_EXPORT(ctest4);
  }
}

static void increment(void) {
  i++;
}

static void print1(void) {
  log_printf("stest1 val=%d\n",i);
}

stest1_t LIB_EXPORT(stest1) = {
    .increment = increment,
    .print = print1,
    .i=63
};

void doit(void){
  LIB_EXPORT(stest1).print();
  LIB_EXPORT(stest1).increment();
  LIB_EXPORT(stest1).print();
}

