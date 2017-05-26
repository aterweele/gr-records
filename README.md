# `gr-records`

Coding challenge for Guaranteed Rate.

## Assumptions

* In all input files, rows are newline delimited.

* "birth date, ascending" means oldest-first.

## Usage

CLI mode:

``` shellsession
$ lein run -- --help
  -d, --daemon                 run the web server
  -h, --help                   print usage message and exit
  -o, --output OUTPUT-TYPE  1  output format. 1 for gender falling
  back to last name, 2 for birth date, 3 for inverse last name
$ lein run -- test-resources/gr_records/*sv -o 1
|     :last-name | :first-name | :gender | :favorite-color | :birth-date |
|----------------+-------------+---------+-----------------+-------------|
|         Atwood |    Margaret |  female |            blue |  11/18/1939 |
...
$
```

To use the web interface ("daemon mode"), first start the process in
one terminal:

``` shellsession
$ lein run -- -d
```

Now you can use it from another:

``` shellsession
$ curl --header "Content-Type:text/csv" --data-binary "@test-resources/gr_records/knvb.csv" -v http://localhost:3000/records
...
$ curl -v http://localhost:3000/records/birthdate
[{"last-name":"van Persie","first-name":"Robin","gender":"male","favorite-color":"oranje","birth-date":"8/6/1983"},{"last-name":"de Guzmán","first-name":"Jonathan","gender":"male","favorite-color":"oranje","birth-date":"9/13/1987"},{"last-name":"Martins Indi","first-name":"Bruno","gender":"male","favorite-color":"oranje","birth-date":"2/8/1992"}]
```

Check the test coverage:

``` shellsession
$ lein cloverage
```

Please note that cloverage applies coverage metrics to the `user` and
`dev` namespaces, which are only used in development.

## Developing

### Setup

When you first clone this repository, run:

```sh
lein setup
```

This will create files for local configuration, and prep your system
for the project.

### Environment

To begin developing, start with a REPL.

```sh
lein repl
```

Then load the development environment.

```clojure
user=> (dev)
:loaded
```

Run `go` to initiate and start the system.

```clojure
dev=> (go)
:started
```

By default this creates a web server at <http://localhost:3000>.

When you make changes to your source files, use `reset` to reload any
modified files and reset the server.

```clojure
dev=> (reset)
:reloading (...)
:resumed
```

### Testing

Testing is fastest through the REPL, as you avoid environment startup
time.

```clojure
dev=> (test)
...
```

But you can also run tests through Leiningen.

```sh
lein test
```

### Generators

This project has several generator functions to help you create files.

To create a new endpoint:

```clojure
dev=> (gen/endpoint "bar")
Creating file src/foo/endpoint/bar.clj
Creating file test/foo/endpoint/bar_test.clj
Creating directory resources/foo/endpoint/bar
nil
```

To create a new component:

```clojure
dev=> (gen/component "baz")
Creating file src/foo/component/baz.clj
Creating file test/foo/component/baz_test.clj
nil
```

To create a new boundary:

```clojure
dev=> (gen/boundary "quz" foo.component.baz.Baz)
Creating file src/foo/boundary/quz.clj
Creating file test/foo/boundary/quz_test.clj
nil
```

## Deploying

FIXME: steps to deploy

## Legal

Copyright © 2017 FIXME
