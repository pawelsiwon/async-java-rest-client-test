package main

import (
	"flag"
	"log"
	"math/rand"
	"net/http"
	"sync/atomic"
	"time"
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
	flag.DurationVar(&app.config.Timeout, "t", 1*time.Second, "Default timeout for connections")
	flag.Parse()

	srv := http.Server{
		Handler: app.handler(),
		Addr:    app.config.Addr,
	}

	if err := srv.ListenAndServe(); err != nil {
		log.Panic(err)
	}
}

func (app *application) handler() http.Handler {
	mux := http.NewServeMux()

	mux.Handle("/", app.route())

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
