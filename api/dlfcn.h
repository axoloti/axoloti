#ifndef API_DLFCN_H
#define API_DLFCN_H

#ifdef __cplusplus
extern "C" {
#endif

int dlclose(void * handle);
const char *dlerror(void);
void *dlopen(const char * file, int mode);
void *dlsym(void * handle, const char * name);
const char *dlname(void * handle);

#ifdef __cplusplus
} // extern "C"
#endif

#endif
