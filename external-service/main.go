package main

import (
	"flag"
	"log"
	"math/rand"
	"net/http"
	"sync/atomic"
	"time"

	"net/http/pprof"
	_ "net/http/pprof"
)

type application struct {
	config struct {
		Addr    string
		Timeout time.Duration
	}
}

func main() {
	app := &application{}

	flag.StringVar(&app.config.Addr, "addr", ":4000", "Address of service")
	flag.DurationVar(&app.config.Timeout, "t", 0, "Timeout for connection, default is 0 which means random time from 0s to 20s")
	flag.Parse()

	srv := http.Server{
		Handler: app.handler(),
		Addr:    app.config.Addr,
	}

	log.Printf("Starting server at %v...\n", app.config.Addr)
	if err := srv.ListenAndServe(); err != nil {
		log.Panic(err)
	}
}

func (app *application) handler() http.Handler {
	mux := http.NewServeMux()

	mux.Handle("/data", app.route())
	mux.HandleFunc("/debug", pprof.Profile)

	return mux
}

func (app *application) route() http.HandlerFunc {
	counter := &atomic.Int32{}

	return func(w http.ResponseWriter, r *http.Request) {
		n := counter.Add(1)
		log.Printf("Started processing data %d", n)

		if app.config.Timeout != 0 {
			time.Sleep(app.config.Timeout)
		} else {
			time.Sleep(time.Duration(rand.Intn(21)) * time.Second)
		}

		w.Write([]byte(`{"status": "OK"}`))

		log.Printf("Finished request of %d...", n)
	}
}
