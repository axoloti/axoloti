#ifndef TEST4_H
#define TEST4_H

#undef LIBNAME
#undef LIB_EXPORT

#define LIBNAME test
#define LIB_EXPORT(name) test4_##name

class Ctest4 {
public:
  virtual void increment();
  virtual void print();
};

extern "C" {
extern Ctest4 * LIB_EXPORT(ctest4p);
}

typedef struct {
  void (*increment)(void);
  void (*print)(void);
  int i;
} stest1_t;

extern stest1_t LIB_EXPORT(stest1);

extern "C" {
Ctest4 * test4_ctest4factory(void);
}

#endif
