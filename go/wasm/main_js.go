package main

import (
	"syscall/js"
	"time"
	"fmt"
)

var 	beforeUnload = make(chan struct{})
func main()  {

	beforeUnloadCB := js.NewEventCallback(0, func(event js.Value) {
		fmt.Println("Shutting down WASM")
		beforeUnload<- struct{}{}
	})
	defer beforeUnloadCB.Release()
	addeventlistener := js.Global().Get("addEventListener")
	addeventlistener.Invoke("beforeunload",beforeUnloadCB)

	alertTimeCB := js.NewCallback(alertTime)
	defer alertTimeCB.Release()

	js.Global().Set("TestComponent",map[string]interface{}{
		"test":"test",
		"test2":[]interface{}{1,2,3},
		"test3":nil,
		"test4":true,
		"test5": 5,
		"test6": 9.241,
		"test7": map[string]interface{}{"foo":"bar"},
		"test8": alertTimeCB,
		"test9": js.TypedArrayOf([]float64{1,2,3}),
	})

	js.Global().Set("time",alertTimeCB)


	fibCB := js.NewCallback(fibJs)
	defer fibCB.Release()

	js.Global().Set("gofib",fibCB)

	<-beforeUnload
}

func alertTime(args []js.Value){
	js.Global().Get("alert").Invoke(time.Now().String())
}


func fibJs(args []js.Value){
	start := time.Now()
	fmt.Println("GO ITER")
	fmt.Println(fib(args[0].Int()))
	fmt.Println(time.Now().Sub(start).String())
}

func fib(n int) int {
	if n <= 1 {
		return n
	}

	fib := 1
	prevFib := 1

	for i:= 2; i<n; i++ {
		temp := fib
		fib+= prevFib
		prevFib = temp
	}
	return fib
}

