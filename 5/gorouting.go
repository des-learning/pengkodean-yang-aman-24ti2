package main

import (
	"fmt"
	"sync"
	"time"
)

// Pass a pointer to the WaitGroup so the function can signal when it is done
func printLetter(letter string, counter *int, mu *sync.Mutex, wg *sync.WaitGroup) {
	defer wg.Done() // Decrements the counter by 1 when the function finishes

	for i := 1; i <= 100; i++ {
		mu.Lock()
		fmt.Printf("%s: %d\n", letter, *counter)
		(*counter)++;
		mu.Unlock()
		time.Sleep(100 * time.Millisecond)
	}
}

func main() {
	var wg sync.WaitGroup
	var mu sync.Mutex
	var counter = 0;

	// Tell the WaitGroup to expect 2 background tasks
	wg.Add(2)

	// Start the goroutines and pass the WaitGroup address
	go printLetter("A", &counter, &mu,  &wg)
	go printLetter("B", &counter, &mu, &wg)

	// Block the main thread here until the counter goes back to 0
	wg.Wait()

	fmt.Println("\nDone!")
}
