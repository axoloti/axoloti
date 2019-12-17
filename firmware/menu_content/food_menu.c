#include "../ui.h"
#include "food_menu.h"

// ------ Food menu stuff ------
// just a silly test...

#define AppleMenu_length 4
const ui_node_t AppleMenu[AppleMenu_length] = {
  { &nodeFunctionTable_action_function, "JamesGrieve", .fnctn = {0}},
  { &nodeFunctionTable_action_function, "GrannySmith", .fnctn = {0}},
  { &nodeFunctionTable_action_function, "Jonagold", .fnctn = {0}},
  { &nodeFunctionTable_action_function, "Cox", .fnctn = {0}},
};

#define NutsMenu_length 10
const ui_node_t NutsMenu[NutsMenu_length] = {
  { &nodeFunctionTable_action_function, "Cashew", .fnctn = {0}},
  { &nodeFunctionTable_action_function, "Peanut", .fnctn = {0}},
  { &nodeFunctionTable_action_function, "Pecan", .fnctn = {0}},
  { &nodeFunctionTable_action_function, "Walnut", .fnctn = {0}},
  { &nodeFunctionTable_action_function, "Pistachio", .fnctn = {0}},
  { &nodeFunctionTable_action_function, "Hazelnut", .fnctn = {0}},
  { &nodeFunctionTable_action_function, "Coconut", .fnctn = {0}},
  { &nodeFunctionTable_action_function, "Brazil nut", .fnctn = {0}},
  { &nodeFunctionTable_action_function, "Macadamia", .fnctn = {0}},
  { &nodeFunctionTable_action_function, "Almond", .fnctn = {0}}
};

#define DishMenu_length 8
const ui_node_t DishMenu[DishMenu_length] = {
  { &nodeFunctionTable_action_function, "Cake", .fnctn = {0}},
  { &nodeFunctionTable_action_function, "Salad", .fnctn = {0}},
  { &nodeFunctionTable_action_function, "Soup", .fnctn = {0}},
  { &nodeFunctionTable_action_function, "Waffle", .fnctn = {0}},
  { &nodeFunctionTable_action_function, "Ice cream", .fnctn = {0}},
  { &nodeFunctionTable_action_function, "Rice", .fnctn = {0}},
  { &nodeFunctionTable_action_function, "Spaghetti", .fnctn = {0}},
  { &nodeFunctionTable_action_function, "Pizza", .fnctn = {0}}
};

#define FoodMenu_length 3
const ui_node_t FoodMenu[FoodMenu_length] = {
  { &nodeFunctionTable_node_list, "Nuts", .nodeList = {NutsMenu, NutsMenu_length}},
  { &nodeFunctionTable_node_list, "Apple", .nodeList = {AppleMenu, AppleMenu_length}},
  { &nodeFunctionTable_node_list, "Dish", .nodeList = {DishMenu, DishMenu_length}}
};
