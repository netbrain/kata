all: clean build run

clean: 
	rm test.wasm

build: 
	GOOS=js \
	GOARCH=wasm \
	go build -o test.wasm main_js.go

run: 
	go run main.go
