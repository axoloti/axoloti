just a test module

still lots to do....

- makefile needs to be easy to generate, probably just a list of source files, and the module name
- common 'rules' will hopefully move into an included makefile (alongside makefile.patch) (makefile.module?)

currently the awk script will only work for namespaces elements
for namespace, the symbol already has an E at the end of the symbol
_ZN4test4funcEi _ZN3foo4test4funcEi
for global, the E is missing, so we have to add it, this is trying since it means we need to parse the symbol correctly
_Z4funci _ZN4test4funcEi


testing:
important thing to test is statics!

note: nm -g reports exported symbols only .. I think this (hopefully) will include static
