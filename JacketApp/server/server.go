package main

import (
	"encoding/json"
	"fmt"
	"io/ioutil"
	"net/http"
	"strconv"

	"github.com/codegangsta/negroni"
)

type Message struct {
	Message string `json:"message"`
	Code    int    `json:"code"`
}

func main() {

	mux := http.NewServeMux()
	mux.HandleFunc("/post", func(w http.ResponseWriter, req *http.Request) {

		code, _ := strconv.Atoi(req.FormValue("code"))
		message := Message{Message: req.FormValue("message"), Code: code}

		response, _ := json.Marshal(message)

		ioutil.WriteFile("public/index.json", response, 0644)
		http.Redirect(w, req, "/", http.StatusFound)
	})

	reset := NewReset()

	n := negroni.New()

	n.Use(reset)
	n.Use(negroni.NewStatic(http.Dir("public")))
	n.UseHandler(mux)

	n.Run(":3002")
}

type Reset struct {
	// Logger is the log.Logger instance used to log messages with the Logger middleware
}

// NewLogger returns a new Logger instance
func NewReset() *Reset {
	return &Reset{}
}

func (l *Reset) ServeHTTP(rw http.ResponseWriter, r *http.Request, next http.HandlerFunc) {
	next(rw, r)
	fmt.Println(r.URL.Path + "TEster")
	if r.URL.Path == "/index.json" {
		message := Message{Message: "", Code: 0}
		response, _ := json.Marshal(message)
		ioutil.WriteFile("public/index.json", response, 0644)
	}

}
