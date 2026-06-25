#include "function.h"
#include <stdio.h>

int main() {
  // test add
  int expectation = add(10, 3);
  if (expectation != 13) {
    printf("test failed");
    return 1;
  }

  printf("all tests passed");

  return 0;
}
